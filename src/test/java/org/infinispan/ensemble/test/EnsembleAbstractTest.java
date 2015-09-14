package org.infinispan.ensemble.test;

import org.apache.avro.generic.GenericContainer;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import java.util.List;

import static java.util.Collections.EMPTY_LIST;

/**
 * @author Pierre Sutra
 */
public abstract class EnsembleAbstractTest<K,T>{

   private Driver driver;

   protected abstract int numberOfSites();
   protected abstract int numberOfNodes();
   protected abstract Class<? extends GenericContainer> valueClass();
   protected abstract Class<? extends K> keyClass();
   protected abstract EnsembleCacheManager getManager();
   protected abstract EnsembleCache<K,T> cache();

   @BeforeClass(alwaysRun = true)
   protected void init() throws Throwable {
      driver = (System.getProperty("org.infinispan.ensemble.test.connectionString")==null) ?
            new SimulationDriver() :
            new RealDriver(System.getProperty("org.infinispan.ensemble.test.connectionString"));
      driver.setNumberOfSites(numberOfSites());
      driver.setNumberOfNodes(numberOfNodes());
      driver.setCacheNames(cacheNames());
      driver.createSites();

      for (String cacheName : cacheNames())
         getManager().loadSchema(valueClass().newInstance().getSchema());
   }

   @AfterMethod(alwaysRun = true)
   protected void clearContent() throws Throwable {
      cache().clear();
   }

   @AfterClass(alwaysRun = true)
   public void destroy(){
      driver.destroy();
      getManager().stop();
   }

   public List<String> sites(){
      return driver.sites();
   }

   public List<String> cacheNames(){
      return EMPTY_LIST;
   }

}
