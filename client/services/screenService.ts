import type { ScreenContent } from '@/interfaces/types'
import { useAuthFetch } from '@/composables/useAuthFetch'

const BASE_URL = '/api/proxy/screens'

export async function fetchScreens(): Promise<ScreenContent[]> {
  const { authFetch } = useAuthFetch()
  return await authFetch<ScreenContent[]>(BASE_URL)
}

export async function fetchScreenById(id: string | number): Promise<ScreenContent> {
  const { authFetch } = useAuthFetch()
  return await authFetch<ScreenContent>(`${BASE_URL}/${id}`)
}

export async function createScreen(screen: ScreenContent): Promise<ScreenContent> {
  const { authFetch } = useAuthFetch()
  return await authFetch<ScreenContent>(BASE_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(screen)
  })
}

export async function updateScreen(
  id: string | number,
  screen: ScreenContent
): Promise<ScreenContent> {
  const { authFetch } = useAuthFetch()
  return await authFetch<ScreenContent>(`${BASE_URL}/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(screen)
  })
}

export async function deleteScreen(id: string | number): Promise<void> {
  const { authFetch } = useAuthFetch()
  await authFetch<void>(`${BASE_URL}/${id}`, { method: 'DELETE' })
}

export async function assignSlideDeck(
  screenId: string | number,
  slideDeckId: string | number
): Promise<ScreenContent> {
  const { authFetch } = useAuthFetch()
  return await authFetch<ScreenContent>(
    `${BASE_URL}/${screenId}/assign-slide-deck/${slideDeckId}`,
    { method: 'POST' }
  )
}

export async function fetchScreenContent(id: string | number): Promise<ScreenContent> {
  const { authFetch } = useAuthFetch()
  return await authFetch<ScreenContent>(`${BASE_URL}/${id}/content`)
}

export async function updateScreenStatus(
  id: string | number,
  status: string
): Promise<ScreenContent> {
  const { authFetch } = useAuthFetch()
  return await authFetch<ScreenContent>(
    `${BASE_URL}/${id}/status?status=${encodeURIComponent(status)}`,
    { method: 'PUT' }
  )
} 