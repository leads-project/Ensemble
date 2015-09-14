package org.infinispan.ensemble.rest;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.Site;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.jboss.logging.Logger;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/*

Create = PUT with a new URI
         POST to a base URI returning a newly created URI
Read   = GET
Update = PUT with an existing URI
Delete = DELETE


void start()
void stop()

*/

@Path("/")
public class EnsembleCacheRestService {

    private class JsonParser {
        ObjectMapper mapper;
        JsonNode root;
        JsonNode current;
        String currentKey;
        Iterator<JsonNode> iterator;
        String payload;

        public JsonParser(String pl, boolean parse) {
            assert parse == false;
            payload = pl.replace('\r', ' ').replace('\n', ' ');
        }

        public JsonParser(String pl) throws IOException {
            this(pl, false);
            mapper = new ObjectMapper();
            currentKey = "root";
            current = root = mapper.readTree(pl);
        }

        public String getPayload() {
            return payload;
        }

        private JsonNode getNode(String key) throws IOException {
            JsonNode node = current.get(key);
            if (node == null)
                throw new IOException("Cannot find key " + key);
            return node;
        }

        private void goHome() {
            current = root;
        }

        private void setCurrent(String key) throws IOException {
            current = getNode(key);
            currentKey = key;
        }

        private String getValue(JsonNode node) throws IOException {
            String value = node.getTextValue();
            if (value == null)
                throw new IOException("Missing value");
            return value;
        }

        public String getCurrentValue() throws IOException {
            return getValue(current);
        }

        public String getCurrentKey() throws IOException {
            return currentKey;
        }

        public String getValue(String key) throws IOException {
            return getValue(getNode(key));
        }

        public boolean hasKey(String key) {
            return (current.get(key) != null);
        }

        public String getValueOrNull(String key) {
            JsonNode node = current.get(key);
            if (node != null)
                return node.getTextValue();
            return null;
        }

        public List<String> getValueListOrNull(String key) {
            try {
                LinkedList<String> list = new LinkedList<String>();
                setCurrent(key);
                iterator = current.getElements();
                while (iterator.hasNext()) {
                    JsonNode node = iterator.next();
                    list.add(getValue(node));
                }
                return list;
            } catch (IOException e) {
                return null;
            }
        }
    }

    private static EnsembleCacheManager manager = null;
    private static Logger logger;

    public static void setEnsembleManager(EnsembleCacheManager ecm) {
        manager = ecm;
        logger = Logger.getLogger(EnsembleCacheRestService.class);
    }

    public EnsembleCacheRestService() {
        assert manager != null;
        assert logger != null;
    }

    /****************************************************************************
     *
     * POST /sites
     * GET /sites/<site name>
     *
     ***************************************************************************/

    @POST
    @Path("sites")
    @Consumes("application/json")
    @Produces("application/json")
    public Response postSites(String payload) {
        try {
            JsonParser parser = new JsonParser(payload);
            String message = "POST /sites PAYLOAD:" + parser.getPayload();
            logger.info(message);

            String name = parser.getValue("name");
            String isLocalString = parser.getValueOrNull("islocal");
            if (isLocalString == null) isLocalString = "true";
            boolean isLocal = Boolean.parseBoolean(isLocalString);
            logger.info("name=" + name);
            String endpoint = parser.getValue("endpoint");
            URL url = new URL(endpoint);
/*
            List<String> endpoints = parser.getValueListOrNull("endpoints");
            LinkedList<URL> urlList = new LinkedList<URL>();
            if (endpoints != null) {
                for (String endpoint : endpoints) {
                    logger.info("endpoint=" + endpoint);
                    urlList.add(new URL(endpoint));
                }
            } else {
                throw new IOException("Must have at least one endpoint");
            }
            URL url = urlList.get(0);
*/

            Site site = new Site(name, url, isLocal);
            manager.addSite(site);

            message = "POST /sites REPLY:" + site.toString();
            logger.info(message);

            return Response.status(Response.Status.OK).entity(site).build();
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

    }

    @GET
    @Path("sites")
    @Produces("application/json")
    @Consumes("application/json")
    public Response getSites(String payload) {
        JsonParser parser = new JsonParser(payload, false);
        String message = "GET /sites PAYLOAD:" + parser.getPayload();
        logger.info(message);

        Collection<Site> sites = manager.sites();

        message = "GET /sites REPLY:" + sites.toString();
        logger.info(message);

        return Response.status(Response.Status.OK).entity(sites).build();
    }

    enum GetCacheMethod {
        NONE, REPL, SITES, PART
    }

    @POST
    @Path("caches")
    @Produces("application/json")
    @Consumes("application/json")
    public Response postCaches(String payload) {
        try {
            JsonParser parser = new JsonParser(payload);
            String message = "POST /caches PAYLOAD:" + parser.getPayload();
            logger.info(message);

            String name = parser.getValue("name");
            String replicationString = parser.getValueOrNull("replication");
            String consistencyString = parser.getValueOrNull("consistency");
            List<String> siteNames = parser.getValueListOrNull("sites");
            List<String> cacheNames = parser.getValueListOrNull("caches");
            boolean hasPartitioner = parser.hasKey("partitioner");
            String partitionerUrl = null;
            List<String> partitionerParameters = null;
            if (hasPartitioner) {
                parser.setCurrent("partitioner");
                partitionerUrl = parser.getValue("url");
                partitionerParameters = parser.getValueListOrNull("parameters");
            }

            int replication = 1; // default
            List<Site> sites = null;
            List<EnsembleCache> caches = null;
            GetCacheMethod method = GetCacheMethod.NONE;
            if (replicationString != null) {
                if (siteNames != null)
                    throw new IOException("Cannot specify replication and sites");
                if (hasPartitioner)
                    throw new IOException("Cannot specify replication and partitioner");
                method = GetCacheMethod.REPL;
                replication = Integer.parseInt(replicationString);
            } else if (siteNames != null) {
                if (hasPartitioner)
                    throw new IOException("Cannot specify sites and partitioner");
                method = GetCacheMethod.SITES;
                sites = new LinkedList<Site>();
                for (String siteName : siteNames) {
                    Site site = manager.getSite(siteName);
                    if (site == null)
                        throw new IOException("Site " + siteName + " unknown");
                    sites.add(site);
                }
            } else if (hasPartitioner) {
                if (consistencyString != null)
                    throw new IOException("Cannot specify partitioner and consistency");
                if (cacheNames == null)
                    throw new IOException("Cannot specify partitioner without cache list");
                method = GetCacheMethod.PART;
                caches = new LinkedList<EnsembleCache>();
                for (String cacheName : cacheNames) {
                    EnsembleCache cache = manager.getCache(cacheName);
                    if (cache == null)
                        throw new IOException("Cache " + cacheName + " unknown");
                    caches.add(cache);
                }
            } else if (cacheNames != null) {
                throw new IOException("Cannot specify cache list without partitioner");
            }

            EnsembleCacheManager.Consistency consistency = // default
                    (consistencyString == null) ? EnsembleCacheManager.Consistency.WEAK
                            : EnsembleCacheManager.Consistency.valueOf(consistencyString);
            EnsembleCache ec = null;

            switch (method) {
                case REPL:
                    ec = manager.getCache(name, replication, consistency);
                    break;
                case SITES:
                    ec = manager.getCache(name, sites, consistency);
                    break;
                case PART:
                    // TODO FIXME
                    break;
                default:
                    ec = manager.getCache(name, 1, consistency);
            }

            return Response.status(Response.Status.OK).entity(ec).build();
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("caches")
    @Produces("application/json")
    @Consumes("application/json")
    public Response getCaches(String payload) {
        JsonParser parser = new JsonParser(payload, false);
        String message = "GET /caches PAYLOAD:" + parser.getPayload();
        logger.info(message);

        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("caches/{name}")
    @Produces("application/json")
    @Consumes("application/json")
    public Response getCaches_CacheName(@PathParam("name") String cacheName,
                                        String payload) {
        JsonParser parser = new JsonParser(payload, false);
        String message = "GET /caches/" + cacheName + " PAYLOAD:" + parser.getPayload();
        logger.info(message);

        EnsembleCache cache = manager.getCache(cacheName);

        message = "GET /caches/" + cacheName + " REPLY:" + cache.toString();
        logger.info(message);

        return Response.status(Response.Status.OK).entity(cache).build();
    }

    @GET
    @Path("caches/{name}/data")
    @Produces("application/json")
    @Consumes("application/json")
    public Response getCaches_CacheName_Data(@PathParam("name") String cacheName,
                                        String payload) {
        JsonParser parser = new JsonParser(payload, true);
        String message = "GET /caches/" + cacheName + "/data PAYLOAD:" + parser.getPayload();
        logger.info(message);

        EnsembleCache cache = manager.getCache(cacheName);

        try {
            String key = parser.getValue("key");
            Object data = cache.get(key);
            return Response.status(Response.Status.OK).entity(data).build();
        } catch (IOException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("caches/{name}/data/{key}")
    @Produces("application/json")
    @Consumes("application/json")
    public Response getCaches_CacheName_Data(@PathParam("name") String cacheName,
                                             @PathParam("key") String key,
                                             String payload) {
        JsonParser parser = new JsonParser(payload, false);
        String message = "GET /caches/" + cacheName + "/data/" + key + " PAYLOAD:" + parser.getPayload();
        logger.info(message);

        EnsembleCache cache = manager.getCache(cacheName);
        Object data = cache.get(key);
        return Response.status(Response.Status.OK).entity(data).build();
    }

    @GET
    @Path("caches/{name}/{field}")
    @Produces("application/json")
    @Consumes("application/json")
    public Response getCaches_CacheName(@PathParam("name") String cacheName,
                                        @PathParam("field") String cacheField,
                                        String payload) {
        JsonParser parser = new JsonParser(payload, cacheField.equals("data"));
        String message = "GET /caches/" + cacheName + " PAYLOAD:" + parser.getPayload();
        logger.info(message);

        EnsembleCache cache = manager.getCache(cacheName);

        if (cacheField.equals("name")) {
            class Tmp {
                @XmlElement(name = "name") String name;
                Tmp(String n) {
                    name = n;
                }
            }
            Tmp tmp = new Tmp(cache.getName());
            return Response.status(Response.Status.OK).entity(tmp).build();
        } else if (cacheField.equals("replication")) {
            return Response.status(Response.Status.NOT_IMPLEMENTED).build();
        } else if (cacheField.equals("consistency")) {
            return Response.status(Response.Status.NOT_IMPLEMENTED).build();
        } else if (cacheField.equals("sites")) {
            return Response.status(Response.Status.NOT_IMPLEMENTED).build();
        } else if (cacheField.equals("caches")) {
            return Response.status(Response.Status.NOT_IMPLEMENTED).build();
        } else if (cacheField.equals("partitioner")) {
            return Response.status(Response.Status.NOT_IMPLEMENTED).build();
        } else if (cacheField.equals("size")) {
            class Tmp {
                @XmlElement(name = "size") int size;
                Tmp(int n) {
                    size = n;
                }
            }
            Tmp tmp = new Tmp(cache.size());
            return Response.status(Response.Status.OK).entity(tmp).build();
        } else {
            String error = "Cannot get field " + cacheField;
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }

    @PUT
    @Path("caches/{name}/data")
    @Produces("application/json")
    @Consumes("application/json")
    public Response putCaches_CacheName_Data(@PathParam("name") String cacheName,
                                             String payload) {
        JsonParser parser = new JsonParser(payload, true);
        String message = "PUT /caches/" + cacheName + "/data PAYLOAD:" + parser.getPayload();
        logger.info(message);

        EnsembleCache cache = manager.getCache(cacheName);

        try {
            String key = parser.getValue("key");
            String value = parser.getValue("value");
            cache.put(key, value);
            return Response.status(Response.Status.OK).build();
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("caches/{name}/data/{key}")
    @Produces("application/json")
    @Consumes("application/json")
    public Response putCaches_CacheName_Data(@PathParam("name") String cacheName,
                                             @PathParam("key") String key,
                                             String payload) {
        JsonParser parser = new JsonParser(payload, true);
        String message = "PUT /caches/" + cacheName + "/data/" + key + " PAYLOAD:" + parser.getPayload();
        logger.info(message);

        EnsembleCache cache = manager.getCache(cacheName);

        try {
            String value = parser.getValue("value");
            cache.put(key, value);
            return Response.status(Response.Status.OK).build();
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("caches/{name}/data/{key}")
    @Produces("application/json")
    @Consumes("application/json")
    public Response postCaches_CacheName_Data(@PathParam("name") String cacheName,
                                              @PathParam("key") String key,
                                              String payload) {
        return putCaches_CacheName_Data(cacheName, key, payload);
    }


    /*****************************************************/

    @PUT
    @Path("{path}")
    public Response _put(@PathParam("path") String path) {
        String message = "unknown PUT path " + path;
        logger.error(message);
        message += "\n";
        return Response.status(Response.Status.NOT_FOUND).entity(message).build();
    }

    @POST
    @Path("{path}")
    public Response _post(@PathParam("path") String path) {
        String message = "unknown POST path " + path;
        logger.error(message);
        message += "\n";
        return Response.status(Response.Status.NOT_FOUND).entity(message).build();
    }

    @DELETE
    @Path("{path}")
    public Response _delete(@PathParam("path") String path) {
        String message = "unknown DELETE path " + path;
        logger.error(message);
        message += "\n";
        return Response.status(Response.Status.NOT_FOUND).entity(message).build();
    }


    /*****************************************************************************************
     *
     * Serve files
     *
     * Install files to be served in the resource folder
     *
     *****************************************************************************************/

    @GET
    @Path("{path}")
    public Response get(@PathParam("path") String path) {

        URL indexURL = getClass().getClassLoader().getResource(path);
        if (indexURL != null) {
            File f = null;
            try {
                f = new File(indexURL.toURI());
                String mt = new MimetypesFileTypeMap().getContentType(f);
                return Response.ok(f, mt).build();
            } catch (URISyntaxException e) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Malformed path " + path + "\n").build();
            }
        } else
            return Response.status(Response.Status.NOT_FOUND).entity("Unknown path " + path + "\n").build();
    }

}
