# Ensemble: a federated geo-distributed key-value store 

Ensemble is a key-value store that sits on top of [Infinispan](http://infinispan.org), 
an open-source data grid platform (key value store, distributed and transactional). 
Ensemble federates under the same banner multiple vanilla Infinispan grid deployments, 
each running at distinct geographically distributed locations, and orchestrates them to provide 
the illusion of a single store spanning all the deployments. 
The end-user of Ensemble decides what replication and consistency guarantees the federated storage should provide.

## Design

Ensemble exposes a Java interface that consists of two key components: an EnsembleCache and an EnsembleCacheManager. An EnsembleCache is a named and typed instance of the key-value store that spans several Infinispan deployments. An EnsembleCacheManager is a container of EnsembleCaches. Both abstractions are directly inherited from the Infinispan API (respectively a Cache and a CacheContainer).

An EnsembleCache contains multiple RemoteCaches; each RemoteCache represents an Infinispan deployment at the scale of a single micro-cloud. Calls to a RemoteCache, and thus to the backing Infinispan instances, are implemented via the HotRod protocol [Ispn-doc]. Once an EnsembleCache is created, the user can store/retrieve data using the regular get() and put() operations. These operations are executed on the appropriate Infinispan instances according to the replication degree and the consistency criteria that characterize the EnsembleCache. We briefly detail such parameters in what follows.

Ensemble supports an eventing mechanisms atop HotRod, that is a full listener API in geo-distributed client-server mode. When a client application wants to listen to cache events, it simply registers to the appropriate Ensemble cache. In the case where the cache is distributed across multiple micro-clouds, a registration per micro-cloud is done by Ensemble, and duplicates are removed automatically at the client-side library (this last filtering is transparent to the application).

## Installation & Usage

To use the query capability of Ensemble, you will need to install locally the Apache Avro support for Infinispan.
This support is available [here](https://github.com/leads-project/infinispan-avro).
Outside of this requirement, Ensemble does not require any specific settings for being use, outside of Infinispan itself. 
A detailed step-by-step explanation of how to deploy Infinispan is available online in the [user guide](http://infinispan.org/docs/8.0.x/user_guide/user_guide.html). 

Below, we explain how to execute an installation.

```
# Building and installing infinispan-avro
git clone https://github.com/leads-project/infinispan-avro.git
cd infinispan-avro
mvn clean install -DskipTests

# Building and installing ensemble
git clone https://github.com/leads-project/Ensemble.git
cd Ensemble
mvn clean install -DskipTests
```

## Code Snippet

To understand how Ensemble works, we suggest to read carefully the [tests](https://github.com/leads-project/Ensemble/tree/master/src/test/java/org/infinispan/ensemble/test).
Notice that all the clusters are emulated on your local machine, 
such tests do not require an access to one or more Infinispan deployments.

```java

      WebPage page1 = somePage();
      WebPage page2 = somePage();

      // get
      cache().put(page1.getKey(),page1);
      assert cache().containsKey(page1.getKey());
      assert cache().get(page1.getKey()).equals(page1);

      // putIfAbsent
      assert cache().putIfAbsent(page2.getKey(),page2)==null;
      cache().putIfAbsent(page1.getKey(),page2);
      assert cache().get(page2.getKey()).equals(page2);

      // quering 
      QueryFactory qf = Search.getQueryFactory(cache());

      WebPage page1 = somePage();
      cache().put(page1.getKey(),page1);
      WebPage page2 = somePage();
      cache().put(page2.getKey(),page2);

      QueryBuilder qb = (QueryBuilder) qf.from(WebPage.class);
      Query query = qb.build();
      List list = query.list();
      assertEquals(list.size(),2);

      qb = (QueryBuilder) qf.from(WebPage.class);
      qb.having("key").eq(page1.getKey());
      query = qb.build();
      assertEquals(query.list().get(0), page1);

```


