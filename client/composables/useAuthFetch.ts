import { useAuth } from '#imports'

export function useAuthFetch() {
  const { token } = useAuth()

  async function authFetch<T = any>(url: string, options: RequestInit = {}): Promise<T> {
    const headers = {
      ...(options.headers || {}),
      Authorization: token.value ? `${token.value}` : ''
    }
    console.log('authFetch', url, headers)
    const res = await fetch(url, { ...options, headers })
    if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`)
    const contentType = res.headers.get('content-type')
    if (contentType && contentType.includes('application/json')) {
      return res.json()
    }
    return res.blob() as any
  }

  return { authFetch }
}
