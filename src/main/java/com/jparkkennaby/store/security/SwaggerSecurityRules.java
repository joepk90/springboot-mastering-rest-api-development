package com.jparkkennaby.store.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class SwaggerSecurityRules implements SecurtyRules {

    @Override
    public void configure(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry
                .requestMatchers("swagger-ui/**").permitAll()
                .requestMatchers("swagger-ui.html").permitAll()
                .requestMatchers("v3/api-docs/**").permitAll();
    }
}
