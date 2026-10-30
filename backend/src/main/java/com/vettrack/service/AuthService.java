package com.vettrack.service;

import com.vettrack.domain.User;
import com.vettrack.dto.request.LoginRequest;
import com.vettrack.dto.response.AuthResponse;
import com.vettrack.repository.UserRepository;
import com.vettrack.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService,
                       UserRepository userRepository,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Attempting authentication for user: {}", request.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        String jwt = jwtService.generateToken(userDetails, user.getRole());
        log.info("User {} successfully authenticated with role {}", user.getUsername(), user.getRole());

        return new AuthResponse(jwt, user.getUsername(), user.getRole(), jwtService.getExpirationMs());
    }
}
