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
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TeamServiceTest {
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateTeamCategory_WithScore_UpdatesSlideDeckVersion() {
        // Arrange
        Long teamId = 1L;
        Long categoryId = 2L;
        
        Team team = new Team("Test Team");
        team.setId(teamId);
        
        Category category = new Category();
        category.setId(categoryId);
        
        Score score = new Score(100.0, 120);
        score.setId(1L);
        score.setTeam(team);
        
        SlideDeck slideDeck = new SlideDeck();
        slideDeck.setVersion(1);
        
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        when(scoreRepository.findByTeam_Id(teamId)).thenReturn(Optional.of(score));
        when(slideDeckRepository.findSlideDecksByScore(score.getId(), categoryId))
            .thenReturn(List.of(slideDeck));
        when(slideDeckRepository.save(any(SlideDeck.class))).thenReturn(slideDeck);
        
        // Act
        Team result = teamService.updateTeamCategory(teamId, categoryId);
        
        // Assert
        assertNotNull(result);
        assertEquals(category, result.getCategory());
        verify(slideDeckRepository).save(argThat(deck -> deck.getVersion() == 2));
    }

    @Test
    void testUpdateTeamCategory_WithoutScore_NoSlideDeckUpdate() {
        // Arrange
        Long teamId = 1L;
        Long categoryId = 2L;
        
        Team team = new Team("Test Team");
        team.setId(teamId);
        
        Category category = new Category();
        category.setId(categoryId);
        
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        when(scoreRepository.findByTeam_Id(teamId)).thenReturn(Optional.empty());
        
        // Act
        Team result = teamService.updateTeamCategory(teamId, categoryId);
        
        // Assert
        assertNotNull(result);
        assertEquals(category, result.getCategory());
        verify(slideDeckRepository, never()).save(any(SlideDeck.class));
    }

    @Test
    void testIncrementVersion_HandlesOverflow() {
        // 测试版本号溢出处理
        // 这里我们需要通过反射来测试私有方法，或者将incrementVersion方法设为包级别可见
        // 为了简化，我们通过公共方法来间接测试
        Long teamId = 1L;
        Long categoryId = 2L;
        
        Team team = new Team("Test Team");
        team.setId(teamId);
        
        Category category = new Category();
        category.setId(categoryId);
        
        Score score = new Score(100.0, 120);
        score.setId(1L);
        score.setTeam(team);
        
        SlideDeck slideDeck = new SlideDeck();
        slideDeck.setVersion(2000000000); // 接近最大值
        
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        when(scoreRepository.findByTeam_Id(teamId)).thenReturn(Optional.of(score));
        when(slideDeckRepository.findSlideDecksByScore(score.getId(), categoryId))
            .thenReturn(List.of(slideDeck));
        when(slideDeckRepository.save(any(SlideDeck.class))).thenReturn(slideDeck);
        
        // Act
        teamService.updateTeamCategory(teamId, categoryId);
        
        // Assert - 版本号应该重置为1
        verify(slideDeckRepository).save(argThat(deck -> deck.getVersion() == 1));
    }
} 