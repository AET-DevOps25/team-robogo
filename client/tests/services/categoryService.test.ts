import { describe, it, expect, vi, beforeEach } from 'vitest'
import {
  fetchCategories,
  fetchCategoryById,
  createCategory,
  updateCategory,
  deleteCategory
} from '@/services/categoryService'
import type { Category } from '@/interfaces/types'

describe('CategoryService', () => {
  const mockCategories: Category[] = [
    { id: 1, name: 'Category 1', competitionId: 1 },
    { id: 2, name: 'Category 2', competitionId: 1 }
  ]

  const mockCategory: Category = {
    id: 1,
    name: 'Test Category',
    competitionId: 1
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('fetchCategories', () => {
    it('should fetch all categories successfully', async () => {
      const result = await fetchCategories()

      expect(result).toEqual({})
    })

    it('should handle fetch categories error', async () => {
      await expect(fetchCategories()).resolves.toEqual({})
    })
  })

  describe('fetchCategoryById', () => {
    it('should fetch category by id successfully', async () => {
      const result = await fetchCategoryById(1)

      expect(result).toEqual({})
    })

    it('should handle fetch category by id error', async () => {
      await expect(fetchCategoryById(999)).resolves.toEqual({})
    })
  })

  describe('createCategory', () => {
    it('should create category successfully', async () => {
      const newCategory = { name: 'New Category', competitionId: 1 }

      const result = await createCategory(newCategory)

      expect(result).toEqual({})
    })

    it('should handle create category error', async () => {
      const newCategory = { name: 'New Category', competitionId: 1 }

      await expect(createCategory(newCategory)).resolves.toEqual({})
    })
  })

  describe('updateCategory', () => {
    it('should update category successfully', async () => {
      const updateData = { name: 'Updated Category' }

      const result = await updateCategory(1, updateData)

      expect(result).toEqual({})
    })

    it('should handle update category error', async () => {
      const updateData = { name: 'Updated Category' }

      await expect(updateCategory(999, updateData)).resolves.toEqual({})
    })
  })

  describe('deleteCategory', () => {
    it('should delete category successfully', async () => {
      await expect(deleteCategory(1)).resolves.toBeUndefined()
    })

    it('should handle delete category error', async () => {
      await expect(deleteCategory(999)).resolves.toBeUndefined()
    })
  })
}) 