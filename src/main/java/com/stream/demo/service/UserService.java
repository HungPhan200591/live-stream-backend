package com.stream.demo.service;

import com.stream.demo.model.dto.UserDTO;
import com.stream.demo.model.entity.Role;
import com.stream.demo.model.entity.User;
import com.stream.demo.model.entity.UserRole;
import com.stream.demo.repository.RoleRepository;
import com.stream.demo.repository.UserRepository;
import com.stream.demo.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    /**
     * Get current authenticated user from SecurityContext
     */
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        }

        throw new IllegalStateException("User not authenticated");
    }

    /**
     * Get user by ID
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    /**
     * Convert User entity to UserDTO
     */
    public UserDTO convertToDTO(User user) {
        // Manual join để lấy role names
        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .toList();
        List<Role> roles = roleRepository.findAllById(roleIds);

        Set<String> roleNames = roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roleNames)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
