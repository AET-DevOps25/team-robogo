import type { ScreenContentDTO } from '@/interfaces/dto'

const BASE_URL = '/api/screens'

/** 获取所有屏幕 */
export async function fetchScreens(): Promise<ScreenContentDTO[]> {
  const res = await fetch(BASE_URL)
  if (!res.ok) throw new Error(`HTTP ${res.status}`)
  return await res.json()
}

/** 获取单个屏幕 */
export async function fetchScreenById(id: string | number): Promise<ScreenContentDTO> {
  const res = await fetch(`${BASE_URL}/${id}`)
  if (!res.ok) throw new Error(`HTTP ${res.status}`)
  return await res.json()
}

/** 创建新屏幕 */
export async function createScreen(screen: ScreenContentDTO): Promise<ScreenContentDTO> {
  const res = await fetch(BASE_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(screen)
  })
  if (!res.ok) throw new Error(`HTTP ${res.status}`)
  return await res.json()
}

/** 更新屏幕 */
export async function updateScreen(
  id: string | number,
  screen: ScreenContentDTO
): Promise<ScreenContentDTO> {
  const res = await fetch(`${BASE_URL}/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(screen)
  })
  if (!res.ok) throw new Error(`HTTP ${res.status}`)
  return await res.json()
}

/** 删除屏幕 */
export async function deleteScreen(id: string | number): Promise<void> {
  const res = await fetch(`${BASE_URL}/${id}`, { method: 'DELETE' })
  if (!res.ok) throw new Error(`HTTP ${res.status}`)
}

/** 给屏幕分配幻灯片组 */
export async function assignSlideDeck(
  screenId: string | number,
  slideDeckId: string | number
): Promise<ScreenContentDTO> {
  const res = await fetch(`${BASE_URL}/${screenId}/assign-slide-deck/${slideDeckId}`, {
    method: 'POST'
  })
  if (!res.ok) throw new Error(`HTTP ${res.status}`)
  return await res.json()
}

/** 获取屏幕内容 */
export async function fetchScreenContent(id: string | number): Promise<ScreenContentDTO> {
  const res = await fetch(`${BASE_URL}/${id}/content`)
  if (!res.ok) throw new Error(`HTTP ${res.status}`)
  return await res.json()
}

/** 更新屏幕状态 */
export async function updateScreenStatus(
  id: string | number,
  status: string
): Promise<ScreenContentDTO> {
  const res = await fetch(`${BASE_URL}/${id}/status?status=${encodeURIComponent(status)}`, {
    method: 'PUT'
  })
  if (!res.ok) throw new Error(`HTTP ${res.status}`)
  return await res.json()
}
