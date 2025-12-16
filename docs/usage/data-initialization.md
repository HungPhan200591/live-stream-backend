# Data Initialization Guide

## Tổng quan

File `DataInitializer.java` được thiết kế để chỉ chạy **1 lần duy nhất** khi cần khởi tạo hoặc reset dữ liệu mặc định.

**Mặc định**: Data initialization bị **TẮT** (`app.data.init-default-data: false`)

## Lợi ích

✅ **Không làm chậm app** - Không có database checks mỗi lần khởi động
✅ **Chỉ chạy khi cần** - Tự control khi nào muốn init/reset data
✅ **Linh hoạt** - Có thể enable qua config hoặc command line

## Cách sử dụng

### Option 1: Sửa file `application.yml` (Khuyến nghị cho lần đầu)

```yaml
app:
  data:
    init-default-data: true  # Thay đổi từ false -> true
```

**Sau khi chạy xong:**
```yaml
app:
  data:
    init-default-data: false  # Đổi lại false để tắt initialization
```

### Option 2: Sử dụng Command Line (Không cần sửa code)

```bash
# Chạy với flag enable initialization
mvn spring-boot:run -Dspring-boot.run.arguments="--app.data.init-default-data=true"

# Hoặc nếu đã build JAR
java -jar target/live-stream-backend-*.jar --app.data.init-default-data=true
```

### Option 3: Environment Variable

```bash
# Windows PowerShell
$env:APP_DATA_INIT_DEFAULT_DATA="true"
mvn spring-boot:run

# Windows CMD
set APP_DATA_INIT_DEFAULT_DATA=true
mvn spring-boot:run
```

## Dữ liệu sẽ được tạo

Khi enable initialization, hệ thống sẽ tạo:

### 🎭 Roles (3 roles)
- `ROLE_USER` - Người dùng thông thường
- `ROLE_STREAMER` - Người phát sóng
- `ROLE_ADMIN` - Quản trị viên

### 👥 Users (3 users)

| Username | Email | Password | Role |
|----------|-------|----------|------|
| admin | admin@livestream.com | `Password123!` | ROLE_ADMIN |
| streamer001 | streamer001@livestream.com | `Password123!` | ROLE_STREAMER |
| user001 | user001@livestream.com | `Password123!` | ROLE_USER |

### 🔗 User-Role Mappings (3 records)
Tự động gán role tương ứng cho từng user.

## Logs khi chạy

Khi initialization được enable, bạn sẽ thấy logs:

```
>>> Initializing default data...
>>> Initializing default roles...
>>> Created role: ROLE_USER
>>> Created role: ROLE_STREAMER
>>> Created role: ROLE_ADMIN
>>> Default roles initialized.
>>> Initializing default users...
>>> Created user: admin (admin@livestream.com)
>>> Assigned role ROLE_ADMIN to user: admin
>>> Created user: streamer001 (streamer001@livestream.com)
>>> Assigned role ROLE_STREAMER to user: streamer001
>>> Created user: user001 (user001@livestream.com)
>>> Assigned role ROLE_USER to user: user001
>>> Default users initialized.
>>> Default password for all users: Password123! (Change this in production!)
>>> Default data initialization completed.
```

## Reset dữ liệu

Để reset lại dữ liệu từ đầu:

1. **Xóa dữ liệu cũ trong database:**
   ```sql
   TRUNCATE TABLE user_roles CASCADE;
   TRUNCATE TABLE users CASCADE;
   TRUNCATE TABLE roles CASCADE;
   ```

2. **Enable initialization:**
   - Set `app.data.init-default-data: true` trong `application.yml`
   - Hoặc dùng command line flag

3. **Chạy application**

4. **Disable lại:**
   - Set `app.data.init-default-data: false`

## Workflow điển hình

### 🆕 Lần đầu setup project

```bash
# 1. Start database
docker-compose up -d postgres

# 2. Run app với initialization (command line - không cần sửa file)
mvn spring-boot:run -Dspring-boot.run.arguments="--app.data.init-default-data=true"

# 3. Lần chạy tiếp theo (không init data nữa)
mvn spring-boot:run
```

### 🔄 Development thường ngày

```bash
# Chỉ cần chạy bình thường - KHÔNG có initialization
mvn spring-boot:run
# hoặc
./mvnw spring-boot:run
```

### 🔁 Khi muốn reset data

```bash
# Chạy 1 lần với flag
mvn spring-boot:run -Dspring-boot.run.arguments="--app.data.init-default-data=true"
```

## Lưu ý quan trọng

> [!WARNING]
> - Default password `Password123!` chỉ dùng cho **development/testing**
> - **KHÔNG** dùng passwords này trong production
> - Nên đổi password ngay sau khi login lần đầu

> [!TIP]
> - Dùng **Command Line flag** để tránh phải sửa file `application.yml`
> - Giữ `init-default-data: false` trong file config
> - Chỉ enable khi thực sự cần init/reset data

## Troubleshooting

### ❓ DataInitializer không chạy dù đã enable

**Kiểm tra:**
1. Property name phải chính xác: `app.data.init-default-data`
2. Value phải là `true` (string, không phải boolean)
3. Xem logs xem có message "Initializing default data..." không

### ❓ Lỗi "User already exists"

**Nguyên nhân:** Data đã tồn tại trong database

**Giải pháp:**
- Nếu muốn giữ data: Disable initialization
- Nếu muốn reset: Xóa data trong database trước

### ❓ Muốn init chỉ roles, không init users

**Giải pháp:** Tạm thời comment out phần `initializeUsers()` trong method `init()`.
