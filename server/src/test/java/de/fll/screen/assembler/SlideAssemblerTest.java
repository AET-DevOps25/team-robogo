package de.fll.screen.assembler;

import de.fll.core.dto.ImageSlideDTO;
import de.fll.core.dto.ScoreSlideDTO;
import de.fll.core.dto.SlideDTO;
import de.fll.screen.model.ImageSlide;
import de.fll.screen.model.ScoreSlide;
import de.fll.screen.model.SlideType;
import de.fll.screen.model.Slide;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SlideAssemblerTest {
    @InjectMocks
    private SlideAssembler assembler;
    @Mock
    private ImageSlideAssembler imageSlideAssembler;
    @Mock
    private ScoreSlideAssembler scoreSlideAssembler;
    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void testToDTO_NullInput() { assertNull(assembler.toDTO(null)); }

    @Test
    void testFromDTO_NullInput() { assertNull(assembler.fromDTO(null)); }

    @Test
    void testToDTO_Basic_IMAGE() {
        ImageSlide slide = new ImageSlide();
        setId(slide, 1L);
        slide.setName("img");
        slide.setIndex(1);
        SlideDTO dto = assembler.toDTO(slide);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("img", dto.getName());
        assertEquals(SlideType.IMAGE.name(), dto.getType());
    }

    @Test
    void testFromDTO_Basic_IMAGE() {
        ImageSlideDTO dto = new ImageSlideDTO();
        dto.setType("IMAGE");
        ImageSlide slide = new ImageSlide();
        when(imageSlideAssembler.fromDTO(dto)).thenReturn(slide);
        assertEquals(slide, assembler.fromDTO(dto));
    }

    @Test
    void testFromDTO_Basic_SCORE() {
        ScoreSlideDTO dto = new ScoreSlideDTO();
        dto.setType("SCORE");
        ScoreSlide slide = new ScoreSlide();
        when(scoreSlideAssembler.fromDTO(dto)).thenReturn(slide);
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