package com.smartmobilehub.auth.service;

import com.smartmobilehub.auth.dto.request.LoginRequest;
import com.smartmobilehub.auth.dto.request.RegisterRequest;
import com.smartmobilehub.auth.dto.request.UpdateProfileRequest;
import com.smartmobilehub.auth.dto.response.AuthResponse;
import com.smartmobilehub.auth.dto.response.UserResponse;
import com.smartmobilehub.auth.entity.Role;
import com.smartmobilehub.auth.entity.User;
import com.smartmobilehub.auth.exception.BusinessException;
import com.smartmobilehub.auth.repository.UserRepository;
import com.smartmobilehub.auth.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                       JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already in use", "EMAIL_EXISTS");
        }

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFirstName(),
                request.getLastName(),
                Role.CUSTOMER // Public registration always creates CUSTOMER
        );

        User savedUser = userRepository.save(user);
        String jwtToken = jwtService.generateToken(savedUser);
        
        return new AuthResponse(jwtToken, mapToUserResponse(savedUser));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // If we reach here, authentication was successful
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("User not found", "USER_NOT_FOUND"));
                
        if (!user.isActive()) {
            throw new BusinessException("Account is disabled", "ACCOUNT_DISABLED");
        }

        String jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken, mapToUserResponse(user));
    }
    
    public UserResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found", "USER_NOT_FOUND"));
        return mapToUserResponse(user);
    }
    
    @Transactional
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found", "USER_NOT_FOUND"));
                
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        
        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.isActive()
        );
    }
}
