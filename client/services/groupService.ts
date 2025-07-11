/* ── src/services/groupService.ts ───────────────────────────── */
import type { SlideGroup } from '@/interfaces/types'
import { fakeSlideGroups }      from '@/data/fakeSlideGroups'       // 同一个 mock 文件也行


export async function fetchGroups(): Promise<SlideGroup[]> {
  try {
    const res = await fetch('/api/groups', { cache: 'no-store' })//replace with api later
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    return (await res.json()) as SlideGroup[]
  } catch (err) {
    console.warn('[groupService] /api/groups failed, use mock groups:', err)
    return fakeSlideGroups
  }
}

export async function saveGroup(g: SlideGroup): Promise<SlideGroup> {
  const res = await fetch(`/api/groups/${g.id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(g)
  })
  if (!res.ok) throw new Error(String(res.status))
  return await res.json()        // 后端已把 version++
}

export async function fetchGroupVersion(id: string): Promise<number> {
  const res = await fetch(`/api/groups/${id}/version`)
  if (!res.ok) throw new Error(String(res.status))
  return (await res.json()).version as number
}