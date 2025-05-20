package com.jparkkennaby.store.filters;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jparkkennaby.store.services.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

/**
 * JwtAuthenticationFilter
 * 
 * - Boilerplate code
 * - Used to filter requests and authenticate
 * - Injected as one of the first filters in the SecurityConfig class
 */

@AllArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var authHeader = request.getHeader("Authorization");

        // if no Authorization header provided, call the next filter and return
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        var token = authHeader.replace("Bearer ", "");
        var jwt = jwtService.parseToken(token);

        // if the token is invalid, call the next filter and return
        if (jwt == null || jwt.isExpired()) {
            filterChain.doFilter(request, response);
            return;
        }

        // else authenticate user (boilerplate)

        /**
         * UsernamePasswordAuthenticationToken
         * 
         * Has two constuctors:
         * - first constructore is for the unauthenticated users.
         * - second construcutor is for authenticated users and takes a third
         * authorities argument
         */

        var authentication = new UsernamePasswordAuthenticationToken(
                jwt.getUserId(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + jwt.getRole()))); // ROLE_ prefix is required

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)); // add additional metadata

        // security context holder stores info about the current authenticated user
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

}
