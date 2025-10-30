# Docker Setup for Habit Tracker

This project uses Docker Compose to manage Redis for caching.

## Quick Start

### Start Redis
```bash
docker-compose up -d
```

### Stop Redis
```bash
docker-compose down
```

### Stop Redis and Remove Data (Clean Slate)
```bash
docker-compose down -v
```

## Services

### Redis (Port 6379)
- **Image**: redis:7-alpine
- **Purpose**: Distributed caching for application
- **Data Persistence**: Volume mounted at `/data` in container
- **Configuration**: `redis.conf` with RDB + AOF persistence
- **Health Check**: Automated ping every 10 seconds

### Redis Commander (Port 8081) - Optional
A web-based GUI for managing Redis data.

**Start with Redis Commander:**
```bash
docker-compose --profile tools up -d
```

**Access:** http://localhost:8081

## Data Persistence

Redis data is persisted in a Docker volume named `redis-data`:

```bash
# View volume info
docker volume inspect habit-tracker_redis-data

# View volume location (Linux/Mac)
docker volume inspect habit-tracker_redis-data | grep Mountpoint

# Backup volume data
docker run --rm -v habit-tracker_redis-data:/data -v $(pwd):/backup alpine tar czf /backup/redis-backup.tar.gz /data
```

## Monitoring

### Check Redis Status
```bash
docker-compose ps
```

### View Redis Logs
```bash
docker-compose logs redis -f
```

### Connect to Redis CLI
```bash
docker exec -it habit-tracker-redis redis-cli
```

**Useful Redis CLI commands:**
```bash
ping                    # Test connection
info                    # Server information
dbsize                  # Number of keys
keys *                  # List all keys (dev only!)
flushall                # Clear all data (careful!)
config get maxmemory    # Check memory limit
```

## Configuration

### Redis Configuration (`redis.conf`)

**Persistence Settings:**
- **RDB**: Snapshots saved to `dump.rdb` (15min, 5min, 1min intervals)
- **AOF**: Append-only file for better durability (`appendonly.aof`)

**Memory:**
- Max memory: 256MB
- Eviction policy: allkeys-lru (Least Recently Used)

**Modify settings:**
1. Edit `redis.conf`
2. Restart Redis: `docker-compose restart redis`

### Application Configuration

Update `backend/src/main/resources/application.properties`:
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

## Troubleshooting

### Redis Won't Start
```bash
# Check logs
docker-compose logs redis

# Check if port is already in use
lsof -i :6379
```

### Clear All Cache Data
```bash
docker exec -it habit-tracker-redis redis-cli FLUSHALL
```

### Reset Everything
```bash
docker-compose down -v
docker-compose up -d
```

## Production Notes

For production environments:
1. Set a strong password in `redis.conf`: `requirepass yourpassword`
2. Update application.properties: `spring.data.redis.password=yourpassword`
3. Use external volume or managed Redis service (AWS ElastiCache, Redis Cloud, etc.)
4. Enable TLS/SSL for secure connections
5. Configure proper backup strategy
