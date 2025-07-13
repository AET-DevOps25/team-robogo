import type { SlideItem } from '@/interfaces/types'
import { mockSlides } from '@/data/mockSlides'
export async function fetchAvailableSlides(): Promise<SlideItem[]> {
  try {
    const res = await fetch('/api/slides', { cache: 'no-store' })
    if (!res.ok) throw new Error(`HTTP ${res.status}`)

    return (await res.json()) as SlideItem[]
  } catch (err) {
    return mockSlides
  }
}
