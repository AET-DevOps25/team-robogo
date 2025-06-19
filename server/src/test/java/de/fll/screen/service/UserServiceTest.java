package de.fll.screen.service;

import de.fll.core.dto.LoginRequestDTO;
import de.fll.core.dto.SignupRequestDTO;
import de.fll.screen.model.User;
import de.fll.screen.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void init_ShouldCreateAdminUser_WhenNoUsersExist() {
        // Arrange
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode("admin")).thenReturn("encodedPassword");

        // Act
        userService.init();

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo("admin");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(savedUser.getEmail()).isEqualTo("admin@fll.de");
    }

    @Test
    void init_ShouldNotCreateAdminUser_WhenUsersExist() {
        // Arrange
        when(userRepository.count()).thenReturn(1L);

        // Act
        userService.init();

        // Assert
        verify(userRepository, never()).save(any());
    }

    @Test
    void signup_ShouldReturnError_WhenUsernameExists() {
        // Arrange
        SignupRequestDTO request = new SignupRequestDTO();
        request.setUsername("existingUser");
        request.setPassword("password");
        request.setEmail("test@example.com");

        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        // Act
        var response = userService.signup(request);

        // Assert
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getError()).isEqualTo("Username already exists");
        verify(userRepository, never()).save(any());
    }

    @Test
    void signup_ShouldReturnError_WhenEmailExists() {
        // Arrange
        SignupRequestDTO request = new SignupRequestDTO();
        request.setUsername("newUser");
        request.setPassword("password");
        request.setEmail("existing@example.com");

        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act
        var response = userService.signup(request);

        // Assert
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getError()).isEqualTo("Email already exists");
        verify(userRepository, never()).save(any());
    }

    @Test
    void signup_ShouldCreateNewUser_WhenValidRequest() {
        // Arrange
        SignupRequestDTO request = new SignupRequestDTO();
        request.setUsername("newUser");
        request.setPassword("password");
        request.setEmail("new@example.com");

        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(jwtService.generateToken("newUser")).thenReturn("jwt-token");
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Act
        var response = userService.signup(request);

        // Assert
        assertThat(response.getSuccess()).isTrue();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUser().getUsername()).isEqualTo("newUser");
        assertThat(response.getUser().getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void login_ShouldReturnError_WhenUserNotFound() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("nonexistent");
        request.setPassword("password");

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        var response = userService.login(request);

        // Assert
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getError()).isEqualTo("Invalid username or password");
    }

    @Test
    void login_ShouldReturnError_WhenPasswordIncorrect() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("user");
        request.setPassword("wrongPassword");

        User user = new User("user", "encodedPassword", "user@example.com");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act
        var response = userService.login(request);

        // Assert
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getError()).isEqualTo("Invalid username or password");
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsValid() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("user");
        request.setPassword("password");

        User user = new User("user", "encodedPassword", "user@example.com");
        user.setId(1L);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken("user")).thenReturn("jwt-token");

        // Act
        var response = userService.login(request);

        // Assert
        assertThat(response.getSuccess()).isTrue();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUser().getUsername()).isEqualTo("user");
        assertThat(response.getUser().getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenUserExists() {
        // Arrange
        User user = new User("user", "password", "user@example.com");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        // Act
        User result = userService.findByUsername("user");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("user");
        assertThat(result.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void findByUsername_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.findByUsername("nonexistent"))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("User not found: nonexistent");
    }
} 