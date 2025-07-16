package de.fll.screen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.fll.core.dto.SlideDeckDTO;
import de.fll.screen.assembler.SlideAssembler;
import de.fll.screen.assembler.SlideDeckAssembler;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.service.SlideDeckService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = SlideDeckController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
    }
)
public class SlideDeckControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SlideDeckService slideDeckService;

    @Autowired
    private ObjectMapper objectMapper;

    private SlideDeck slideDeck;

    private SlideDeckDTO slideDeckDTO;

    @MockBean
    private SlideDeckAssembler slideDeckAssembler;

    @MockBean
    private SlideAssembler slideAssembler;

    @BeforeEach
    void setUp() {
        slideDeck = new SlideDeck();
        slideDeck.setName("Test Deck");
        slideDeck.setTransitionTime(5);
        slideDeck.setVersion(1);
        slideDeckDTO = new SlideDeckDTO();
        slideDeckDTO.setId(1L);
        slideDeckDTO.setName("Test Deck");
        slideDeckDTO.setTransitionTime(5);
        slideDeckDTO.setVersion(1);
    }

    @Test
    void testGetAllSlideDecks() throws Exception {
        when(slideDeckService.getAllSlideDecks()).thenReturn(Collections.singletonList(slideDeck));
        when(slideDeckAssembler.toDTO(slideDeck)).thenReturn(slideDeckDTO);
        mockMvc.perform(get("/slidedecks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Deck"));
    }

    @Test
    void testGetSlideDeckById() throws Exception {
        when(slideDeckService.getSlideDeckById(1L)).thenReturn(Optional.of(slideDeck));
        when(slideDeckAssembler.toDTO(slideDeck)).thenReturn(slideDeckDTO);
        mockMvc.perform(get("/slidedecks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Deck"));
    }

    @Test
    void testCreateSlideDeck() throws Exception {
        when(slideDeckAssembler.fromDTO(any(SlideDeckDTO.class))).thenReturn(slideDeck);
        when(slideDeckService.createSlideDeck(any(SlideDeck.class))).thenReturn(slideDeck);
        when(slideDeckAssembler.toDTO(slideDeck)).thenReturn(slideDeckDTO);
        mockMvc.perform(post("/slidedecks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(slideDeckDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Deck"));
    }

    @Test
    void testUpdateSlideDeck() throws Exception {
        when(slideDeckAssembler.fromDTO(any(SlideDeckDTO.class))).thenReturn(slideDeck);
        when(slideDeckService.updateSlideDeck(any(Long.class), any(SlideDeck.class))).thenReturn(slideDeck);
        when(slideDeckService.getSlideDeckById(1L)).thenReturn(Optional.of(slideDeck));
        when(slideDeckAssembler.toDTO(slideDeck)).thenReturn(slideDeckDTO);
        mockMvc.perform(put("/slidedecks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(slideDeckDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Deck"));
    }

    @Test
    void testDeleteSlideDeck() throws Exception {
        when(slideDeckAssembler.toDTO(slideDeck)).thenReturn(slideDeckDTO);
        mockMvc.perform(delete("/slidedecks/1"))
                .andExpect(status().isOk());
    }
} 