# Phase 2: Development Simulation APIs

> **Status**: ✅ COMPLETED  
> **Dependencies**: Phase 1 (Foundation)

---

## Business Goals

### Use Cases Covered
- Development & Testing infrastructure (không có business use case trực tiếp)

### Business Value Delivered
- ✅ Fast development workflow không cần external services
- ✅ Testing infrastructure sẵn sàng
- ✅ Giả lập các sự kiện external (OBS stream, Payment gateway)
- ✅ Debugging và troubleshooting dễ dàng hơn

### Enables Future Phases
- All phases (testing infrastructure)

---

## Technical Implementation

### 2.1. Simulation Controller

**Purpose**: Giả lập các sự kiện từ external systems

```java
@RestController
@RequestMapping("/api/dev/simulate")
@Tag(name = "Development Simulation", description = "Simulate external events for testing")
public class SimulationController {
    
    private final StreamService streamService;
    private final WalletService walletService;
    
    /**
     * Giả lập OBS stream start
     * Thay thế: OBS -(RTMP)-> Media Server -(Webhook)-> Backend
     */
    @PostMapping("/stream/start")
    @Operation(summary = "Simulate stream start event")
    public ApiResponse<StreamDTO> simulateStreamStart(
            @RequestBody @Valid SimulateStreamStartRequest request) {
        
        // Validate stream key exists
        StreamDTO stream = streamService.startStream(request.getStreamKey());
        return ApiResponse.success(stream, "Stream started (simulated)");
    }
    
    /**
     * Giả lập OBS stream end
     */
    @PostMapping("/stream/end")
    @Operation(summary = "Simulate stream end event")
    public ApiResponse<StreamDTO> simulateStreamEnd(
            @RequestBody @Valid SimulateStreamEndRequest request) {
        
        StreamDTO stream = streamService.endStream(request.getStreamKey());
        return ApiResponse.success(stream, "Stream ended (simulated)");
    }
    
    /**
     * Giả lập payment deposit
     * Thay thế: Payment Gateway -(Callback)-> Backend
     */
    @PostMapping("/payment/deposit")
    @Operation(summary = "Simulate payment deposit")
    public ApiResponse<WalletDTO> simulateDeposit(
            @RequestBody @Valid SimulateDepositRequest request) {
        
        WalletDTO wallet = walletService.deposit(
                request.getUserId(),
                request.getAmount(),
                "Simulated deposit"
        );
        return ApiResponse.success(wallet, "Deposit successful (simulated)");
    }
}
```

---

### 2.2. Testing Endpoints

**Purpose**: Verify infrastructure connectivity

```java
@RestController
@RequestMapping("/api/test")
@Tag(name = "Testing", description = "Infrastructure testing endpoints")
public class TestController {
    
    private final JdbcTemplate jdbcTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private final RabbitTemplate rabbitTemplate;
    
    /**
     * Test PostgreSQL connection
     */
    @GetMapping("/sql")
    @Operation(summary = "Test PostgreSQL connection")
    public ApiResponse<String> testSQL() {
        String version = jdbcTemplate.queryForObject(
                "SELECT version()", String.class);
        return ApiResponse.success(version, "PostgreSQL connected");
    }
    
    /**
     * Test Redis connection
     */
    @GetMapping("/redis")
    @Operation(summary = "Test Redis connection")
    public ApiResponse<String> testRedis() {
        redisTemplate.opsForValue().set("test:ping", "pong", 10, TimeUnit.SECONDS);
        String result = redisTemplate.opsForValue().get("test:ping");
        return ApiResponse.success(result, "Redis connected");
    }
    
    /**
     * Test RabbitMQ connection
     */
    @GetMapping("/rabbitmq")
    @Operation(summary = "Test RabbitMQ connection")
    public ApiResponse<String> testRabbitMQ() {
        String testMessage = "Test message at " + LocalDateTime.now();
        rabbitTemplate.convertAndSend("test.queue", testMessage);
        return ApiResponse.success(testMessage, "RabbitMQ connected");
    }
}
```

---

### 2.3. Request DTOs

```java
@Data
public class SimulateStreamStartRequest {
    @NotBlank
    @Schema(description = "Stream key", example = "abc123def456")
    private String streamKey;
}

@Data
public class SimulateStreamEndRequest {
    @NotBlank
    @Schema(description = "Stream key", example = "abc123def456")
    private String streamKey;
}

@Data
public class SimulateDepositRequest {
    @NotNull
    @Schema(description = "User ID", example = "1")
    private Long userId;
    
    @NotNull
    @Positive
    @Schema(description = "Amount to deposit", example = "1000.00")
    private BigDecimal amount;
}
```

---

## Verification Plan

### Manual Testing

#### 1. Test Stream Simulation

```http
### Simulate Stream Start
POST http://localhost:8080/api/dev/simulate/stream/start
Content-Type: application/json

{
  "streamKey": "your-stream-key-here"
}

### Simulate Stream End
POST http://localhost:8080/api/dev/simulate/stream/end
Content-Type: application/json

{
  "streamKey": "your-stream-key-here"
}
```

**Expected**: Stream status changes (isLive toggle)

---

#### 2. Test Payment Simulation

```http
### Simulate Deposit
POST http://localhost:8080/api/dev/simulate/payment/deposit
Content-Type: application/json

{
  "userId": 1,
  "amount": 1000.00
}
```

**Expected**: User wallet balance increases

---

#### 3. Test Infrastructure

```http
### Test PostgreSQL
GET http://localhost:8080/api/test/sql

### Test Redis
GET http://localhost:8080/api/test/redis

### Test RabbitMQ
GET http://localhost:8080/api/test/rabbitmq
```

**Expected**: All return 200 OK with connection info

---

## Checklist

- [x] `POST /api/dev/simulate/stream/start` implemented
- [x] `POST /api/dev/simulate/stream/end` implemented
- [x] `POST /api/dev/simulate/payment/deposit` implemented
- [x] `GET /api/test/sql` implemented
- [x] `GET /api/test/redis` implemented
- [x] `GET /api/test/rabbitmq` implemented
- [x] All endpoints documented in Swagger
- [x] HTTP test file created

---

## Dependencies

### Required
- Phase 1: Foundation

### Enables
- All future phases (testing capability)

---

## Notes

### Security Considerations

> ⚠️ **CRITICAL**: These endpoints MUST be disabled in production!

**Options**:
1. Profile-based: Only enable in `dev` profile
2. IP Whitelist: Restrict to localhost/internal IPs
3. Remove entirely: Delete before production deployment

**Recommended**: Use Spring Profile

```java
@Profile("dev")
@RestController
@RequestMapping("/api/dev")
public class SimulationController {
    // ...
}
```

### Use Cases

**When to use simulation endpoints**:
- ✅ Local development without external services
- ✅ Integration testing
- ✅ Debugging specific scenarios
- ✅ Demo/presentation

**When NOT to use**:
- ❌ Production environment
- ❌ Load testing (use real flows)
- ❌ Security testing
