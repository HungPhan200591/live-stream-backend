# Redis Usage Guide - Live Stream Backend

## 📚 Overview

Hướng dẫn sử dụng Redis cho caching trong dự án Spring Boot Livestream Backend. Document này định nghĩa conventions, best practices và coding rules khi làm việc với Redis.

---

## 🎯 Use Cases

### Khi NÊN dùng Redis Cache

✅ **Session Data**: Short-lived data với expiration time (ví dụ: User Sessions)  
✅ **Hot Data**: Data được truy cập thường xuyên (ví dụ: Active livestream info)  
✅ **Computed Results**: Expensive calculations cần cache (ví dụ: Leaderboards)  
✅ **Rate Limiting**: Track limits per user/IP  
✅ **Temporary State**: Trạng thái tạm thời không cần persist lâu dài

### Khi KHÔNG NÊN dùng Redis Cache

❌ **Primary Data Store**: Redis là cache, không phải database chính  
❌ **Large Objects**: Tránh cache objects > 1MB  
❌ **Infrequent Access**: Data ít truy cập không đáng cache  
❌ **Critical Data**: Data không thể mất khi Redis restart

---

## ⚙️ Architecture

### Type-Safe Redis Configuration

```
RedisConfig
├── RedisTemplateBeanNames (static class)
│   └── Compile-time constants cho @Qualifier
├── redisObjectMapper() @Bean
│   └── Shared ObjectMapper (thread-safe, singleton)
├── createRedisTemplate<T>() private helper
│   └── Factory method tái sử dụng logic
└── [dtoType]RedisTemplate() @Bean methods
    └── Explicit bean cho mỗi DTO type
```

**Design Decisions:**
- ✅ **Jackson2JsonRedisSerializer** (type-safe) thay vì `GenericJackson2JsonRedisSerializer` (unsafe)
- ✅ **Explicit @Bean methods** thay vì dynamic registration (debuggable)
- ✅ **Shared ObjectMapper** thay vì create mới mỗi lần (performance)

---

## 📝 Implementation Guide

### Step 1: Tạo Cache DTO

**Rules:**
- DTO PHẢI nằm trong package `com.stream.demo.model.dto.cache.*`
- KHÔNG dùng JPA annotations (`@Entity`, `@Table`, etc.)
- PHẢI có static method `fromEntity()` để convert từ Entity
- KHÔNG có computed properties (getters với logic phức tạp)

**Template:**

```java
package com.stream.demo.model.dto.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * [DTO Name] Cache DTO
 * <p>
 * DTO riêng cho Redis cache, tách biệt khỏi JPA Entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyCacheDTO {

    // Fields - chỉ primitive types và common Java types
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    
    /**
     * Convert từ Entity sang DTO
     */
    public static MyCacheDTO fromEntity(MyEntity entity) {
        return MyCacheDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
```

**❌ KHÔNG làm:**
```java
// ❌ JPA annotations
@Entity
public class MyCacheDTO { }

// ❌ Nested objects (phức tạp serialize)
private List<NestedObject> items;

// ❌ Circular references
private MyCacheDTO parent;
private List<MyCacheDTO> children;
```

---

### Step 2: Register RedisTemplate Bean

**File:** `d:\Study\Project\live-stream-backend\src\main\java\com\stream\demo\config\RedisConfig.java`

#### 2.1 Add Bean Name Constant

```java
public static class RedisTemplateBeanNames {
    private RedisTemplateBeanNames() {}

    public static final String SESSION_CACHE = "sessionCacheRedisTemplate";
    public static final String MY_CACHE = "myCacheRedisTemplate";  // ← Add this
}
```

#### 2.2 Add @Bean Method

```java
@Bean(name = RedisTemplateBeanNames.MY_CACHE)
public RedisTemplate<String, MyCacheDTO> myCacheRedisTemplate(
        RedisConnectionFactory factory, ObjectMapper redisObjectMapper) {
    return createRedisTemplate(factory, redisObjectMapper, MyCacheDTO.class);
}
```

**Naming Convention:**
- Bean name: `[camelCaseName]RedisTemplate`
- Method name: = Bean name
- Constant name: UPPER_SNAKE_CASE của camelCaseName

---

### Step 3: Tạo Cache Service

**Rules:**
- Service name: `[Entity]CacheService`
- Package: `com.stream.demo.service`
- PHẢI inject RedisTemplate với `@Qualifier`
- PHẢI define cache key prefix với version

**Template:**

```java
package com.stream.demo.service;

import com.stream.demo.config.RedisConfig;
import com.stream.demo.model.dto.cache.MyCacheDTO;
import com.stream.demo.model.entity.MyEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

/**
 * My Entity Cache Service
 */
@Service
@RequiredArgsConstructor
public class MyCacheService {

    @Qualifier(RedisConfig.RedisTemplateBeanNames.MY_CACHE)
    private final RedisTemplate<String, MyCacheDTO> myCacheRedisTemplate;

    private static final String CACHE_VERSION = "v1";
    private static final String CACHE_PREFIX = "my_entity:" + CACHE_VERSION + ":";
    private static final Duration DEFAULT_TTL = Duration.ofHours(1);

    /**
     * Cache entity vào Redis
     */
    public void cache(MyEntity entity) {
        String key = CACHE_PREFIX + entity.getId();
        MyCacheDTO dto = MyCacheDTO.fromEntity(entity);
        myCacheRedisTemplate.opsForValue().set(key, dto, DEFAULT_TTL);
    }

    /**
     * Get entity từ cache
     */
    public Optional<MyCacheDTO> getFromCache(Long id) {
        String key = CACHE_PREFIX + id;
        MyCacheDTO dto = myCacheRedisTemplate.opsForValue().get(key);
        return Optional.ofNullable(dto);
    }

    /**
     * Invalidate cache
     */
    public void invalidate(Long id) {
        String key = CACHE_PREFIX + id;
        myCacheRedisTemplate.delete(key);
    }
}
```

---

## 🔑 Cache Key Convention

### Format

```
[entity_name]:[version]:[identifier]
```

**Examples:**
- `session:v1:uuid-here`
- `livestream:v1:123`
- `user_profile:v2:456`

### Rules

✅ **PHẢI có version prefix** để dễ dàng migrate khi breaking changes  
✅ **Dùng lowercase với underscore** cho entity name  
✅ **Identifier cuối cùng** (ID, UUID, etc.)  
❌ **KHÔNG dùng spaces hoặc special characters** ngoài `:` và `_`

### Versioning Strategy

Khi cần breaking change (rename field, change type):

```java
// Old
private static final String CACHE_VERSION = "v1";

// New - bump version
private static final String CACHE_VERSION = "v2";
```

**Benefit:** Old cache (`v1`) tự expire, new cache (`v2`) hoạt động song song → zero-downtime migration

---

## ⏱️ TTL (Time To Live) Guidelines

| Use Case | Recommended TTL | Rationale |
|----------|-----------------|-----------|
| **Session** | Match session expiration | Sync với business logic |
| **Hot Data** | 5-15 minutes | Balance freshness vs load |
| **Computed Results** | 1 hour | Expensive to recompute |
| **Rate Limiting** | 1 minute - 1 hour | Match rate limit window |
| **Temporary State** | As needed | Business requirement |

**Code Example:**

```java
// Dynamic TTL based on entity
long ttlSeconds = ChronoUnit.SECONDS.between(
    LocalDateTime.now(), 
    entity.getExpiresAt()
);
if (ttlSeconds > 0) {
    template.opsForValue().set(key, dto, Duration.ofSeconds(ttlSeconds));
}

// Fixed TTL
private static final Duration DEFAULT_TTL = Duration.ofMinutes(15);
template.opsForValue().set(key, dto, DEFAULT_TTL);
```

---

## ⚠️ Common Pitfalls & Solutions

### 1. Serialization Errors

**Problem:** `SerializationException: Could not read JSON`

**Causes:**
- Class renamed/moved → full class name in cache outdated
- Field type changed → incompatible deserialization
- Missing Jackson module (e.g., `JavaTimeModule`)

**Solutions:**
✅ Bump cache version  
✅ Flush Redis khi breaking change: `docker exec -it <redis-container> redis-cli FLUSHDB`  
✅ Ensure `@JsonIgnoreProperties(ignoreUnknown = true)` trên DTO

---

### 2. Cache Stampede

**Problem:** Cache expires → nhiều requests cùng lúc query DB

**Solution:** Cache-Aside with Lock Pattern

```java
public Optional<MyCacheDTO> getFromCache(Long id) {
    String key = CACHE_PREFIX + id;
    
    // Try cache first
    MyCacheDTO cached = template.get(key);
    if (cached != null) return Optional.of(cached);
    
    // Cache miss - use distributed lock để prevent stampede
    String lockKey = key + ":lock";
    Boolean lockAcquired = template.opsForValue()
        .setIfAbsent(lockKey, "1", Duration.ofSeconds(5));
    
    if (Boolean.TRUE.equals(lockAcquired)) {
        try {
            // Load from DB
            MyEntity entity = repository.findById(id).orElse(null);
            if (entity != null) {
                cache(entity);
                return Optional.of(MyCacheDTO.fromEntity(entity));
            }
        } finally {
            template.delete(lockKey);
        }
    } else {
        // Another thread đang load - retry hoặc return empty
        Thread.sleep(100);
        return getFromCache(id);
    }
    
    return Optional.empty();
}
```

---

### 3. Memory Bloat

**Problem:** Redis memory tăng không kiểm soát

**Prevention:**
✅ **Always set TTL** - no eternal keys  
✅ **Monitor cache size**: `redis-cli INFO memory`  
✅ **Set maxmemory policy** trong `docker-compose.yml`:

```yaml
redis:
  command: redis-server --maxmemory 256mb --maxmemory-policy allkeys-lru
```

---

### 4. Bean Not Found

**Error:** `required a bean of type 'RedisTemplate' that could not be found`

**Causes:**
- Quên register `@Bean` method trong `RedisConfig`
- Sai bean name trong `@Qualifier`
- Bean name constant không match

**Solution:**
1. Check `RedisConfig.RedisTemplateBeanNames` có constant
2. Check `@Bean(name = ...)` có match constant
3. Check `@Qualifier(...)` dùng đúng constant

---

## 🧪 Testing

### Unit Test with Embedded Redis

**Dependency:**

```xml
<dependency>
    <groupId>it.ozimov</groupId>
    <artifactId>embedded-redis</artifactId>
    <version>0.7.3</version>
    <scope>test</scope>
</dependency>
```

**Test Example:**

```java
@SpringBootTest
@TestPropertySource(properties = {
    "spring.redis.host=localhost",
    "spring.redis.port=6370"  // Different port
})
class MyCacheServiceTest {
    
    @Autowired
    private MyCacheService cacheService;
    
    @Test
    void shouldCacheAndRetrieve() {
        MyEntity entity = new MyEntity(1L, "Test");
        
        cacheService.cache(entity);
        
        Optional<MyCacheDTO> cached = cacheService.getFromCache(1L);
        assertThat(cached).isPresent();
        assertThat(cached.get().getName()).isEqualTo("Test");
    }
}
```

---

## 📊 Monitoring & Debugging

### Redis CLI Commands

```bash
# Connect to Redis
docker exec -it <redis-container> redis-cli

# List all keys
KEYS *

# Get key value
GET session:v1:some-uuid

# Check TTL
TTL session:v1:some-uuid

# Delete key
DEL session:v1:some-uuid

# Flush all (⚠️ DANGER)
FLUSHDB
```

### Spring Boot Actuator

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: metrics,health
  metrics:
    export:
      redis:
        enabled: true
```

**Metrics to monitor:**
- `redis.commands.count` - Operations per second
- `redis.memory.used` - Memory usage
- `redis.keyspace.hits` - Cache hit rate
- `redis.keyspace.misses` - Cache miss rate

**Formula:**
```
Cache Hit Rate = hits / (hits + misses) * 100%
```

Target: **> 80%** hit rate

---

## ✅ Checklist: Adding New Cache

- [ ] Tạo Cache DTO trong `model/dto/cache/`
- [ ] Implement `fromEntity()` static method
- [ ] Add bean name constant trong `RedisConfig.RedisTemplateBeanNames`
- [ ] Add `@Bean` method trong `RedisConfig`
- [ ] Tạo `[Entity]CacheService` với `@Qualifier`
- [ ] Define cache key prefix với version
- [ ] Set appropriate TTL
- [ ] Update service layer để integrate cache
- [ ] Test cache hit/miss scenarios
- [ ] Document trong file này (nếu có pattern mới)

---

## 🔗 References

- [Spring Data Redis Reference](https://docs.spring.io/spring-data/redis/reference/)
- [Redis Best Practices](https://redis.io/docs/manual/patterns/)
- [Jackson Annotations](https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations)

---

**Last Updated:** 2025-12-17  
**Maintainer:** Backend Team  
**Questions?** Check with team lead trước khi implement patterns mới.
