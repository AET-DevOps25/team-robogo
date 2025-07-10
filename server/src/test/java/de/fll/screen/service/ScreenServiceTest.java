package de.fll.screen.service;

import de.fll.screen.model.Screen;
import de.fll.screen.model.ScreenStatus;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.repository.ScreenRepository;
import de.fll.screen.repository.SlideDeckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ScreenServiceTest {

    @InjectMocks
    private ScreenService screenService;

    @Mock
    private ScreenRepository screenRepository;

    @Mock
    private SlideDeckRepository slideDeckRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateScreen() {
        Screen screen = new Screen();
        screen.setName("TestScreen");
        screen.setStatus(ScreenStatus.ONLINE);
        when(screenRepository.save(any(Screen.class))).thenReturn(screen);

        Screen result = screenService.createScreen(screen);
        assertEquals("TestScreen", result.getName());
        assertEquals(ScreenStatus.ONLINE, result.getStatus());
    }

    @Test
    void testGetAllScreens() {
        Screen screen1 = new Screen();
        Screen screen2 = new Screen();
        when(screenRepository.findAll()).thenReturn(Arrays.asList(screen1, screen2));

        List<Screen> screens = screenService.getAllScreens();
        assertEquals(2, screens.size());
    }

    @Test
    void testUpdateScreen() {
        Screen oldScreen = new Screen();
        oldScreen.setName("Old");
        oldScreen.setStatus(ScreenStatus.OFFLINE);

        Screen newScreen = new Screen();
        newScreen.setName("New");
        newScreen.setStatus(ScreenStatus.ONLINE);

        when(screenRepository.findById(1L)).thenReturn(Optional.of(oldScreen));
        when(screenRepository.save(any(Screen.class))).thenReturn(oldScreen);

        Screen updated = screenService.updateScreen(1L, newScreen);
        assertEquals("New", updated.getName());
        assertEquals(ScreenStatus.ONLINE, updated.getStatus());
    }

    @Test
    void testUpdateScreenNotFound() {
        when(screenRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> screenService.updateScreen(1L, new Screen()));
    }

    @Test
    void testAssignSlideDeck() {
        Screen screen = new Screen();
        SlideDeck deck = new SlideDeck();

        when(screenRepository.findById(1L)).thenReturn(Optional.of(screen));
        when(slideDeckRepository.findById(2L)).thenReturn(Optional.of(deck));
        when(screenRepository.save(any(Screen.class))).thenReturn(screen);

        Screen result = screenService.assignSlideDeck(1L, 2L);
        assertEquals(deck, result.getSlideDeck());
    }

    @Test
    void testAssignSlideDeckScreenNotFound() {
        when(screenRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> screenService.assignSlideDeck(1L, 2L));
    }

    @Test
    void testAssignSlideDeckDeckNotFound() {
        Screen screen = new Screen();
        when(screenRepository.findById(1L)).thenReturn(Optional.of(screen));
        when(slideDeckRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> screenService.assignSlideDeck(1L, 2L));
    }

    @Test
    void testDeleteScreen() {
        doNothing().when(screenRepository).deleteById(1L);
        screenService.deleteScreen(1L);
        verify(screenRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetScreenByIdNotFound() {
        when(screenRepository.findById(1L)).thenReturn(Optional.empty());
        assertTrue(screenService.getScreenById(1L).isEmpty());
    }

    @Test
    void testUpdateScreenStatus() {
        Screen screen = new Screen();
        screen.setStatus(ScreenStatus.OFFLINE);
        when(screenRepository.findById(1L)).thenReturn(Optional.of(screen));
        when(screenRepository.save(any(Screen.class))).thenReturn(screen);
        Screen updated = screenService.updateScreenStatus(1L, ScreenStatus.ONLINE);

        assertEquals(ScreenStatus.ONLINE, updated.getStatus());
    }
}
