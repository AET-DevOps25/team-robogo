package de.fll.screen.service;

import de.fll.screen.model.Screen;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.model.ScreenStatus;
import de.fll.screen.repository.ScreenRepository;
import de.fll.screen.repository.SlideDeckRepository;
import de.fll.screen.repository.CompetitionRepository;
import de.fll.screen.assembler.SlideDeckAssembler;
import de.fll.core.dto.ScreenContentDTO;
import de.fll.core.dto.SlideDeckDTO;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScreenServiceTest {

    @InjectMocks
    private ScreenService screenService;

    @Mock
    private ScreenRepository screenRepository;

    @Mock
    private SlideDeckRepository slideDeckRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private SlideDeckAssembler slideDeckAssembler;

    @Mock
    private Counter screenStatusChangeCounter;

    private Screen mockScreen;
    private SlideDeck mockSlideDeck;

    @BeforeEach
    void setUp() {
        mockScreen = new Screen();
        mockScreen.setName("Test Screen");
        mockScreen.setStatus(ScreenStatus.ONLINE);

        mockSlideDeck = new SlideDeck();
        mockSlideDeck.setName("Test SlideDeck");

        // Mock Counter to avoid NullPointerException
        lenient().doNothing().when(screenStatusChangeCounter).increment();
    }

    @Test
    void getAllScreens_ShouldReturnAllScreens() {
        // Arrange
        List<Screen> expectedScreens = Arrays.asList(mockScreen);
        when(screenRepository.findAll()).thenReturn(expectedScreens);

        // Act
        List<Screen> result = screenService.getAllScreens();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockScreen, result.get(0));
    }

    @Test
    void getScreenById_ShouldReturnScreen_WhenScreenExists() {
        // Arrange
        Long screenId = 1L;
        when(screenRepository.findById(screenId)).thenReturn(Optional.of(mockScreen));

        // Act
        Optional<Screen> result = screenService.getScreenById(screenId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mockScreen, result.get());
    }

    @Test
    void getScreenById_ShouldReturnEmpty_WhenScreenNotFound() {
        // Arrange
        Long screenId = 999L;
        when(screenRepository.findById(screenId)).thenReturn(Optional.empty());

        // Act
        Optional<Screen> result = screenService.getScreenById(screenId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void createScreen_ShouldCreateScreenSuccessfully() {
        // Arrange
        Screen newScreen = new Screen();
        newScreen.setName("New Screen");
        newScreen.setStatus(ScreenStatus.OFFLINE);
        
        when(screenRepository.save(any(Screen.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Screen result = screenService.createScreen(newScreen);

        // Assert
        assertNotNull(result);
        assertEquals("New Screen", result.getName());
        assertEquals(ScreenStatus.OFFLINE, result.getStatus());
        verify(screenRepository).save(newScreen);
    }

    @Test
    void updateScreen_ShouldUpdateScreenSuccessfully() {
        // Arrange
        Long screenId = 1L;
        Screen screenDetails = new Screen();
        screenDetails.setName("Updated Screen");
        screenDetails.setStatus(ScreenStatus.OFFLINE);

        when(screenRepository.findById(screenId)).thenReturn(Optional.of(mockScreen));
        when(screenRepository.save(any(Screen.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Screen result = screenService.updateScreen(screenId, screenDetails);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Screen", result.getName());
        assertEquals(ScreenStatus.OFFLINE, result.getStatus());
        verify(screenRepository).save(mockScreen);
    }

    @Test
    void updateScreen_ShouldThrowException_WhenScreenNotFound() {
        // Arrange
        Long screenId = 999L;
        Screen screenDetails = new Screen();
        when(screenRepository.findById(screenId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            screenService.updateScreen(screenId, screenDetails);
        });
    }

    @Test
    void deleteScreen_ShouldDeleteScreenSuccessfully() {
        // Arrange
        Long screenId = 1L;

        // Act
        screenService.deleteScreen(screenId);

        // Assert
        verify(screenRepository).deleteById(screenId);
    }

    @Test
    void assignSlideDeck_ShouldAssignSlideDeckSuccessfully() {
        // Arrange
        Long screenId = 1L;
        Long slideDeckId = 1L;

        when(screenRepository.findById(screenId)).thenReturn(Optional.of(mockScreen));
        when(slideDeckRepository.findById(slideDeckId)).thenReturn(Optional.of(mockSlideDeck));
        when(screenRepository.save(any(Screen.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Screen result = screenService.assignSlideDeck(screenId, slideDeckId);

        // Assert
        assertNotNull(result);
        assertEquals(mockSlideDeck, result.getSlideDeck());
        verify(screenRepository).save(mockScreen);
    }

    @Test
    void assignSlideDeck_ShouldThrowException_WhenScreenNotFound() {
        // Arrange
        Long screenId = 999L;
        Long slideDeckId = 1L;
        when(screenRepository.findById(screenId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            screenService.assignSlideDeck(screenId, slideDeckId);
        });
    }

    @Test
    void assignSlideDeck_ShouldThrowException_WhenSlideDeckNotFound() {
        // Arrange
        Long screenId = 1L;
        Long slideDeckId = 999L;
        when(screenRepository.findById(screenId)).thenReturn(Optional.of(mockScreen));
        when(slideDeckRepository.findById(slideDeckId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            screenService.assignSlideDeck(screenId, slideDeckId);
        });
    }

    @Test
    void updateScreenStatus_ShouldUpdateStatusSuccessfully() {
        // Arrange
        Long screenId = 1L;
        ScreenStatus newStatus = ScreenStatus.OFFLINE;

        when(screenRepository.findById(screenId)).thenReturn(Optional.of(mockScreen));
        when(screenRepository.save(any(Screen.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Screen result = screenService.updateScreenStatus(screenId, newStatus);

        // Assert
        assertNotNull(result);
        assertEquals(newStatus, result.getStatus());
        verify(screenRepository).save(mockScreen);
    }

    @Test
    void updateScreenStatus_ShouldThrowException_WhenScreenNotFound() {
        // Arrange
        Long screenId = 999L;
        ScreenStatus newStatus = ScreenStatus.OFFLINE;
        when(screenRepository.findById(screenId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            screenService.updateScreenStatus(screenId, newStatus);
        });
    }

    @Test
    void createScreenFromDTO_ShouldReturnNull_WhenDTOIsNull() {
        // Act
        Screen result = screenService.createScreenFromDTO(null);

        // Assert
        assertNull(result);
    }

    @Test
    void createScreenFromDTO_ShouldCreateScreen_WhenValidDTO() {
        // Arrange
        ScreenContentDTO dto = new ScreenContentDTO();
        dto.setName("Test Screen from DTO");
        dto.setStatus("ONLINE");
        
        when(screenRepository.save(any(Screen.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Screen result = screenService.createScreenFromDTO(dto);

        // Assert
        assertNotNull(result);
        assertEquals("Test Screen from DTO", result.getName());
        assertEquals(ScreenStatus.ONLINE, result.getStatus());
        verify(screenRepository).save(any(Screen.class));
    }

    @Test
    void createScreenFromDTO_ShouldCreateScreen_WhenDTOHasSlideDeck() {
        // Arrange
        ScreenContentDTO dto = new ScreenContentDTO();
        dto.setName("Test Screen with SlideDeck");
        dto.setStatus("ONLINE");
        
        SlideDeckDTO slideDeckDTO = new SlideDeckDTO();
        slideDeckDTO.setVersion(1);
        dto.setSlideDeck(slideDeckDTO);
        
        when(slideDeckAssembler.fromDTO(slideDeckDTO)).thenReturn(mockSlideDeck);
        when(screenRepository.save(any(Screen.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Screen result = screenService.createScreenFromDTO(dto);

        // Assert
        assertNotNull(result);
        assertEquals("Test Screen with SlideDeck", result.getName());
        assertEquals(ScreenStatus.ONLINE, result.getStatus());
        assertEquals(mockSlideDeck, result.getSlideDeck());
        verify(screenRepository).save(any(Screen.class));
    }

    @Test
    void createScreenFromDTO_ShouldCreateScreen_WhenDTOHasNullStatus() {
        // Arrange
        ScreenContentDTO dto = new ScreenContentDTO();
        dto.setName("Test Screen with null status");
        dto.setStatus(null);
        
        when(screenRepository.save(any(Screen.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Screen result = screenService.createScreenFromDTO(dto);

        // Assert
        assertNotNull(result);
        assertEquals("Test Screen with null status", result.getName());
        assertNull(result.getStatus());
        verify(screenRepository).save(any(Screen.class));
    }

    @Test
    void createScreenFromDTO_ShouldCreateScreen_WhenDTOHasNullSlideDeck() {
        // Arrange
        ScreenContentDTO dto = new ScreenContentDTO();
        dto.setName("Test Screen with null slideDeck");
        dto.setStatus("ONLINE");
        dto.setSlideDeck(null);
        
        when(screenRepository.save(any(Screen.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Screen result = screenService.createScreenFromDTO(dto);

        // Assert
        assertNotNull(result);
        assertEquals("Test Screen with null slideDeck", result.getName());
        assertEquals(ScreenStatus.ONLINE, result.getStatus());
        assertNull(result.getSlideDeck());
        verify(screenRepository).save(any(Screen.class));
    }
} 