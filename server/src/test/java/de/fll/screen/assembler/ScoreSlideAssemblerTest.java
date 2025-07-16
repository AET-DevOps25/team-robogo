package de.fll.screen.assembler;

import de.fll.core.dto.ScoreSlideDTO;
import de.fll.screen.model.ScoreSlide;
import de.fll.screen.repository.SlideRepository;
import de.fll.screen.service.ScoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import de.fll.core.dto.CategoryDTO;
import de.fll.core.dto.ScoreDTO;
import de.fll.core.dto.TeamDTO;
import de.fll.screen.model.Category;

class ScoreSlideAssemblerTest {
    @InjectMocks
    private ScoreSlideAssembler assembler;
    @Mock
    private SlideRepository slideRepository;
    @Mock
    private ScoreService scoreService;
    @Mock
    private CategoryAssembler categoryAssembler;
    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void testToDTO_NullInput() { assertNull(assembler.toDTO(null)); }

    @Test
    void testFromDTO_NullInput() { assertNull(assembler.fromDTO(null)); }

    @Test
    void testToDTO_Basic() {
        ScoreSlide slide = new ScoreSlide();
        setId(slide, 1L);
        slide.setName("slide");
        slide.setIndex(2);
        // mock category
        Category category = new Category();
        setId(category, 10L);
        category.setName("FLL Robot Game");
        slide.setCategory(category);

        // mock categoryAssembler
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .id(10L)
                .name("FLL Robot Game")
                .competitionId(100L)
                .categoryScoring("FLL_ROBOT_GAME")
                .build();
        when(categoryAssembler.toDTO(category)).thenReturn(categoryDTO);

        // mock scores
        TeamDTO teamDTO = TeamDTO.builder().id(1L).name("Alpha Team").build();
        ScoreDTO score1 = ScoreDTO.builder().points(100.0).time(120).highlight(false).team(teamDTO).rank(1).build();
        ScoreDTO score2 = ScoreDTO.builder().points(90.0).time(130).highlight(false).team(teamDTO).rank(2).build();
        when(scoreService.getScoreDTOsWithHighlight(category)).thenReturn(List.of(score1, score2));

        ScoreSlideDTO dto = assembler.toDTO(slide);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("slide", dto.getName());
        assertEquals(2, dto.getIndex());
        assertEquals("SCORE", dto.getType()); // 假设类型为 SCORE
        assertEquals(categoryDTO, dto.getCategory());
        assertEquals(2, dto.getScores().size());
        assertEquals(score1, dto.getScores().get(0));
        assertEquals(score2, dto.getScores().get(1));
    }

    @Test
    void testFromDTO_Basic() {
        ScoreSlideDTO dto = new ScoreSlideDTO();
        dto.setId(2L);
        ScoreSlide slide = new ScoreSlide();
        when(slideRepository.findById(2L)).thenReturn(java.util.Optional.of(slide));
        assertEquals(slide, assembler.fromDTO(dto));
    }

    private void setId(Object obj, Long id) {
        try {
            Class<?> clazz = obj.getClass();
            Field idField = null;
            while (clazz != null) {
                try {
                    idField = clazz.getDeclaredField("id");
                    break;
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
            if (idField == null) throw new NoSuchFieldException("id field not found");
            idField.setAccessible(true);
            idField.set(obj, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
} 