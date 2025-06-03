package com.jparkkennaby.store.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jparkkennaby.store.filters.JwtAuthenticationFilter;
import com.jparkkennaby.store.security.SecurityRules;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // at runtime, springboot will initialise SecurtyRules list with instances of
    // any classes that implement the SecurtyRules interface and are marked as beans
    // (using the @Component annotation).
    private final List<SecurityRules> featureSecurtyRules;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // bcrypt is the most secure and recommended hashing algorythm
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // The Dao Authentication Provider is the most common implementation
        var provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // stateless sessions (token-based authentication)
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // disable CSRF (cross site request forgery)
                .csrf(AbstractHttpConfigurer::disable)

                // Authorize specific requests
                .authorizeHttpRequests(c -> {
                    // configure the security rules defined the security package
                    featureSecurtyRules.forEach(r -> r.configure(c));
                    c.anyRequest().authenticated();
                })
                // add the jwtAuthenticationFilter as early as possible in the filter chain
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(c -> {
                    // updates the default unauthenticated http status:
                    // 403 (forbidden) -> 401 (unathorized)
                    c.authenticationEntryPoint(
                            new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

                    // updates forbidden routes to return a 403 status
                    c.accessDeniedHandler((request, response, accessDeniiedException) -> response
                            .setStatus(HttpStatus.FORBIDDEN.value()));
                });

        // make all requests public
        // http.authorizeHttpRequests(c -> c.anyRequest().permitAll());
        return http.build();
    }
}
