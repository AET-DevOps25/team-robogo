package de.fll.screen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fll.core.dto.LoginRequestDTO;
import de.fll.core.dto.LoginResponseDTO;
import de.fll.core.dto.SessionResponseDTO;
import de.fll.core.dto.UserDTO;
import de.fll.screen.model.User;
import de.fll.screen.service.UserService;
import de.fll.screen.service.JwtService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = {AuthController.class, GlobalExceptionHandler.class},
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
    }
)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequestDTO loginRequest;
    private LoginResponseDTO loginResponse;
    private User user;
    private Claims claims;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        loginResponse = LoginResponseDTO.builder()
            .success(true)
            .token("test-token")
            .build();

        user = new User("testuser", "password");
        setId(user, 1L);

        claims = org.mockito.Mockito.mock(Claims.class);
        when(claims.getSubject()).thenReturn("testuser");
    }

    private void setId(Object entity, Long id) {
        try {
            // 首先尝试在当前类中查找 id 字段
            java.lang.reflect.Field field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            setFieldValue(field, entity, id);
        } catch (NoSuchFieldException e) {
            // 如果当前类没有 id 字段，尝试在父类中查找
            try {
                java.lang.reflect.Field field = entity.getClass().getSuperclass().getDeclaredField("id");
                field.setAccessible(true);
                setFieldValue(field, entity, id);
            } catch (Exception ex) {
                throw new RuntimeException("Cannot set id field on " + entity.getClass().getName(), ex);
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot set id field on " + entity.getClass().getName(), e);
        }
    }

    private void setFieldValue(java.lang.reflect.Field field, Object entity, Long id) throws IllegalAccessException {
        if (field.getType() == long.class) {
            field.setLong(entity, id);
        } else if (field.getType() == Long.class) {
            field.set(entity, id);
        } else if (field.getType() == int.class) {
            field.setInt(entity, id.intValue());
        } else if (field.getType() == Integer.class) {
            field.set(entity, id.intValue());
        } else {
            field.set(entity, id);
        }
    }

    @Test
    void testLoginSuccess() throws Exception {
        when(userService.login(any(LoginRequestDTO.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("test-token"));
    }

    @Test
    void testLoginWithEmptyUsername() throws Exception {
        loginRequest.setUsername("");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Username or password is empty"));
    }

    @Test
    void testLoginWithEmptyPassword() throws Exception {
        loginRequest.setPassword("");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Username or password is empty"));
    }

    @Test
    void testLoginWithNullUsername() throws Exception {
        loginRequest.setUsername(null);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Username or password is empty"));
    }

    @Test
    void testLoginWithNullPassword() throws Exception {
        loginRequest.setPassword(null);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Username or password is empty"));
    }

    @Test
    void testSessionWithValidToken() throws Exception {
        when(jwtService.parseToken("valid-token")).thenReturn(claims);
        when(userService.findByUsername("testuser")).thenReturn(user);

        mockMvc.perform(get("/auth/session")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andExpect(jsonPath("$.user.id").value(1));
    }

    @Test
    void testSessionWithInvalidToken() throws Exception {
        when(jwtService.parseToken("invalid-token")).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(get("/auth/session")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.error").value("Invalid token"));
    }

    @Test
    void testSessionWithoutToken() throws Exception {
        mockMvc.perform(get("/auth/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    void testSessionWithInvalidAuthHeader() throws Exception {
        mockMvc.perform(get("/auth/session")
                .header("Authorization", "InvalidHeader"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    void testLogout() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
} 