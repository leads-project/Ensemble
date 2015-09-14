package org.infinispan.ensemble.test;

import java.util.List;

/**
 *
 * @author Pierre Sutra
 */
public interface Driver {

   public abstract int getNumberOfSites();
   public abstract void setNumberOfSites(int numberOfSites);
   
   public abstract int getNumberOfNodes();
   public abstract void setNumberOfNodes(int numberOfNodes);

   public abstract List<String> getCacheNames();
   public abstract void setCacheNames(List<String> cacheNames);

   public abstract void createSites() throws Throwable;
   public abstract List<String> sites();
   
   public abstract void destroy();
   
}
