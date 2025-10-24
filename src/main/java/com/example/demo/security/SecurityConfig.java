package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - anyone can access
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/schools/signup").permitAll()
                        .requestMatchers("/api/schools/name/**").permitAll()
                        .requestMatchers("/api/schools").permitAll() // Allow listing schools for registration

                        //Profile endpoint - qlquer usuario autenticado pode acessar
                        .requestMatchers("/api/profile/me").authenticated()

                        // Admin only endpoints
                        .requestMatchers("/api/admins/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/users/**").hasAuthority("ROLE_ADMIN") // Only admins can manage users
                        .requestMatchers("/api/school-feedback/all").hasAuthority("ROLE_ADMIN")

                        // School-specific endpoints - schools and admins can access
                        .requestMatchers("/api/schools/**").hasAnyAuthority("ROLE_SCHOOL", "ROLE_ADMIN")
                        .requestMatchers("/api/school-feedback/my-school").hasAuthority("ROLE_SCHOOL")

                        // Student-specific endpoints - students and admins can access
                        .requestMatchers("/api/students/**").hasAnyAuthority("ROLE_STUDENT", "ROLE_ADMIN")
                        .requestMatchers("/api/school-feedback/student-school").hasAnyAuthority("ROLE_STUDENT", "ROLE_ADMIN")

                        // Swagger endpoits
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/swagger-resources",
                                "/webjars/**",
                                "/configuration/ui",
                                "/configuration/security"
                        ).permitAll()

                        // Other endpoints require authentication but any role
                        .requestMatchers("/api/feedback/**").authenticated()
                        .requestMatchers("/api/tags/**").authenticated()

                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


}