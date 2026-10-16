# Library Cache Hazelcast

Applies to the Lutece caches created on Hazelcast the settings that JCache cannot carry: the maximum number of entries
(`maxElementsInMemory` of `caches.properties` and of the `core.cache.status.<name>.*` keys). Without it every Hazelcast
cache keeps the default eviction of 10000 entries.

The library provides a CDI bean implementing `fr.paris.lutece.portal.service.cache.ICacheConfigurationCustomizer`
(lutece-core 8.0.2 or later), discovered by the core when a cache is created.

## Usage

Add it only to a site that uses Hazelcast as JCache provider (the Hazelcast jar is provided by the site):

```xml
<dependency>
    <groupId>fr.paris.lutece.plugins</groupId>
    <artifactId>library-cache-hazelcast</artifactId>
    <type>jar</type>
</dependency>
```

## Limits

- Hazelcast applies the configuration when the cache is created in the cluster: a new size needs a cold restart of the
  cluster. A cache declared by name in the member configuration keeps that configuration.
- The size is an `ENTRY_COUNT` per member, LRU eviction.
