package de.fll.screen.assembler;

import de.fll.core.dto.SlideDeckDTO;
import de.fll.screen.model.Competition;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.repository.CompetitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SlideDeckAssemblerTest {
    @InjectMocks
    private SlideDeckAssembler assembler;
    @Mock
    private CompetitionRepository competitionRepository;
    @Mock
    private SlideAssembler slideAssembler;
    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void testToDTO_NullInput() { assertNull(assembler.toDTO(null)); }

    @Test
    void testFromDTO_NullInput() { assertNull(assembler.fromDTO(null)); }

    @Test
    void testToDTO_Basic() {
        SlideDeck deck = new SlideDeck();
        setId(deck, 1L);
        deck.setName("deck");
        Competition comp = new Competition();
        comp.setId(2L);
        deck.setCompetition(comp);
        SlideDeckDTO dto = assembler.toDTO(deck);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("deck", dto.getName());
        assertEquals(2L, dto.getCompetitionId());
    }

    @Test
    void testFromDTO_Basic() {
        SlideDeckDTO dto = new SlideDeckDTO();
        dto.setName("deck");
        dto.setCompetitionId(2L);
        Competition comp = new Competition();
        when(competitionRepository.findById(2L)).thenReturn(java.util.Optional.of(comp));
        SlideDeck deck = assembler.fromDTO(dto);
        assertNotNull(deck);
        assertEquals("deck", deck.getName());
        assertEquals(comp, deck.getCompetition());
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