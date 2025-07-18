package de.fll.screen.service;

import de.fll.screen.model.Team;
import de.fll.screen.model.Category;
import de.fll.screen.model.Score;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.repository.TeamRepository;
import de.fll.screen.repository.CategoryRepository;
import de.fll.screen.repository.ScoreRepository;
import de.fll.screen.repository.SlideDeckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ScoreRepository scoreRepository;

    @Mock
    private SlideDeckRepository slideDeckRepository;

    @InjectMocks
    private TeamService teamService;

    private Team mockTeam;
    private Category mockCategory;
    private Score mockScore;

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
    }

    @Test
    void createTeam_ShouldCreateTeamSuccessfully() {
        // Arrange
        Team newTeam = new Team();
        newTeam.setName("New Team");
        newTeam.setCategory(mockCategory);
        
        when(teamRepository.save(any(Team.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Team result = teamService.createTeam(newTeam);

        // Assert
        assertNotNull(result);
        assertEquals("New Team", result.getName());
        assertEquals(mockCategory, result.getCategory());
        verify(teamRepository).save(newTeam);
    }

    @Test
    void getAllTeams_ShouldReturnAllTeams() {
        // Arrange
        List<Team> expectedTeams = Arrays.asList(mockTeam);
        when(teamRepository.findAll()).thenReturn(expectedTeams);

        // Act
        List<Team> result = teamService.getAllTeams();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockTeam, result.get(0));
    }

    @Test
    void getTeamById_ShouldReturnTeam_WhenTeamExists() {
        // Arrange
        Long teamId = 1L;
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(mockTeam));

        // Act
        Team result = teamService.getTeamById(teamId);

        // Assert
        assertNotNull(result);
        assertEquals(mockTeam, result);
    }

    @Test
    void getTeamById_ShouldThrowException_WhenTeamNotFound() {
        // Arrange
        Long teamId = 999L;
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            teamService.getTeamById(teamId);
        });
    }

    @Test
    void updateTeam_ShouldUpdateTeamSuccessfully() {
        // Arrange
        Long teamId = 1L;
        Team teamDetails = new Team();
        teamDetails.setName("Updated Team");
        teamDetails.setCategory(mockCategory);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(mockTeam));
        when(teamRepository.save(any(Team.class))).thenAnswer(i -> i.getArgument(0));
        when(scoreRepository.findByTeam_Id(teamId)).thenReturn(Optional.of(mockScore));
        when(slideDeckRepository.findSlideDecksByScore(anyLong(), anyLong())).thenReturn(new ArrayList<>());

        // Act
        Team result = teamService.updateTeam(teamId, teamDetails);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Team", result.getName());
        assertEquals(mockCategory, result.getCategory());
        verify(teamRepository).save(mockTeam);
    }

    @Test
    void updateTeam_ShouldThrowException_WhenTeamNotFound() {
        // Arrange
        Long teamId = 999L;
        Team teamDetails = new Team();
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            teamService.updateTeam(teamId, teamDetails);
        });
    }

    @Test
    void updateTeam_ShouldUpdateOnlyName_WhenOnlyNameProvided() {
        // Arrange
        Long teamId = 1L;
        Team teamDetails = new Team();
        teamDetails.setName("Updated Team");
        // 不设置category

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(mockTeam));
        when(teamRepository.save(any(Team.class))).thenAnswer(i -> i.getArgument(0));
        when(scoreRepository.findByTeam_Id(teamId)).thenReturn(Optional.of(mockScore));
        when(slideDeckRepository.findSlideDecksByScore(anyLong(), anyLong())).thenReturn(new ArrayList<>());

        // Act
        Team result = teamService.updateTeam(teamId, teamDetails);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Team", result.getName());
        assertEquals(mockCategory, result.getCategory()); // 原有category保持不变
    }



    @Test
    void updateTeamCategory_ShouldUpdateTeamCategorySuccessfully() {
        // Arrange
        Long teamId = 1L;
        Long categoryId = 2L;
        Category newCategory = new Category();
        newCategory.setId(categoryId);
        newCategory.setName("New Category");

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(mockTeam));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(newCategory));
        when(teamRepository.save(any(Team.class))).thenAnswer(i -> i.getArgument(0));
        when(scoreRepository.findByTeam_Id(teamId)).thenReturn(Optional.of(mockScore));
        when(slideDeckRepository.findSlideDecksByScore(anyLong(), anyLong())).thenReturn(new ArrayList<>());

        // Act
        Team result = teamService.updateTeamCategory(teamId, categoryId);

        // Assert
        assertNotNull(result);
        assertEquals(newCategory, result.getCategory());
        verify(teamRepository).save(mockTeam);
    }

    @Test
    void updateTeamCategory_ShouldThrowException_WhenTeamNotFound() {
        // Arrange
        Long teamId = 999L;
        Long categoryId = 1L;
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            teamService.updateTeamCategory(teamId, categoryId);
        });
    }

    @Test
    void updateTeamCategory_ShouldThrowException_WhenCategoryNotFound() {
        // Arrange
        Long teamId = 1L;
        Long categoryId = 999L;
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(mockTeam));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            teamService.updateTeamCategory(teamId, categoryId);
        });
    }

    @Test
    void deleteTeam_ShouldDeleteTeamSuccessfully() {
        // Arrange
        Long teamId = 1L;
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(mockTeam));
        when(scoreRepository.findByTeam_Id(teamId)).thenReturn(Optional.of(mockScore));
        when(slideDeckRepository.findSlideDecksByScore(anyLong(), anyLong())).thenReturn(new ArrayList<>());

        // Act
        teamService.deleteTeam(teamId);

        // Assert
        verify(teamRepository).delete(mockTeam);
    }

    @Test
    void deleteTeam_ShouldThrowException_WhenTeamNotFound() {
        // Arrange
        Long teamId = 999L;
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            teamService.deleteTeam(teamId);
        });
    }

    @Test
    void updateSlideDeckVersionsForTeam_ShouldUpdateVersions_WhenTeamHasScore() {
        // Arrange
        Team teamDetails = new Team();
        teamDetails.setName("Updated Team");
        List<SlideDeck> affectedDecks = Arrays.asList(new SlideDeck());
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));
        when(teamRepository.save(any(Team.class))).thenAnswer(i -> i.getArgument(0));
        when(scoreRepository.findByTeam_Id(mockTeam.getId())).thenReturn(Optional.of(mockScore));
        when(slideDeckRepository.findSlideDecksByScore(mockScore.getId(), mockCategory.getId())).thenReturn(affectedDecks);
        when(slideDeckRepository.save(any(SlideDeck.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        teamService.updateTeam(mockTeam.getId(), teamDetails);

        // Assert
        verify(slideDeckRepository, atLeastOnce()).save(any(SlideDeck.class));
    }

    @Test
    void updateSlideDeckVersionsForTeam_ShouldNotUpdateVersions_WhenTeamHasNoScore() {
        // Arrange
        Team teamDetails = new Team();
        teamDetails.setName("Updated Team");
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));
        when(teamRepository.save(any(Team.class))).thenAnswer(i -> i.getArgument(0));
        when(scoreRepository.findByTeam_Id(mockTeam.getId())).thenReturn(Optional.empty());

        // Act
        teamService.updateTeam(mockTeam.getId(), teamDetails);

        // Assert
        verify(slideDeckRepository, never()).findSlideDecksByScore(anyLong(), anyLong());
    }

    @Test
    void updateSlideDeckVersionsForTeam_ShouldNotUpdateVersions_WhenTeamHasNoCategory() {
        // Arrange
        Team teamWithoutCategory = new Team();
        teamWithoutCategory.setId(1L);
        teamWithoutCategory.setName("Team without category");
        teamWithoutCategory.setCategory(null);

        when(teamRepository.findById(teamWithoutCategory.getId())).thenReturn(Optional.of(teamWithoutCategory));
        when(teamRepository.save(any(Team.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        teamService.updateTeam(teamWithoutCategory.getId(), teamWithoutCategory);

        // Assert
        verify(slideDeckRepository, never()).findSlideDecksByScore(anyLong(), anyLong());
    }

    @Test
    void incrementVersion_ShouldHandleOverflow() {
        // Arrange
        SlideDeck deck = new SlideDeck();
        deck.setVersion(2000000000); // 接近最大值
        List<SlideDeck> affectedDecks = Arrays.asList(deck);
        
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));
        when(teamRepository.save(any(Team.class))).thenAnswer(i -> i.getArgument(0));
        when(scoreRepository.findByTeam_Id(mockTeam.getId())).thenReturn(Optional.of(mockScore));
        when(slideDeckRepository.findSlideDecksByScore(anyLong(), anyLong())).thenReturn(affectedDecks);
        when(slideDeckRepository.save(any(SlideDeck.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        teamService.updateTeam(mockTeam.getId(), mockTeam);

        // Assert
        verify(slideDeckRepository).save(any(SlideDeck.class));
    }
} 