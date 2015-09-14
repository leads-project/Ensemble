package org.infinispan.ensemble.cache.distributed;

import org.infinispan.commons.CacheException;
import org.infinispan.commons.util.concurrent.NotifyingFuture;
import org.infinispan.commons.util.concurrent.NotifyingFutureImpl;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.infinispan.ensemble.cache.distributed.partitioning.Partitioner;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Pierre Sutra
 */
public class DistributedEnsembleCache<K,V> extends EnsembleCache<K,V> {

   private Partitioner<K,V> partitioner;
   private boolean frontierMode;
   private EnsembleCache<K,V> frontierCache;
   private ExecutorService executorService;

   public DistributedEnsembleCache(String name, List<? extends EnsembleCache<K, V>> caches, Partitioner<K, V> partitioner){
      this(name,caches,partitioner,false);
   }

   public DistributedEnsembleCache(String name, List<? extends EnsembleCache<K, V>> caches, Partitioner<K, V> partitioner, boolean fm){
      this(name,caches,caches.get(0),partitioner,fm);
   }

   /**
    *
    * Create a DistributedEnsembleCache.
    * If the cache operates in frontier mode, the frontier cache is defined as local SiteEnsembleCache.
    * In case no such cache exists, the frontier cache is the last cache in the list.
    *
    *
    * @param name
    * @param caches
    * @param cache
    * @param partitioner
    * @param fm
    */
   public DistributedEnsembleCache(String name, List<? extends EnsembleCache<K, V>> caches, EnsembleCache<K,V> cache, Partitioner<K, V> partitioner, boolean fm){
      super(name,caches);
      assert fm | (cache!=null);
      this.partitioner = partitioner;
      this.frontierMode = fm;

      if (frontierMode) {
         for (EnsembleCache<K, V> ensembleCache : caches)
            if (ensembleCache.isLocal())
               frontierCache = ensembleCache;
         if (frontierCache == null)
            throw new CacheException("Invalid parameters");
      }
      
      executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

   }

   public EnsembleCache<K,V> getFrontierCache(){
      return frontierCache;
   }

   public boolean isFrontierMode(){
      return frontierMode;
   }

   @Override
   public int size() {
      int ret=0;
      for (EnsembleCache ensembleCache : caches){
         ret+=ensembleCache.size();
      }
      return ret;
   }

   @Override
   public boolean isEmpty() {
      for (EnsembleCache ensembleCache : caches)
         if (!ensembleCache.isEmpty())
            return false;
      return true;
   }

   @Override
   public V get(Object key) {
      return frontierMode ? frontierCache.get(key) : partitioner.locate((K)key).get(key);
   }

   @Override
   public V put(K key, V value) {
      return partitioner.locate(key).put(key,value);
   }

   @Override
   public V putIfAbsent(K key, V value) {
      return partitioner.locate(key).putIfAbsent(key,value);
   }

   @Override
   public NotifyingFuture<V> putIfAbsentAsync(K key, V value) {
      return partitioner.locate(key).putIfAbsentAsync(key,value);
   }

   @Override
   public NotifyingFuture<V> putAsync(K key, V value){
      return partitioner.locate(key).putAsync(key,value);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> map) {
      Map<EnsembleCache,Map<K,V>> maps = new HashMap<>();
      for (K key : map.keySet()) {
         EnsembleCache dest = partitioner.locate(key);
         if (!maps.containsKey(dest))
            maps.put(dest, new HashMap<K,V>());
         maps.get(dest).put(key,map.get(key));
      }
      for (EnsembleCache dest : maps.keySet()) {
         if (frontierMode && !dest.isLocal()){
            for(Entry<K,V> e : maps.get(dest).entrySet()) {
               dest.putIfAbsent(e.getKey(), e.getValue());
            }
         }else{
            dest.putAll(maps.get(dest));
         }
      }
   }

   @Override
   public NotifyingFuture<Void> putAllAsync(final Map<? extends K, ? extends V> data) {
      final NotifyingFutureImpl<Void> result = new NotifyingFutureImpl<>();
      Future<Void> future = executorService.submit(new Callable<Void>() {
         @Override
         public Void call() throws Exception {
            try {
               putAll(data);
               try {
                  result.notifyDone(null);
               } catch (Throwable t) {
                  log.trace("Error when notifying", t);
               }
               return null;
            } catch (Exception e) {
               try {
                  result.notifyException(e);
               } catch (Throwable t) {
                  log.trace("Error when notifying", t);
               }
               throw e;
            }
         }
      });
      result.setFuture(future);
      return result;

   }

   @Override
   public boolean containsKey(Object o) {
      for (EnsembleCache cache : caches){
         if (frontierMode) {
            if (cache.equals(frontierCache) && cache.containsKey(o)) {
               return true;
            }
         } else if (cache.containsKey(o)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public void clear() {
      for (EnsembleCache cache: caches)
         cache.clear();
   }

   @Override
   public V remove(Object o) {
      return partitioner.locate((K) o).remove(o);
   }

   @Override
   public Set<K> keySet() {
      Set<K> ret = new HashSet<>();
      for (EnsembleCache<K,V> cache : caches) {
         ret.addAll(cache.keySet());
      }
      return ret;
   }



   // LIFE CYCLE

   @Override
   public void stop(){
      for (EnsembleCache cache : caches) {
         cache.stop();
      }
   }

   @Override
   public void start(){
      for (EnsembleCache cache : caches) {
         cache.start();
      }
   }


}
