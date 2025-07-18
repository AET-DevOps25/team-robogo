package de.fll.screen.service;

import de.fll.screen.model.Category;
import de.fll.screen.model.Score;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.repository.CategoryRepository; 
import de.fll.screen.repository.ScoreRepository;
import de.fll.screen.repository.SlideDeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ScoreRepository scoreRepository;
    private final SlideDeckRepository slideDeckRepository;

    @Autowired
    public CategoryService(
        CategoryRepository categoryRepository,
        ScoreRepository scoreRepository,
        SlideDeckRepository slideDeckRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.scoreRepository = scoreRepository;
        this.slideDeckRepository = slideDeckRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
    }

    public Category createCategory(Category category) {
        Category savedCategory = categoryRepository.save(category);
        return savedCategory;
    }

    public Category updateCategory(Long categoryId, Category categoryDetails) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
        
        // 更新category信息
        if (categoryDetails.getName() != null) {
            category.setName(categoryDetails.getName());
        }
        
        Category savedCategory = categoryRepository.save(category);
        
        // 当category发生变化时，更新相关的SlideDeck版本
        updateSlideDeckVersionsForCategory(savedCategory);
        
        return savedCategory;
    }

    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
        
        // 在删除前更新相关的SlideDeck版本
        updateSlideDeckVersionsForCategory(category);
        
        categoryRepository.delete(category);
    }

    /**
     * 更新包含特定Category的Score的SlideDeck版本
     * 当Category发生变化时，需要更新显示该Category的Score的SlideDeck版本
     */
    private void updateSlideDeckVersionsForCategory(Category category) {
        if (category == null) {
            return;
        }
        
        Long categoryId = category.getId();
        
        // 查找该category下所有有score的team
        List<Score> categoryScores = scoreRepository.findByTeam_Category_Id(categoryId);
        
        if (categoryScores.isEmpty()) {
            return; // 如果category下没有score，不需要更新SlideDeck
        }
        
        // 为每个score更新相关的SlideDeck版本
        for (Score score : categoryScores) {
            List<SlideDeck> affectedSlideDecks = 
                slideDeckRepository.findSlideDecksByScore(score.getId(), categoryId);
            
            // 更新每个受影响SlideDeck的版本
            for (SlideDeck slideDeck : affectedSlideDecks) {
                slideDeck.setVersion(incrementVersion(slideDeck.getVersion()));
                slideDeckRepository.save(slideDeck);
            }
        }
    }

    /**
     * 安全地递增版本号，处理溢出问题
     * 当版本号接近最大值时，重置为1
     */
    private int incrementVersion(int currentVersion) {
        // 当版本号达到 2,000,000,000 时重置为 1，避免溢出
        if (currentVersion >= 2000000000) {
            return 1;
        }
        return currentVersion + 1;
    }
} 