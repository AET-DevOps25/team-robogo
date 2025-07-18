import { useAuthFetch } from '@/composables/useAuthFetch'
import type { SlideItem } from '@/interfaces/types'

const BASE_URL = '/api/proxy/slides'

/**
 * 获取所有slide（可选deckId过滤）
 * @param deckId 可选，按deck过滤
 * @returns Promise<SlideItem[]>
 */
export async function fetchSlides(deckId?: number): Promise<SlideItem[]> {
  const { authFetch } = useAuthFetch()
  const url = deckId ? `${BASE_URL}?deckId=${deckId}` : BASE_URL
  return await authFetch<SlideItem[]>(url)
}

/**
 * 获取单个slide
 * @param id slide的id
 * @returns Promise<SlideItem>
 */
export async function fetchSlideById(id: number): Promise<SlideItem> {
  const { authFetch } = useAuthFetch()
  return await authFetch<SlideItem>(`${BASE_URL}/${id}`)
}

/**
 * 新增slide（需指定deckId）
 * @param deckId slide所属deckId
 * @param slide slide对象
 * @returns Promise<SlideItem>
 */
export async function createSlide(deckId: number, slide: SlideItem): Promise<SlideItem> {
  const { authFetch } = useAuthFetch()
  return await authFetch<SlideItem>(`${BASE_URL}?deckId=${deckId}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(slide)
  })
}

/**
 * 更新slide
 * @param id slide的id
 * @param slide slide对象
 * @returns Promise<SlideItem>
 */
export async function updateSlide(id: number, slide: SlideItem): Promise<SlideItem> {
  const { authFetch } = useAuthFetch()
  return await authFetch<SlideItem>(`${BASE_URL}/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(slide)
  })
}

/**
 * 删除slide
 * @param id slide的id
 * @returns Promise<void>
 */
export async function deleteSlide(id: number): Promise<void> {
  const { authFetch } = useAuthFetch()
  await authFetch<void>(`${BASE_URL}/${id}`, { method: 'DELETE' })
}
