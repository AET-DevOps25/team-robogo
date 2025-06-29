package de.fll.screen.service;

import de.fll.core.dto.LoginRequestDTO;
import de.fll.core.dto.LoginResponseDTO;
import de.fll.core.dto.UserDTO;
import de.fll.screen.model.User;
import de.fll.screen.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    @Value("${admin.username:admin}")
    private String adminUsername;
    @Value("${admin.password:admin}")
    private String adminPassword;
    @Value("${admin.email:admin@fll.de}")
    private String adminEmail;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                      JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            User admin = new User(adminUsername, passwordEncoder.encode(adminPassword), adminEmail);
            admin.setEmailVerified(true);
            userRepository.save(admin);
        }
    }

    @Transactional
    public boolean verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
            .orElse(null);

        if (user == null || 
            user.getVerificationTokenExpiry().isBefore(LocalDateTime.now()) || 
            user.isEmailVerified()) {
            return false;
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
        return true;
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO request) {
        return userRepository.findByUsername(request.getUsername())
            .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
            .map(user -> {
                // Check if email is verified
                if (!user.isEmailVerified()) {
                    return LoginResponseDTO.builder()
                        .success(false)
                        .error("Please verify your email address before logging in")
                        .build();
                }

                String token = jwtService.generateToken(user.getUsername());
                return LoginResponseDTO.builder()
                    .success(true)
                    .token(token)
                    .user(UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .build())
                    .build();
            })
            .orElse(LoginResponseDTO.builder()
                .success(false)
                .error("Invalid username or password")
                .build());
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
} 