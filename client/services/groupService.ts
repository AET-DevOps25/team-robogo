/* ── src/services/groupService.ts ───────────────────────────── */
import type { SlideGroup } from '@/interfaces/types'
import { fakeSlideGroups } from '@/data/fakeSlideGroups'
import { useScreenStore } from '@/stores/useScreenStore'

export async function fetchGroups(): Promise<SlideGroup[]> {
  try {
    const res = await fetch('/api/groups', { cache: 'no-store' })
    if (!res.ok) throw new Error(`HTTP ${res.status}`)

    // 后端返回的结构里没有 lastResetAt —— 统一补上
    const now = Date.now()
    const groups = ((await res.json()) as SlideGroup[]).map(g => ({
      ...g,
      lastResetAt: now
    }))

    return groups
  } catch (err) {
    const now = Date.now()
    return fakeSlideGroups.map(g => ({ ...g, lastResetAt: now }))
  }
}

export async function saveGroup(local: SlideGroup): Promise<SlideGroup> {
  const { lastResetAt, ...payload } = local

  const res = await fetch(`/api/groups/${payload.id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })

  if (!res.ok) {
    throw new Error(String(res.status))
  }

  const saved: SlideGroup = await res.json()

  const merged: SlideGroup = { ...saved, lastResetAt }

  const store = useScreenStore()
  store.replaceGroup(merged)

  return merged
}
