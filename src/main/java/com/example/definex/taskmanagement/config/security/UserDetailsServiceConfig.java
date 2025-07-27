package com.example.definex.taskmanagement.config.security;

import com.example.definex.taskmanagement.exception.constants.MessageKey;
import com.example.definex.taskmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@RequiredArgsConstructor
public class UserDetailsServiceConfig {
    private final UserRepository userRepository;
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(MessageKey.USER_NOT_FOUND_WITH_EMAIL.getMessage() +email));
    }
}