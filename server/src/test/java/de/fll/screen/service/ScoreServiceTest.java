package de.fll.screen.service;

import de.fll.core.dto.ScoreDTO;
import de.fll.core.dto.ScoreSlideDTO;
import de.fll.screen.model.Score;
import de.fll.screen.model.Team;
import de.fll.screen.model.Category;
import de.fll.screen.model.ScoreSlide;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.repository.ScoreRepository;
import de.fll.screen.repository.TeamRepository;
import de.fll.screen.repository.CategoryRepository;
import de.fll.screen.repository.SlideRepository;
import de.fll.screen.repository.SlideDeckRepository;
import de.fll.screen.assembler.TeamAssembler;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class ScoreServiceTest {

    @MockBean
    private ScoreRepository scoreRepository;

    @MockBean
    private TeamRepository teamRepository;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private SlideRepository slideRepository;

    @MockBean
    private SlideDeckRepository slideDeckRepository;

    @MockBean
    private TeamAssembler teamAssembler;

    @MockBean
    private RankingService rankingService;

    @MockBean(name = "scoreUpdateCounter")
    private Counter scoreUpdateCounter;

    @Autowired
    private ScoreService scoreService;

    private Team mockTeam;
    private Category mockCategory;
    private Score mockScore;
    private ScoreSlide mockScoreSlide;

    @BeforeEach
    void setUp() {
        mockCategory = new Category();
        mockCategory.setId(1L);
        mockCategory.setName("Test Category");

        mockTeam = new Team();
        mockTeam.setId(1L);
        mockTeam.setName("Test Team");
        mockTeam.setCategory(mockCategory);

        mockScore = new Score();
        mockScore.setId(1L);
        mockScore.setPoints(100.0);
        mockScore.setTime(120);
        mockScore.setTeam(mockTeam);

        mockScoreSlide = new ScoreSlide();
        mockScoreSlide.setName("Test Score Slide");
        mockScoreSlide.setCategory(mockCategory);

        // Mock Counter to avoid NullPointerException
        lenient().doNothing().when(scoreUpdateCounter).increment();
        
        // Mock RankingService to avoid NullPointerException
        lenient().when(rankingService.getRankedTeams(any(Category.class), any())).thenReturn(new ArrayList<>());
        
        // Mock SlideDeckRepository to avoid NullPointerException in updateSlideDeckVersionsForScore
        lenient().when(slideDeckRepository.findSlideDecksByScore(anyLong(), anyLong())).thenReturn(new ArrayList<>());
    }

    @Test
    void addScore_ShouldCreateNewScore_WhenNoExistingScore() {
        // Arrange
        Long teamId = 1L;
        double points = 150.0;
        int time = 180;

        when(scoreRepository.findByTeam_Id(teamId)).thenReturn(Optional.empty());
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(mockTeam));
        when(scoreRepository.save(any(Score.class))).thenAnswer(i -> {
            Score savedScore = i.getArgument(0);
            savedScore.setId(1L); // 设置ID
            // 确保Team有正确的Category
            savedScore.getTeam().setCategory(mockCategory);
            return savedScore;
        });
        when(slideDeckRepository.findSlideDecksByScore(1L, mockCategory.getId())).thenReturn(new ArrayList<>());

        // Act
        Score result = scoreService.addScore(teamId, points, time);

        // Assert
        assertNotNull(result);
        assertEquals(points, result.getPoints());
        assertEquals(time, result.getTime());
        assertEquals(mockTeam, result.getTeam());
        verify(scoreRepository).save(any(Score.class));
    }

    @Test
    void addScore_ShouldUpdateExistingScore_WhenScoreExists() {
        // Arrange
        Long teamId = 1L;
        double points = 200.0;
        int time = 240;

        when(scoreRepository.findByTeam_Id(teamId)).thenReturn(Optional.of(mockScore));
        when(scoreRepository.save(any(Score.class))).thenAnswer(i -> {
            Score savedScore = i.getArgument(0);
            savedScore.setId(1L); // 设置ID
            // 确保Team有正确的Category
            savedScore.getTeam().setCategory(mockCategory);
            return savedScore;
        });
        when(slideDeckRepository.findSlideDecksByScore(1L, mockCategory.getId())).thenReturn(new ArrayList<>());

        // Act
        Score result = scoreService.addScore(teamId, points, time);

        // Assert
        assertNotNull(result);
        assertEquals(points, result.getPoints());
        assertEquals(time, result.getTime());
        verify(scoreRepository).save(mockScore);
    }

    @Test
    void addScore_ShouldThrowException_WhenTeamNotFound() {
        // Arrange
        Long teamId = 999L;
        when(scoreRepository.findByTeam_Id(teamId)).thenReturn(Optional.empty());
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            scoreService.addScore(teamId, 100.0, 120);
        });
    }

    @Test
    void updateScore_ShouldUpdateScoreSuccessfully() {
        // Arrange
        Long scoreId = 1L;
        Long teamId = 1L;
        double points = 250.0;
        int time = 300;

        when(scoreRepository.findById(scoreId)).thenReturn(Optional.of(mockScore));
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(mockTeam));
        when(scoreRepository.save(any(Score.class))).thenAnswer(i -> {
            Score savedScore = i.getArgument(0);
            savedScore.setId(1L); // 设置ID
            // 确保Team有正确的Category
            savedScore.getTeam().setCategory(mockCategory);
            return savedScore;
        });
        when(slideDeckRepository.findSlideDecksByScore(1L, mockCategory.getId())).thenReturn(new ArrayList<>());

        // Act
        Score result = scoreService.updateScore(scoreId, teamId, points, time);

        // Assert
        assertNotNull(result);
        assertEquals(points, result.getPoints());
        assertEquals(time, result.getTime());
        assertEquals(mockTeam, result.getTeam());
        verify(scoreRepository).save(mockScore);
    }

    @Test
    void updateScore_ShouldThrowException_WhenScoreNotFound() {
        // Arrange
        Long scoreId = 999L;
        when(scoreRepository.findById(scoreId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            scoreService.updateScore(scoreId, 1L, 100.0, 120);
        });
    }

    @Test
    void updateScore_ShouldThrowException_WhenTeamNotFound() {
        // Arrange
        Long scoreId = 1L;
        Long teamId = 999L;
        when(scoreRepository.findById(scoreId)).thenReturn(Optional.of(mockScore));
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            scoreService.updateScore(scoreId, teamId, 100.0, 120);
        });
    }

    @Test
    void deleteScore_ShouldDeleteScoreSuccessfully() {
        // Arrange
        Long scoreId = 1L;
        when(scoreRepository.findById(scoreId)).thenReturn(Optional.of(mockScore));
        when(slideDeckRepository.findSlideDecksByScore(anyLong(), anyLong())).thenReturn(new ArrayList<>());

        // Act
        scoreService.deleteScore(scoreId);

        // Assert
        verify(scoreRepository).delete(mockScore);
    }

    @Test
    void deleteScore_ShouldThrowException_WhenScoreNotFound() {
        // Arrange
        Long scoreId = 999L;
        when(scoreRepository.findById(scoreId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            scoreService.deleteScore(scoreId);
        });
    }

    @Test
    void getScoresForAllTeams_ShouldReturnAllScores() {
        // Arrange
        List<Score> expectedScores = Arrays.asList(mockScore);
        when(scoreRepository.findAll()).thenReturn(expectedScores);

        // Act
        List<Score> result = scoreService.getScoresForAllTeams();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockScore, result.get(0));
    }

    @Test
    void getScoresByCategoryId_ShouldReturnCategoryScores() {
        // Arrange
        Long categoryId = 1L;
        List<Score> expectedScores = Arrays.asList(mockScore);
        when(categoryRepository.findScoresByCategoryId(categoryId)).thenReturn(expectedScores);

        // Act
        List<Score> result = scoreService.getScoresByCategoryId(categoryId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockScore, result.get(0));
    }

    @Test
    void getScoresByScoreSlide_ShouldReturnEmptyList_WhenScoreSlideIsNull() {
        // Act
        List<Score> result = scoreService.getScoresByScoreSlide(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getScoresByScoreSlide_ShouldReturnEmptyList_WhenCategoryIsNull() {
        // Arrange
        ScoreSlide scoreSlide = new ScoreSlide();
        scoreSlide.setCategory(null);

        // Act
        List<Score> result = scoreService.getScoresByScoreSlide(scoreSlide);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getScoresByScoreSlide_ShouldReturnScores_WhenValidScoreSlide() {
        // Arrange
        List<Score> expectedScores = Arrays.asList(mockScore);
        when(slideRepository.findScoresByScoreSlideCategory(mockCategory.getId())).thenReturn(expectedScores);

        // Act
        List<Score> result = scoreService.getScoresByScoreSlide(mockScoreSlide);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockScore, result.get(0));
    }

    @Test
    void getScoreDTOsWithHighlight_ShouldReturnEmptyList_WhenCategoryIsNull() {
        // Act
        List<ScoreDTO> result = scoreService.getScoreDTOsWithHighlight((Category) null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getScoreDTOsWithHighlight_ShouldReturnRankedTeams() {
        // Arrange
        Set<Team> teams = new HashSet<>(Arrays.asList(mockTeam));
        List<ScoreDTO> expectedDTOs = Arrays.asList(new ScoreDTO());
        when(categoryRepository.findTeamsByCategoryId(mockCategory.getId())).thenReturn(teams);
        when(rankingService.getRankedTeams(mockCategory, teams)).thenReturn(expectedDTOs);

        // Act
        List<ScoreDTO> result = scoreService.getScoreDTOsWithHighlight(mockCategory);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getScoreDTOsWithHighlight_ShouldReturnEmptyList_WhenCategoryIdNotFound() {
        // Arrange
        Long categoryId = 999L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act
        List<ScoreDTO> result = scoreService.getScoreDTOsWithHighlight(categoryId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getScoreDTOsWithHighlight_ShouldReturnRankedTeams_WhenCategoryIdFound() {
        // Arrange
        Long categoryId = 1L;
        Set<Team> teams = new HashSet<>(Arrays.asList(mockTeam));
        List<ScoreDTO> expectedDTOs = Arrays.asList(new ScoreDTO());
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        when(categoryRepository.findTeamsByCategoryId(categoryId)).thenReturn(teams);
        when(rankingService.getRankedTeams(mockCategory, teams)).thenReturn(expectedDTOs);

        // Act
        List<ScoreDTO> result = scoreService.getScoreDTOsWithHighlight(categoryId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllTeamsScoreDTOsWithHighlight_ShouldReturnAllCategoriesScores() {
        // Arrange
        List<Category> categories = Arrays.asList(mockCategory);
        Set<Team> teams = new HashSet<>(Arrays.asList(mockTeam));
        List<ScoreDTO> expectedDTOs = Arrays.asList(new ScoreDTO());
        when(categoryRepository.findTeamsByCategoryId(mockCategory.getId())).thenReturn(teams);
        when(rankingService.getRankedTeams(mockCategory, teams)).thenReturn(expectedDTOs);

        // Act
        List<ScoreDTO> result = scoreService.getAllTeamsScoreDTOsWithHighlight(categories);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void createScoreSlideFromDTO_ShouldReturnNull_WhenDTOIsNull() {
        // Act
        ScoreSlide result = scoreService.createScoreSlideFromDTO(null);

        // Assert
        assertNull(result);
    }

    @Test
    void createScoreSlideFromDTO_ShouldCreateScoreSlide_WhenValidDTO() {
        // Arrange
        ScoreSlideDTO dto = new ScoreSlideDTO();
        dto.setName("Test Slide");
        dto.setIndex(0);
        
        when(slideRepository.save(any(ScoreSlide.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        ScoreSlide result = scoreService.createScoreSlideFromDTO(dto);

        // Assert
        assertNotNull(result);
        assertEquals("Test Slide", result.getName());
        assertEquals(0, result.getIndex());
    }

    @Test
    void updateSlideDeckVersionsForScore_ShouldUpdateVersions_WhenScoreHasValidData() {
        // Arrange
        SlideDeck deck = new SlideDeck();
        deck.setName("Test Deck");
        deck.setVersion(1);
        List<SlideDeck> affectedDecks = Arrays.asList(deck);
        
        when(scoreRepository.findByTeam_Id(mockTeam.getId())).thenReturn(Optional.empty());
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));
        when(scoreRepository.save(any(Score.class))).thenAnswer(i -> {
            Score savedScore = i.getArgument(0);
            savedScore.setId(1L); // 设置ID
            // 确保Team有正确的Category
            savedScore.getTeam().setCategory(mockCategory);
            return savedScore;
        });
        when(slideDeckRepository.findSlideDecksByScore(1L, mockCategory.getId())).thenReturn(affectedDecks);
        when(slideDeckRepository.save(any(SlideDeck.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        scoreService.addScore(mockTeam.getId(), 100.0, 120);

        // Assert
        verify(slideDeckRepository, atLeastOnce()).save(any(SlideDeck.class));
    }

    @Test
    void updateSlideDeckVersionsForScore_ShouldHandleVersionOverflow() {
        // Arrange
        SlideDeck deck = new SlideDeck();
        deck.setName("Test Deck");
        deck.setVersion(2000000000); // 接近最大值
        List<SlideDeck> affectedDecks = Arrays.asList(deck);
        
        when(scoreRepository.findByTeam_Id(mockTeam.getId())).thenReturn(Optional.empty());
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));
        when(scoreRepository.save(any(Score.class))).thenAnswer(i -> {
            Score savedScore = i.getArgument(0);
            savedScore.setId(1L); // 设置ID
            // 确保Team有正确的Category
            savedScore.getTeam().setCategory(mockCategory);
            return savedScore;
        });
        when(slideDeckRepository.findSlideDecksByScore(1L, mockCategory.getId())).thenReturn(affectedDecks);
        when(slideDeckRepository.save(any(SlideDeck.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        scoreService.addScore(mockTeam.getId(), 100.0, 120);

        // Assert
        verify(slideDeckRepository).save(any(SlideDeck.class));
    }
} 