package com.example.definex.taskmanagement.service.impl;

import com.example.definex.taskmanagement.config.security.JwtService;
import com.example.definex.taskmanagement.dto.request.LoginRequest;
import com.example.definex.taskmanagement.dto.request.RegisterRequest;
import com.example.definex.taskmanagement.dto.response.AuthResponse;
import com.example.definex.taskmanagement.entities.User;
import com.example.definex.taskmanagement.exception.UserEmailAlreadyExistsException;
import com.example.definex.taskmanagement.exception.constants.MessageKey;
import com.example.definex.taskmanagement.repository.UserRepository;
import com.example.definex.taskmanagement.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    public AuthResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();

        String jwt = jwtService.generateToken(user);

        return new AuthResponse(jwt, user.getUsername(), user.getRole());
    }
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserEmailAlreadyExistsException(MessageKey.THIS_EMAIL_ALREADY_EXISTS.toString());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepository.save(user);

        String jwt = jwtService.generateToken(user);

        return new AuthResponse(jwt, user.getUsername(), user.getRole());
    }
}
