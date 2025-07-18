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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Management", description = "User authentication and session management APIs")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Validates user credentials and returns JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful", 
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad request parameters"),
        @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    public ResponseEntity<LoginResponseDTO> login(
            @Parameter(description = "Login request information", required = true)
            @RequestBody LoginRequestDTO request) {
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
    @Operation(summary = "Validate Session", description = "Validates JWT token and returns current user session information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Session validation successful", 
                    content = @Content(schema = @Schema(implementation = SessionResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Invalid or expired token")
    })
    public ResponseEntity<SessionResponseDTO> session(
            @Parameter(description = "JWT authentication token", required = false)
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
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
    @Operation(summary = "User Logout", description = "User logout operation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout successful")
    })
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