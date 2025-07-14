/* ── src/services/groupService.ts ───────────────────────────── */
import type { SlideGroup } from '@/interfaces/types'
import { mockSlideGroups } from '@/data/mockSlideGroups' // mock
import { useScreenStore } from '@/stores/useScreenStore'
import { useAuthFetch } from '@/composables/useAuthFetch'
export async function fetchGroups(): Promise<SlideGroup[]> {
  try {
    // TODO: fix fetch and api, remove mockgroups

    const { authFetch } = useAuthFetch()
    const res = await authFetch<any>('/api/proxy/slidedecks', {
      cache: 'no-store'
    })
    if (!res.ok) throw new Error(`HTTP ${res.status}`)

    // add lastResetAt for all groups - which is not stored in database
    const now = Date.now()
    const groups = ((await res.json()) as SlideGroup[]).map(g => ({
      ...g,
      lastResetAt: now
    }))

    return groups
  } catch (err) {
    console.warn('[groupService] /api/groups failed, use mock groups:', err)

    // mock
    const now = Date.now()
    return mockSlideGroups.map(g => ({ ...g, lastResetAt: now }))
  }
}

/** save group; merge version if successfully saved; keep lastResetAt */
export async function saveGroup(local: SlideGroup): Promise<SlideGroup> {
  // 1) put group info w/o lastResetAt - which is not in database
  const { lastResetAt, ...payload } = local
  const { authFetch } = useAuthFetch()
  const res = await authFetch<any>(`/api/proxy/slidedecks/${payload.id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })
  console.log(res)
  if (!res.ok) {
    // 409 version conflict
    throw new Error(String(res.status))
  }

  // 2) get new group info including new group version
  const saved: SlideGroup = await res.json()

  // 3) replace local group with new group：keep lastResetAt
  const merged: SlideGroup = { ...saved, lastResetAt }

  // write back to store
  const store = useScreenStore()
  store.replaceGroup(merged) //

  return merged
}

interface CreateDeckDTO {
  name: string
}
interface DeckDTO {
  id: number
  name: string
  transitionTime: number
  version: number
}

function dtoToGroup(dto: DeckDTO): SlideGroup {
  return {
    id: String(dto.id),
    slideIds: [],
    speed: dto.transitionTime,
    version: dto.version,
    lastResetAt: Date.now()
  }
}

/** POST /slidedecks  - create new group (blank) */
export async function createGroup(name: string): Promise<SlideGroup> {
  const { authFetch } = useAuthFetch()

  const res = await authFetch('/api/proxy/slidedecks', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(<CreateDeckDTO>{ name })
  })

  if (!res.ok) throw new Error(`HTTP ${res.status}`)

  const dto = (await res.json()) as DeckDTO
  return dtoToGroup(dto)
}
