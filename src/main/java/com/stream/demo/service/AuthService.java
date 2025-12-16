package com.stream.demo.service;

import com.stream.demo.model.dto.AuthResponse;
import com.stream.demo.model.dto.LoginRequest;
import com.stream.demo.model.dto.RegisterRequest;
import com.stream.demo.model.entity.Role;
import com.stream.demo.model.entity.User;
import com.stream.demo.model.entity.UserRole;
import com.stream.demo.repository.RoleRepository;
import com.stream.demo.repository.UserRepository;
import com.stream.demo.repository.UserRoleRepository;
import com.stream.demo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final UserRoleRepository userRoleRepository;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;
        private final JwtTokenProvider jwtTokenProvider;
        private final JwtBlacklistService jwtBlacklistService;

        @Transactional
        public AuthResponse register(RegisterRequest request) {
                // Validate: username not exists
                if (Boolean.TRUE.equals(userRepository.existsByUsername(request.getUsername()))) {
                        throw new IllegalArgumentException("Username already exists");
                }

                // Validate: email not exists
                if (Boolean.TRUE.equals(userRepository.existsByEmail(request.getEmail()))) {
                        throw new IllegalArgumentException("Email already exists");
                }

                // Create new user (no roles field)
                User user = User.builder()
                                .username(request.getUsername())
                                .email(request.getEmail())
                                .passwordHash(passwordEncoder.encode(request.getPassword()))
                                .build();

                userRepository.save(user);

                // Get default role (ROLE_USER)
                Role defaultRole = roleRepository.findByName(Role.RoleType.ROLE_USER.name())
                                .orElseThrow(() -> new IllegalStateException("Default role not found"));

                // Create UserRole record explicitly
                UserRole userRole = UserRole.builder()
                                .userId(user.getId())
                                .roleId(defaultRole.getId())
                                .build();

                userRoleRepository.save(userRole);

                // Auto-login after registration
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

                String accessToken = jwtTokenProvider.generateToken(authentication);
                String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

                Set<String> roleNames = authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toSet());

                return AuthResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .tokenType("Bearer")
                                .expiresIn(jwtTokenProvider.getExpirationTime())
                                .username(user.getUsername())
                                .roles(roleNames)
                                .build();
        }

        public AuthResponse login(LoginRequest request) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

                String accessToken = jwtTokenProvider.generateToken(authentication);
                String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

                Set<String> roleNames = authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toSet());

                return AuthResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .tokenType("Bearer")
                                .expiresIn(jwtTokenProvider.getExpirationTime())
                                .username(request.getUsername())
                                .roles(roleNames)
                                .build();
        }

        /**
         * Refresh access token using refresh token
         * Làm mới access token bằng refresh token
         */
        public AuthResponse refreshAccessToken(String refreshToken) {
                // Validate refresh token
                if (!jwtTokenProvider.validateToken(refreshToken)) {
                        throw new IllegalArgumentException("Invalid refresh token");
                }

                // Check if refresh token is blacklisted
                if (jwtBlacklistService.isBlacklisted(refreshToken)) {
                        throw new IllegalArgumentException("Refresh token has been revoked");
                }

                // Extract username and load user
                String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                // Load user roles
                List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
                Set<String> roleNames = userRoles.stream()
                                .map(ur -> {
                                        Role role = roleRepository.findById(ur.getRoleId())
                                                        .orElseThrow(() -> new IllegalStateException("Role not found"));
                                        return role.getName();
                                })
                                .collect(Collectors.toSet());

                // Create Authentication object for generating new access token
                List<GrantedAuthority> authorities = roleNames.stream()
                                .map(roleName -> (GrantedAuthority) () -> roleName)
                                .collect(Collectors.toList());

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                                username, null, authorities);

                String newAccessToken = jwtTokenProvider.generateToken(authentication);

                return AuthResponse.builder()
                                .accessToken(newAccessToken)
                                .refreshToken(refreshToken) // Keep same refresh token
                                .tokenType("Bearer")
                                .expiresIn(jwtTokenProvider.getExpirationTime())
                                .username(username)
                                .roles(roleNames)
                                .build();
        }

        /**
         * Logout user by adding token to blacklist
         * Đăng xuất bằng cách thêm token vào blacklist trong Redis
         */
        public void logout(String token) {
                // Calculate remaining time to live for the token
                long remainingTime = getRemainingExpirationTime(token);

                // Add to blacklist with TTL
                jwtBlacklistService.addToBlacklist(token, remainingTime);
        }

        /**
         * Calculate remaining expiration time of a token in seconds
         * Tính thời gian còn lại trước khi token hết hạn (đơn vị: giây)
         */
        private long getRemainingExpirationTime(String token) {
                try {
                        java.util.Date expiration = jwtTokenProvider.getExpirationFromToken(token);
                        long remainingMs = expiration.getTime() - System.currentTimeMillis();
                        return Math.max(remainingMs / 1000, 0); // Convert to seconds
                } catch (Exception e) {
                        return 0;
                }
        }
}
