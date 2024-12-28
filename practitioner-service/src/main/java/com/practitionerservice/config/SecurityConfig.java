package com.practitionerservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {


    @Autowired
    private JwtTokenFilter jwtTokenFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // Aktivera CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/practitioners/**").hasAnyAuthority("ROLE_PATIENT", "ROLE_STAFF", "ROLE_DOCTOR", "ROLE_INTERNAL", "DOCTOR", "STAFF", "PATIENT")
                        .requestMatchers(HttpMethod.POST, "/api/practitioners").hasAnyAuthority("ROLE_PATIENT", "ROLE_STAFF", "ROLE_DOCTOR", "ROLE_INTERNAL", "DOCTOR", "STAFF", "PATIENT")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Lösenordshantering med BCrypt
    }
}
