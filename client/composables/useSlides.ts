import { ref } from 'vue'
import type { SlideItem } from '@/interfaces/types'
import { fetchAvailableSlides } from '@/services/slideService'

const slides = ref<SlideItem[]>([])

export function useSlides() {
  async function refresh() {
    slides.value = await fetchAvailableSlides() // 打接口
  }

  /** 本地上传时把新 Slide 塞进列表（同时可选再调后台） */
  function add(newSlide: SlideItem) {
    slides.value.push(newSlide)
  }

  return { slides, refresh, add }
}
