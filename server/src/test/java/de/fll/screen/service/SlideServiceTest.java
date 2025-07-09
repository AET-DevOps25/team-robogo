package de.fll.screen.service;

import de.fll.screen.model.*;
import de.fll.core.dto.SlideDisplayDTO;
import de.fll.core.dto.ScoreDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SlideServiceTest {

    @InjectMocks
    private SlideService slideService;

    @Mock
    private ScoreService scoreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAssembleSlideDisplay_ImageSlide() {
        ImageSlide imageSlide = new ImageSlide();
        imageSlide.setName("Image1");
        SlideImageMeta meta = new SlideImageMeta();
        meta.setName("img.png");
        meta.setContentType("image/png");
        imageSlide.setImageMeta(meta);

        SlideDisplayDTO dto = slideService.assembleSlideDisplay(imageSlide, null);
        assertEquals("Image1", dto.getName());
        assertEquals("image", dto.getType());
        assertNotNull(dto.getImageMeta());
        assertEquals("img.png", dto.getImageMeta().getName());
        assertEquals("image/png", dto.getImageMeta().getContentType());
        assertNull(dto.getScores());
    }

    @Test
    void testAssembleSlideDisplay_ScoreSlide() {
        ScoreSlide scoreSlide = new ScoreSlide();
        scoreSlide.setName("Score1");
        List<Score> allScores = new ArrayList<>();
        Score score = new Score(99.0, 123);
        Team team = new Team("T1");
        Category category = new Category();
        team.setCategory(category);
        score.setTeam(team);
        allScores.add(score);
        List<ScoreDTO> mockScoreDTOs = List.of(ScoreDTO.builder().points(99.0).time(123).highlight(true).build());
        when(scoreService.getAllTeamsScoreDTOsWithHighlight(anyList(), eq(category))).thenReturn(mockScoreDTOs);

        SlideDisplayDTO dto = slideService.assembleSlideDisplay(scoreSlide, allScores);
        assertEquals("Score1", dto.getName());
        assertEquals("score", dto.getType());
        assertNull(dto.getImageMeta());
        assertNotNull(dto.getScores());
        assertEquals(1, dto.getScores().size());
        assertEquals(99.0, dto.getScores().get(0).getPoints());
        assertEquals(123, dto.getScores().get(0).getTime());
        assertTrue(dto.getScores().get(0).getHighlight());
    }
} 