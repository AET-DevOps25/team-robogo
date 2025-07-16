package de.fll.screen.assembler;

import de.fll.core.dto.ImageSlideMetaDTO;
import de.fll.screen.model.SlideImageMeta;
import de.fll.screen.repository.SlideImageMetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImageSlideMetaAssemblerTest {
    @InjectMocks
    private ImageSlideMetaAssembler assembler;
    @Mock
    private SlideImageMetaRepository slideImageMetaRepository;
    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void testToDTO_NullInput() { assertNull(assembler.toDTO(null)); }

    @Test
    void testFromDTO_NullInput() { assertNull(assembler.fromDTO(null)); }

    @Test
    void testToDTO_Basic() {
        SlideImageMeta meta = new SlideImageMeta();
        setId(meta, 1L);
        meta.setName("meta");
        meta.setContentType("image/png");
        ImageSlideMetaDTO dto = assembler.toDTO(meta);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("meta", dto.getName());
        assertEquals("image/png", dto.getContentType());
    }

    @Test
    void testFromDTO_Basic() {
        ImageSlideMetaDTO dto = new ImageSlideMetaDTO();
        dto.setId(2L);
        SlideImageMeta meta = new SlideImageMeta();
        when(slideImageMetaRepository.findById(2L)).thenReturn(java.util.Optional.of(meta));
        assertEquals(meta, assembler.fromDTO(dto));
    }

    private void setId(SlideImageMeta meta, Long id) {
        try {
            Field idField = meta.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(meta, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
} 