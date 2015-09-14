# Ensemble: a federated geo-distributed key-value store 

Ensemble is a key-value store that sits on top of [Infinispan](infinispan.org), an open-source data grid platform (key value store, distributed and transactional). Ensemble federates under the same banner multiple vanilla Infinispan grid deployments,  each running at distinct geographically distributed locations, and orchestrates them to provide the illusion of a single store spanning all the deployments. The end-user of Ensemble decides what replication and consistency guarantees the federated storage should provide.

Ensemble exports the core interface of Infinispan: the cache abstraction. A cache provides a concurrent map API with extended capabilities to index data,  listen their updates, and retrieve them with [Apache Lucene](lucene.apache.org)  queries. Ensemble is available as a client-side library that interacts with the remote Infinispan deployments via the Hot Rod binary protocol.

