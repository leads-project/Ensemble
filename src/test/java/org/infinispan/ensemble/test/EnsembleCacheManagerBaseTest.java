package org.infinispan.ensemble.test;

import org.apache.avro.generic.GenericContainer;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.testng.annotations.Test;

import java.io.Serializable;

import static org.testng.Assert.assertEquals;

/**
 * @author Pierre Sutra
 */
@Test(groups = "functional", testName = "EnsembleCacheManagerBaseTest")
public class EnsembleCacheManagerBaseTest  extends EnsembleAbstractTest<Serializable, GenericContainer> {

   private EnsembleCacheManager manager;
   private EnsembleCache<Serializable, GenericContainer> cache;

   @Override
   protected Class<Serializable> keyClass(){
      return Serializable.class;
   }

   @Override
   protected EnsembleCacheManager getManager() {
      if (manager==null) {
         synchronized (this) {
            if (manager==null)
               manager = new EnsembleCacheManager(
                     sites(),
                     null
               );
         }
      }
      return  manager;
   }

   @Override
   protected Class<GenericContainer> valueClass(){
      return GenericContainer.class;
   }

   @Override
   protected EnsembleCache<Serializable, GenericContainer> cache() {
      cache = manager.getCache();
      return cache;
   }

   @Override
   protected int numberOfSites() {
      return 3;
   }

   @Override
   protected int numberOfNodes() {
      return 1;
   }

   @Test
   public void baseManagerOperations() {
      getManager().start();
      EnsembleCache cache  = getManager().getCache("test");
      EnsembleCache cache1 = getManager().getCache("test");
      assertEquals(cache,cache1);

      getManager().stop();
      getManager().start();
      EnsembleCache cache2 = getManager().getCache("test");
      assertEquals(cache2.sites(), cache1.sites());
   }

}
