package org.infinispan.ensemble.cache.distributed.partitioning;

import org.infinispan.ensemble.cache.EnsembleCache;
import org.infinispan.ensemble.search.Search;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Pierre Sutra
 */
public abstract class ClusteringBasedPartitioner<K,V> extends Partitioner<K,V> {

    private Map<String, EnsembleCache<K,V>> cacheMap;
    private EnsembleCache<K,Coordinates> location;
    private QueryFactory queryFactory;
    private Random random;

    /**
     *
     * @param caches the caches to partition
     * @param location cache to store the locations of the keys
     */
    public ClusteringBasedPartitioner(List<EnsembleCache<K, V>> caches,
                                      EnsembleCache<K, Coordinates> location) {
        super(caches);

        cacheMap = new HashMap<>();
        for(EnsembleCache<K,V> ensembleCache : caches)
            cacheMap.put(ensembleCache.getName(),ensembleCache);

        this.location = location;
        this.queryFactory = Search.getQueryFactory(location);
        this.random = new Random(System.currentTimeMillis());
    }

    @Override
    public EnsembleCache<K, V> locate(K k) {

        Coordinates coordinatesK  = location.get(k);

        if (coordinatesK==null) {

            coordinatesK = buildCoordinates(k);

            Query query = queryFactory.from(Coordinates.class).build();
            List<Coordinates> list = query.list();

            double m = Double.MAX_VALUE;
            for (Coordinates coordinates :list) {
                double d = distanceFrom(coordinates.getLatitude(),coordinates.getLongitude(),coordinatesK.getLatitude(),coordinatesK.getLongitude());
                if (d<m) {
                    m = d;
                    coordinatesK.setCache(coordinates.getCache());
                }
            }

            if (coordinatesK.getCache()==null)
                coordinatesK.setCache(caches.get(random.nextInt(caches.size())).getName());
            location.put(k,coordinatesK);
        }

        return cacheMap.get(coordinatesK.getCache().toString());
    }

    protected abstract Coordinates buildCoordinates(K k);

    // Helpers

    private double distanceFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return   earthRadius * c;
    }

}
