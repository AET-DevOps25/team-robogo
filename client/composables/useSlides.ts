import { ref, watch } from 'vue'
import type { ImageSlideMeta } from '@/interfaces/types'
import { fetchAllImageMetas } from '@/services/slideImageService'
export function useSlides(slideDeck: { slides: ImageSlideMeta[] }) {
  // 本地 slides 响应式副本
  const slides = ref<ImageSlideMeta[]>([...slideDeck.slides])

  // 保持与外部 slideDeck.slides 同步
  watch(
    () => slideDeck.slides,
    newSlides => {
      slides.value = [...newSlides]
    },
    { deep: true }
  )
  async function refresh() {
    slides.value = await fetchAllImageMetas() // 打接口
  }
  function add(newSlide: ImageSlideMeta) {
    slides.value.push(newSlide)
    slideDeck.slides.push(newSlide) // 同步到原 deck
  }

  function remove(slideId: string | number) {
    slides.value = slides.value.filter((s: ImageSlideMeta) => s.id !== slideId)
    slideDeck.slides = slideDeck.slides.filter((s: ImageSlideMeta) => s.id !== slideId)
  }

  // 其它操作如 reorder、update 可继续扩展

  return { slides, add, remove, refresh }
}
