import { defineStore } from 'pinia'
import { ref, watch } from 'vue'
import type { SlideItem, SlideDeck } from '@/interfaces/types'
import { fetchSlideDeckById } from '@/services/slideDeckService'

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
  // 当前 deck 数据
  const currentDeck = ref<SlideDeck | null>(null)
  // 当前 deck 的 version
  const currentDeckVersion = ref(0)

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

  // 设置当前 deck 数据
  function setCurrentDeck(deck: SlideDeck) {
    currentDeck.value = deck
    currentDeckVersion.value = deck.version
  }

  // 检查并刷新 deck 数据
  async function checkAndRefreshDeck() {
    if (!currentDeckId.value) return

    try {
      const freshDeck = await fetchSlideDeckById(currentDeckId.value)

      // 只有当新版本号大于当前版本号时才更新
      // 处理版本号溢出和回绕的情况
      const shouldUpdate =
        !currentDeck.value || isVersionNewer(freshDeck.version, currentDeckVersion.value)

      if (shouldUpdate) {
        console.log(
          `Deck version changed from ${currentDeckVersion.value} to ${freshDeck.version}, refreshing...`
        )

        setCurrentDeck(freshDeck)

        // 更新 slide 数量
        slideCount.value = freshDeck.slides?.length ?? 0

        // 如果当前索引超出范围，重置为0
        if (currentSlideIndex.value >= slideCount.value) {
          currentSlideIndex.value = 0
        }

        // 重启 timer 以应用新的 slides，但保持当前的速度设置
        if (timer.value) {
          startTimer(currentDeckId.value, freshDeck.slides ?? [], interval.value)
        }
      }
    } catch (error) {
      console.error('Failed to check deck version:', error)
    }
  }

  // 检查版本号是否更新，处理溢出和回绕
  function isVersionNewer(newVersion: number, currentVersion: number): boolean {
    // 处理版本号溢出：如果新版本号是负数且当前版本号是正数，说明发生了溢出
    if (newVersion < 0 && currentVersion > 0) {
      return true // 新版本号溢出，认为是更新
    }

    // 处理版本号回绕：如果当前版本号是负数且新版本号是正数，说明发生了回绕
    if (currentVersion < 0 && newVersion > 0) {
      return newVersion > currentVersion + 2147483647 // 考虑回绕后的差值
    }

    // 正常情况：新版本号大于当前版本号
    return newVersion > currentVersion
  }

  // 监听 version 变化，定期检查
  let versionCheckTimer: ReturnType<typeof setInterval> | null = null

  function startVersionCheck() {
    stopVersionCheck()
    // 每5秒检查一次 version
    versionCheckTimer = setInterval(checkAndRefreshDeck, 5000)
  }

  function stopVersionCheck() {
    if (versionCheckTimer) {
      clearInterval(versionCheckTimer)
      versionCheckTimer = null
    }
  }

  // 当 currentDeckId 变化时，开始监听 version
  watch(currentDeckId, newDeckId => {
    if (newDeckId) {
      startVersionCheck()
    } else {
      stopVersionCheck()
    }
  })

  return {
    currentDeckId,
    currentSlideIndex,
    interval,
    currentDeck,
    currentDeckVersion,
    startTimer,
    stopTimer,
    resetTimer,
    setIntervalMs,
    setCurrentDeck,
    checkAndRefreshDeck,
    startVersionCheck,
    stopVersionCheck,
    timer,
    slideCount
  }
})
