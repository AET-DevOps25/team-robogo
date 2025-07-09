package de.fll.screen.controller;

import de.fll.core.dto.LoginRequestDTO;
import de.fll.core.dto.LoginResponseDTO;
import de.fll.core.dto.SessionResponseDTO;
import de.fll.core.dto.UserDTO;
import de.fll.screen.model.User;
import de.fll.screen.service.UserService;
import de.fll.screen.service.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        logger.debug("Received login request for user: {}", request.getUsername());
        
        if (request.getUsername() == null || request.getPassword() == null || 
            request.getUsername().isEmpty() || request.getPassword().isEmpty()) {
            logger.warn("Login failed: username or password is empty");
            return ResponseEntity.badRequest().body(LoginResponseDTO.builder()
                .success(false)
                .error("Username or password is empty")
                .build());
        }

        LoginResponseDTO response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/session")
    public ResponseEntity<SessionResponseDTO> session(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(SessionResponseDTO.builder()
                .valid(false)
                .build());
        }

        String token = extractToken(authHeader);
        try {
            Claims claims = jwtService.parseToken(token);
            String username = claims.getSubject();
            User user = userService.findByUsername(username);

            return ResponseEntity.ok(SessionResponseDTO.builder()
                .valid(true)
                .user(UserDTO.builder()
                    .id(user.getId())
                    .username(username)
                    .build())
                .build());
        } catch (Exception e) {
            logger.warn("Invalid session token", e);
            return ResponseEntity.ok(SessionResponseDTO.builder()
                .valid(false)
                .error("Invalid token")
                .build());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LoginResponseDTO> logout() {
        return ResponseEntity.ok(LoginResponseDTO.builder()
            .success(true)
            .build());
    }


    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}