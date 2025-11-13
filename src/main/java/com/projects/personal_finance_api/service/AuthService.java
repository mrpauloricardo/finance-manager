package com.projects.personal_finance_api.service;

import java.time.LocalDateTime;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.projects.personal_finance_api.dto.request.LoginRequest;
import com.projects.personal_finance_api.dto.request.RegisterRequest;
import com.projects.personal_finance_api.dto.response.AuthResponse;
import com.projects.personal_finance_api.dto.response.UserResponse;
import com.projects.personal_finance_api.entity.User;
import com.projects.personal_finance_api.exception.BadRequestException;
import com.projects.personal_finance_api.repository.UserRepository;
import com.projects.personal_finance_api.security.JwtUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public UserResponse register(RegisterRequest request) {
        // 1. Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        // 2. Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already in use");
        }

        // 3. Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // 4. Encode password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 5. Set createdAt timestamp
        user.setCreatedAt(LocalDateTime.now());

        // 6. Save user to the database
        User savedUser = userRepository.save(user);

        // 7. Convert User -> UserResponse and returns
        return UserResponse.fromEntity(savedUser);

    }

    public AuthResponse login(LoginRequest request) {

        // 1. Mount authentication token from email and password that came from request
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword());

        // 2. Delegates authentication to AuthenticationManager [launch exception if fails]
        authenticationManager.authenticate(authenticationToken);

        // 3. Find user on database by email [to fill data on response]
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found"));

        // 4. Generate JWT token - username is the subject
        String jwt = jwtUtil.generateToken(user.getUsername());

        // 5. Mounts and returns AuthResponse [contains JWT token and user data]
        return new AuthResponse(jwt, "Bearer", user.getUsername(), user.getEmail());
    }

}
