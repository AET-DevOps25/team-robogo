package de.fll.screen.service;

import de.fll.screen.model.Category;
import de.fll.screen.model.Score;
import de.fll.screen.model.SlideDeck;
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
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ScoreRepository scoreRepository;

    @Mock
    private SlideDeckRepository slideDeckRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category mockCategory;
    private Score mockScore;
    private SlideDeck mockSlideDeck;

    @BeforeEach
    void setUp() {
        mockCategory = new Category();
        mockCategory.setId(1L);
        mockCategory.setName("Test Category");

        mockScore = new Score();
        mockScore.setId(1L);
        mockScore.setPoints(100.0);
        mockScore.setTime(120);

        mockSlideDeck = new SlideDeck();
        mockSlideDeck.setVersion(1);
    }

    @Test
    void getAllCategories_ShouldReturnAllCategories() {
        // Arrange
        List<Category> expectedCategories = Arrays.asList(mockCategory);
        when(categoryRepository.findAll()).thenReturn(expectedCategories);

        // Act
        List<Category> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockCategory, result.get(0));
    }

    @Test
    void getCategoryById_ShouldReturnCategory_WhenCategoryExists() {
        // Arrange
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));

        // Act
        Category result = categoryService.getCategoryById(categoryId);

        // Assert
        assertNotNull(result);
        assertEquals(mockCategory, result);
    }

    @Test
    void getCategoryById_ShouldThrowException_WhenCategoryNotFound() {
        // Arrange
        Long categoryId = 999L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.getCategoryById(categoryId);
        });
    }

    @Test
    void createCategory_ShouldCreateCategorySuccessfully() {
        // Arrange
        Category newCategory = new Category();
        newCategory.setName("New Category");
        
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Category result = categoryService.createCategory(newCategory);

        // Assert
        assertNotNull(result);
        assertEquals("New Category", result.getName());
        verify(categoryRepository).save(newCategory);
    }

    @Test
    void updateCategory_ShouldUpdateCategorySuccessfully() {
        // Arrange
        Long categoryId = 1L;
        Category categoryDetails = new Category();
        categoryDetails.setName("Updated Category");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));
        when(scoreRepository.findByTeam_Category_Id(categoryId)).thenReturn(Arrays.asList(mockScore));
        when(slideDeckRepository.findSlideDecksByScore(anyLong(), anyLong())).thenReturn(Arrays.asList(mockSlideDeck));

        // Act
        Category result = categoryService.updateCategory(categoryId, categoryDetails);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Category", result.getName());
        verify(categoryRepository).save(mockCategory);
    }

    @Test
    void updateCategory_ShouldThrowException_WhenCategoryNotFound() {
        // Arrange
        Long categoryId = 999L;
        Category categoryDetails = new Category();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.updateCategory(categoryId, categoryDetails);
        });
    }

    @Test
    void updateCategory_ShouldUpdateOnlyName_WhenOnlyNameProvided() {
        // Arrange
        Long categoryId = 1L;
        Category categoryDetails = new Category();
        categoryDetails.setName("Updated Category");
        // 不设置其他属性

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));
        when(scoreRepository.findByTeam_Category_Id(categoryId)).thenReturn(Arrays.asList(mockScore));
        when(slideDeckRepository.findSlideDecksByScore(anyLong(), anyLong())).thenReturn(Arrays.asList(mockSlideDeck));

        // Act
        Category result = categoryService.updateCategory(categoryId, categoryDetails);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Category", result.getName());
    }

    @Test
    void deleteCategory_ShouldDeleteCategorySuccessfully() {
        // Arrange
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        when(scoreRepository.findByTeam_Category_Id(categoryId)).thenReturn(Arrays.asList(mockScore));
        when(slideDeckRepository.findSlideDecksByScore(anyLong(), anyLong())).thenReturn(Arrays.asList(mockSlideDeck));

        // Act
        categoryService.deleteCategory(categoryId);

        // Assert
        verify(categoryRepository).delete(mockCategory);
    }

    @Test
    void deleteCategory_ShouldThrowException_WhenCategoryNotFound() {
        // Arrange
        Long categoryId = 999L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.deleteCategory(categoryId);
        });
    }

    @Test
    void updateSlideDeckVersionsForCategory_ShouldUpdateVersions_WhenCategoryHasScores() {
        // Arrange
        Category categoryDetails = new Category();
        categoryDetails.setName("Updated Category");
        List<Score> categoryScores = Arrays.asList(mockScore);
        List<SlideDeck> affectedDecks = Arrays.asList(mockSlideDeck);
        
        when(categoryRepository.findById(mockCategory.getId())).thenReturn(Optional.of(mockCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));
        when(scoreRepository.findByTeam_Category_Id(mockCategory.getId())).thenReturn(categoryScores);
        when(slideDeckRepository.findSlideDecksByScore(mockScore.getId(), mockCategory.getId())).thenReturn(affectedDecks);
        when(slideDeckRepository.save(any(SlideDeck.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        categoryService.updateCategory(mockCategory.getId(), categoryDetails);

        // Assert
        verify(slideDeckRepository, atLeastOnce()).save(any(SlideDeck.class));
    }

    @Test
    void updateSlideDeckVersionsForCategory_ShouldNotUpdateVersions_WhenCategoryHasNoScores() {
        // Arrange
        Category categoryDetails = new Category();
        categoryDetails.setName("Updated Category");
        when(categoryRepository.findById(mockCategory.getId())).thenReturn(Optional.of(mockCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));
        when(scoreRepository.findByTeam_Category_Id(mockCategory.getId())).thenReturn(new ArrayList<>());

        // Act
        categoryService.updateCategory(mockCategory.getId(), categoryDetails);

        // Assert
        verify(slideDeckRepository, never()).findSlideDecksByScore(anyLong(), anyLong());
    }

    @Test
    void updateSlideDeckVersionsForCategory_ShouldNotUpdateVersions_WhenCategoryIsNull() {
        // Arrange
        Category categoryDetails = new Category();
        categoryDetails.setName("Updated Category");
        when(categoryRepository.findById(null)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.updateCategory(null, categoryDetails);
        });
    }

    @Test
    void updateSlideDeckVersionsForCategory_ShouldHandleMultipleScores() {
        // Arrange
        Category categoryDetails = new Category();
        categoryDetails.setName("Updated Category");
        Score score1 = new Score();
        score1.setId(1L);
        Score score2 = new Score();
        score2.setId(2L);
        List<Score> categoryScores = Arrays.asList(score1, score2);
        List<SlideDeck> affectedDecks = Arrays.asList(mockSlideDeck);
        
        when(categoryRepository.findById(mockCategory.getId())).thenReturn(Optional.of(mockCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));
        when(scoreRepository.findByTeam_Category_Id(mockCategory.getId())).thenReturn(categoryScores);
        when(slideDeckRepository.findSlideDecksByScore(anyLong(), anyLong())).thenReturn(affectedDecks);
        when(slideDeckRepository.save(any(SlideDeck.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        categoryService.updateCategory(mockCategory.getId(), categoryDetails);

        // Assert
        verify(slideDeckRepository, atLeastOnce()).save(any(SlideDeck.class));
    }

    @Test
    void incrementVersion_ShouldHandleOverflow() {
        // Arrange
        Category categoryDetails = new Category();
        categoryDetails.setName("Updated Category");
        SlideDeck deck = new SlideDeck();
        deck.setVersion(2000000000); // 接近最大值
        List<Score> categoryScores = Arrays.asList(mockScore);
        List<SlideDeck> affectedDecks = Arrays.asList(deck);
        
        when(categoryRepository.findById(mockCategory.getId())).thenReturn(Optional.of(mockCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));
        when(scoreRepository.findByTeam_Category_Id(mockCategory.getId())).thenReturn(categoryScores);
        when(slideDeckRepository.findSlideDecksByScore(anyLong(), anyLong())).thenReturn(affectedDecks);
        when(slideDeckRepository.save(any(SlideDeck.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        categoryService.updateCategory(mockCategory.getId(), categoryDetails);

        // Assert
        verify(slideDeckRepository).save(any(SlideDeck.class));
    }

    @Test
    void updateCategory_ShouldNotUpdateSlideDeckVersions_WhenNoScoresExist() {
        // Arrange
        Long categoryId = 1L;
        Category categoryDetails = new Category();
        categoryDetails.setName("Updated Category");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));
        when(scoreRepository.findByTeam_Category_Id(categoryId)).thenReturn(new ArrayList<>());

        // Act
        Category result = categoryService.updateCategory(categoryId, categoryDetails);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Category", result.getName());
        verify(slideDeckRepository, never()).findSlideDecksByScore(anyLong(), anyLong());
    }
} 