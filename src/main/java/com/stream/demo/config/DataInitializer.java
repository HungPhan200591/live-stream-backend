package com.stream.demo.config;

import com.stream.demo.model.entity.Role;
import com.stream.demo.model.entity.User;
import com.stream.demo.model.entity.UserRole;
import com.stream.demo.repository.RoleRepository;
import com.stream.demo.repository.UserRepository;
import com.stream.demo.repository.UserRoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Initialize default data (roles, users, and user_roles) in database.
 * 
 * This component only runs when the property 'app.data.init-default-data' is
 * set to true.
 * By default, it is disabled to avoid unnecessary database checks on every
 * application startup.
 * 
 * To enable data initialization:
 * - Set app.data.init-default-data=true in application.yml
 * - Or use command line: --app.data.init-default-data=true
 * - Or use environment variable: APP_DATA_INIT_DEFAULT_DATA=true
 * 
 * After first initialization, set it back to false to improve startup
 * performance.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.data.init-default-data", havingValue = "true", matchIfMissing = false // Default:
                                                                                                         // disabled
)
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    // Default password for all demo users (should be changed in production!)
    private static final String DEFAULT_PASSWORD = "Password123!";

    @PostConstruct
    public void init() {
        log.info(">>> Initializing default data...");

        // Step 1: Create default roles
        initializeRoles();

        // Step 2: Create default users
        initializeUsers();

        log.info(">>> Default data initialization completed.");
    }

    private void initializeRoles() {
        log.info(">>> Initializing default roles...");

        createRoleIfNotExists(Role.RoleType.ROLE_USER.name());
        createRoleIfNotExists(Role.RoleType.ROLE_STREAMER.name());
        createRoleIfNotExists(Role.RoleType.ROLE_ADMIN.name());

        log.info(">>> Default roles initialized.");
    }

    private void initializeUsers() {
        log.info(">>> Initializing default users...");

        // Admin user with ADMIN role
        createUserWithRoleIfNotExists(
                "admin",
                "admin@livestream.com",
                DEFAULT_PASSWORD,
                Role.RoleType.ROLE_ADMIN.name());

        // Streamer user with STREAMER role
        createUserWithRoleIfNotExists(
                "streamer001",
                "streamer001@livestream.com",
                DEFAULT_PASSWORD,
                Role.RoleType.ROLE_STREAMER.name());

        // Regular user with USER role
        createUserWithRoleIfNotExists(
                "user001",
                "user001@livestream.com",
                DEFAULT_PASSWORD,
                Role.RoleType.ROLE_USER.name());

        log.info(">>> Default users initialized.");
        log.warn(">>> Default password for all users: {} (Change this in production!)", DEFAULT_PASSWORD);
    }

    private void createRoleIfNotExists(String roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = Role.builder()
                    .name(roleName)
                    .build();
            roleRepository.save(role);
            log.info(">>> Created role: {}", roleName);
        }
    }

    private void createUserWithRoleIfNotExists(String username, String email, String password, String roleName) {
        // Check if user already exists
        if (userRepository.findByUsername(username).isPresent()) {
            log.info(">>> User already exists: {}", username);
            return;
        }

        // Create user
        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .build();
        user = userRepository.save(user);
        log.info(">>> Created user: {} ({})", username, email);

        // Assign role to user
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        UserRole userRole = UserRole.builder()
                .userId(user.getId())
                .roleId(role.getId())
                .build();
        userRoleRepository.save(userRole);
        log.info(">>> Assigned role {} to user: {}", roleName, username);
    }
}
