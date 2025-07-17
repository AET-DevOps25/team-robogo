<template>
  <div>
    <!-- 播放速度调整 -->
    <div>
      <label>播放速度(ms):</label>
      <input v-model="interval" type="number" min="1000" step="500" @change="updateInterval" />
    </div>
    <!-- 当前 deck slides -->
    <div>
      <h3>当前幻灯片</h3>
      <ul>
        <li v-for="slide in deckSlides" :key="slide.id">{{ slide.name }}</li>
      </ul>
    </div>
    <!-- 添加 slide 区域 -->
    <div>
      <h3>添加幻灯片</h3>
      <select v-model="selectedSlideId">
        <option v-for="slide in slidesStore.allSlides" :key="slide.id" :value="slide.id">
          {{ slide.name }}
        </option>
      </select>
      <button @click="addSlide">添加到当前Deck</button>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { useDeckStore } from '@/stores/useDeckStore'
  import { useSlidesStore } from '@/stores/useSlidesStore'
  import { fetchSlideDeckById, addSlideToDeck, updateSlideDeck } from '@/services/slideDeckService'
  import type { SlideItem, SlideDeck } from '@/interfaces/types'

  interface SlideDeckCardProps {
    deckId: number
  }

  const props = defineProps<SlideDeckCardProps>()
  const deckStore = useDeckStore()
  const slidesStore = useSlidesStore()

  const interval = ref(deckStore.interval)
  const deckSlides = ref<SlideItem[]>([])
  const selectedSlideId = ref<number | null>(null)

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

  async function addSlide() {
    if (!selectedSlideId.value) return
    const slide = slidesStore.allSlides.find(s => s.id === selectedSlideId.value)
    if (!slide) return
    await addSlideToDeck(props.deckId, slide)
    await loadDeckSlides()
  }
</script>
