// plugins/slide-timer.client.ts
import { defineNuxtPlugin } from '#app'
import { useScreenStore } from '@/stores/useScreenStore'

export default defineNuxtPlugin(() => {
  const store = useScreenStore()
  if (!store._timer) store.startSlideTimer()   // 只启动一次
})