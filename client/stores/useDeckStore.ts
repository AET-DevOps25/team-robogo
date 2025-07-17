import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { SlideItem } from '@/interfaces/types'

export const useDeckStore = defineStore('deck', () => {
  // 当前播放的deckId
  const currentDeckId = ref<number | null>(null)
  // 当前播放的slide索引
  const currentSlideIndex = ref(0)
  // 定时器句柄
  const timer = ref<ReturnType<typeof setInterval> | null>(null)
  // 切换间隔（毫秒）
  const interval = ref(5000)
  // 当前deck的slide数量
  const slideCount = ref(0)

  // 启动timer，支持自定义interval
  function startTimer(deckId: number, slides: SlideItem[], customInterval?: number) {
    stopTimer()
    currentDeckId.value = deckId
    currentSlideIndex.value = 0
    slideCount.value = slides.length
    if (customInterval) interval.value = customInterval
    timer.value = setInterval(() => {
      if (slideCount.value > 0) {
        currentSlideIndex.value = (currentSlideIndex.value + 1) % slideCount.value
      }
    }, interval.value)
  }

  // 停止timer
  function stopTimer() {
    if (timer.value) {
      clearInterval(timer.value)
      timer.value = null
    }
  }

  // 重置timer
  function resetTimer() {
    currentSlideIndex.value = 0
  }

  // 更新切换速度
  function setIntervalMs(ms: number) {
    interval.value = ms
    // 如果timer正在运行，重启timer以应用新速度
    if (timer.value && currentDeckId.value !== null && slideCount.value > 0) {
      startTimer(currentDeckId.value, new Array(slideCount.value), ms)
    }
  }

  return {
    currentDeckId,
    currentSlideIndex,
    interval,
    startTimer,
    stopTimer,
    resetTimer,
    setIntervalMs,
    timer,
    slideCount
  }
})
