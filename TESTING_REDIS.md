# Testing Redis Cache Implementation

This guide walks you through testing the Redis caching implementation locally.

## Prerequisites

- Docker and Docker Compose installed
- Java 25 (or compatible version)
- The habit-tracker project cloned locally

## Step-by-Step Testing Guide

### Step 1: Start Redis

```bash
# Start Redis with Docker Compose
docker compose up -d

# Verify Redis is running
docker compose ps

# Expected output:
# NAME                       STATUS
# habit-tracker-redis        Up (healthy)
```

### Step 2: Test Redis Connection

```bash
# Connect to Redis CLI
docker exec -it habit-tracker-redis redis-cli

# Test connection
127.0.0.1:6379> ping
PONG

# Check database is empty
127.0.0.1:6379> dbsize
(integer) 0

# Exit
127.0.0.1:6379> exit
```

### Step 3: Build the Application

```bash
cd backend

# Build without tests first (to download dependencies)
./gradlew build -x test

# Expected output:
# BUILD SUCCESSFUL
```

### Step 4: Run Unit Tests

```bash
# Run all tests (includes cache tests)
./gradlew test

# Run only cache-related tests
./gradlew test --tests "*CacheTest"

# Expected output:
# UserServiceCacheTest > shouldCacheUserById() PASSED
# UserServiceCacheTest > shouldEvictCacheOnUserRegistration() PASSED
# UserServiceCacheTest > shouldUseDifferentCachesForDifferentKeys() PASSED
# UserServiceCacheTest > shouldVerifyAllCachesAreConfigured() PASSED
```

**Note:** Tests use embedded Redis on port 6370, so they won't interfere with your development Redis.

### Step 5: Start the Application

```bash
# Start Spring Boot application
./gradlew bootRun

# Expected output in logs:
# - RedisConnectionFactory bean created
# - CacheManager initialized
# - Application started successfully
```

Look for these log messages:
```
o.s.d.r.core.RedisConnectionFactory    : Connecting to Redis at localhost:6379
c.e.d.common.config.CacheConfig        : Initializing Redis cache manager
o.s.cache.annotation.CachingConfigurer : Cache manager configured with 6 caches
```

### Step 6: Verify Cache Behavior Manually

#### Option A: Using Redis CLI

```bash
# In a new terminal, monitor Redis commands
docker exec -it habit-tracker-redis redis-cli MONITOR

# In another terminal, make API calls to your app
# Watch the MONITOR output to see cache operations
```

#### Option B: Using Redis Commander GUI

```bash
# Start Redis with web GUI
docker compose --profile tools up -d

# Open browser to http://localhost:8081
# You can visually see cache keys being created/deleted
```

### Step 7: Manual API Testing

If your app is running, test caching behavior:

```bash
# Register a user (this should evict usersByEmail cache)
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# Response: {"userId": 1}

# Check Redis for cached data
docker exec -it habit-tracker-redis redis-cli

# List all keys
127.0.0.1:6379> KEYS *
# You should see cache keys like:
# 1) "users::1"
# 2) "usersByEmail::test@example.com"

# View cached user data
127.0.0.1:6379> GET "users::1"
# Shows JSON representation of the User object

# Check cache TTL (time to live)
127.0.0.1:6379> TTL "users::1"
(integer) 3600  # 1 hour in seconds

127.0.0.1:6379> TTL "completionCounts::1"
(integer) 600   # 10 minutes in seconds
```

### Step 8: Test Cache Eviction

```bash
# Create a habit (should evict habitsByUser cache)
curl -X POST http://localhost:8080/api/v1/habits \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title":"Morning Exercise",
    "description":"30 min workout",
    "periodType":"DAILY"
  }'

# In Redis CLI, verify cache was evicted
127.0.0.1:6379> KEYS habitsByUser::*
(empty array)  # Cache should be empty after creation

# Get habits (should populate cache)
curl http://localhost:8080/api/v1/habits \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Check cache again
127.0.0.1:6379> KEYS habitsByUser::*
1) "habitsByUser::1"  # Cache populated
```

## Troubleshooting

### Issue: Application fails to start

**Error:** `Unable to connect to Redis at localhost:6379`

**Solution:**
```bash
# Check if Redis is running
docker compose ps

# If not running, start it
docker compose up -d

# Check Redis logs
docker compose logs redis
```

### Issue: Tests fail with Redis connection error

**Error:** `Could not start embedded Redis`

**Solution:**
Check if port 6370 is already in use:
```bash
# Linux/Mac
lsof -i :6370

# Kill the process using the port
kill -9 <PID>

# Or use a different port in test application.properties
```

### Issue: Cache not working (always hitting database)

**Checklist:**
1. Verify `@EnableCaching` is in CacheConfig
2. Check Spring Boot logs for cache manager initialization
3. Verify Redis connection in application.properties
4. Check method has `@Cacheable` annotation
5. Ensure method is called from outside the class (Spring proxies)

**Debug:**
```bash
# Enable cache debug logging
# Add to application.properties:
logging.level.org.springframework.cache=DEBUG
logging.level.org.springframework.data.redis=DEBUG
```

### Issue: Stale cache data

**Clear all caches:**
```bash
docker exec -it habit-tracker-redis redis-cli FLUSHALL
```

**Or clear specific cache:**
```java
@Autowired
private CacheManager cacheManager;

public void clearUserCache() {
    cacheManager.getCache("users").clear();
}
```

## Performance Testing

### Test cache hit rate:

```bash
# Get cache stats from Redis
docker exec -it habit-tracker-redis redis-cli INFO stats

# Look for:
# keyspace_hits:100
# keyspace_misses:10
# Hit rate = 100/(100+10) = 90.9%
```

### Monitor cache memory usage:

```bash
docker exec -it habit-tracker-redis redis-cli INFO memory

# Look for:
# used_memory_human:1.2M
# maxmemory_human:256M
```

### Test cache eviction under load:

```bash
# Fill cache beyond maxmemory (256MB)
# Watch LRU eviction in action

docker exec -it habit-tracker-redis redis-cli INFO stats | grep evicted
# evicted_keys:150
```

## Expected Test Results

### ✅ Successful Tests:

```
UserServiceCacheTest
  ✓ shouldCacheUserById()                          [PASSED]
  ✓ shouldEvictCacheOnUserRegistration()          [PASSED]
  ✓ shouldUseDifferentCachesForDifferentKeys()    [PASSED]
  ✓ shouldVerifyAllCachesAreConfigured()          [PASSED]

BUILD SUCCESSFUL in 12s
```

### ✅ Successful Redis Verification:

```bash
# All caches configured
127.0.0.1:6379> CONFIG GET maxmemory
1) "maxmemory"
2) "268435456"  # 256MB

# Persistence enabled
127.0.0.1:6379> CONFIG GET save
1) "save"
2) "900 1 300 10 60 10000"

# AOF enabled
127.0.0.1:6379> CONFIG GET appendonly
1) "appendonly"
2) "yes"
```

## Next Steps

Once everything is working:

1. **Profile cache performance:**
   - Add metrics collection
   - Monitor hit/miss rates in production
   - Adjust TTL based on actual usage patterns

2. **Production deployment:**
   - Use managed Redis (AWS ElastiCache, Redis Cloud, etc.)
   - Enable Redis authentication
   - Configure Redis replication for HA
   - Set up monitoring and alerting

3. **Optimize cache configuration:**
   - Fine-tune TTL per cache based on data update frequency
   - Adjust maxmemory based on actual usage
   - Consider Redis Cluster for very large datasets

## Additional Resources

- [Spring Cache Documentation](https://docs.spring.io/spring-framework/reference/integration/cache.html)
- [Spring Data Redis](https://spring.io/projects/spring-data-redis)
- [Redis Documentation](https://redis.io/docs/)
- [Redis Commands Reference](https://redis.io/commands/)

---

**Need help?** Check the logs:
- Application: `backend/build/logs/` or console output
- Redis: `docker compose logs redis -f`
- Tests: `backend/build/reports/tests/test/index.html`
