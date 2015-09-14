package org.infinispan.ensemble.cache.replicated;

import org.infinispan.commons.util.concurrent.NotifyingFuture;
import org.infinispan.ensemble.cache.EnsembleCache;

import java.util.List;

/**
 * @author Pierre Sutra
 */
public class WeakEnsembleCache<K,V> extends ReplicatedEnsembleCache<K,V> {

   public WeakEnsembleCache(String name, List<EnsembleCache<K,V>> caches){
      super(name,caches);
   }

   @Override
   public String getVersion() {
      return someCache().getVersion();
   }

   @Override
   public V put(K key, V value) {
      EnsembleCache<K,V> cache = someCache();
      for(EnsembleCache<K,V> c : caches){
         if (!c.equals(cache))
            c.putAsync(key, value);
      }
      return cache.put(key,value);
   }

   @Override
   public V putIfAbsent(K key, V value) {
      EnsembleCache<K,V> cache = someCache();
      for(EnsembleCache<K,V> c : caches){
         if (!c.equals(cache))
            c.putAsync(key, value);
      }
      return cache.putIfAbsent(key,value);
   }

   @Override
   public NotifyingFuture<V> putIfAbsentAsync(K key, V value) {
      EnsembleCache<K,V> cache = someCache();
      for(EnsembleCache<K,V> c : caches){
         if (!c.equals(cache))
            c.putIfAbsentAsync(key, value);
      }
      return cache.putIfAbsentAsync(key,value);
   }



   /**
    * {@inheritDoc}
    *
    * Notice that if the replication factor is greater than 1, there is no consistency guarantee.
    * Otherwise, the consistency of the concerned cache applies.
    */
   @Override
   public V get(Object k) {
      return someCache().get(k);
   }

   @Override
   public int size() {
      return someCache().size();
   }

   @Override
   public boolean isEmpty() {
      return someCache().isEmpty();
   }

   public boolean containsKey(Object o) { return someCache().containsKey(o); }

}
