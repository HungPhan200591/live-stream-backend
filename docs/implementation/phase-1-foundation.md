# Phase 1: Foundation & Infrastructure

> **Status**: ✅ COMPLETED  
> **Dependencies**: None (Foundation phase)

---

## Business Goals

### Use Cases Covered
- Infrastructure setup (không có business use case trực tiếp)

### Business Value Delivered
- ✅ Platform infrastructure sẵn sàng cho development
- ✅ Common utilities để tăng tốc độ development
- ✅ API documentation framework (Swagger)
- ✅ Consistent error handling across all APIs

### Enables Future Phases
- All phases (foundation cho toàn bộ project)

---

## Technical Implementation

### 1.1. Project Initialization

**Spring Boot Dependencies**:
```xml
<dependencies>
    <!-- Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Redis -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    
    <!-- RabbitMQ -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-amqp</artifactId>
    </dependency>
    
    <!-- WebSocket -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>
    
    <!-- Actuator -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <!-- PostgreSQL -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- Swagger/OpenAPI -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.2.0</version>
    </dependency>
</dependencies>
```

---

### 1.2. Docker Compose Setup

**File**: `docker-compose.yml`

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16
    container_name: livestream-postgres
    environment:
      POSTGRES_DB: livestream_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    container_name: livestream-redis
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes
    volumes:
      - redis_data:/data

  rabbitmq:
    image: rabbitmq:3-management-alpine
    container_name: livestream-rabbitmq
    environment:
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: admin
    ports:
      - "5672:5672"   # AMQP
      - "15672:15672" # Management UI
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq

volumes:
  postgres_data:
  redis_data:
  rabbitmq_data:
```

---

### 1.3. Package Structure (Layered Architecture)

```
src/main/java/com/stream/demo/
├── config/                    # Configuration classes
│   ├── OpenApiConfig.java
│   ├── RedisConfig.java
│   ├── RabbitMQConfig.java
│   └── WebSocketConfig.java
├── controller/                # REST Controllers
├── service/                   # Business Logic
├── repository/                # Data Access
├── model/
│   ├── entity/               # JPA Entities
│   ├── dto/                  # Data Transfer Objects
│   │   ├── request/
│   │   ├── response/
│   │   └── cache/            # Redis Cache DTOs
│   └── enums/                # Enumerations
├── security/                  # Security components
├── exception/                 # Custom Exceptions
└── util/                      # Utility classes
```

---

### 1.4. Common Utilities

#### ApiResponse Wrapper

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private LocalDateTime timestamp;
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
```

#### GlobalExceptionHandler

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
            AccessDeniedException ex) {
        log.error("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Access denied"));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(errors));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex) {
        log.error("Unexpected error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal server error"));
    }
}
```

---

### 1.5. OpenAPI/Swagger Configuration

```java
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Livestream Backend API")
                        .version("1.0")
                        .description("Spring Boot Livestream Platform API Documentation"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
```

---

## Verification Plan

### Infrastructure Verification

#### 1. Docker Services

```bash
# Start all services
docker-compose up -d

# Verify PostgreSQL
docker exec -it livestream-postgres psql -U postgres -d livestream_db -c "SELECT version();"

# Verify Redis
docker exec -it livestream-redis redis-cli PING
# Expected: PONG

# Verify RabbitMQ
# Open browser: http://localhost:15672
# Login: admin/admin
```

#### 2. Spring Boot Application

```bash
# Build application
mvn clean package -DskipTests

# Run application
mvn spring-boot:run

# Verify startup
# Check logs for: "Started LiveStreamBackendApplication"
```

#### 3. Swagger UI

```
Open browser: http://localhost:8080/swagger-ui.html
Verify: API documentation loads successfully
```

---

## Checklist

- [x] Spring Boot project initialized với dependencies
- [x] Docker Compose configured (PostgreSQL, Redis, RabbitMQ)
- [x] Package structure created (Layered Architecture)
- [x] ApiResponse wrapper implemented
- [x] GlobalExceptionHandler implemented
- [x] OpenAPI/Swagger configuration
- [x] Application starts successfully
- [x] All infrastructure services running

---

## Dependencies

### Required
- None (foundation phase)

### Enables
- All future phases

---

## Notes

### Key Decisions

1. **Layered Architecture**: Controller → Service → Repository pattern
2. **Docker Compose**: Local development environment
3. **ApiResponse Wrapper**: Consistent API response format
4. **Swagger**: Auto-generated API documentation
5. **Global Exception Handler**: Centralized error handling

### Configuration Files

- `application.yml`: Spring Boot configuration
- `docker-compose.yml`: Infrastructure services
- `pom.xml`: Maven dependencies
