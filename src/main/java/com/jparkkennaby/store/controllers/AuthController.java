package com.jparkkennaby.store.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jparkkennaby.store.config.JwtConfig;
import com.jparkkennaby.store.dtos.JwtResponse;
import com.jparkkennaby.store.dtos.LoginRequest;
import com.jparkkennaby.store.dtos.UserDto;
import com.jparkkennaby.store.mappers.UserMapper;
import com.jparkkennaby.store.repositories.UserRepository;
import com.jparkkennaby.store.services.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestHeader;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtConfig jwtConfig;

    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
        // auth context is set in the JwtAuthenticationFilter during http request
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal(); // user id set in filter

        // find by id is more efficient than by email
        var user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        var userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);

    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response // provides low level access to the http response
    ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        // exception should never be reached, because the authentication manager will
        // have already thrown an exception if the user doesn't exist or is invalid
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        var cookie = new Cookie("refreshToken", refreshToken.toString());
        cookie.setHttpOnly(true); // cannot be accessed by javascript
        cookie.setPath("/auth/refresh"); // path where the cookie can be sent too
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration()); // cookie will expire after 7 days
        cookie.setSecure(true); // can only be set over https connections
        response.addCookie(cookie); // sets the cookie on the reponse

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(
            @CookieValue(value = "refreshToken") String refreshToken) {

        var jwt = jwtService.parseToken(refreshToken);
        if (jwt == null || jwt.isExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var user = userRepository.findById(jwt.getUserId()).orElseThrow(); // should never happen
        var accessToken = jwtService.generateAccessToken(user);

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }

    // @PostMapping("/validate")
    // public boolean validate(@RequestHeader("Authorization") String authHeader) {
    // // print intentionally committed to show filter functionality (LoggingFilter)
    // System.out.println("Validate called");

    // var token = authHeader.replace("Bearer ", "");
    // return jwtService.validateToken(token);
    // }

    // updates the response status to 401 (instead a 403 forbidden is returned)
    // when the user is not found or the password is incorrect
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
