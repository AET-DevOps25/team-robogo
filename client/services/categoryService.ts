import { useAuthFetch } from '@/composables/useAuthFetch'
import type { Category } from '@/interfaces/types'

const BASE_URL = '/api/proxy/categories'

/**
 * 获取所有分类
 * @returns Promise<Category[]>
 */
export async function fetchCategories(): Promise<Category[]> {
  const { authFetch } = useAuthFetch()
  return await authFetch<Category[]>(BASE_URL)
}

/**
 * 根据ID获取分类
 * @param id 分类ID
 * @returns Promise<Category>
 */
export async function fetchCategoryById(id: number): Promise<Category> {
  const { authFetch } = useAuthFetch()
  return await authFetch<Category>(`${BASE_URL}/${id}`)
}

/**
 * 创建分类
 * @param category Category对象
 * @returns Promise<Category>
 */
export async function createCategory(category: Omit<Category, 'id'>): Promise<Category> {
  const { authFetch } = useAuthFetch()
  return await authFetch<Category>(BASE_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(category)
  })
}

/**
 * 删除分类
 * @param id 分类ID
 * @returns Promise<void>
 */
export async function deleteCategory(id: number): Promise<void> {
  const { authFetch } = useAuthFetch()
  await authFetch<void>(`${BASE_URL}/${id}`, { method: 'DELETE' })
} 