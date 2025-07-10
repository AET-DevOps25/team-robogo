package de.fll.screen.assembler;

import de.fll.core.dto.ImageSlideDTO;
import de.fll.core.dto.SlideImageMetaDTO;
import de.fll.screen.model.ImageSlide;
import de.fll.screen.model.SlideImageMeta;
import de.fll.screen.repository.SlideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImageSlideAssemblerTest {
    @InjectMocks
    private ImageSlideAssembler assembler;
    @Mock
    private SlideRepository slideRepository;
    @Mock
    private ImageSlideMetaAssembler imageSlideMetaAssembler;
    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void testToDTO_NullInput() { assertNull(assembler.toDTO(null)); }

    @Test
    void testFromDTO_NullInput() { assertNull(assembler.fromDTO(null)); }

    @Test
    void testToDTO_Basic() {
        ImageSlide slide = new ImageSlide();
        setId(slide, 1L);
        slide.setName("img");
        SlideImageMeta meta = new SlideImageMeta();
        slide.setImageMeta(meta);
        when(imageSlideMetaAssembler.toDTO(meta)).thenReturn(new SlideImageMetaDTO());
        ImageSlideDTO dto = assembler.toDTO(slide);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("img", dto.getName());
    }

    @Test
    void testFromDTO_Basic() {
        ImageSlideDTO dto = new ImageSlideDTO();
        dto.setId(2L);
        SlideImageMetaDTO metaDTO = new SlideImageMetaDTO();
        dto.setImageMeta(metaDTO);
        ImageSlide slide = new ImageSlide();
        when(slideRepository.findById(2L)).thenReturn(java.util.Optional.of(slide));
        when(imageSlideMetaAssembler.fromDTO(metaDTO)).thenReturn(new SlideImageMeta());
        ImageSlide result = assembler.fromDTO(dto);
        assertEquals(slide, result);
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