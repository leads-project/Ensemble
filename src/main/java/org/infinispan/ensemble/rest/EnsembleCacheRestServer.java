package org.infinispan.ensemble.rest;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * REST server to access Infinispan Ensembles
 *
 * @author Marcelo Pasin
 */

public class EnsembleCacheRestServer {

    private List<RemoteCacheManager> remoteServers = new ArrayList<RemoteCacheManager>();

    private class HotRodConfigurationBuilder
            extends org.infinispan.client.hotrod.configuration.ConfigurationBuilder {
        HotRodConfigurationBuilder(String server) {
            String spl[] = server.split(":");
            String host = spl[0];
            int port = Integer.parseInt(spl[1]);
            this.addServer()
                    .host(host)
                    .port(port)
                    .pingOnStartup(false);
        }
    }



    private void run() {
        Properties sysProps = System.getProperties();
        PropertyLoader.load(sysProps);
        Logger logger = Logger.getLogger(this.getClass());

        String overridePort = sysProps.getProperty("ECrest.port", "11021");
        int port = Integer.valueOf(overridePort);

        EnsembleCacheManager ecm = new EnsembleCacheManager();
        EnsembleCacheRestService.setEnsembleManager(ecm);

        TJWSEmbeddedJaxrsServer tjws = new TJWSEmbeddedJaxrsServer();
        tjws.setPort(port);
        tjws.setRootResourcePath("/");
        tjws.getDeployment().getActualResourceClasses().add(EnsembleCacheRestService.class);

        logger.info("Listening to port: " + port);

        tjws.start();

    }

    public static void main(String args[]) {
        new EnsembleCacheRestServer().run();
    }
}
