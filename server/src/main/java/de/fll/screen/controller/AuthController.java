package de.fll.screen.controller;

import de.fll.screen.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    @Autowired
    private AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        // TODO: 这里应校验用户名密码，示例直接通过
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "用户名或密码不能为空"));
        }
        // 生成token
        String token = jwtUtil.generateToken(username);
        Map<String, Object> user = Map.of("id", 1, "username", username);
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("token", token);
        return ResponseEntity.ok(result);
    }

    // 获取当前用户信息
    @GetMapping("/user")
    public ResponseEntity<?> user(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        if (token == null) {
            return ResponseEntity.status(401).body(Map.of("error", "未登录"));
        }
        Claims claims = jwtUtil.parseToken(token);
        String username = claims.getSubject();
        return ResponseEntity.ok(Map.of("id", 1, "username", username));
    }

    // 获取当前 session 状态
    @GetMapping("/session")
    public ResponseEntity<?> session(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(Map.of("user", null));
        }
        String token = extractToken(authHeader);
        try {
            Claims claims = jwtUtil.parseToken(token);
            String username = claims.getSubject();
            return ResponseEntity.ok(Map.of("user", Map.of("id", 1, "username", username)));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("user", null));
        }
    }

    // 登出接口（JWT 无状态，前端清除 token 即可）
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("ok", true));
    }

    // 工具方法：提取 Bearer token
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
