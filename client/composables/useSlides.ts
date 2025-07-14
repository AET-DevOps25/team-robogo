import { ref, watch } from 'vue'
import type { SlideItem } from '@/interfaces/types'

export function useSlides(slideDeck: { slides: SlideItem[] }) {
  // 本地 slides 响应式副本
  const slides = ref<SlideItem[]>([...slideDeck.slides])

  // 保持与外部 slideDeck.slides 同步
  watch(
    () => slideDeck.slides,
    newSlides => {
      slides.value = [...newSlides]
    },
    { deep: true }
  )

  function add(newSlide: SlideItem) {
    slides.value.push(newSlide)
    slideDeck.slides.push(newSlide) // 同步到原 deck
  }

  function remove(slideId: string | number) {
    slides.value = slides.value.filter((s: SlideItem) => s.id !== slideId)
    slideDeck.slides = slideDeck.slides.filter((s: SlideItem) => s.id !== slideId)
  }

  // 其它操作如 reorder、update 可继续扩展

  return { slides, add, remove }
}
