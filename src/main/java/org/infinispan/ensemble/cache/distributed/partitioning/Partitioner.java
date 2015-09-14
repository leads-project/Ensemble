package org.infinispan.ensemble.cache.distributed.partitioning;

import org.infinispan.ensemble.cache.EnsembleCache;

import java.util.List;

/**
 * @author Pierre Sutra
 */
public abstract  class  Partitioner<K,V>{

    List<EnsembleCache<K,V>> caches;

    public Partitioner(List<EnsembleCache<K,V>> caches){
        this.caches = caches;
    }

    public abstract EnsembleCache<K,V> locate(K k);

}
