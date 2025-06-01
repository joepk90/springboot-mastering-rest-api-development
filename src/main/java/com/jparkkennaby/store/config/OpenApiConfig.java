package com.jparkkennaby.store.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.servers.Server;
import lombok.Data;

@Configuration
@Data
public class OpenApiConfig {

    @Value("${swaggerUseSSL}")
    private boolean swaggerUseSSL;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info().title("Spring Store API").version("v1"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))); // Optional
    }

    // hacky approach to making swagger use ssl - there is probably a more elegant
    // solution than this
    @Bean
    public OpenApiCustomizer forceHttpsInServers() {
        return openApi -> {
            if (!swaggerUseSSL || openApi.getServers() == null) {
                return;
            }

            for (Server server : openApi.getServers()) {
                String url = server.getUrl();
                if (url != null && url.startsWith("http://")) {
                    server.setUrl(url.replace("http://", "https://"));
                }
            }
        };
    }
}
