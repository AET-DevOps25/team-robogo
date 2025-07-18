package de.fll.screen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fll.core.dto.CategoryDTO;
import de.fll.screen.assembler.CategoryAssembler;
import de.fll.screen.model.Category;
import de.fll.screen.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = {CategoryController.class, GlobalExceptionHandler.class},
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
    }
)
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private CategoryAssembler categoryAssembler;

    @Autowired
    private ObjectMapper objectMapper;

    private Category category1;
    private Category category2;
    private CategoryDTO categoryDTO1;
    private CategoryDTO categoryDTO2;

    @BeforeEach
    void setUp() {
        category1 = new Category();
        setId(category1, 1L);
        category1.setName("Test Category 1");

        category2 = new Category();
        setId(category2, 2L);
        category2.setName("Test Category 2");

        categoryDTO1 = new CategoryDTO();
        categoryDTO1.setId(1L);
        categoryDTO1.setName("Test Category 1");

        categoryDTO2 = new CategoryDTO();
        categoryDTO2.setId(2L);
        categoryDTO2.setName("Test Category 2");
    }

    private void setId(Object entity, Long id) {
        try {
            // 首先尝试在当前类中查找 id 字段
            java.lang.reflect.Field field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            setFieldValue(field, entity, id);
        } catch (NoSuchFieldException e) {
            // 如果当前类没有 id 字段，尝试在父类中查找
            try {
                java.lang.reflect.Field field = entity.getClass().getSuperclass().getDeclaredField("id");
                field.setAccessible(true);
                setFieldValue(field, entity, id);
            } catch (Exception ex) {
                throw new RuntimeException("Cannot set id field on " + entity.getClass().getName(), ex);
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot set id field on " + entity.getClass().getName(), e);
        }
    }

    private void setFieldValue(java.lang.reflect.Field field, Object entity, Long id) throws IllegalAccessException {
        if (field.getType() == long.class) {
            field.setLong(entity, id);
        } else if (field.getType() == Long.class) {
            field.set(entity, id);
        } else if (field.getType() == int.class) {
            field.setInt(entity, id.intValue());
        } else if (field.getType() == Integer.class) {
            field.set(entity, id.intValue());
        } else {
            field.set(entity, id);
        }
    }

    @Test
    void testGetAllCategories() throws Exception {
        List<Category> categories = Arrays.asList(category1, category2);
        when(categoryService.getAllCategories()).thenReturn(categories);
        when(categoryAssembler.toDTO(category1)).thenReturn(categoryDTO1);
        when(categoryAssembler.toDTO(category2)).thenReturn(categoryDTO2);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Category 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Test Category 2"));
    }

    @Test
    void testGetCategoryById() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(category1);
        when(categoryAssembler.toDTO(category1)).thenReturn(categoryDTO1);

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Category 1"));
    }

    @Test
    void testCreateCategory() throws Exception {
        Category newCategory = new Category();
        newCategory.setName("New Category");
        setId(newCategory, 3L);

        CategoryDTO newCategoryDTO = new CategoryDTO();
        newCategoryDTO.setId(3L);
        newCategoryDTO.setName("New Category");

        when(categoryService.createCategory(any(Category.class))).thenReturn(newCategory);
        when(categoryAssembler.toDTO(newCategory)).thenReturn(newCategoryDTO);

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategoryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("New Category"));
    }

    @Test
    void testUpdateCategory() throws Exception {
        Category updatedCategory = new Category();
        updatedCategory.setName("Updated Category");
        setId(updatedCategory, 1L);

        CategoryDTO updatedCategoryDTO = new CategoryDTO();
        updatedCategoryDTO.setId(1L);
        updatedCategoryDTO.setName("Updated Category");

        when(categoryService.updateCategory(eq(1L), any(Category.class))).thenReturn(updatedCategory);
        when(categoryAssembler.toDTO(updatedCategory)).thenReturn(updatedCategoryDTO);

        mockMvc.perform(put("/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCategoryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Category"));
    }

    @Test
    void testDeleteCategory() throws Exception {
        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCategoryByIdNotFound() throws Exception {
        when(categoryService.getCategoryById(999L)).thenThrow(new RuntimeException("Category not found"));

        mockMvc.perform(get("/categories/999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testCreateCategoryWithEmptyName() throws Exception {
        CategoryDTO emptyNameDTO = new CategoryDTO();
        emptyNameDTO.setName("");

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyNameDTO)))
                .andExpect(status().isOk()); // 根据实际业务逻辑调整期望状态
    }

    @Test
    void testUpdateCategoryWithEmptyName() throws Exception {
        CategoryDTO emptyNameDTO = new CategoryDTO();
        emptyNameDTO.setName("");

        Category updatedCategory = new Category();
        updatedCategory.setName("");
        setId(updatedCategory, 1L);

        when(categoryService.updateCategory(eq(1L), any(Category.class))).thenReturn(updatedCategory);
        when(categoryAssembler.toDTO(updatedCategory)).thenReturn(emptyNameDTO);

        mockMvc.perform(put("/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyNameDTO)))
                .andExpect(status().isOk());
    }
} 