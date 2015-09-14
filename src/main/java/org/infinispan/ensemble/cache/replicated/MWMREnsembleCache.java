package org.infinispan.ensemble.cache.replicated;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.VersionedValue;
import org.infinispan.commons.util.concurrent.NotifyingFuture;
import org.infinispan.ensemble.cache.EnsembleCache;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * @author Pierre Sutra
 */
public class MWMREnsembleCache<K,V> extends ReplicatedEnsembleCache<K,V> {

    public MWMREnsembleCache(String name, List<EnsembleCache<K, V>> caches){
        super(name,caches);
    }

    @Override
    public V get(Object key) {
        Map<RemoteCache<K,V>, VersionedValue<V>> previous = previousValues((K)key);
        VersionedValue<V> g = greatestValue(previous);
        if(g.getValue()==null)
            return null;
        if(!isStable(previous, g))
            writeStable((K) key, g.getValue(), g.getVersion(), previous.keySet());
        return g.getValue();
    }

    @Override
    public V put(K key, V value) {
        Map<RemoteCache<K,V>, VersionedValue<V>> previous = previousValues(key);
        VersionedValue<V> g = greatestValue(previous);
        writeStable(key, value, g.getVersion(), previous.keySet());
        if(g.getValue()!=null)
            return g.getValue();
        return null;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        Map<RemoteCache<K,V>, VersionedValue<V>> previous = previousValues(key);
        VersionedValue<V> g = greatestValue(previous);
        if (g.getVersion()==0) {
            writeStable(key, value, g.getVersion(), previous.keySet());
            if (g.getValue() != null)
                return g.getValue();
            return null;
        }
        return g.getValue();
    }

    //
    // HELPERS
    //

    private void writeStable(K key, V value, long version, Set<RemoteCache<K, V>> caches) {
        List<NotifyingFuture<Boolean>> futures = new ArrayList<NotifyingFuture<Boolean>>();
        for(RemoteCache<K,V> c : caches) {
            futures.add(c.replaceWithVersionAsync(key, value, version));
        }
        for(NotifyingFuture<Boolean> future : futures){
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

    }

    private Map<RemoteCache<K,V>, VersionedValue<V>> previousValues(K k){
        Map<RemoteCache<K,V>,VersionedValue<V>> values = new HashMap<>();
        for(EnsembleCache<K,V> cache : quorumCache()){
            try {
                values.put(
                      cache,
                      ((Callable<VersionedValue<V>>) () -> cache.getVersioned(k)).call());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Map<RemoteCache<K,V>, VersionedValue<V>> ret = new HashMap<>();
        for(RemoteCache<K,V> cache : values.keySet()){
            VersionedValue<V> tmp = values.get(cache);
            ret.put(cache,tmp);
        }
        return ret;
    }

    private boolean isStable(Map<RemoteCache<K, V>, VersionedValue<V>> map, VersionedValue<V> v){
        int count = 0;
        if(v==null) return true;
        for(VersionedValue<V> w: map.values()){
            if( w!=null && w.getVersion()==v.getVersion())
                count++;
        }
        return count >= quorumSize();
    }

    private VersionedValue<V> greatestValue(Map<RemoteCache<K,V>,VersionedValue<V>> map){
        VersionedValue<V> ret = new VersionedValue<V>() {
            @Override
            public long getVersion() {
                return 0;
            }

            @Override
            public V getValue() {
                return null;  // TODO: Customise this generated block
            }
        };
        for(VersionedValue<V> v: map.values()){
            if ( v!=null && (ret.getValue()==null || v.getVersion()>ret.getVersion()))
                ret = v;
        }
        return ret;
    }

}
