import { useAuthFetch } from '@/composables/useAuthFetch'
import type { Score, Category, SlideItem } from '@/interfaces/types'

const BASE_URL = '/api/proxy/scores'

/**
 * 根据分类获取分数
 * @param categoryId 分类ID
 * @returns Promise<Score[]>
 */
export async function fetchScoresByCategory(categoryId: number): Promise<Score[]> {
  const { authFetch } = useAuthFetch()
  return await authFetch<Score[]>(`${BASE_URL}/category/${categoryId}`)
}

/**
 * 根据分类获取高亮分数
 * @param category 分类对象
 * @returns Promise<Score[]>
 */
export async function fetchScoresWithHighlight(category: Category): Promise<Score[]> {
  const { authFetch } = useAuthFetch()
  return await authFetch<Score[]>(`${BASE_URL}/highlight`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(category)
  })
}

/**
 * 添加分数
 * @param score Score对象
 * @returns Promise<Score>
 */
export async function addScore(score: Score): Promise<Score> {
  const { authFetch } = useAuthFetch()
  return await authFetch<Score>(BASE_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(score)
  })
}

/**
 * 更新分数
 * @param scoreId 分数ID
 * @param score Score对象
 * @returns Promise<Score>
 */
export async function updateScore(scoreId: number, score: Partial<Score>): Promise<Score> {
  const { authFetch } = useAuthFetch()
  return await authFetch<Score>(`${BASE_URL}/${scoreId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(score)
  })
}

/**
 * 删除分数
 * @param scoreId 分数ID
 * @returns Promise<void>
 */
export async function deleteScore(scoreId: number): Promise<void> {
  const { authFetch } = useAuthFetch()
  await authFetch<void>(`${BASE_URL}/${scoreId}`, { method: 'DELETE' })
}

/**
 * 创建ScoreSlide
 * @param scoreSlide ScoreSlide对象（SlideItem类型，type为SCORE）
 * @returns Promise<SlideItem>
 */
export async function createScoreSlide(scoreSlide: SlideItem): Promise<SlideItem> {
  const { authFetch } = useAuthFetch()
  return await authFetch<SlideItem>(`${BASE_URL}/slide`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(scoreSlide)
  })
}
