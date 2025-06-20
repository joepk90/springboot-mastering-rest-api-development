package com.jparkkennaby.store.aspects;

import com.jparkkennaby.store.annoations.CheckOwnership;
import com.jparkkennaby.store.dtos.ErrorDto;
import com.jparkkennaby.store.services.JwtService;

import java.lang.reflect.Method;
import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;

/**
 * CheckOwnershipAspect (Proof of Concept)
 * 
 * - It is probably better to handle this directly in the controller or service.
 * - This will cause a double query due to the entityManager lookup (find).
 * - Warning: This annoation has not been tested!
 * 
 * Example Usage:
 * 
 * @CheckOwnership(entity = Profile.class)
 * 
 */

@Aspect
@Component
public class CheckOwnershipAspect {

    private final JwtService jwtService;

    @PersistenceContext
    private EntityManager entityManager;

    public CheckOwnershipAspect(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Around("@within(checkOwnership) || @annotation(checkOwnership)")
    public Object checkOwnership(ProceedingJoinPoint pjp, CheckOwnership checkOwnership) throws Throwable {
        HttpServletRequest request = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest)
                .orElse(null);

        if (request == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorDto("No request context found"));
        }

        String auth = request.getHeader("Authorization");

        if (auth == null || !auth.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorDto("Missing or invalid Authorization header"));
        }

        String token = auth.substring(7);
        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorDto("Invalid JWT token"));
        }

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        CheckOwnership annotation = method.getAnnotation(CheckOwnership.class);

        Class<?> entityClass = annotation.entity();

        // Get the ID argument (assumes it's the first parameter and type Long)
        Object[] args = pjp.getArgs();
        if (args.length == 0 || !(args[0] instanceof Long entityId)) {
            throw new IllegalArgumentException("Missing or invalid ID argument.");
        }

        // Fetch entity dynamically
        Object entity = entityManager.find(entityClass, entityId);
        if (entity == null) {
            throw new IllegalArgumentException("Entity not found.");
        }

        var userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Assume entity has a `getUser().getId()` method
        Method getUserMethod = entityClass.getMethod("getUser");
        Object user = getUserMethod.invoke(entity);
        Method getUserIdMethod = user.getClass().getMethod("getId");
        Long ownerId = (Long) getUserIdMethod.invoke(user);

        if (!userId.equals(ownerId)) {
            throw new IllegalAccessException("You do not own this " + entityClass.getSimpleName());
        }

        return pjp.proceed();
    }
}
