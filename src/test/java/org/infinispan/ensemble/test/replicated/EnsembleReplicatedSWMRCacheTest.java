package org.infinispan.ensemble.test.replicated;

import example.avro.WebPage;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.infinispan.ensemble.test.EnsembleCacheBaseTest;
import org.testng.annotations.Test;


/**
 * @author Pierre Sutra
 */
@Test(groups = "functional", testName = "EnsembleReplicatedSWMRCacheTest")
public class EnsembleReplicatedSWMRCacheTest extends EnsembleCacheBaseTest {

   private EnsembleCache<CharSequence,WebPage> cache;

   @Override
   protected synchronized EnsembleCache<CharSequence, WebPage> cache() {
      if (cache==null)
         cache = getManager().getCache(cacheName,numberOfSites()/2, EnsembleCacheManager.Consistency.SWMR);
      return cache;
   }

   @Override
   protected int numberOfSites() {
      return 3;
   }

   @Override
   public void asyncBaseOperations() {
      // FIXME
   }

}
