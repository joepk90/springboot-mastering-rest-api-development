package com.jparkkennaby.store.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// import jakarta.annotation.PostConstruct;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "spring")
public class TableLimitConfig {

    private Integer maxValue = 100;
    private Map<String, Integer> limits;

    // @PostConstruct
    // public void afterPropertiesSet() {
    // System.out.println("Loaded Table Limits:");
    // System.out.println(limits);
    // }

    public Integer getLimit(String entityName) {
        return limits.getOrDefault(entityName.toLowerCase(), maxValue);
    }

    public Map<String, Integer> getLimits() {
        return limits;
    }

    public void setLimits(Map<String, Integer> limits) {
        this.limits = limits;
    }
}
