package de.fll.screen.assembler;

import de.fll.core.dto.ScreenContentDTO;
import de.fll.core.dto.SlideDeckDTO;
import de.fll.screen.model.Screen;
import de.fll.screen.model.ScreenStatus;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.service.ScoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import de.fll.screen.service.ScreenService;

class ScreenContentAssemblerTest {
    @InjectMocks
    private ScreenContentAssembler assembler;
    @Mock
    private SlideDeckAssembler slideDeckAssembler;

    @Mock
    private ScreenService screenService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void testToDTO_NullInput() { assertNull(assembler.toDTO(null)); }

    @Test
    void testFromDTO_NullInput() { assertNull(assembler.fromDTO(null)); }

    @Test
    void testToDTO_Basic() {
        Screen screen = new Screen();
        setId(screen, 1L);
        screen.setName("screen");
        screen.setStatus(ScreenStatus.ONLINE);
        SlideDeck deck = new SlideDeck();
        screen.setSlideDeck(deck);
        when(slideDeckAssembler.toDTO(deck)).thenReturn(new SlideDeckDTO());
        ScreenContentDTO dto = assembler.toDTO(screen);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("screen", dto.getName());
        assertEquals("ONLINE", dto.getStatus());
    }

    @Test
    void testFromDTO_Basic() {
        ScreenContentDTO dto = new ScreenContentDTO();
        dto.setName("screen");
        dto.setStatus("ONLINE");

        Screen mockScreen = new Screen();
        when(screenService.createScreenFromDTO(dto)).thenReturn(mockScreen);

        Screen screen = assembler.fromDTO(dto);
        assertNotNull(screen);
        assertEquals(mockScreen, screen);
    }

    private void setId(Screen screen, Long id) {
        try {
            Field idField = screen.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(screen, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
} 