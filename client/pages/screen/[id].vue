<!-- File: src/pages/screen/[id].vue -->
<script setup lang="ts">
  import { useRoute } from 'vue-router'
  import { computed, ref, onMounted, onUnmounted, watch } from 'vue'
  import { useI18n } from 'vue-i18n'
  import { Swiper, SwiperSlide } from 'swiper/vue'
  import { EffectFade, Autoplay } from 'swiper/modules'
  import 'swiper/css'
  import 'swiper/css/effect-fade'
  import { useDeckStore } from '@/stores/useDeckStore'
  import { useSlidesStore } from '@/stores/useSlidesStore'
  import { fetchSlideDeckById } from '@/services/slideDeckService'
  import { fetchScreenById } from '@/services/screenService'
  import type { SlideDeck, SlideItem, ScreenContent } from '@/interfaces/types'
  import { SlideType } from '@/interfaces/types'
  import ImageSlideDisplay from '@/components/ImageSlideDisplay.vue'
  import ScoreSlideDisplay from '@/components/ScoreSlideDisplay.vue'
  import DefaultSlideDisplay from '@/components/DefaultSlideDisplay.vue'

  const route = useRoute()
  const deckStore = useDeckStore()
  const _slidesStore = useSlidesStore()
  const { t } = useI18n()

  const screenId = route.params.id as string
  const screen = ref<ScreenContent | null>(null)
  const currentDeck = ref<SlideDeck | null>(null)
  const slides = ref<SlideItem[]>([])
  const swiperRef = ref<any>(null)

  // 获取当前屏幕信息
  const loadScreen = async () => {
    try {
      const screenData = await fetchScreenById(Number(screenId))
      screen.value = screenData

      if (screenData.slideDeck?.id) {
        await loadSlideDeck(screenData.slideDeck.id)
      }
    } catch (error) {
      console.error('Failed to load screen:', error)
    }
  }

  // 获取当前屏幕的slideDeck
  const loadSlideDeck = async (deckId: number) => {
    try {
      const deck = await fetchSlideDeckById(deckId)
      currentDeck.value = deck
      slides.value = deck.slides || []

      // 设置deck store
      deckStore.currentDeckId = deck.id
      deckStore.setCurrentDeck(deck)
    } catch (error) {
      console.error('Failed to load slide deck:', error)
    }
  }

  // 轮播配置 - 使用autoplay实现自动轮播
  const swiperOptions = computed(() => ({
    modules: [EffectFade, Autoplay],
    effect: 'fade',
    fadeEffect: {
      crossFade: true
    },
    loop: slides.value.length > 1,
    allowTouchMove: false,
    autoplay:
      slides.value.length > 1
        ? {
            delay: currentDeck.value?.transitionTime || 5000,
            disableOnInteraction: false
          }
        : false,
    onSwiper: (swiper: any) => {
      swiperRef.value = swiper
    }
  }))

  // 获取幻灯片组件
  const getSlideComponent = (slide: SlideItem) => {
    switch (slide.type) {
      case SlideType.IMAGE:
        return ImageSlideDisplay
      case SlideType.SCORE:
        return ScoreSlideDisplay
      default:
        return DefaultSlideDisplay
    }
  }

  // 监听slideDeck变化
  watch(
    () => screen.value?.slideDeck?.id,
    async newDeckId => {
      if (newDeckId) {
        await loadSlideDeck(newDeckId)
      }
    }
  )

  // 监听transitionTime变化，更新轮播速度
  watch(
    () => currentDeck.value?.transitionTime,
    newTransitionTime => {
      if (swiperRef.value && newTransitionTime && slides.value.length > 1) {
        swiperRef.value.autoplay.stop()
        swiperRef.value.autoplay.params.delay = newTransitionTime
        swiperRef.value.autoplay.start()
      }
    }
  )

  onMounted(async () => {
    await loadScreen()
  })

  onUnmounted(() => {
    // 清理store
    if (deckStore.currentDeckId === currentDeck.value?.id) {
      deckStore.stopVersionCheck()
    }
  })
</script>

<template>
  <div class="w-full h-screen bg-black flex items-center justify-center overflow-hidden">
    <div v-if="slides.length > 0" class="w-full h-full">
      <Swiper v-bind="swiperOptions" class="w-full h-full">
        <SwiperSlide v-for="slide in slides" :key="slide.id" class="w-full h-full">
          <component :is="getSlideComponent(slide)" :slide="slide" />
        </SwiperSlide>
      </Swiper>
    </div>

    <!-- 没有幻灯片时显示 -->
    <div v-else class="text-white text-3xl">
      <div v-if="screen?.slideDeck">{{ t('loadingSlides') }}</div>
      <div v-else>{{ t('noSlideDeckAssigned') }}</div>
    </div>
  </div>
</template>

<style scoped>
  .swiper {
    width: 100%;
    height: 100%;
  }

  .swiper-slide {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
</style>
