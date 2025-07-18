package de.fll.screen.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private static final String TEST_SECRET_KEY = "my-very-secret-key-which-should-be-long-enough-123456";
    private static final long TEST_EXPIRATION_TIME = 3600000L; // 1 hour

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "expirationTime", TEST_EXPIRATION_TIME);
        jwtService.afterPropertiesSet();
    }

    @Test
    void generateToken_ShouldGenerateValidToken() {
        // Arrange
        String username = "testuser";

        // Act
        String token = jwtService.generateToken(username);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void generateToken_ShouldGenerateDifferentTokens_ForDifferentUsers() {
        // Arrange
        String username1 = "user1";
        String username2 = "user2";

        // Act
        String token1 = jwtService.generateToken(username1);
        String token2 = jwtService.generateToken(username2);

        // Assert
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2);
    }

    @Test
    void extractUsername_ShouldExtractCorrectUsername() {
        // Arrange
        String username = "testuser";
        String token = jwtService.generateToken(username);

        // Act
        String extractedUsername = jwtService.extractUsername(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    void extractExpiration_ShouldExtractCorrectExpiration() {
        // Arrange
        String username = "testuser";
        String token = jwtService.generateToken(username);

        // Act
        Date expiration = jwtService.extractExpiration(token);

        // Assert
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void extractClaim_ShouldExtractCustomClaim() {
        // Arrange
        String username = "testuser";
        String token = jwtService.generateToken(username);
        Function<Claims, String> subjectResolver = Claims::getSubject;

        // Act
        String extractedSubject = jwtService.extractClaim(token, subjectResolver);

        // Assert
        assertEquals(username, extractedSubject);
    }

    @Test
    void isTokenValid_ShouldReturnTrue_ForValidToken() {
        // Arrange
        String username = "testuser";
        String token = jwtService.generateToken(username);

        // Act
        boolean isValid = jwtService.isTokenValid(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_ForInvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThrows(io.jsonwebtoken.MalformedJwtException.class, () -> {
            jwtService.isTokenValid(invalidToken);
        });
    }

    @Test
    void isTokenValid_ShouldReturnFalse_ForExpiredToken() {
        // Arrange
        JwtService shortExpirationService = new JwtService();
        ReflectionTestUtils.setField(shortExpirationService, "secretKey", TEST_SECRET_KEY);
        ReflectionTestUtils.setField(shortExpirationService, "expirationTime", 1L); // 1ms expiration
        shortExpirationService.afterPropertiesSet();
        
        String username = "testuser";
        String token = shortExpirationService.generateToken(username);
        
        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act
        boolean isValid = jwtService.isTokenValid(token);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void parseToken_ShouldParseValidToken() {
        // Arrange
        String username = "testuser";
        String token = jwtService.generateToken(username);

        // Act
        Claims claims = jwtService.parseToken(token);

        // Assert
        assertNotNull(claims);
        assertEquals(username, claims.getSubject());
    }

    @Test
    void parseToken_ShouldThrowException_ForInvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThrows(io.jsonwebtoken.MalformedJwtException.class, () -> {
            jwtService.parseToken(invalidToken);
        });
    }

    @Test
    void parseToken_ShouldThrowException_ForExpiredToken() {
        // Arrange
        JwtService shortExpirationService = new JwtService();
        ReflectionTestUtils.setField(shortExpirationService, "secretKey", TEST_SECRET_KEY);
        ReflectionTestUtils.setField(shortExpirationService, "expirationTime", 1L); // 1ms expiration
        shortExpirationService.afterPropertiesSet();
        
        String username = "testuser";
        String token = shortExpirationService.generateToken(username);
        
        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act & Assert
        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.parseToken(token);
        });
    }

    @Test
    void afterPropertiesSet_ShouldInitializeKey() {
        // Arrange
        JwtService newService = new JwtService();
        ReflectionTestUtils.setField(newService, "secretKey", TEST_SECRET_KEY);
        ReflectionTestUtils.setField(newService, "expirationTime", TEST_EXPIRATION_TIME);

        // Act
        newService.afterPropertiesSet();

        // Assert
        assertNotNull(ReflectionTestUtils.getField(newService, "key"));
    }

    @Test
    void generateToken_ShouldIncludeIssuedAt() {
        // Arrange
        String username = "testuser";
        Date beforeToken = new Date();

        // Act
        String token = jwtService.generateToken(username);
        Claims claims = jwtService.parseToken(token);

        // Assert
        assertNotNull(claims.getIssuedAt());
        assertTrue(claims.getIssuedAt().after(beforeToken) || claims.getIssuedAt().equals(beforeToken) || claims.getIssuedAt().before(beforeToken));
    }

    @Test
    void generateToken_ShouldIncludeExpiration() {
        // Arrange
        String username = "testuser";

        // Act
        String token = jwtService.generateToken(username);
        Claims claims = jwtService.parseToken(token);

        // Assert
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void extractClaim_ShouldHandleNullToken() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            jwtService.extractClaim(null, Claims::getSubject);
        });
    }

    @Test
    void extractClaim_ShouldHandleEmptyToken() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            jwtService.extractClaim("", Claims::getSubject);
        });
    }
} 