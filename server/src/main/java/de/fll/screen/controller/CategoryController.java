package de.fll.screen.controller;

import de.fll.core.dto.CategoryDTO;
import de.fll.screen.model.Category;
import de.fll.screen.service.CategoryService;
import de.fll.screen.assembler.CategoryAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryAssembler categoryAssembler;

    // 获取所有分类
    @GetMapping
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories().stream()
                .map(categoryAssembler::toDTO)
                .collect(Collectors.toList());
    }

    // 根据ID获取分类
    @GetMapping("/{id}")
    public CategoryDTO getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return categoryAssembler.toDTO(category);
    }
} 