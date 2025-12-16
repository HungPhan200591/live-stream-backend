package com.stream.demo.config;

import com.stream.demo.model.entity.Role;
import com.stream.demo.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Initialize default roles in database if they don't exist
 */
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;

    @PostConstruct
    public void init() {
        System.out.println(">>> Initializing default roles...");

        // Create default roles if not exist
        createRoleIfNotExists(Role.RoleType.ROLE_USER.name());
        createRoleIfNotExists(Role.RoleType.ROLE_STREAMER.name());
        createRoleIfNotExists(Role.RoleType.ROLE_ADMIN.name());

        System.out.println(">>> Default roles initialized.");
    }

    private void createRoleIfNotExists(String roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = Role.builder()
                    .name(roleName)
                    .build();
            roleRepository.save(role);
            System.out.println(">>> Created role: " + roleName);
        }
    }
}
