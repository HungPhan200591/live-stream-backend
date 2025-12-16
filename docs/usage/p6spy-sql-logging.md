# P6Spy SQL Logging Configuration

## Tổng quan

**P6Spy** là một JDBC proxy driver giúp log SQL statements với **actual parameter values** thay vì dấu `?`.

### ✅ Lợi ích:
- ✨ **Thấy SQL thật** - Không còn dấu `?`, thấy được giá trị thực
- ⏱️ **Execution time** - Biết query nào chậm
- 🎨 **Format đẹp** - SQL được format dễ đọc
- 🔍 **Debug dễ dàng** - Copy SQL ra chạy trực tiếp được luôn

## 📝 Đã cấu hình:

### 1. Dependencies ([`pom.xml`](file:///d:/Study/Project/live-stream-backend/pom.xml))
```xml
<dependency>
    <groupId>p6spy</groupId>
    <artifactId>p6spy</artifactId>
    <version>3.9.1</version>
</dependency>
```

### 2. Datasource ([`application.yml`](file:///d:/Study/Project/live-stream-backend/src/main/resources/application.yml))
```yaml
datasource:
  url: jdbc:p6spy:postgresql://localhost:15432/livestream
  driver-class-name: com.p6spy.engine.spy.P6SpyDriver
jpa:
  show-sql: false  # P6Spy sẽ handle logging
```

### 3. P6Spy Config ([`spy.properties`](file:///d:/Study/Project/live-stream-backend/src/main/resources/spy.properties))
- Exclude noise (result, resultset, info, debug)
- Custom format: timestamp | execution time | SQL with values

## 🚀 Cách sử dụng:

### Rebuild và restart application:

```bash
# Stop app hiện tại (Ctrl+C)

# Rebuild để download dependency
mvn clean install -DskipTests

# Chạy lại
mvn spring-boot:run
```

## 📊 Kết quả mong đợi:

### ❌ Trước (Hibernate logging):
```
Hibernate: insert into user_roles (created_at, role_id, user_id) values (?, ?, ?)
```

### ✅ Sau (P6Spy logging):
```
2025-12-16 22:45:30.123 | ExecutionTime: 5ms | Connection: 1 | statement | 
insert into user_roles (created_at, role_id, user_id) values 
('2025-12-16 22:45:30.120', 1, 3)
```

**Lưu ý:** Bạn sẽ thấy:
- Thời gian chính xác thay vì `?`
- Chế độ execute trong bao lâu (ExecutionTime)
- Connection ID
- SQL có thể copy ra chạy trực tiếp!

## ⚙️ Tùy chỉnh:

### Chỉ log slow queries (> 100ms):
Uncomment trong [`spy.properties`](file:///d:/Study/Project/live-stream-backend/src/main/resources/spy.properties):
```properties
executionThreshold=100
```

### Log vào file thay vì console:
```properties
logfile=/path/to/sql.log
```

### Thêm stacktrace để biết query từ đâu:
```properties
stacktrace=true
```

### Hiển thị nhiều thông tin hơn:
```properties
# Bỏ comment dòng này
# excludecategories=info,debug,result,resultset
```

## 🔧 Troubleshooting:

### Không thấy SQL logs
1. Kiểm tra `spy.properties` có trong `src/main/resources/`
2. Verify URL: `jdbc:p6spy:postgresql://...`
3. Verify driver: `com.p6spy.engine.spy.P6SpyDriver`
4. Xem có error khi start app không

### Quá nhiều logs (noise)
Thêm vào `spy.properties`:
```properties
excludecategories=info,debug,result,resultset,commit,rollback
```

### Chỉ muốn log INSERT/UPDATE/DELETE
```properties
filter=true
# Tạo custom filter class
```

## 📚 Tài liệu tham khảo:

- [P6Spy Documentation](https://p6spy.readthedocs.io/)
- [P6Spy GitHub](https://github.com/p6spy/p6spy)
- [Configuration Options](https://p6spy.readthedocs.io/en/latest/configandusage.html)

## 💡 Tips:

1. **Development**: Enable P6Spy để debug dễ dàng
2. **Production**: Disable hoặc chỉ log slow queries (set high threshold)
3. **Performance**: P6Spy có overhead nhỏ (~5-10%), acceptable cho dev
4. **Log File**: Trong production nên log ra file thay vì stdout

## 🎯 Next Steps:

Sau khi restart app, bạn sẽ thấy logs kiểu:
```
2025-12-16 22:45:30.123 | ExecutionTime: 5ms | Connection: 1 | statement | 
select u1_0.id, u1_0.created_at, u1_0.email, u1_0.password_hash, u1_0.updated_at, u1_0.username 
from users u1_0 
where u1_0.username='admin'
```

**Enjoy beautiful SQL logs!** 🎉
