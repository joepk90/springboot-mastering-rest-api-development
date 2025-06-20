package com.jparkkennaby.store.aspects;

import com.jparkkennaby.store.annoations.ValidateJwt;
import com.jparkkennaby.store.dtos.ErrorDto;
import com.jparkkennaby.store.entities.Role;
import com.jparkkennaby.store.services.JwtService;

import java.util.Arrays;
import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

/**
 * Example Usage
 * 
 * @ValidateJwt({ Role.USER })
 */

@Aspect
@Component
public class JwtValidationAspect {

    private final JwtService jwtService;

    public JwtValidationAspect(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Around("@within(validateJwt) || @annotation(validateJwt)")
    public Object validateToken(ProceedingJoinPoint pjp, ValidateJwt validateJwt) throws Throwable {
        HttpServletRequest request = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest)
                .orElse(null);

        if (request == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No request context found");
        }

        String auth = request.getHeader("Authorization");

        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        String token = auth.substring(7);
        if (!jwtService.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT token");
        }

        Role role = jwtService.getRole(token);

        Role[] requiredRoles = validateJwt.value();

        boolean hasRequiredRole = Arrays.asList(requiredRoles).contains(role);

        if (!hasRequiredRole) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorDto("User does not have permission"));
        }

        // Token is valid – proceed
        return pjp.proceed();
    }
}
