package de.fll.screen.service;

import de.fll.screen.model.Category;
import de.fll.screen.model.Team;
import de.fll.screen.model.Score;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.repository.CategoryRepository;
import de.fll.screen.repository.TeamRepository;
import de.fll.screen.repository.ScoreRepository;
import de.fll.screen.repository.SlideDeckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private ScoreRepository scoreRepository;
    @Mock
    private SlideDeckRepository slideDeckRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateCategory_WithScores_UpdatesSlideDeckVersions() {
        // Arrange
        Long categoryId = 1L;
        
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Old Name");
        
        Team team1 = new Team("Team 1");
        team1.setId(1L);
        team1.setCategory(category);
        
        Team team2 = new Team("Team 2");
        team2.setId(2L);
        team2.setCategory(category);
        
        Score score1 = new Score(100.0, 120);
        score1.setId(1L);
        score1.setTeam(team1);
        
        Score score2 = new Score(90.0, 130);
        score2.setId(2L);
        score2.setTeam(team2);
        
        SlideDeck slideDeck1 = new SlideDeck();
        slideDeck1.setVersion(1);
        
        SlideDeck slideDeck2 = new SlideDeck();
        slideDeck2.setVersion(2);
        
        Category updatedCategory = new Category();
        updatedCategory.setId(categoryId);
        updatedCategory.setName("New Name");
        
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        when(scoreRepository.findByTeam_Category_Id(categoryId))
            .thenReturn(List.of(score1, score2));
        when(slideDeckRepository.findSlideDecksByScore(score1.getId(), categoryId))
            .thenReturn(List.of(slideDeck1));
        when(slideDeckRepository.findSlideDecksByScore(score2.getId(), categoryId))
            .thenReturn(List.of(slideDeck2));
        when(slideDeckRepository.save(any(SlideDeck.class))).thenReturn(slideDeck1);
        
        // Act
        Category result = categoryService.updateCategory(categoryId, updatedCategory);
        
        // Assert
        assertNotNull(result);
        assertEquals("New Name", result.getName());
        verify(slideDeckRepository, times(2)).save(any(SlideDeck.class));
    }

    @Test
    void testUpdateCategory_WithoutScores_NoSlideDeckUpdate() {
        // Arrange
        Long categoryId = 1L;
        
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Old Name");
        
        Category updatedCategory = new Category();
        updatedCategory.setId(categoryId);
        updatedCategory.setName("New Name");
        
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        when(scoreRepository.findByTeam_Category_Id(categoryId)).thenReturn(List.of());
        
        // Act
        Category result = categoryService.updateCategory(categoryId, updatedCategory);
        
        // Assert
        assertNotNull(result);
        assertEquals("New Name", result.getName());
        verify(slideDeckRepository, never()).save(any(SlideDeck.class));
    }

    @Test
    void testDeleteCategory_WithScores_UpdatesSlideDeckVersions() {
        // Arrange
        Long categoryId = 1L;
        
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Test Category");
        
        Team team = new Team("Test Team");
        team.setId(1L);
        team.setCategory(category);
        
        Score score = new Score(100.0, 120);
        score.setId(1L);
        score.setTeam(team);
        
        SlideDeck slideDeck = new SlideDeck();
        slideDeck.setVersion(1);
        
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(scoreRepository.findByTeam_Category_Id(categoryId))
            .thenReturn(List.of(score));
        when(slideDeckRepository.findSlideDecksByScore(score.getId(), categoryId))
            .thenReturn(List.of(slideDeck));
        when(slideDeckRepository.save(any(SlideDeck.class))).thenReturn(slideDeck);
        
        // Act
        categoryService.deleteCategory(categoryId);
        
        // Assert
        verify(categoryRepository).delete(category);
        verify(slideDeckRepository).save(argThat(deck -> deck.getVersion() == 2));
    }

    @Test
    void testIncrementVersion_HandlesOverflow() {
        // 测试版本号溢出处理
        Long categoryId = 1L;
        
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Test Category");
        
        Team team = new Team("Test Team");
        team.setId(1L);
        team.setCategory(category);
        
        Score score = new Score(100.0, 120);
        score.setId(1L);
        score.setTeam(team);
        
        SlideDeck slideDeck = new SlideDeck();
        slideDeck.setVersion(2000000000); // 接近最大值
        
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(scoreRepository.findByTeam_Category_Id(categoryId))
            .thenReturn(List.of(score));
        when(slideDeckRepository.findSlideDecksByScore(score.getId(), categoryId))
            .thenReturn(List.of(slideDeck));
        when(slideDeckRepository.save(any(SlideDeck.class))).thenReturn(slideDeck);
        
        // Act
        categoryService.deleteCategory(categoryId);
        
        // Assert - 版本号应该重置为1
        verify(slideDeckRepository).save(argThat(deck -> deck.getVersion() == 1));
    }
} 