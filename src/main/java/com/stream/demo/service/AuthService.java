package com.stream.demo.service;

import com.stream.demo.model.dto.response.AuthResponse;
import com.stream.demo.model.dto.request.LoginRequest;
import com.stream.demo.model.dto.request.RegisterRequest;
import com.stream.demo.model.entity.Role;
import com.stream.demo.model.entity.User;
import com.stream.demo.model.entity.UserRole;
import com.stream.demo.model.entity.UserSession;
import com.stream.demo.repository.RoleRepository;
import com.stream.demo.repository.UserRepository;
import com.stream.demo.repository.UserRoleRepository;
import com.stream.demo.security.CustomUserDetailsService;
import com.stream.demo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Authentication Service
 * <p>
 * Service xử lý authentication với Session-backed JWT.
 * Session là source of truth, JWT chỉ là carrier.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final UserRoleRepository userRoleRepository;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;
        private final JwtTokenProvider jwtTokenProvider;
        private final SessionService sessionService;
        private final CustomUserDetailsService customUserDetailsService;

        /**
         * Register new user và auto-login
         */
        @Transactional
        public AuthResponse register(RegisterRequest request, String deviceId, String deviceName, String ipAddress) {
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

                // NEW: Create session for this user
                UserSession session = sessionService.createSession(
                                user.getId(), deviceId, deviceName, ipAddress);

                String accessToken = jwtTokenProvider.generateToken(authentication);
                String refreshToken = jwtTokenProvider.generateRefreshToken(
                                authentication, session.getSessionId(), deviceId);

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

        /**
         * User login
         */
        public AuthResponse login(LoginRequest request, String deviceId, String deviceName, String ipAddress) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

                // NEW: Get user and create session
                User currentUser = userRepository.findByUsername(request.getUsername())
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                UserSession session = sessionService.createSession(
                                currentUser.getId(), deviceId, deviceName, ipAddress);

                String accessToken = jwtTokenProvider.generateToken(authentication);
                // NEW: Refresh token chứa session_id
                String refreshToken = jwtTokenProvider.generateRefreshToken(
                                authentication, session.getSessionId(), deviceId);

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
         * Validate session từ DB trước khi issue new access token
         */
        public AuthResponse refreshAccessToken(String refreshToken) {
                // Validate refresh token signature + expiry
                if (!jwtTokenProvider.validateToken(refreshToken)) {
                        throw new IllegalArgumentException("Invalid refresh token");
                }

                // NEW: Extract session_id và validate session từ DB
                UUID sessionId = jwtTokenProvider.getSessionIdFromToken(refreshToken);
                sessionService.validateSession(sessionId); // Throws exception if invalid

                // Extract username
                String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

                // Load UserDetails correctly using CustomUserDetailsService
                org.springframework.security.core.userdetails.UserDetails userDetails = customUserDetailsService
                                .loadUserByUsername(username);

                // Create Authentication object with UserDetails principal
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                String newAccessToken = jwtTokenProvider.generateToken(authentication);

                Set<String> roleNames = userDetails.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toSet());

                return AuthResponse.builder()
                                .accessToken(newAccessToken)
                                .refreshToken(refreshToken) // KHÔNG rotate refresh token
                                .tokenType("Bearer")
                                .expiresIn(jwtTokenProvider.getExpirationTime())
                                .username(username)
                                .roles(roleNames)
                                .build();
        }

        /**
         * Logout user by revoking session
         * Revoke session trong DB, refresh token sẽ invalid ngay lập tức
         */
        public void logout(String refreshToken) {
                UUID sessionId = jwtTokenProvider.getSessionIdFromToken(refreshToken);
                sessionService.revokeSession(sessionId);
        }

        /**
         * Logout from all devices
         * Revoke tất cả sessions của user
         */
        public void logoutAll(Long userId) {
                sessionService.revokeAllUserSessions(userId);
        }
}
