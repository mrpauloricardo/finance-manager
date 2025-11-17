package com.projects.personal_finance_api.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.projects.personal_finance_api.dto.request.LoginRequest;
import com.projects.personal_finance_api.dto.request.RegisterRequest;
import com.projects.personal_finance_api.dto.response.AuthResponse;
import com.projects.personal_finance_api.dto.response.UserResponse;
import com.projects.personal_finance_api.entity.Category;
import com.projects.personal_finance_api.entity.CategoryType;
import com.projects.personal_finance_api.entity.Roles;
import com.projects.personal_finance_api.entity.User;
import com.projects.personal_finance_api.exception.BadRequestException;
import com.projects.personal_finance_api.repository.CategoryRepository;
import com.projects.personal_finance_api.repository.UserRepository;
import com.projects.personal_finance_api.security.JwtUtil;

@Service
public class AuthService {

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtUtil jwtUtil,
            CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.categoryRepository = categoryRepository;
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

        // 5. Set 'USER' role
        user.setRole(Roles.USER);

        // 6. Set createdAt timestamp
        user.setCreatedAt(LocalDateTime.now());

        // 7. Save user to the database
        User savedUser = userRepository.save(user);

        // 8. Create default categories for this new user
        createDefaultCategoriesForUser(savedUser);

        // 9. Return UserResponse
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

    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));

        return UserResponse.fromEntity(user);
    }

    @SuppressWarnings("null")
    public void createDefaultCategoriesForUser(User user) {
        List<Category> defaults = List.of(
                new Category("Salário", CategoryType.INCOME, user),
                new Category("Freelance", CategoryType.INCOME, user),
                new Category("Investimentos", CategoryType.INCOME, user),
                new Category("Outros", CategoryType.INCOME, user),

                new Category("Alimentação", CategoryType.EXPENSE, user),
                new Category("Aluguel", CategoryType.EXPENSE, user),
                new Category("Transporte", CategoryType.EXPENSE, user),
                new Category("Água", CategoryType.EXPENSE, user),
                new Category("Luz", CategoryType.EXPENSE, user),
                new Category("Gás", CategoryType.EXPENSE, user),
                new Category("Internet", CategoryType.EXPENSE, user),
                new Category("Farmácia", CategoryType.EXPENSE, user),
                new Category("Lazer", CategoryType.EXPENSE, user),
                new Category("Assinaturas", CategoryType.EXPENSE, user));

        categoryRepository.saveAll(defaults);
    }

}
