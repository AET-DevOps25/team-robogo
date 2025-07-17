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
      <draggable v-model="deckSlides" item-key="id" class="grid grid-cols-2 gap-4" @end="onDragEnd">
        <template #item="{ element }">
          <div class="p-2 rounded border border-blue-200 transition">
            <SlideCard :item="element" class="w-full" />
          </div>
        </template>
        <template #footer>
          <div
            class="flex flex-col items-center justify-center p-6 border-2 border-dashed border-green-400 rounded cursor-pointer hover:bg-green-50 transition"
            @click="openAddDialog"
          >
            <span class="text-4xl text-green-400 mb-2">＋</span>
            <span class="text-green-700 font-semibold">添加新幻灯片</span>
          </div>
        </template>
      </draggable>
      <button
        v-if="isOrderChanged"
        class="mt-4 px-4 py-1 bg-blue-500 text-white rounded shadow hover:bg-blue-600 transition"
        @click="saveOrder"
      >
        保存顺序
      </button>
    </div>
  </div>
  <teleport to="body">
    <div
      v-if="showAddDialog"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40"
    >
      <div class="bg-white rounded-lg shadow-lg p-6 max-w-2xl w-full relative">
        <button
          class="absolute top-2 right-2 text-gray-400 hover:text-gray-600 text-xl"
          @click="closeAddDialog"
        >
          ×
        </button>
        <h2 class="text-lg font-bold mb-4 text-green-700">选择要添加的幻灯片</h2>
        <div class="grid grid-cols-2 gap-4">
          <div
            v-for="slide in slidesStore.allSlides"
            :key="slide.id"
            class="p-2 rounded border border-gray-200 transition cursor-pointer hover:border-green-400"
            @click="handleAddSlide(slide)"
          >
            <SlideCard :item="slide" class="w-full" />
          </div>
        </div>
      </div>
    </div>
  </teleport>
</template>

<script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import draggable from 'vuedraggable'
  import SlideCard from './SlideCard.vue'
  import { useDeckStore } from '@/stores/useDeckStore'
  import { useSlidesStore } from '@/stores/useSlidesStore'
  import {
    fetchSlideDeckById,
    addSlideToDeck,
    updateSlideDeck,
    reorderSlides
  } from '@/services/slideDeckService'
  import type { SlideItem, SlideDeck } from '@/interfaces/types'

  interface SlideDeckCardProps {
    deckId: number
  }

  const props = defineProps<SlideDeckCardProps>()
  const deckStore = useDeckStore()
  const slidesStore = useSlidesStore()

  const interval = ref(deckStore.interval)
  const deckSlides = ref<SlideItem[]>([])
  const showAddDialog = ref(false)
  const isOrderChanged = ref(false)

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

  function openAddDialog() {
    showAddDialog.value = true
  }
  function closeAddDialog() {
    showAddDialog.value = false
  }

  async function handleAddSlide(slide: SlideItem) {
    await addSlideToDeck(props.deckId, slide)
    await loadDeckSlides()
    closeAddDialog()
  }

  function onDragEnd() {
    isOrderChanged.value = true
  }

  async function saveOrder() {
    const newOrder = deckSlides.value.map(s => s.id)
    await reorderSlides(props.deckId, newOrder)
    isOrderChanged.value = false
    await loadDeckSlides()
  }
</script>
