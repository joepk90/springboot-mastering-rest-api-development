package com.jparkkennaby.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
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
                        .anyRequest().authenticated());

        // make all requests public
        // http.authorizeHttpRequests(c -> c.anyRequest().permitAll());
        return http.build();
    }
}
