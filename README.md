# Ensemble: a federated geo-distributed key-value store 

Ensemble is a key-value store that sits on top of [Infinispan](http://infinispan.org), 
an open-source data grid platform (key value store, distributed and transactional). 
Ensemble federates under the same banner multiple vanilla Infinispan grid deployments, 
each running at distinct geographically distributed locations, and orchestrates them to provide 
the illusion of a single store spanning all the deployments. 
The end-user of Ensemble decides what replication and consistency guarantees the federated storage should provide.

## Overview

Ensemble exposes a Java interface that consists of two key components: an EnsembleCache and an EnsembleCacheManager. An EnsembleCache is a named and typed instance of the key-value store that spans several Infinispan deployments. An EnsembleCacheManager is a container of EnsembleCaches. Both abstractions are directly inherited from the Infinispan API (respectively a Cache and a CacheContainer).

An EnsembleCache contains multiple RemoteCaches; each RemoteCache represents an Infinispan deployment at the scale of a single micro-cloud. Calls to a RemoteCache, and thus to the backing Infinispan instances, are implemented via the [HotRod protocol](http://infinispan.org/documentation/). Once an EnsembleCache is created, the user can store/retrieve data using the regular get() and put() operations. These operations are executed on the appropriate Infinispan instances according to the replication degree and the consistency criteria that characterize the EnsembleCache. We briefly detail such parameters in what follows.

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

## Design

From an abstract point of view, an EnsembleCache can be seen as the construction of dependable registers atop fault-prone ones. Indeed, in the targeted LEADS architecture, each Infinispan deployment operating in a micro-cloud is subject to be unresponsive from the outside (e.g., the micro-cloud is subject to a power outage or the internet link fails). Based on this observation, we developed Ensemble using existing algorithms and principles taken from dependable shared-memory computing literature.

We distinguish two types of EnsembleCache: a ReplicatedEnsembleCache fully replicates the data it holds to the set of micro-clouds it has been assigned to, whereas a DistributedEnsembleCache partially replicates its content across its set of allocated micro-clouds. (The usage of the terms “replicated” and “distributed” come from the terminology in use in Infinispan). 

*ReplicatedEnsembleCache.* With a ReplicatedEnsembleCache, data is fully replicated across multiple micro-clouds. A ReplicatedEnsembleCache can be instantiated in various flavours, each flavour representing a distinct consistency level. Specifically: 
WeakEnsembleCache implements a ReplicatedEnsembleCache with weakly consistent operations. The implementation is straightforward: to execute a put() operation, the client applies put() to a quorum of RemoteCaches, and to execute a get(), the client accesses (at random) one of these RemoteCaches.
SWMREnsembleCache implements an atomic single-writer multiple-reader atomic API of a Cache on top of several RemoteCaches. In this case, the system makes use of a primary-based replication schema: every read access the primary, and a write first writes to the primary then asynchronously and in parallel to the other caches.
MWMREnsembleCache implements the complete Cache API at the atomic level, offering an atomic multi-readers multi-writers cache. To implement this abstraction, Ensemble relies on the following classical quorum algorithm: when the client executes a put(k,v), a MWMREnsembleCache first retrieves the greatest version (timestamp) t stored at a quorum of RemoteCaches; then it executes put(k,(t+1,v)) at a quorum of RemoteCaches. This last write is conditional. A get(k) operation executes a similar operation, i.e., first it retrieves the value v associated with the greatest timestamp, and then it writes back this value before returning. 

*DistributedEnsembleCache.* With a ReplicatedEnsembleCache, data is stored in a dependable manner and it can also be replicated at the local micro-cloud in order to improve the response time of Ensemble to clients' requests. On the other hand, such a construction does not fully leverage the available storage in the micro-cloud federation. The notion of a DistributedEnsembleCache retains the dependability property of a ReplicatedEnsembleCache, while allowing data to be replicated across several, but not all, of the micro-clouds. A DistributedEnsembleCache also allows using explicit partitioning and placement strategies for data, and to improve locality of accesses in some scenarios. Similarly to a ReplicatedEnsembleCache, a DistributedEnsembleCache implements a BasicCache API on top of multiple caches. However, in this case the underlying caches are (i) either ReplicatedEnsembleCaches or RemoteCaches, and (ii) at construction time a Partitioner object is given to map keys to the appropriate caches. This gives us the following constructor: 

```java
 public DistributedEnsembleCache(String name, 
                                 List<? extends EnsembleCache<K, V>> caches, 
                                 Partitioner<K, V> partitioner)
```

Where a Partitioner is an interface exporting the following method: 

```java
 	 public abstract EnsembleCache<K,V> locate(K k)
```

As an example, the ModuloPartitioner implements a simple modulo operation on the hash value of k to retrieve the EnsembleCache that should store the content indexed by key k. 

*Frontier Mode.* In a DistributedEnsembleCache, put and get operations behave as usual. This means that a put(k,v) inserts tuple (k,v) in the EnsembleCache E returned by locate(k), and get(k) operation returns the tuple (k,v) stored at E. When a DistributedEnsembleCache operates in frontier mode, put operations work as previously, but get() operations solely returns data located in the local site. 
