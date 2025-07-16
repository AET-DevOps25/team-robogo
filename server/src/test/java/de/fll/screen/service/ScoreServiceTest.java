package de.fll.screen.service;

import de.fll.core.dto.CategoryDTO;
import de.fll.core.dto.TeamDTO;
import de.fll.core.dto.ScoreDTO;
import de.fll.core.dto.ScoreSlideDTO;
import de.fll.screen.model.*;
import de.fll.screen.repository.*;
import de.fll.screen.assembler.TeamAssembler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

class ScoreServiceTest {

    @Mock
    private TeamAssembler teamAssembler;
    
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
        // mock save 返回参数本身
        when(scoreRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        // mock count 返回1
        when(scoreRepository.count()).thenReturn(1L);
        // 默认mock fromDTO返回新Team，防止空指针
        when(teamAssembler.fromDTO(any(TeamDTO.class))).thenReturn(new Team());
    }

    @Test
    void testCreateScoreSlideFromDTO_Normal() {
        // 构造 DTO
        ScoreSlideDTO dto = new ScoreSlideDTO();
        dto.setName("slide1");
        dto.setIndex(1);
        dto.setCategory(CategoryDTO.builder().id(100L).build());

        ScoreDTO scoreDTO = new ScoreDTO();
        scoreDTO.setTeam(TeamDTO.builder().id(200L).build());
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
        // mock fromDTO返回正确team
        when(teamAssembler.fromDTO(scoreDTO.getTeam())).thenReturn(team);

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
        dto.setCategory(CategoryDTO.builder().id(999L).build());
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