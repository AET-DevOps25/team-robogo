package de.fll.screen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fll.core.dto.SlideDTO;
import de.fll.core.dto.ScoreSlideDTO;
import de.fll.screen.assembler.SlideAssembler;
import de.fll.screen.model.Slide;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.model.ScoreSlide;
import de.fll.screen.repository.SlideRepository;
import de.fll.screen.repository.SlideDeckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = {SlideController.class, GlobalExceptionHandler.class},
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
    }
)
public class SlideControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SlideRepository slideRepository;

    @MockBean
    private SlideDeckRepository slideDeckRepository;

    @MockBean
    private SlideAssembler slideAssembler;

    @Autowired
    private ObjectMapper objectMapper;

    private Slide slide1;
    private Slide slide2;
    private SlideDTO slideDTO1;
    private SlideDTO slideDTO2;
    private SlideDeck slideDeck;

    @BeforeEach
    void setUp() {
        slideDeck = new SlideDeck();
        setId(slideDeck, 1L);
        slideDeck.setName("Test Deck");

        slide1 = new ScoreSlide();
        setId(slide1, 1L);
        slide1.setName("Test Slide 1");
        slide1.setIndex(0);
        slide1.setSlidedeck(slideDeck);

        slide2 = new ScoreSlide();
        setId(slide2, 2L);
        slide2.setName("Test Slide 2");
        slide2.setIndex(1);
        slide2.setSlidedeck(slideDeck);

        slideDTO1 = new SlideDTO();
        slideDTO1.setId(1L);
        slideDTO1.setName("Test Slide 1");
        slideDTO1.setIndex(0);

        slideDTO2 = new SlideDTO();
        slideDTO2.setId(2L);
        slideDTO2.setName("Test Slide 2");
        slideDTO2.setIndex(1);
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
    void testGetAllSlides() throws Exception {
        List<Slide> slides = Arrays.asList(slide1, slide2);
        when(slideRepository.findAll()).thenReturn(slides);
        when(slideAssembler.toDTO(slide1)).thenReturn(slideDTO1);
        when(slideAssembler.toDTO(slide2)).thenReturn(slideDTO2);

        mockMvc.perform(get("/slides"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Slide 1"))
                .andExpect(jsonPath("$[0].index").value(0))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Test Slide 2"))
                .andExpect(jsonPath("$[1].index").value(1));
    }

    @Test
    void testGetAllSlidesByDeckId() throws Exception {
        List<Slide> slides = Arrays.asList(slide1, slide2);
        when(slideRepository.findBySlidedeck_Id(1L)).thenReturn(slides);
        when(slideAssembler.toDTO(slide1)).thenReturn(slideDTO1);
        when(slideAssembler.toDTO(slide2)).thenReturn(slideDTO2);

        mockMvc.perform(get("/slides?deckId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Slide 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Test Slide 2"));
    }

    @Test
    void testGetSlideById() throws Exception {
        when(slideRepository.findById(1L)).thenReturn(Optional.of(slide1));
        when(slideAssembler.toDTO(slide1)).thenReturn(slideDTO1);

        mockMvc.perform(get("/slides/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Slide 1"))
                .andExpect(jsonPath("$.index").value(0));
    }

    @Test
    void testGetSlideByIdNotFound() throws Exception {
        when(slideRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/slides/999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testCreateSlide() throws Exception {
        Slide newSlide = new ScoreSlide();
        setId(newSlide, 3L);
        newSlide.setName("New Slide");
        newSlide.setIndex(2);
        newSlide.setSlidedeck(slideDeck);

        ScoreSlideDTO newSlideDTO = new ScoreSlideDTO();
        newSlideDTO.setId(3L);
        newSlideDTO.setName("New Slide");
        newSlideDTO.setIndex(2);
        newSlideDTO.setType("SCORE");

        when(slideDeckRepository.findById(1L)).thenReturn(Optional.of(slideDeck));
        when(slideAssembler.fromDTO(newSlideDTO)).thenReturn(newSlide);
        when(slideRepository.save(newSlide)).thenReturn(newSlide);
        when(slideAssembler.toDTO(newSlide)).thenReturn(newSlideDTO);

        mockMvc.perform(post("/slides?deckId=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newSlideDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("New Slide"))
                .andExpect(jsonPath("$.index").value(2));
    }

    @Test
    void testCreateSlideWithInvalidDeckId() throws Exception {
        ScoreSlideDTO newSlideDTO = new ScoreSlideDTO();
        newSlideDTO.setName("New Slide");
        newSlideDTO.setIndex(2);

        when(slideDeckRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/slides?deckId=999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newSlideDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUpdateSlide() throws Exception {
        Slide updatedSlide = new ScoreSlide();
        setId(updatedSlide, 1L);
        updatedSlide.setName("Updated Slide");
        updatedSlide.setIndex(5);
        updatedSlide.setSlidedeck(slideDeck);

        ScoreSlideDTO updatedSlideDTO = new ScoreSlideDTO();
        updatedSlideDTO.setId(1L);
        updatedSlideDTO.setName("Updated Slide");
        updatedSlideDTO.setIndex(5);
        updatedSlideDTO.setType("SCORE");

        when(slideRepository.findById(1L)).thenReturn(Optional.of(slide1));
        when(slideAssembler.fromDTO(updatedSlideDTO)).thenReturn(updatedSlide);
        when(slideRepository.save(updatedSlide)).thenReturn(updatedSlide);
        when(slideAssembler.toDTO(updatedSlide)).thenReturn(updatedSlideDTO);

        mockMvc.perform(put("/slides/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedSlideDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Slide"))
                .andExpect(jsonPath("$.index").value(5));
    }

    @Test
    void testUpdateSlideNotFound() throws Exception {
        ScoreSlideDTO updatedSlideDTO = new ScoreSlideDTO();
        updatedSlideDTO.setName("Updated Slide");
        updatedSlideDTO.setIndex(5);

        when(slideRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/slides/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedSlideDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testDeleteSlide() throws Exception {
        mockMvc.perform(delete("/slides/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllSlidesEmptyResult() throws Exception {
        when(slideRepository.findAll()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/slides"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetAllSlidesByDeckIdEmptyResult() throws Exception {
        when(slideRepository.findBySlidedeck_Id(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/slides?deckId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testCreateSlideWithEmptyName() throws Exception {
        ScoreSlideDTO emptyNameDTO = new ScoreSlideDTO();
        emptyNameDTO.setName("");
        emptyNameDTO.setIndex(0);
        emptyNameDTO.setType("SCORE");

        Slide newSlide = new ScoreSlide();
        setId(newSlide, 3L);
        newSlide.setName("");
        newSlide.setIndex(0);
        newSlide.setSlidedeck(slideDeck);

        when(slideDeckRepository.findById(1L)).thenReturn(Optional.of(slideDeck));
        when(slideAssembler.fromDTO(emptyNameDTO)).thenReturn(newSlide);
        when(slideRepository.save(newSlide)).thenReturn(newSlide);
        when(slideAssembler.toDTO(newSlide)).thenReturn(emptyNameDTO);

        mockMvc.perform(post("/slides?deckId=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyNameDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateSlideWithNegativeIndex() throws Exception {
        ScoreSlideDTO negativeIndexDTO = new ScoreSlideDTO();
        negativeIndexDTO.setName("Test Slide");
        negativeIndexDTO.setIndex(-1);
        negativeIndexDTO.setType("SCORE");

        Slide updatedSlide = new ScoreSlide();
        setId(updatedSlide, 1L);
        updatedSlide.setName("Test Slide");
        updatedSlide.setIndex(-1);
        updatedSlide.setSlidedeck(slideDeck);

        when(slideRepository.findById(1L)).thenReturn(Optional.of(slide1));
        when(slideAssembler.fromDTO(negativeIndexDTO)).thenReturn(updatedSlide);
        when(slideRepository.save(updatedSlide)).thenReturn(updatedSlide);
        when(slideAssembler.toDTO(updatedSlide)).thenReturn(negativeIndexDTO);

        mockMvc.perform(put("/slides/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(negativeIndexDTO)))
                .andExpect(status().isOk());
    }
} 