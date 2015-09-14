package org.infinispan.ensemble.cache.replicated;

import org.infinispan.commons.util.concurrent.NotifyingFuture;
import org.infinispan.ensemble.cache.EnsembleCache;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An atomic single-writer multiple-reader ensemble cache object on top of multiple ensemble cache.
 *
 * @author Pierre Sutra
 */
public class SWMREnsembleCache<K,V> extends ReplicatedEnsembleCache<K,V> {

    EnsembleCache<K,V> primary;

    public SWMREnsembleCache(String name, List<EnsembleCache<K, V>> caches) {
        super(name, caches);
        primary = caches.iterator().next();
    }

    //
    // READ
    //

    @Override
    public boolean containsKey(Object o) {
        return primary.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return primary.containsValue(o);
    }

    @Override
    public V get(Object o) {
        return primary.get(o);
    }

    @Override
    public Set<K> keySet() {
        return primary.keySet();
    }

    @Override
    public Collection<V> values() {
        return primary.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return primary.entrySet();
    }

    @Override
    public int size() {
        return primary.size();
    }

    @Override
    public boolean isEmpty() {
        return primary.isEmpty();
    }


    //
    // WRITE
    //

    @Override
    public V put(K key, V value) {
        V ret = primary.put(key, value);
        for(EnsembleCache<K,V> cache : quorumCacheContaining(primary)){
            if(!cache.equals(primary))
                cache.put(key,value);
        }
        return ret;
    }

    @Override
    public V remove(Object o) {
        V ret = primary.remove(o);
        for(EnsembleCache<K,V> cache : quorumCacheContaining(primary)){
            if(!cache.equals(primary))
                cache.remove(o);
        }
        return ret;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        primary.putAll(map);
        for(EnsembleCache<K,V> cache : quorumCacheContaining(primary)){
            if(!cache.equals(primary))
                cache.putAll(map);
        }
    }

    @Override
    public void clear() {
        primary.clear();
        for(EnsembleCache<K,V> cache : quorumCacheContaining(primary)){
            if(!cache.equals(primary))
                cache.clear();
        }
    }

    @Override
    public V putIfAbsent(K k, V v) {
        V ret = primary.putIfAbsent(k,v);
        for(EnsembleCache<K,V> cache : quorumCacheContaining(primary)){
            if(!cache.equals(primary))
                cache.putIfAbsent(k,v);
        }
        return ret;
    }

    @Override
    public NotifyingFuture<V> putIfAbsentAsync(K k, V v) {
       NotifyingFuture<V> ret = primary.putIfAbsentAsync(k, v);
       for(EnsembleCache<K,V> cache : quorumCacheContaining(primary)){
          if(!cache.equals(primary))
             cache.putIfAbsentAsync(k,v);
       }
       return ret;
    }


    @Override
    public boolean remove(Object o, Object o2) {
        boolean ret = primary.remove(o,o2);
        for(EnsembleCache<K,V> cache : quorumCacheContaining(primary)){
            if(!cache.equals(primary))
                cache.remove(o,o2);
        }
        return ret;
    }

    @Override
    public boolean replace(K k, V v, V v2) {
        boolean ret = primary.replace(k,v,v2);
        for(EnsembleCache<K,V> cache : quorumCacheContaining(primary)){
            if(!cache.equals(primary))
                cache.replace(k,v,v2);
        }
        return ret;
    }

    @Override
    public V replace(K k, V v) {
        V ret = primary.replace(k,v);
        for(EnsembleCache<K,V> cache : quorumCacheContaining(primary)){
            if(!cache.equals(primary))
                cache.replace(k,v);
        }
        return ret;
    }
}
