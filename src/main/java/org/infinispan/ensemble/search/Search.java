package org.infinispan.ensemble.search;

import org.infinispan.avro.hotrod.QueryFactory;
import org.infinispan.commons.CacheException;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.infinispan.ensemble.cache.SiteEnsembleCache;
import org.infinispan.ensemble.cache.distributed.DistributedEnsembleCache;
import org.infinispan.ensemble.cache.replicated.ReplicatedEnsembleCache;

import java.util.List;


/**
  * @author Pierre Sutra
 */
public class Search {

    private Search() {
    }

    public static QueryFactory getQueryFactory(EnsembleCache cache) {

        if ( cache instanceof DistributedEnsembleCache){
            DistributedEnsembleCache distributedEnsembleCache =  ((DistributedEnsembleCache)cache);
            if (distributedEnsembleCache.isFrontierMode())
                return Search.getQueryFactory(((DistributedEnsembleCache) cache).getFrontierCache());
        } else if (cache instanceof ReplicatedEnsembleCache) {
            List<SiteEnsembleCache> list = cache.getCaches();
            for (SiteEnsembleCache c : list) {
                if (c.isLocal())
                    return new QueryFactory(c.getDelegeate());
            }
        } else if (cache instanceof SiteEnsembleCache){
            return new QueryFactory(((SiteEnsembleCache) cache).getDelegeate());
        }

        throw new CacheException("Unsupported Ensemble cache type and parameters.");

    }

}
