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