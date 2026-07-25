# P6Spy SQL Logging

> Trạng thái: `CURRENT DEV TOOL`<br>
> Cập nhật: 2026-07-25

P6Spy đang được cấu hình qua [pom.xml](../../pom.xml), [application.yml](../../src/main/resources/application.yml) và [spy.properties](../../src/main/resources/spy.properties). Nó giúp quan sát SQL có bind value và execution time trong local development.

## Current configuration

```yaml
spring:
  datasource:
    url: jdbc:p6spy:postgresql://localhost:15432/livestream
    driver-class-name: com.p6spy.engine.spy.P6SpyDriver
  jpa:
    show-sql: false
```

Chạy ứng dụng:

```powershell
.\mvnw.cmd spring-boot:run
```

`GET /api/test/sql` gọi `userRepository.findAll()` để tạo SQL quan sát; endpoint này phải chỉ tồn tại trong dev/test profile sau Stage 0.

## Dùng cho learning case

- Đếm query để tái hiện manual N+1.
- Quan sát transaction boundary và lock SQL.
- Copy query sang `EXPLAIN (ANALYZE, BUFFERS)` với dataset có kiểm soát.
- So sánh query trước/sau optimization; P6Spy log không thay benchmark.

## Security và production

- Bind value có thể chứa email, token hoặc dữ liệu nhạy cảm; không bật verbose SQL log mặc định trong production.
- Không khẳng định overhead cố định nếu chưa benchmark workload của project.
- Structured application log, metrics và tracing là observability khác; P6Spy không thay thế chúng.

## Troubleshooting

1. Kiểm tra P6Spy dependency và datasource prefix `jdbc:p6spy:`.
2. Kiểm tra `spy.properties` có trên runtime classpath.
3. Kiểm tra logger `p6spy`/`com.p6spy` trong `application.yml`.
4. Nếu log quá nhiều, dùng category filter hoặc execution threshold trong development config.
