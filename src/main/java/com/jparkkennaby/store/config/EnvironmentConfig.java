// package com.jparkkennaby.store.config;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.env.Environment;

// import jakarta.annotation.PostConstruct;

// @Configuration
// public class EnvironmentConfig {

// @Autowired
// private Environment env;

// @PostConstruct
// public void printEnv() {
// System.out.println("🌱 Loaded Environment Variables:");

// for (String key : env.getActiveProfiles()) {
// System.out.println("Profile: " + key);
// }

// // Just an example of a few expected keys
// System.out.println("JWT_SECRET: " + env.getProperty("JWT_SECRET"));
// System.out.println("STRIPE_SECRET_KEY: " +
// env.getProperty("STRIPE_SECRET_KEY"));
// System.out.println("STRIPE_WEBHOOK_SECRET_KEY: " +
// env.getProperty("STRIPE_WEBHOOK_SECRET_KEY"));
// }
// }