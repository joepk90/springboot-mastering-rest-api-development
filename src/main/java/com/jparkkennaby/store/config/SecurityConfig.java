package com.jparkkennaby.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // bcrypt is the most secure and recommended hashing algorythm
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // stateless sessions (token-based authentication)
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // disable CSRF (cross site request forgery)
                .csrf(AbstractHttpConfigurer::disable)

                // Authorize specific requests
                .authorizeHttpRequests(c -> c
                        .requestMatchers("carts/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "users/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "auth/**").permitAll()
                        .anyRequest().authenticated());

        // make all requests public
        // http.authorizeHttpRequests(c -> c.anyRequest().permitAll());
        return http.build();
    }
}
