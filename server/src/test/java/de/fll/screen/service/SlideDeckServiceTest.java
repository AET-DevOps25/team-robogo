package de.fll.screen.service;

import de.fll.screen.model.SlideDeck;
import de.fll.screen.model.Slide;
import de.fll.screen.repository.SlideDeckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SlideDeckServiceTest {
    @InjectMocks
    private SlideDeckService slideDeckService;

    @Mock
    private SlideDeckRepository slideDeckRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateSlideDeck() {
        SlideDeck deck = new SlideDeck();
        when(slideDeckRepository.save(deck)).thenReturn(deck);
        SlideDeck result = slideDeckService.createSlideDeck(deck);
        assertEquals(deck, result);
    }

    @Test
    void testUpdateSlideDeckVersionIncrement() {
        SlideDeck deck = new SlideDeck();
        deck.setName("old");
        deck.setTransitionTime(1);
        deck.setVersion(1);
        when(slideDeckRepository.findById(1L)).thenReturn(Optional.of(deck));
        when(slideDeckRepository.save(any(SlideDeck.class))).thenAnswer(i -> i.getArgument(0));
        SlideDeck update = new SlideDeck();
        update.setName("new");
        update.setTransitionTime(2);
        update.setVersion(1);
        SlideDeck result = slideDeckService.updateSlideDeck(1L, update);
        assertEquals("new", result.getName());
        assertEquals(2, result.getTransitionTime());
        assertEquals(2, result.getVersion()); // version自增
    }

    @Test
    void testAddSlideToDeckVersionIncrement() {
        SlideDeck deck = new SlideDeck();
        deck.setVersion(1);
        Slide slide = mock(Slide.class);
        when(slideDeckRepository.findById(1L)).thenReturn(Optional.of(deck));
        when(slideDeckRepository.save(any(SlideDeck.class))).thenAnswer(i -> i.getArgument(0));
        SlideDeck result = slideDeckService.addSlideToDeck(1L, slide);
        assertTrue(result.getSlides().contains(slide));
        assertEquals(2, result.getVersion());
    }

    @Test
    void testRemoveSlideFromDeckVersionIncrement() {
        SlideDeck deck = new SlideDeck();
        deck.setVersion(1);
        Slide slide = mock(Slide.class);
        when(slide.getId()).thenReturn(2L);
        deck.getSlides().add(slide);
        when(slideDeckRepository.findById(1L)).thenReturn(Optional.of(deck));
        when(slideDeckRepository.save(any(SlideDeck.class))).thenAnswer(i -> i.getArgument(0));
        SlideDeck result = slideDeckService.removeSlideFromDeck(1L, 2L);
        assertFalse(result.getSlides().contains(slide));
        assertEquals(2, result.getVersion());
    }

    @Test
    void testReorderSlidesVersionIncrement() {
        SlideDeck deck = new SlideDeck();
        deck.setVersion(1);
        Slide slide1 = mock(Slide.class);
        Slide slide2 = mock(Slide.class);
        when(slide1.getId()).thenReturn(1L);
        when(slide2.getId()).thenReturn(2L);
        deck.getSlides().add(slide1);
        deck.getSlides().add(slide2);
        when(slideDeckRepository.findById(1L)).thenReturn(Optional.of(deck));
        when(slideDeckRepository.save(any(SlideDeck.class))).thenAnswer(i -> i.getArgument(0));
        List<Long> newOrder = Arrays.asList(2L, 1L);
        SlideDeck result = slideDeckService.reorderSlides(1L, newOrder);
        assertEquals(slide2, result.getSlides().get(0));
        assertEquals(slide1, result.getSlides().get(1));
        assertEquals(2, result.getVersion());
    }

    @Test
    void testGetAllSlideDecks() {
        SlideDeck deck = new SlideDeck();
        when(slideDeckRepository.findAll()).thenReturn(Collections.singletonList(deck));
        List<SlideDeck> result = slideDeckService.getAllSlideDecks();
        assertEquals(1, result.size());
    }

    @Test
    void testGetSlideDeckById() {
        SlideDeck deck = new SlideDeck();
        when(slideDeckRepository.findById(1L)).thenReturn(Optional.of(deck));
        Optional<SlideDeck> result = slideDeckService.getSlideDeckById(1L);
        assertTrue(result.isPresent());
    }

    @Test
    void testDeleteSlideDeck() {
        doNothing().when(slideDeckRepository).deleteById(1L);
        slideDeckService.deleteSlideDeck(1L);
        verify(slideDeckRepository, times(1)).deleteById(1L);
    }
} 