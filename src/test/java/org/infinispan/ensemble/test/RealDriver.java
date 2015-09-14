package org.infinispan.ensemble.test;

import java.util.Arrays;
import java.util.List;

/**
 * @author Pierre Sutra
 */
public class RealDriver implements Driver {

   public List<String> sites;
   
   public RealDriver(String connectionString){
      sites = Arrays.asList(connectionString.split("\\|")); 
   }
   
   @Override 
   public int getNumberOfSites() {
      return 0;  // TODO: Customise this generated block
   }

   @Override 
   public void setNumberOfSites(int numberOfSites) {
      // TODO: Customise this generated block
   }

   @Override 
   public int getNumberOfNodes() {
      return 0;  // TODO: Customise this generated block
   }

   @Override 
   public void setNumberOfNodes(int numberOfNodes) {
      // TODO: Customise this generated block
   }

   @Override 
   public List<String> getCacheNames() {
      return null;  // TODO: Customise this generated block
   }

   @Override 
   public void setCacheNames(List<String> cacheNames) {
      // TODO: Customise this generated block
   }

   @Override 
   public void createSites() throws Throwable {
      // TODO: Customise this generated block
   }

   @Override 
   public List<String> sites() {
      return sites;
   }

   @Override public void destroy() {
      // TODO: Customise this generated block
   }
}
