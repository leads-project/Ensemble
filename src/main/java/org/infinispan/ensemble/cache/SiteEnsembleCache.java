package org.infinispan.ensemble.cache;

import org.infinispan.client.hotrod.*;
import org.infinispan.client.hotrod.impl.RemoteCacheImpl;
import org.infinispan.commons.util.CloseableIterator;
import org.infinispan.commons.util.concurrent.NotifyingFuture;
import org.infinispan.ensemble.Site;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * A RemoteCache object wrapper.
 *
 * @author Pierre Sutra
 */
public class SiteEnsembleCache<K,V> extends EnsembleCache<K,V> implements RemoteCache<K,V>{

    private Site site;
    private RemoteCacheImpl<K,V> delegate;

    public SiteEnsembleCache(Site site, RemoteCache delegate) {
        super(delegate.getName(), Collections.EMPTY_LIST);
        this.delegate = (RemoteCacheImpl<K, V>) delegate;
        this.site = site;
    }

    public RemoteCacheImpl<K,V> getDelegeate(){
        return delegate;
    }

    @Override
    public Set<Site> sites(){
        return Collections.singleton(site);
    }

    @Override
    public boolean isLocal(){
        return site.isLocal();
    }

    @Override
    public NotifyingFuture<V> putAsync(K key, V value) {
        return delegate.putAsync(key, value);
    }

    @Override
    public NotifyingFuture<V> putAsync(K key, V value, long lifespan, TimeUnit unit) {
        return delegate.putAsync(key, value, lifespan, unit);
    }

    @Override
    public NotifyingFuture<V> putAsync(K key, V value, long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit) {
        return delegate.putAsync(key, value, lifespan, lifespanUnit, maxIdle, maxIdleUnit);
    }

    @Override
    public NotifyingFuture<Void> putAllAsync(Map<? extends K, ? extends V> data) {
        return delegate.putAllAsync(data);
    }

    @Override
    public NotifyingFuture<Void> putAllAsync(Map<? extends K, ? extends V> data, long lifespan, TimeUnit unit) {
        return delegate.putAllAsync(data, lifespan, unit);
    }

    @Override
    public NotifyingFuture<Void> putAllAsync(Map<? extends K, ? extends V> data, long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit) {
        return delegate.putAllAsync(data, lifespan, lifespanUnit, maxIdle, maxIdleUnit);
    }

    @Override
    public NotifyingFuture<Void> clearAsync() {
        return delegate.clearAsync();
    }

    @Override
    public NotifyingFuture<V> putIfAbsentAsync(K key, V value) {
        return delegate.putIfAbsentAsync(key, value);
    }

    @Override
    public NotifyingFuture<V> putIfAbsentAsync(K key, V value, long lifespan, TimeUnit unit) {
        return delegate.putIfAbsentAsync(key, value, lifespan, unit);
    }

    @Override
    public NotifyingFuture<V> putIfAbsentAsync(K key, V value, long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit) {
        return delegate.putIfAbsentAsync(key, value, lifespan, lifespanUnit, maxIdle, maxIdleUnit);
    }

    @Override
    public NotifyingFuture<V> removeAsync(Object key) {
        return delegate.removeAsync(key);
    }

    @Override
    public NotifyingFuture<Boolean> removeAsync(Object key, Object value) {
        return delegate.removeAsync(key, value);
    }

    @Override
    public NotifyingFuture<V> replaceAsync(K key, V value) {
        return delegate.replaceAsync(key, value);
    }

    @Override
    public NotifyingFuture<V> replaceAsync(K key, V value, long lifespan, TimeUnit unit) {
        return delegate.replaceAsync(key, value, lifespan, unit);
    }

    @Override
    public NotifyingFuture<V> replaceAsync(K key, V value, long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit) {
        return delegate.replaceAsync(key, value, lifespan, lifespanUnit, maxIdle, maxIdleUnit);
    }

    @Override
    public NotifyingFuture<Boolean> replaceAsync(K key, V oldValue, V newValue) {
        return delegate.replaceAsync(key, oldValue, newValue);
    }

    @Override
    public NotifyingFuture<Boolean> replaceAsync(K key, V oldValue, V newValue, long lifespan, TimeUnit unit) {
        return delegate.replaceAsync(key, oldValue, newValue, lifespan, unit);
    }

    @Override
    public NotifyingFuture<Boolean> replaceAsync(K key, V oldValue, V newValue, long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit) {
        return delegate.replaceAsync(key, oldValue, newValue, lifespan, lifespanUnit, maxIdle, maxIdleUnit);
    }

    @Override
    public NotifyingFuture<V> getAsync(K key) {
        return delegate.getAsync(key);
    }

    @Override
    public boolean containsKey(Object k) {
        return delegate.containsKey(k);
    }

    @Override
    public boolean containsValue(Object o) {
        return delegate.containsValue(o);
    }


    @Override
    public boolean removeWithVersion(K key, long version) {
        return delegate.removeWithVersion(key, version);
    }

    @Override
    public NotifyingFuture<Boolean> removeWithVersionAsync(K key, long version) {
        return delegate.removeWithVersionAsync(key, version);
    }

    @Override
    public boolean replaceWithVersion(K key, V newValue, long version) {
        return delegate.replaceWithVersion(key, newValue, version);
    }

    @Override
    public boolean replaceWithVersion(K key, V newValue, long version, int lifespanSeconds) {
        return delegate.replaceWithVersion(key, newValue, version, lifespanSeconds);
    }

    @Override
    public boolean replaceWithVersion(K key, V newValue, long version, int lifespanSeconds, int maxIdleTimeSeconds) {
        return delegate.replaceWithVersion(key, newValue, version, lifespanSeconds, maxIdleTimeSeconds);
    }

    @Override
    public boolean replaceWithVersion(K k, V v, long l, long l1, TimeUnit timeUnit, long l2,
          TimeUnit timeUnit1) {
        return delegate.replaceWithVersion(k, v, l, l1, timeUnit, l2, timeUnit1);
    }

    @Override
    public NotifyingFuture<Boolean> replaceWithVersionAsync(K key, V newValue, long version) {
        return delegate.replaceWithVersionAsync(key, newValue, version);
    }

    @Override
    public NotifyingFuture<Boolean> replaceWithVersionAsync(K key, V newValue, long version, int lifespanSeconds) {
        return delegate.replaceWithVersionAsync(key, newValue, version, lifespanSeconds);
    }

    @Override
    public NotifyingFuture<Boolean> replaceWithVersionAsync(K key, V newValue, long version, int lifespanSeconds, int maxIdleSeconds) {
        return delegate.replaceWithVersionAsync(key, newValue, version, lifespanSeconds, maxIdleSeconds);
    }

    @Override
    public CloseableIterator<Entry<Object, Object>> retrieveEntries(String s, Set<Integer> set, int i) {
        return delegate.retrieveEntries(s,i);
    }

    @Override
    public CloseableIterator<Entry<Object, Object>> retrieveEntries(String s, int i) {
        return delegate.retrieveEntries(s,i);
    }

    @Override
    public VersionedValue<V> getVersioned(K key) {
        return delegate.getVersioned(key);
    }

    @Override
    public MetadataValue<V> getWithMetadata(K key) {
        return delegate.getWithMetadata(key);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public ServerStatistics stats() {
        return delegate.stats();
    }

    @Override
    public RemoteCache<K, V> withFlags(Flag... flags) {
        return delegate.withFlags(flags);
    }

    @Override
    public RemoteCacheManager getRemoteCacheManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<K, V> getBulk() {
        return delegate.getBulk();
    }

    @Override
    public Map<K, V> getBulk(int size) {
        return delegate.getBulk(size);
    }

    @Override
    public Map<K, V> getAll(Set<? extends K> set) {
        return delegate.getAll(set);
    }

    @Override
    public String getProtocolVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addClientListener(Object listener) {
        delegate.addClientListener(listener);
    }

    @Override
    public void addClientListener(Object listener, Object[] filterFactoryParams, Object[] converterFactoryParams) {
        delegate.addClientListener(listener, filterFactoryParams, converterFactoryParams);
    }

    @Override
    public void removeClientListener(Object listener) {
        delegate.remove(listener);
    }

    @Override
    public Set<Object> getListeners() {
        return delegate.getListeners();
    }

    @Override
    public <T> T execute(String s, Map<String, ?> map) {
        return delegate.execute(s,map);
    }

    @Override
    public CacheTopologyInfo getCacheTopologyInfo() {
        return delegate.getCacheTopologyInfo();
    }

    @Override
    public V get(Object o) {
        return delegate.get(o);
    }

    @Override
    public String getVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V put(K key, V value) {
        return delegate.put(key,value);
    }

    @Override
    public V putIfAbsent(K k, V v) {
        return delegate.putIfAbsent(k,v);
    }

    // NYI

    @Override
    public V put(K key, V value, long lifespan, TimeUnit unit) {
        return delegate.put(key, value, lifespan, unit);
    }

    @Override
    public V putIfAbsent(K key, V value, long lifespan, TimeUnit unit) {
        return delegate.putIfAbsent(key, value, lifespan, unit);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map, long lifespan, TimeUnit unit) {
        delegate.putAll(map, lifespan, unit);
    }

    @Override
    public V replace(K key, V value, long lifespan, TimeUnit unit) {
        return delegate.replace(key, value, lifespan, unit);
    }

    @Override
    public boolean replace(K key, V oldValue, V value, long lifespan, TimeUnit unit) {
        return delegate.replace(key, oldValue, value, lifespan, unit);
    }

    @Override
    public V put(K key, V value, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
        return delegate.put(key, value, lifespan, lifespanUnit, maxIdleTime, maxIdleTimeUnit);
    }

    @Override
    public V putIfAbsent(K key, V value, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
        return delegate.putIfAbsent(key, value, lifespan, lifespanUnit, maxIdleTime, maxIdleTimeUnit);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
        delegate.putAll(map, lifespan, lifespanUnit, maxIdleTime, maxIdleTimeUnit);;
    }

    @Override
    public V replace(K key, V value, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
        return delegate.replace(key, value, lifespan, lifespanUnit, maxIdleTime, maxIdleTimeUnit);
    }

    @Override
    public boolean replace(K key, V oldValue, V value, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
        return delegate.replace(key, oldValue, value, lifespan, lifespanUnit, maxIdleTime, maxIdleTimeUnit);
    }

    @Override
    public V remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        delegate.putAll(map);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public boolean remove(Object o, Object o2) {
        return delegate.remove(o, o2);
    }

    @Override
    public boolean replace(K k, V v, V v2) {
        return delegate.replace(k, v, v2);
    }

    @Override
    public V replace(K k, V v) {
        return delegate.replace(k,v);
    }

    @Override
    public void start() {
        delegate.start();
    }

    @Override
    public void stop() {
        delegate.stop();
    }

    @Override
    public String toString(){
        return site.toString();
    }
}
