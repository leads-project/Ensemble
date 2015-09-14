package org.infinispan.ensemble.cache;

import org.infinispan.client.hotrod.*;
import org.infinispan.commons.util.CloseableIterator;
import org.infinispan.commons.util.concurrent.NotifyingFuture;
import org.infinispan.ensemble.Site;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 *
 * An EnsembleCache offers a ConcurrentMap API over a list of EnsembleCaches.
 * Such an abstraction is of interest in various cases,
 * and typically when aggregating multiple Infinispan deployments.
 *
 * @author Pierre Sutra
 */
public abstract class EnsembleCache<K,V> implements RemoteCache<K,V>{

   protected static final Log log = LogFactory.getLog(EnsembleCache.class);

   protected String name;

   protected List<? extends EnsembleCache<K,V>> caches;

   public EnsembleCache(String name, List<? extends EnsembleCache<K,V>> caches){
      this.name = name;
      this.caches= caches;
   }

   public boolean isLocal(){
      return false;
   }

   // READ

   @Override
   public int size() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isEmpty() {
      throw new UnsupportedOperationException();
   }


   @Override
   public Set<K> keySet() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Collection<V> values() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean containsKey(Object o) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean containsValue(Object o) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V get(Object o) {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<V> getAsync(K key) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getVersion() {
      throw new UnsupportedOperationException();
   }

   // WRITE

   @Override
   public V putIfAbsent(K k, V v) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean remove(Object o, Object o2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean replace(K k, V v, V v2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V replace(K k, V v) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V remove(Object o) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> map) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear()  {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<V> putAsync(K key, V value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<V> putAsync(K key, V value, long lifespan, TimeUnit unit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<V> putAsync(K key, V value, long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<Void> putAllAsync(Map<? extends K, ? extends V> data) {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<Void> putAllAsync(Map<? extends K, ? extends V> data, long lifespan, TimeUnit unit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<Void> putAllAsync(Map<? extends K, ? extends V> data, long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<Void> clearAsync() {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<V> putIfAbsentAsync(K key, V value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<V> putIfAbsentAsync(K key, V value, long lifespan, TimeUnit unit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<V> putIfAbsentAsync(K key, V value, long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<V> removeAsync(Object key) {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<Boolean> removeAsync(Object key, Object value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<V> replaceAsync(K key, V value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<V> replaceAsync(K key, V value, long lifespan, TimeUnit unit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<V> replaceAsync(K key, V value, long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<Boolean> replaceAsync(K key, V oldValue, V newValue) {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<Boolean> replaceAsync(K key, V oldValue, V newValue, long lifespan, TimeUnit unit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<Boolean> replaceAsync(K key, V oldValue, V newValue, long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V put(K key, V value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V put(K key, V value, long lifespan, TimeUnit unit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V putIfAbsent(K key, V value, long lifespan, TimeUnit unit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> map, long lifespan, TimeUnit unit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V replace(K key, V value, long lifespan, TimeUnit unit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean replace(K key, V oldValue, V value, long lifespan, TimeUnit unit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V put(K key, V value, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V putIfAbsent(K key, V value, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> map, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V replace(K key, V value, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean replace(K key, V oldValue, V value, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeWithVersion(K key, long version) {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<Boolean> removeWithVersionAsync(K key, long version) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean replaceWithVersion(K key, V newValue, long version) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean replaceWithVersion(K key, V newValue, long version, int lifespanSeconds) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean replaceWithVersion(K key, V newValue, long version, int lifespanSeconds,
         int maxIdleTimeSeconds) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean replaceWithVersion(K k, V v, long l, long l1, TimeUnit timeUnit, long l2,
         TimeUnit timeUnit1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<Boolean> replaceWithVersionAsync(K key, V newValue, long version) {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<Boolean> replaceWithVersionAsync(K key, V newValue, long version,
         int lifespanSeconds) {
      throw new UnsupportedOperationException();
   }

   @Override
   public NotifyingFuture<Boolean> replaceWithVersionAsync(K key, V newValue, long version,
         int lifespanSeconds, int maxIdleSeconds) {
      throw new UnsupportedOperationException();
   }

   @Override
   public CloseableIterator<Entry<Object, Object>> retrieveEntries(String s, Set<Integer> set, int i) {
      throw new UnsupportedOperationException();
   }

   @Override
   public CloseableIterator<Entry<Object, Object>> retrieveEntries(String s, int i) {
      throw new UnsupportedOperationException();
   }

   @Override
   public VersionedValue<V> getVersioned(K key) {
      throw new UnsupportedOperationException();
   }

   @Override
   public MetadataValue<V> getWithMetadata(K key) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ServerStatistics stats() {
      throw new UnsupportedOperationException();
   }

   @Override
   public RemoteCache<K, V> withFlags(Flag... flags) {
      throw new UnsupportedOperationException();
   }

   @Override
   public RemoteCacheManager getRemoteCacheManager() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Map<K, V> getBulk() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Map<K, V> getBulk(int size) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Map<K, V> getAll(Set<? extends K> set) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getProtocolVersion() {
      throw new UnsupportedOperationException();
   }

   // LISTENERS

   @Override
   public void addClientListener(Object listener) {
      for (EnsembleCache cache : caches) {
         cache.addClientListener(listener);
      }
   }

   @Override
   public void addClientListener(Object listener, Object[] filterFactoryParams,
         Object[] converterFactoryParams) {
      for (EnsembleCache cache : caches) {
         cache.addClientListener(listener, filterFactoryParams, converterFactoryParams);
      }
   }

   @Override
   public void removeClientListener(Object listener) {
      for (EnsembleCache cache : caches) {
         cache.remove(listener);
      }
   }

   @Override
   public Set<Object> getListeners() {
      return caches.iterator().next().getListeners();
   }

   @Override
   public <T> T execute(String s, Map<String, ?> map) {
      throw new UnsupportedOperationException();
   }

   @Override
   public CacheTopologyInfo getCacheTopologyInfo() {
      throw new UnsupportedOperationException();
   }

   // LIFE CYCLE

   @Override
   public void start() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void stop() {
      throw new UnsupportedOperationException();
   }

   // OTHERS

   public String getName() {
      return name;
   }

   public List<? extends EnsembleCache<K,V>> getCaches(){ return caches;}

   public Set<Site> sites(){
      Set<Site> ret = new HashSet<>();
      for(EnsembleCache<K,V> cache : caches){
         ret.addAll(cache.sites());
      }
      return ret;
   }

   @Override
   public String toString(){
      String ret=getName()+"(";
      for(EnsembleCache cache : caches)
         ret+=cache.toString()+",";
      return ret.substring(0,ret.length()-1)+")";
   }


   @Override
   public int hashCode(){
      return name.hashCode();
   }

}
