package de.fll.screen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fll.screen.model.Screen;
import de.fll.screen.model.ScreenStatus;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.service.ScreenService;
import de.fll.screen.assembler.ScreenContentAssembler;
import de.fll.core.dto.ScreenContentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = ScreenController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
    }
)
public class ScreenControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScreenService screenService;

    @MockBean
    private ScreenContentAssembler screenContentAssembler;

    @Autowired
    private ObjectMapper objectMapper;

    private Screen screen;
    private SlideDeck slideDeck;

    @BeforeEach
    void setUp() {
        slideDeck = new SlideDeck();
        setId(slideDeck, 2L);
        slideDeck.setName("Deck");
        screen = new Screen();
        screen.setName("Screen1");
        screen.setStatus(ScreenStatus.ONLINE);
        setId(screen, 1L);
        screen.setSlideDeck(slideDeck);
    }

    private void setId(Object entity, Long id) {
        try {
            java.lang.reflect.Field field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            if (field.getType() == long.class) {
                field.setLong(entity, id);
            } else {
                field.set(entity, id);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetAllScreens() throws Exception {
        when(screenService.getAllScreens()).thenReturn(Collections.singletonList(screen));
        mockMvc.perform(get("/screens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Screen1"));
    }

    @Test
    void testGetScreenById() throws Exception {
        when(screenService.getScreenById(1L)).thenReturn(Optional.of(screen));
        mockMvc.perform(get("/screens/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Screen1"));
    }

    @Test
    void testCreateScreen() throws Exception {
        when(screenService.createScreen(any(Screen.class))).thenReturn(screen);
        mockMvc.perform(post("/screens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(screen)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Screen1"));
    }

    @Test
    void testUpdateScreen() throws Exception {
        when(screenService.updateScreen(eq(1L), any(Screen.class))).thenReturn(screen);
        mockMvc.perform(put("/screens/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(screen)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Screen1"));
    }

    @Test
    void testDeleteScreen() throws Exception {
        mockMvc.perform(delete("/screens/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testAssignSlideDeck() throws Exception {
        when(screenService.assignSlideDeck(1L, 2L)).thenReturn(screen);
        mockMvc.perform(post("/screens/1/assign-slide-deck/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slideDeck.name").value("Deck"));
    }

    @Test
    void testUpdateScreenStatus() throws Exception {
        when(screenService.updateScreenStatus(1L, ScreenStatus.OFFLINE)).thenReturn(screen);
        mockMvc.perform(put("/screens/1/status?status=OFFLINE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Screen1"));
    }

    @Test
    void testGetScreenContent() throws Exception {
        ScreenContentDTO dto = new ScreenContentDTO();
        dto.setId(1L);
        dto.setName("Screen1");
        dto.setStatus("ONLINE");
        when(screenService.getScreenById(1L)).thenReturn(Optional.of(screen));
        when(screenContentAssembler.toDTO(screen)).thenReturn(dto);
        mockMvc.perform(get("/screens/1/content"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Screen1"));
    }
} 