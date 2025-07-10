// composables/useSlides.ts
import { ref } from 'vue'
import type { SlideItem } from '@/interfaces/types'

export const slides = ref<SlideItem[]>([])   
export function useSlides() { return { slides } }