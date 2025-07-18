package de.fll.screen.service;

import de.fll.core.dto.LoginRequestDTO;
import de.fll.core.dto.LoginResponseDTO;
import de.fll.core.dto.UserDTO;
import de.fll.screen.model.User;
import de.fll.screen.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private User mockUser;
    private LoginRequestDTO mockLoginRequest;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPassword("encodedpassword");

        mockLoginRequest = new LoginRequestDTO();
        mockLoginRequest.setUsername("testuser");
        mockLoginRequest.setPassword("password");

        // Set default admin credentials
        ReflectionTestUtils.setField(userService, "adminUsername", "admin");
        ReflectionTestUtils.setField(userService, "adminPassword", "admin");
    }

    @Test
    void init_ShouldCreateAdminUser_WhenNoUsersExist() {
        // Arrange
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedadminpassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        userService.init();

        // Assert
        verify(userRepository).count();
        verify(passwordEncoder).encode("admin");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void init_ShouldNotCreateAdminUser_WhenUsersExist() {
        // Arrange
        when(userRepository.count()).thenReturn(1L);

        // Act
        userService.init();

        // Assert
        verify(userRepository).count();
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnSuccessResponse_WhenValidCredentials() {
        // Arrange
        String token = "valid.jwt.token";
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "encodedpassword")).thenReturn(true);
        when(jwtService.generateToken("testuser")).thenReturn(token);

        // Act
        LoginResponseDTO result = userService.login(mockLoginRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(token, result.getToken());
        assertNotNull(result.getUser());
        assertEquals(1L, result.getUser().getId());
        assertEquals("testuser", result.getUser().getUsername());
        assertNull(result.getError());
    }

    @Test
    void login_ShouldReturnFailureResponse_WhenInvalidUsername() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        mockLoginRequest.setUsername("nonexistent");

        // Act
        LoginResponseDTO result = userService.login(mockLoginRequest);

        // Assert
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertNull(result.getToken());
        assertNull(result.getUser());
        assertEquals("Invalid username or password", result.getError());
    }

    @Test
    void login_ShouldReturnFailureResponse_WhenInvalidPassword() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrongpassword", "encodedpassword")).thenReturn(false);
        mockLoginRequest.setPassword("wrongpassword");

        // Act
        LoginResponseDTO result = userService.login(mockLoginRequest);

        // Assert
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertNull(result.getToken());
        assertNull(result.getUser());
        assertEquals("Invalid username or password", result.getError());
    }

    @Test
    void login_ShouldReturnFailureResponse_WhenNullUsername() {
        // Arrange
        mockLoginRequest.setUsername(null);

        // Act
        LoginResponseDTO result = userService.login(mockLoginRequest);

        // Assert
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertNull(result.getToken());
        assertNull(result.getUser());
        assertEquals("Invalid username or password", result.getError());
    }

    @Test
    void login_ShouldReturnFailureResponse_WhenNullPassword() {
        // Arrange
        mockLoginRequest.setPassword(null);

        // Act
        LoginResponseDTO result = userService.login(mockLoginRequest);

        // Assert
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertNull(result.getToken());
        assertNull(result.getUser());
        assertEquals("Invalid username or password", result.getError());
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenUserExists() {
        // Arrange
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Act
        User result = userService.findByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(mockUser, result);
    }

    @Test
    void findByUsername_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.findByUsername(username);
        });
    }

    @Test
    void findByUsername_ShouldThrowException_WhenUsernameIsNull() {
        // Arrange
        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.findByUsername(null);
        });
    }

    @Test
    void login_ShouldHandleEmptyUsername() {
        // Arrange
        mockLoginRequest.setUsername("");

        // Act
        LoginResponseDTO result = userService.login(mockLoginRequest);

        // Assert
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("Invalid username or password", result.getError());
    }

    @Test
    void login_ShouldHandleEmptyPassword() {
        // Arrange
        mockLoginRequest.setPassword("");

        // Act
        LoginResponseDTO result = userService.login(mockLoginRequest);

        // Assert
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("Invalid username or password", result.getError());
    }

    @Test
    void init_ShouldHandleDatabaseException() {
        // Arrange
        when(userRepository.count()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert - should throw exception
        assertThrows(RuntimeException.class, () -> {
            userService.init();
        });
    }

    @Test
    void login_ShouldHandlePasswordEncoderException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenThrow(new RuntimeException("Encoder error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.login(mockLoginRequest);
        });
    }

    @Test
    void login_ShouldHandleJwtServiceException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "encodedpassword")).thenReturn(true);
        when(jwtService.generateToken("testuser")).thenThrow(new RuntimeException("JWT error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.login(mockLoginRequest);
        });
    }
} 