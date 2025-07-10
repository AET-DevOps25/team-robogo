package de.fll.screen.service;

import de.fll.core.dto.ScoreDTO;
import de.fll.core.dto.ScoreSlideDTO;
import de.fll.screen.model.*;
import de.fll.screen.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScoreServiceTest {

    @InjectMocks
    private ScoreService scoreService;

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private SlideRepository slideRepository;
    @Mock
    private ScoreRepository scoreRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateScoreSlideFromDTO_Normal() {
        // 构造 DTO
        ScoreSlideDTO dto = new ScoreSlideDTO();
        dto.setName("slide1");
        dto.setIndex(1);
        dto.setCategoryId(100L);

        ScoreDTO scoreDTO = new ScoreDTO();
        scoreDTO.setTeamId(200L);
        scoreDTO.setPoints(50.0);
        scoreDTO.setTime(123);
        dto.setScores(List.of(scoreDTO));

        // Mock category/team
        Category category = new Category();
        category.setId(100L);
        Team team = new Team();
        team.setId(200L);

        when(categoryRepository.findById(100L)).thenReturn(Optional.of(category));
        when(teamRepository.findById(200L)).thenReturn(Optional.of(team));

        // 调用
        ScoreSlide slide = scoreService.createScoreSlideFromDTO(dto);

        // 断言
        assertNotNull(slide);
        assertEquals("slide1", slide.getName());
        assertEquals(1, slide.getIndex());
        assertEquals(category, slide.getCategory());
        assertEquals(1, slide.getScores().size());
        Score score = slide.getScores().get(0);
        assertEquals(team, score.getTeam());
        assertEquals(50, score.getPoints());
        assertEquals(123, score.getTime());

        // 验证保存
        verify(slideRepository).save(slide);
        verify(scoreRepository).save(score);
    }

    @Test
    void testCreateScoreSlideFromDTO_NullCategory() {
        ScoreSlideDTO dto = new ScoreSlideDTO();
        dto.setCategoryId(999L);
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        ScoreSlide slide = scoreService.createScoreSlideFromDTO(dto);
        assertNull(slide.getCategory());
    }

    @Test
    void testCreateScoreSlideFromDTO_NullTeam() {
        ScoreSlideDTO dto = new ScoreSlideDTO();
        dto.setScores(List.of(new ScoreDTO()));
        when(teamRepository.findById(any())).thenReturn(Optional.empty());

        ScoreSlide slide = scoreService.createScoreSlideFromDTO(dto);
        assertTrue(slide.getScores().isEmpty());
    }

    @Test
    void testCreateScoreSlideFromDTO_NullDTO() {
        assertNull(scoreService.createScoreSlideFromDTO(null));
    }
} 