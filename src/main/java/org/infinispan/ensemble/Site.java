package org.infinispan.ensemble;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.ensemble.cache.SiteEnsembleCache;
import org.infinispan.manager.CacheContainer;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.URL;

/**
 *
 * A site is a geographical location where an ISPN instance is deployed.
 * This deployment is accessed via the container field of the site.
 * A single site can be marked local.
 *
 * @author Pierre Sutra
 */

public class Site implements Externalizable{


   //
   // CLASS FIELDS
   //
   private static final Log log = LogFactory.getLog(Site.class);
   private static Site localSite;

   //
   // OBJECT FIELDS
   //

   private String name;
   private boolean isLocal;
   private RemoteCacheManager container;

   public static Site valueOf(String servers, Configuration configuration, boolean isLocal) {
      RemoteCacheManager manager = new RemoteCacheManager(configuration,true);
      return new Site(
            servers,
            manager,
            isLocal);
   }

   //
   // OBJECT METHODS
   //

   public Site(String name, RemoteCacheManager container, boolean isLocal) {
      this.name = name;
      this.isLocal = isLocal;
      this.container= container;
      synchronized(this.getClass()){
         if(isLocal && localSite==null)
            localSite = this;
      }
   }

   public Site(URL url, boolean isLocal) {
      name = url.toString();
      container = new RemoteCacheManager(url.getHost(), url.getPort(), true);
      this.isLocal = isLocal;
   }

   public Site(String name, URL url, boolean isLocal) {
      this.name = name;
      container = new RemoteCacheManager(url.getHost(), url.getPort(), true);
      this.isLocal = isLocal;
   }


   public boolean isLocal(){
      return isLocal;
   }

   public String getName(){
      return name;
   }

   public RemoteCacheManager getManager(){
      return container;
   }

   public <K,V> SiteEnsembleCache<K,V> getCache(String name){
      return new SiteEnsembleCache<>(this,container.getCache(name));
   }

   public <K,V> SiteEnsembleCache<K,V> getCache(){
      return getCache(CacheContainer.DEFAULT_CACHE_NAME);
   }

   public boolean isOwner(RemoteCache remoteCache){
      return container.equals(remoteCache.getRemoteCacheManager());
   }

   @Override
   public boolean equals(Object o){
      if (!(o instanceof Site)) return false;
      return ((Site)o).getName().equals(this.getName());
   }


   @Override
   public String toString(){
      return "@"+name;
   }

   @Override
   public void writeExternal(ObjectOutput out) throws IOException {
      // TODO: Customise this generated block
   }

   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      // TODO: Customise this generated block
   }
}
