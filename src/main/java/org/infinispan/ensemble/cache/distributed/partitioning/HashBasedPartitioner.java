package org.infinispan.ensemble.cache.distributed.partitioning;

import org.infinispan.ensemble.cache.EnsembleCache;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.security.MessageDigest;


/**
 * @author Pierre Sutra
 */
public class HashBasedPartitioner<K,V> extends Partitioner<K,V> {

   protected static final Log log = LogFactory.getLog(HashBasedPartitioner.class);

   MessageDigest messageDigest;

   public HashBasedPartitioner(List<EnsembleCache<K,V>> caches){
      super(caches);
      log.debug(caches);
      try {
         messageDigest = MessageDigest.getInstance("SHA-256");
      } catch (NoSuchAlgorithmException e) {
         e.printStackTrace();
      }
   }

   @Override
   public synchronized EnsembleCache<K, V> locate(K k) {
      int index = 0;
      messageDigest.reset();
      messageDigest.update(k.toString().getBytes());
      for (byte b : messageDigest.digest()) index+=b;
      EnsembleCache<K,V> ret = caches.get(Math.abs(index)%caches.size());
      log.debug("locating "+k+" in "+ret);
      return ret;
   }

}
