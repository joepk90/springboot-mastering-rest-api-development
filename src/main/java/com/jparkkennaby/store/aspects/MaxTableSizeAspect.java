package com.jparkkennaby.store.aspects;

import com.jparkkennaby.store.annoations.MaxTableSizeCheck;
import com.jparkkennaby.store.config.TableLimitConfig;
import com.jparkkennaby.store.exceptions.MaxTableRecordLimitReached;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpMethod;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class MaxTableSizeAspect {

    private final TableLimitConfig tableLimitConfig;

    @PersistenceContext
    private EntityManager entityManager;

    public MaxTableSizeAspect(TableLimitConfig tableLimitConfig) {
        this.tableLimitConfig = tableLimitConfig;
    }

    @Before("@annotation(annotationName)")
    public void checkFromMethodAnnotation(JoinPoint joinPoint, MaxTableSizeCheck annotationName) {
        var isAllowed = isRequestAllowed();

        if (isAllowed) {
            performCheck(annotationName);
        }
    }

    @Before("@within(annotationName)")
    public void checkFromClassAnnotation(JoinPoint joinPoint, MaxTableSizeCheck annotationName) {
        var isAllowed = isRequestAllowed();

        if (isAllowed) {
            performCheck(annotationName);
        }
    }

    private Optional<String> getRequestMethod() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null)
            return null;

        HttpServletRequest request = attributes.getRequest();
        return Optional.ofNullable(request.getMethod());
    }

    private boolean isRequestAllowed() {
        List<String> allowedMethods = List.of(
                HttpMethod.POST.toString());
        // HttpMethod.GET.toString(),
        // HttpMethod.PUT.toString(),
        // HttpMethod.DELETE.toString()

        var method = getRequestMethod();

        if (method == null || method.isEmpty()) {
            return false;
        }

        return allowedMethods.contains(method.get());
    }

    public void performCheck(MaxTableSizeCheck maxSizeCheck) {
        Class<?> entityClass = maxSizeCheck.entity();
        String name = entityClass.getSimpleName();

        Long count = entityManager.createQuery(
                "SELECT COUNT(e) FROM " + name + " e", Long.class).getSingleResult();

        int limit = tableLimitConfig.getLimit(name);

        if (count >= limit) {
            throw new MaxTableRecordLimitReached("Max record limit (" + limit + ") for entity " + name + " reached.");
        }
    }
}
