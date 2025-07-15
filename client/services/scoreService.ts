import { useAuthFetch } from '@/composables/useAuthFetch'
import type { Score, Category, SlideItem } from '@/interfaces/types'

const BASE_URL = '/api/proxy/scores'

/**
 * 获取所有分数
 * @returns Promise<Score[]>
 */
export async function fetchAllScores(): Promise<Score[]> {
  const { authFetch } = useAuthFetch()
  return await authFetch<Score[]>(BASE_URL)
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