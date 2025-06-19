package de.fll.screen.controller;

import de.fll.core.proto.Auth.LoginRequest;
import de.fll.core.proto.Auth.LoginResponse;
import de.fll.core.proto.Auth.SessionResponse;
import de.fll.core.proto.UserOuterClass.User;
import de.fll.screen.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    private AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        if (request.getUsername().isEmpty() || request.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body(LoginResponse.newBuilder()
                .setSuccess(false)
                .setError("Username or password is empty")
                .build());
        }

        // TODO: 这里应校验用户名密码，示例直接通过
        String token = jwtUtil.generateToken(request.getUsername());
        
        User user = User.newBuilder()
            .setId(1)
            .setUsername(request.getUsername())
            .build();

        LoginResponse response = LoginResponse.newBuilder()
            .setSuccess(true)
            .setToken(token)
            .setUser(user)
            .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<User> user(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        if (token == null) {
            return ResponseEntity.status(401).build();
        }

        Claims claims = jwtUtil.parseToken(token);
        String username = claims.getSubject();

        User user = User.newBuilder()
            .setId(1)
            .setUsername(username)
            .build();

        return ResponseEntity.ok(user);
    }

    @GetMapping("/session")
    public ResponseEntity<SessionResponse> session(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(SessionResponse.newBuilder()
                .setValid(false)
                .build());
        }

        String token = extractToken(authHeader);
        try {
            Claims claims = jwtUtil.parseToken(token);
            String username = claims.getSubject();

            User user = User.newBuilder()
                .setId(1)
                .setUsername(username)
                .build();

            SessionResponse response = SessionResponse.newBuilder()
                .setValid(true)
                .setUser(user)
                .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(SessionResponse.newBuilder()
                .setValid(false)
                .setError("Invalid token")
                .build());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LoginResponse> logout() {
        return ResponseEntity.ok(LoginResponse.newBuilder()
            .setSuccess(true)
            .build());
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
