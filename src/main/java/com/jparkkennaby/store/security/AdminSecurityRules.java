package com.jparkkennaby.store.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

import com.jparkkennaby.store.entities.Role;

@Component
public class AdminSecurityRules implements SecurtyRules {

    @Override
    public void configure(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry
                .requestMatchers("admin/**").hasRole(Role.ADMIN.name());
    }
}
