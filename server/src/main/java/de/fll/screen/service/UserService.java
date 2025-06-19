package de.fll.screen.service;

import de.fll.core.dto.LoginRequestDTO;
import de.fll.core.dto.LoginResponseDTO;
import de.fll.core.dto.SignupRequestDTO;
import de.fll.core.dto.UserDTO;
import de.fll.screen.model.User;
import de.fll.screen.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            User admin = new User("admin", passwordEncoder.encode("admin"), "admin@fll.de");
            userRepository.save(admin);
        }
    }

    @Transactional
    public LoginResponseDTO signup(SignupRequestDTO request) {
        // check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            return LoginResponseDTO.builder()
                .success(false)
                .error("Username already exists")
                .build();
        }

        // check if email already exists
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            return LoginResponseDTO.builder()
                .success(false)
                .error("Email already exists")
                .build();
        }

        // create new user
        User user = new User(request.getUsername(), passwordEncoder.encode(request.getPassword()), request.getEmail());
        user = userRepository.save(user);

        // generate JWT token
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
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO request) {
        return userRepository.findByUsername(request.getUsername())
            .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
            .map(user -> {
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