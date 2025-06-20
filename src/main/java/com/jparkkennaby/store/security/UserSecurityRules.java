package com.jparkkennaby.store.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

import com.jparkkennaby.store.users.Role;

@Component
public class UserSecurityRules implements SecurityRules {

    @Override
    public void configure(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry
                .requestMatchers(HttpMethod.POST, "/users/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/users/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/users/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole(Role.ADMIN.name());
    }
}
