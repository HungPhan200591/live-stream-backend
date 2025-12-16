# Coding Rules - Spring Boot Livestream Backend

## Database Design Rules

### ❌ CẤM SỬ DỤNG JPA Relationships Annotations

**KHÔNG được sử dụng:**
- `@ManyToMany`
- `@ManyToOne`
- `@OneToMany`
- `@OneToOne`

**LÝ DO:**
- Giảm coupling giữa entities
- Tránh N+1 query problem
- Dễ control performance
- Dễ debug và maintain
- Tránh lazy loading issues

**THAY VÀO ĐÓ:**
- Sử dụng **explicit join table entities**
- Query manually qua Repository khi cần
- Sử dụng DTO để compose data

**VÍ DỤ:**

❌ **SAI - Dùng @ManyToMany:**
```java
@Entity
public class User {
    @ManyToMany
    private Set<Role> roles;
}
```

✅ **ĐÚNG - Dùng Join Table Entity:**
```java
@Entity
public class User {
    private Long id;
    // No relationship annotations
}

@Entity
public class Role {
    private Long id;
}

@Entity
@Table(name = "user_roles")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "role_id", nullable = false)
    private Long roleId;
}
```

## Development Workflow Rules

### 1. Plan Approval
- **LUÔN LUÔN** phải đợi user approve implementation plan trước khi EXECUTION
- Không được tự ý chuyển sang EXECUTION mode
- Sử dụng `notify_user` với `BlockedOnUser: true` để request approval

### 2. Build & Test
- **KHÔNG** được tự ý run `mvn compile`, `mvn test`, `mvn package`
- **KHÔNG** được tự ý run Docker commands
- **CHỈ** implement code thuần
- User sẽ tự run build/test khi cần

### 3. Code Implementation
- Focus vào code implementation
- Để user tự verify và test
- Chỉ fix compilation errors nếu được yêu cầu
