import type { ScreenContent } from '@/interfaces/types'
import { useAuthFetch } from '@/composables/useAuthFetch'

const BASE_URL = '/api/proxy/screens'

/**
 * 获取所有 screens
 * @returns Promise<ScreenContent[]>
 */
export async function fetchScreens(): Promise<ScreenContent[]> {
  const { authFetch } = useAuthFetch()
  return await authFetch<ScreenContent[]>(BASE_URL)
}

/**
 * 获取指定 screen
 * @param id screen 的 id
 * @returns Promise<ScreenContent>
 */
export async function fetchScreenById(id: number): Promise<ScreenContent> {
  const { authFetch } = useAuthFetch()
  return await authFetch<ScreenContent>(`${BASE_URL}/${id}`)
}

/**
 * 创建 screen
 * @param screen screenContent 对象
 * @returns Promise<ScreenContent>
 */
export async function createScreen(screen: ScreenContent): Promise<ScreenContent> {
  const { authFetch } = useAuthFetch()
  return await authFetch<ScreenContent>(BASE_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(screen)
  })
}

/**
 * 更新 screen
 * @param id screen 的 id
 * @param screen screenContent 对象
 * @returns Promise<ScreenContent>
 */
export async function updateScreen(id: number, screen: ScreenContent): Promise<ScreenContent> {
  const { authFetch } = useAuthFetch()
  return await authFetch<ScreenContent>(`${BASE_URL}/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(screen)
  })
}

/**
 * 删除 screen
 * @param id screen 的 id
 * @returns Promise<void>
 */
export async function deleteScreen(id: number): Promise<void> {
  const { authFetch } = useAuthFetch()
  await authFetch<void>(`${BASE_URL}/${id}`, { method: 'DELETE' })
}

/**
 * 为 screen 分配 slideDeck
 * @param screenId screen 的 id
 * @param slideDeckId slideDeck 的 id
 * @returns Promise<ScreenContent>
 */
export async function assignSlideDeck(
  screenId: number,
  slideDeckId: number
): Promise<ScreenContent> {
  const { authFetch } = useAuthFetch()
  return await authFetch<ScreenContent>(
    `${BASE_URL}/${screenId}/assign-slide-deck/${slideDeckId}`,
    { method: 'POST' }
  )
}

/**
 * 获取指定 screen 的内容
 * @param id screen 的 id
 * @returns Promise<ScreenContent>
 */
export async function fetchScreenContent(id: number): Promise<ScreenContent> {
  const { authFetch } = useAuthFetch()
  return await authFetch<ScreenContent>(`${BASE_URL}/${id}/content`)
}

/**
 * 更新 screen 状态
 * @param id screen 的 id
 * @param status 新状态
 * @returns Promise<ScreenContent>
 */
export async function updateScreenStatus(id: number, status: string): Promise<ScreenContent> {
  const { authFetch } = useAuthFetch()
  return await authFetch<ScreenContent>(
    `${BASE_URL}/${id}/status?status=${encodeURIComponent(status)}`,
    { method: 'PUT' }
  )
}


