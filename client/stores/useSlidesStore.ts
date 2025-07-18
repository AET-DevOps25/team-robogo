import { defineStore } from 'pinia'
import { ref, watch } from 'vue'
import { fetchSlides } from '@/services/slideService'
import type { SlideItem } from '@/interfaces/types'

export const useSlidesStore = defineStore('slides', () => {
  const allSlides = ref<SlideItem[]>([])
  let timer: ReturnType<typeof setInterval> | null = null
  const slideChangeCallbacks: Array<(slides: SlideItem[]) => void> = []

  // 拉取全部slides
  async function refresh() {
    allSlides.value = await fetchSlides()
  }

  // 启动定时轮询
  function startAutoRefresh(interval = 10000) {
    stopAutoRefresh()
    timer = setInterval(refresh, interval)
    refresh()
  }

  // 停止自动刷新
  function stopAutoRefresh() {
    if (timer) {
      clearInterval(timer)
      timer = null
    }
  }

  // 注册slides变化回调
  function onSlidesChanged(cb: (slides: SlideItem[]) => void) {
    slideChangeCallbacks.push(cb)
  }

  // 监听allSlides变化，自动触发所有回调
  watch(allSlides, newVal => {
    slideChangeCallbacks.forEach(cb => cb(newVal))
  })

  return {
    allSlides,
    refresh,
    startAutoRefresh,
    stopAutoRefresh,
    onSlidesChanged
  }
})
