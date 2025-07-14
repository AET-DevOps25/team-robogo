package de.fll.screen.assembler;

import de.fll.core.dto.CategoryDTO;
import de.fll.screen.model.Category;
import de.fll.screen.model.CategoryScoring;
import de.fll.screen.repository.CategoryRepository;
import de.fll.screen.repository.CompetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryAssembler implements AbstractDTOAssembler<Category, CategoryDTO> {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    public CategoryDTO toDTO(Category category) {
        if (category == null) return null;
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .competitionId(category.getCompetition() != null ? category.getCompetition().getId() : null)
                .categoryScoring(category.getCategoryScoring() != null ? category.getCategoryScoring().name() : null)
                .build();
    }

    @Override
    public Category fromDTO(CategoryDTO dto) {
        if (dto == null) return null;
        if (dto.getId() != null) {
            return categoryRepository.findById(dto.getId()).orElse(null);
        }
        Category category = new Category();
        category.setName(dto.getName());
        category.setCompetition(competitionRepository.findById(dto.getCompetitionId()).orElse(null));
        category.setCategoryScoring(dto.getCategoryScoring() != null ? CategoryScoring.valueOf(dto.getCategoryScoring()) : null);
        return category;
    }
} 