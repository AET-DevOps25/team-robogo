<template>
  <div class="max-w-md mx-auto bg-white rounded-xl shadow-md p-6 space-y-6">
    <!-- 播放速度调整 -->
    <div class="flex items-center gap-3">
      <label class="font-semibold text-gray-700">播放速度(ms):</label>
      <input
        v-model="interval"
        class="w-28 px-2 py-1 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-400 transition"
        type="number"
        min="1000"
        step="500"
        @change="updateInterval"
      />
    </div>
    <!-- 当前 deck slides -->
    <div>
      <h3 class="text-lg font-bold text-blue-700 mb-2 flex items-center gap-2">
        <span class="inline-block w-2 h-2 bg-blue-400 rounded-full" />
        当前幻灯片
      </h3>
      <div class="grid grid-cols-2 gap-4">
        <div
          v-for="slide in deckSlides"
          :key="slide.id"
          class="p-2 rounded border border-blue-200 transition"
        >
          <SlideCard :item="slide" class="w-full" />
        </div>
        <!-- 添加幻灯片的空卡片 -->
        <div
          class="flex flex-col items-center justify-center p-6 border-2 border-dashed border-green-400 rounded cursor-pointer hover:bg-green-50 transition"
          @click="addSlide"
        >
          <span class="text-4xl text-green-400 mb-2">＋</span>
          <span class="text-green-700 font-semibold">添加新幻灯片</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import SlideCard from './SlideCard.vue'
  import { useDeckStore } from '@/stores/useDeckStore'
  import { useSlidesStore } from '@/stores/useSlidesStore'
  import { fetchSlideDeckById, updateSlideDeck } from '@/services/slideDeckService'
  import type { SlideItem, SlideDeck } from '@/interfaces/types'

  interface SlideDeckCardProps {
    deckId: number
  }

  const props = defineProps<SlideDeckCardProps>()
  const deckStore = useDeckStore()
  const slidesStore = useSlidesStore()

  const interval = ref(deckStore.interval)
  const deckSlides = ref<SlideItem[]>([])
  // 删除selectedSlideId相关
  // const selectedSlideId = ref<number | null>(null)

  onMounted(async () => {
    await slidesStore.refresh()
    await loadDeckSlides()
  })

  async function loadDeckSlides() {
    const deck: SlideDeck = await fetchSlideDeckById(props.deckId)
    deckSlides.value = deck.slides ?? []
    // 同步 transitionTime 到 interval（假设 transitionTime 单位为秒）
    if (deck.transitionTime) {
      interval.value = deck.transitionTime * 1000
    }
  }

  // updateSlideDeck 只需要传递部分字段，类型断言为 Partial<SlideDeck>
  async function updateInterval() {
    deckStore.setIntervalMs(interval.value)
    // 同步到后端
    await updateSlideDeck(props.deckId, {
      transitionTime: interval.value / 1000
    } as Partial<SlideDeck>)
  }

  // 恢复addSlide函数，示例为添加一个默认幻灯片
  function addSlide() {
    alert('请实现添加幻灯片的具体逻辑')
  }
</script>
