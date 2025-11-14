package com.projects.personal_finance_api.security;

import com.projects.personal_finance_api.repository.UserRepository;
import com.projects.personal_finance_api.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        // Converte a entidade User para o tipo UserDetails do Spring
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername()) // ou user.getUsername(), se for o campo principal
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name()) // ajuste depois conforme roles reais
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
