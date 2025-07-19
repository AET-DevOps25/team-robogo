import { useAuthFetch } from '@/composables/useAuthFetch'
import type { SlideDeck, SlideItem } from '@/interfaces/types'

const BASE_URL = '/api/proxy/slidedecks'

/**
 * 获取所有 slideDeck
 * @returns Promise<SlideDeck[]>
 */
export async function fetchSlideDecks(): Promise<SlideDeck[]> {
  const { authFetch } = useAuthFetch()
  const decks = await authFetch<SlideDeck[]>(BASE_URL)
  return decks
}

/**
 * 获取指定 slideDeck
 * @param deckId slideDeck 的 id
 * @returns Promise<SlideDeck>
 */
export async function fetchSlideDeckById(deckId: string | number): Promise<SlideDeck> {
  const { authFetch } = useAuthFetch()
  return await authFetch<SlideDeck>(`${BASE_URL}/${deckId}`)
}

/**
 * 创建 slideDeck
 * @param deck slideDeck 对象
 * @returns Promise<SlideDeck>
 */
export async function createSlideDeck(deck: SlideDeck): Promise<SlideDeck> {
  const { authFetch } = useAuthFetch()
  return await authFetch<SlideDeck>(BASE_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(deck)
  })
}

/**
 * 更新 slideDeck
 * @param deckId slideDeck 的 id
 * @param deck slideDeck 对象
 * @returns Promise<SlideDeck>
 */
export async function updateSlideDeck(
  deckId: string | number,
  deck: Partial<SlideDeck>
): Promise<SlideDeck> {
  const { authFetch } = useAuthFetch()
  return await authFetch<SlideDeck>(`${BASE_URL}/${deckId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(deck)
  })
}

/**
 * 删除 slideDeck
 * @param deckId slideDeck 的 id
 * @returns Promise<void>
 */
export async function deleteSlideDeck(deckId: string | number): Promise<void> {
  const { authFetch } = useAuthFetch()
  await authFetch<void>(`${BASE_URL}/${deckId}`, { method: 'DELETE' })
}

/**
 * 重新排序 slideDeck 内的 slides
 * @param deckId slideDeck 的 id
 * @param newOrder slide id 数组
 * @returns Promise<SlideDeck>
 */
export async function reorderSlides(
  deckId: string | number,
  newOrder: (string | number)[]
): Promise<SlideDeck> {
  const { authFetch } = useAuthFetch()
  return await authFetch<SlideDeck>(`${BASE_URL}/${deckId}/slides/reorder`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(newOrder)
  })
}

/**
 * 获取指定 slideDeck 下的单个 slide（多态）
 * @param deckId slideDeck 的 id
 * @param slideId slide 的 id
 * @returns Promise<SlideItem>
 */
export async function fetchSlideById(
  deckId: string | number,
  slideId: string | number
): Promise<SlideItem> {
  const { authFetch } = useAuthFetch()
  return await authFetch<SlideItem>(`${BASE_URL}/${deckId}/slides/${slideId}`)
}

/**
 * 更新 slideDeck 的播放速度
 * @param deckId slideDeck 的 id
 * @param transitionTime 过渡时间（秒）
 * @returns Promise<SlideDeck>
 */
export async function updateSlideDeckSpeed(
  deckId: string | number,
  transitionTime: number
): Promise<SlideDeck> {
  const { authFetch } = useAuthFetch()
  return await authFetch<SlideDeck>(`${BASE_URL}/${deckId}/speed`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ transitionTime })
  })
}
