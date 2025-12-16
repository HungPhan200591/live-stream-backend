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

        @Transactional
        public AuthResponse register(RegisterRequest request) {
                // Validate: username not exists
                if (userRepository.existsByUsername(request.getUsername())) {
                        throw new IllegalArgumentException("Username already exists");
                }

                // Validate: email not exists
                if (userRepository.existsByEmail(request.getEmail())) {
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

                String token = jwtTokenProvider.generateToken(authentication);

                Set<String> roleNames = authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toSet());

                return AuthResponse.builder()
                                .accessToken(token)
                                .username(user.getUsername())
                                .roles(roleNames)
                                .build();
        }

        public AuthResponse login(LoginRequest request) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

                String token = jwtTokenProvider.generateToken(authentication);

                Set<String> roleNames = authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toSet());

                return AuthResponse.builder()
                                .accessToken(token)
                                .username(request.getUsername())
                                .roles(roleNames)
                                .build();
        }
}
