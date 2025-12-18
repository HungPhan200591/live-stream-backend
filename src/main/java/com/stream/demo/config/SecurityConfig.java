package com.stream.demo.config;

import com.stream.demo.security.CustomUserDetailsService;
import com.stream.demo.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ============================================================
                        // PUBLIC ENDPOINTS (No Authentication Required)
                        // ============================================================

                        // Authentication
                        .requestMatchers("/api/auth/**").permitAll()

                        // Webhooks (use secret key verification, not JWT)
                        // See: docs/concepts/webhooks.md
                        .requestMatchers("/api/webhooks/**").permitAll()

                        // Development/Testing endpoints
                        .requestMatchers("/api/dev/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()

                        // Swagger/OpenAPI Documentation
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**")
                        .permitAll()

                        // Public viewing endpoints
                        .requestMatchers(HttpMethod.GET, "/api/streams/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/gifts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/analytics/leaderboard").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/chat/*/history").permitAll()

                        // ============================================================
                        // ROLE-BASED ENDPOINTS
                        // ============================================================

                        // Admin Only
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/analytics/**").hasRole("ADMIN") // Except leaderboard (already public
                                                                               // above)

                        // ============================================================
                        // AUTHENTICATED ENDPOINTS (All Roles)
                        // ============================================================

                        // All other endpoints require authentication
                        // Fine-grained authorization will be handled by @PreAuthorize
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
