package com.jparkkennaby.store.security;

import org.springframework.stereotype.Component;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

@Component
public class MessageSecurityRules implements SecurityRules {

    @Override
    public void configure(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry
                .requestMatchers(HttpMethod.GET, "/messages/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/messages/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/messages/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/messages/**").permitAll();
    }
}

