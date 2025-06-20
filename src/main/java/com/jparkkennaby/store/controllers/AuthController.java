package com.jparkkennaby.store.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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
import com.jparkkennaby.store.services.AuthService;

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
    private final UserMapper userMapper;
    private final JwtConfig jwtConfig;
    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
        var user = authService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        var userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/login")
    public JwtResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response // provides low level access to the http response
    ) {
        var loginResult = authService.login(request);
        var refreshToken = loginResult.getRefreshToken().toString();
        var cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true); // cannot be accessed by javascript
        cookie.setPath("/auth/refresh"); // path where the cookie can be sent too
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration()); // cookie will expire after 7 days
        cookie.setSecure(true); // can only be set over https connections
        response.addCookie(cookie); // sets the cookie on the reponse

        return new JwtResponse(loginResult.getAccessToken().toString());
    }

    @PostMapping("/refresh")
    public JwtResponse refresh(@CookieValue(value = "refreshToken") String refreshToken) {
        var accessToken = authService.refreshAccessToken(refreshToken);
        return new JwtResponse(accessToken.toString());
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
