package com.jparkkennaby.store.filters;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Example filter:
 * Output of the filter (in the terminal):
 * Request: /auth/validate
 * Validate called
 * Response: 200
 */

@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // On request, log the request URI
        System.out.println("Request: " + request.getRequestURI());

        // Do Controller logic (any prints in the controller will be printed here)
        filterChain.doFilter(request, response);

        // On respnse, log the status
        System.out.println("Response: " + response.getStatus());
    }
}
