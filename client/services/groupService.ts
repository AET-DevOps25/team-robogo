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
    console.warn('[groupService] /api/groups failed, use mock groups:', err)

    // mock 同样直接覆盖
    const now = Date.now()
    return fakeSlideGroups.map(g => ({ ...g, lastResetAt: now }))
  }
}

/** 保存分组；成功后自动合并 version、保留 lastResetAt */
export async function saveGroup(local: SlideGroup): Promise<SlideGroup> {
  // 1) 去掉 lastResetAt，只发后端需要的结构字段
  const { lastResetAt, ...payload } = local

  const res = await fetch(`/api/groups/${payload.id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })

  if (!res.ok) {
    // 409＝版本冲突，其他 status 自行处理
    throw new Error(String(res.status))
  }

  // 2) 后端返回的新 version / 结构
  const saved: SlideGroup = await res.json()

  // 3) 合并回本地：保留原 lastResetAt
  const merged: SlideGroup = { ...saved, lastResetAt }

  // 根据需要写回 store
  const store = useScreenStore()
  store.replaceGroup(merged) // 第二个参数可选：是否重播

  return merged
}
