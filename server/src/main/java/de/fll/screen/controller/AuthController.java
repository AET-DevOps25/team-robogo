package de.fll.screen.controller;

// Import protobuf generated request/response types
import de.fll.core.proto.Auth.LoginRequest;
import de.fll.core.proto.Auth.LoginResponse;
import de.fll.core.proto.Auth.SessionResponse;
import de.fll.core.proto.UserOuterClass.User;
// JWT utilities
import de.fll.screen.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// REST controller for handling authentication-related requests
@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    // JWT utility for token generation and validation
    private final JwtUtil jwtUtil;

    // Constructor injection of JwtUtil
    private AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // Login endpoint
    // consumes: Accepts protobuf request formats
    // produces: Supports protobuf response formats
    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        logger.debug("Received login request for user: {}", request.getUsername());
        
        // Validate username and password are not empty
        if (request.getUsername() == null || request.getPassword() == null || 
            request.getUsername().isEmpty() || request.getPassword().isEmpty()) {
            logger.warn("Login failed: username or password is empty");
            return ResponseEntity.badRequest().body(LoginResponse.newBuilder()
                .setSuccess(false)
                .setError("Username or password is empty")
                .build());
        }

        // TODO: Validate username and password

        // Generate JWT token
        String token = jwtUtil.generateToken(request.getUsername());
        logger.debug("Generated token for user: {}", request.getUsername());

        // Build user information
        User user = User.newBuilder()
            .setId(1)
            .setUsername(request.getUsername())
            .build();

        // Construct login response
        LoginResponse response = LoginResponse.newBuilder()
            .setSuccess(true)
            .setToken(token)
            .setUser(user)
            .build();
        
        logger.debug("Login successful for user: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }

    // Endpoint to retrieve user information
    @GetMapping("/user")
    public ResponseEntity<User> user(@RequestHeader("Authorization") String authHeader) {
        // Extract token from request header
        String token = extractToken(authHeader);
        if (token == null) {
            return ResponseEntity.status(401).build();
        }

        // Parse token to get user information
        Claims claims = jwtUtil.parseToken(token);
        String username = claims.getSubject();

        // Build and return user information
        User user = User.newBuilder()
            .setId(1)
            .setUsername(username)
            .build();

        return ResponseEntity.ok(user);
    }

    // Endpoint to validate session status
    @GetMapping("/session")
    public ResponseEntity<SessionResponse> session(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        // Check if Authorization header exists and has correct format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(SessionResponse.newBuilder()
                .setValid(false)
                .build());
        }

        // Extract and validate token
        String token = extractToken(authHeader);
        try {
            Claims claims = jwtUtil.parseToken(token);
            String username = claims.getSubject();

            // Build user information
            User user = User.newBuilder()
                .setId(1)
                .setUsername(username)
                .build();

            // Return valid session response
            SessionResponse response = SessionResponse.newBuilder()
                .setValid(true)
                .setUser(user)
                .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Return error response for invalid token
            return ResponseEntity.ok(SessionResponse.newBuilder()
                .setValid(false)
                .setError("Invalid token")
                .build());
        }
    }

    // Logout endpoint
    @PostMapping("/logout")
    public ResponseEntity<LoginResponse> logout() {
        // Simply return success response, actual token invalidation should be handled by client
        return ResponseEntity.ok(LoginResponse.newBuilder()
            .setSuccess(true)
            .build());
    }

    // Helper method to extract token from Authorization header
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
}
