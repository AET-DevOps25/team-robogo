import type { SlideItem } from '@/interfaces/types'
import { mockSlides } from '@/data/mockSlides'
import { useAuthFetch } from '@/composables/useAuthFetch'

export async function fetchAvailableSlides(): Promise<SlideItem[]> {
  try {
    // TODO: fix fetch and api, remove mockSlides

    const { authFetch } = useAuthFetch()
    const data = await authFetch<any>('/api/proxy/slide-images', {
      cache: 'no-store'
    })
    console.log(data)
    // return data
    return mockSlides
  } catch (err) {
    console.warn('[slideService] /api/slides failed，use mock slides: ', err)
    return mockSlides
  }
}
