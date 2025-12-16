package com.stream.demo.security;

import com.stream.demo.model.entity.Role;
import com.stream.demo.model.entity.User;
import com.stream.demo.model.entity.UserRole;
import com.stream.demo.repository.RoleRepository;
import com.stream.demo.repository.UserRepository;
import com.stream.demo.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

        private final UserRepository userRepository;
        private final UserRoleRepository userRoleRepository;
        private final RoleRepository roleRepository;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                // 1. Find User
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

                // 2. Manual join: UserRole -> Role
                List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
                List<Long> roleIds = userRoles.stream()
                                .map(UserRole::getRoleId)
                                .toList();
                List<Role> roles = roleRepository.findAllById(roleIds);

                // 3. Build authorities
                Set<GrantedAuthority> authorities = roles.stream()
                                .map(role -> new SimpleGrantedAuthority(role.getName()))
                                .collect(Collectors.toSet());

                return org.springframework.security.core.userdetails.User.builder()
                                .username(user.getUsername())
                                .password(user.getPasswordHash())
                                .authorities(authorities)
                                .build();
        }
}
