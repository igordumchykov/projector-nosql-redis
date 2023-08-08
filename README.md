# projector-nosql-redis
- Tests [Redis eviction policies](https://redis.io/docs/reference/eviction/)
- Tests [probabilistic early expiration](https://en.wikipedia.org/wiki/Cache_stampede)

# Prerequisites
1. Docker
2. java 18

# Run Redis cluster
```shell
docker-compose up -d
```

# Redis eviction policies

## Configuration
Login into redis container, set `maxmemory` and `maxmemory-policy`:
```shell
CONFIG SET maxmemory <size>
CONFIG SET maxmemory-policy <policy name>
```
## Scripts [location](redis/src/main/kotlin/com/jdum/projector/nosql/redis/RedisApplication.kt)

## 1.allkeys-lru

### Config
`CONFIG SET maxmemory 2mb`
`CONFIG SET maxmemory-policy allkeys-lru`

### Run function: 
`allKeysLru()`

### Console output
```
Found: constant-key-1
evicted constant-key-2
Found: constant-key-3
Found: key-1
Found: key-1499
```

## 2.allkeys-lfu

### Config
`CONFIG SET maxmemory 2mb`
`CONFIG SET maxmemory-policy allkeys-lfu`

### Run function:
`allKeysLfu()`

### Console output
```
evicted keys 1-500
evicted constants 1,2
```

## 3.volatile-lfu

### Config
`CONFIG SET maxmemory 2mb`
`CONFIG SET maxmemory-policy volatile-lfu`

### Run function:
`volatilePolicies()`

### Console output
```
evicted 1-100
evicted constant-key-1
evicted constant-key-2
evicted constant-key-3
evicted key-1
evicted key-100
evicted key-101
Found: key-1499
```

## 4.volatile-lru, volatile-ttl

### Config
`CONFIG SET maxmemory 2mb`
`CONFIG SET maxmemory-policy volatile-lru, <volatile-ttl>`

### Run function:
`volatilePolicies()`

### Console output
```
evicted 1-00
evicted constant-key-1
evicted constant-key-2
evicted constant-key-3
evicted key-1
evicted key-100
evicted key-101
Found: key-1499
```

## 5.allkeys-random

### Config
`CONFIG SET maxmemory 2mb`
`CONFIG SET maxmemory-policy allkeys-random`

### Run function:
`allKeysRandom()`

### Console output
```
evicted all keys
```

## 6.noeviction

### Config
`CONFIG SET maxmemory 0`
`CONFIG SET maxmemory-policy noeviction`

### Run function:
`allKeysRandom()`

### Console output
```
If set maxmemory, get an error: OOM command not allowed when used memory > 'maxmemory'
If set maxmemory 0, all keys are evicted
```

# Probabilistic early expiration
### Redis [wrapper](redis/src/main/kotlin/com/jdum/projector/nosql/redis/ProbabilisticExpirationCache.kt)

### Run function:
`probabilisticCaching()`
### Console output
```
Read value 0: CacheEntry(value=value-1, creationTime=1691494209376, expiryTime=1691494214376)
Read value 1: CacheEntry(value=value-1, creationTime=1691494209376, expiryTime=1691494214376)
Read value 2: CacheEntry(value=value-1, creationTime=1691494209376, expiryTime=1691494214376)
Read value 3: CacheEntry(value=value-1, creationTime=1691494209376, expiryTime=1691494214376)
Delete key: key-1
Read value 4: null
Read value 5: null
Read value 6: null
Read value 7: null
Read value 8: null
```
