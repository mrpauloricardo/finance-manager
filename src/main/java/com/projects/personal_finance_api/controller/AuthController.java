package com.projects.personal_finance_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.projects.personal_finance_api.dto.request.LoginRequest;
import com.projects.personal_finance_api.dto.request.RegisterRequest;
import com.projects.personal_finance_api.dto.response.AuthResponse;
import com.projects.personal_finance_api.dto.response.UserResponse;
import com.projects.personal_finance_api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Endpoint POST /auth/register
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterRequest request) {

        UserResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint POST /auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // Endpoint GET /auth/me
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {

        // 1. Gets the current authenticated user
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 2. Finds the user by username on database
        UserResponse response = authService.getCurrentUser(username);

        // 3. Mounts and returns the UserResponse DTO
        return ResponseEntity.ok(response);
    }

}
