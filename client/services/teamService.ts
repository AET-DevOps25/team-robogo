import { useAuthFetch } from '@/composables/useAuthFetch'
import type { Team } from '@/interfaces/types'

const BASE_URL = '/api/proxy/teams'

/**
 * 获取所有团队
 * @returns Promise<Team[]>
 */
export async function fetchTeams(): Promise<Team[]> {
  const { authFetch } = useAuthFetch()
  return await authFetch<Team[]>(BASE_URL)
}

/**
 * 获取单个团队
 * @param teamId 团队id
 * @returns Promise<Team>
 */
export async function fetchTeamById(teamId: string | number): Promise<Team> {
  const { authFetch } = useAuthFetch()
  return await authFetch<Team>(`${BASE_URL}/${teamId}`)
}

/**
 * 创建团队
 * @param team Team对象
 * @returns Promise<Team>
 */
export async function createTeam(team: Omit<Team, 'id'>): Promise<Team> {
  const { authFetch } = useAuthFetch()
  return await authFetch<Team>(BASE_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(team)
  })
}

/**
 * 更新团队分类
 * @param teamId 团队ID
 * @param categoryId 分类ID
 * @returns Promise<Team>
 */
export async function updateTeamCategory(teamId: number, categoryId: number): Promise<Team> {
  const { authFetch } = useAuthFetch()
  return await authFetch<Team>(`${BASE_URL}/${teamId}/category`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(categoryId)
  })
}

/**
 * 删除团队
 * @param teamId 团队id
 * @returns Promise<void>
 */
export async function deleteTeam(teamId: string | number): Promise<void> {
  const { authFetch } = useAuthFetch()
  await authFetch<void>(`${BASE_URL}/${teamId}`, { method: 'DELETE' })
}
