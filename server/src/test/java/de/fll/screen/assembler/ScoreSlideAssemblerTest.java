package de.fll.screen.assembler;

import de.fll.core.dto.ScoreSlideDTO;
import de.fll.screen.model.ScoreSlide;
import de.fll.screen.repository.SlideRepository;
import de.fll.screen.service.ScoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import de.fll.screen.model.Category;

class ScoreSlideAssemblerTest {
    @InjectMocks
    private ScoreSlideAssembler assembler;
    @Mock
    private SlideRepository slideRepository;
    @Mock
    private ScoreService scoreService;
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
        Category category = new Category();
        setId(category, 10L);
        slide.setCategory(category);

        when(scoreService.getAllTeamsScoreDTOsWithHighlight(any())).thenReturn(null);
        ScoreSlideDTO dto = assembler.toDTO(slide);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("slide", dto.getName());
        assertEquals(10L, dto.getCategoryId());
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