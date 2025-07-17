<template>
  <div class="max-w-md mx-auto bg-white dark:bg-gray-800 rounded-xl shadow-md p-6 space-y-6">
    <!-- 播放速度调整 -->
    <div class="flex items-center gap-3">
      <label class="font-semibold text-gray-700 dark:text-gray-300">播放速度(ms):</label>
      <input
        v-model="interval"
        class="w-28 px-2 py-1 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white rounded focus:outline-none focus:ring-2 focus:ring-blue-400 transition"
        type="number"
        min="1000"
        step="500"
        @change="updateInterval"
      />
    </div>
    <!-- 当前 deck slides -->
    <div>
      <h3 class="text-lg font-bold text-blue-700 dark:text-blue-400 mb-2 flex items-center gap-2">
        <span class="inline-block w-2 h-2 bg-blue-400 rounded-full" />
        当前幻灯片
      </h3>
      <draggable v-model="deckSlides" item-key="id" class="grid grid-cols-2 gap-4" @end="onDragEnd">
        <template #item="{ element }">
          <div class="p-2 rounded border border-blue-200 dark:border-blue-600 transition">
            <SlideCard :item="element" class="w-full" />
          </div>
        </template>
        <template #footer>
          <div
            class="flex flex-col items-center justify-center p-6 border-2 border-dashed border-green-400 dark:border-green-500 rounded cursor-pointer hover:bg-green-50 dark:hover:bg-green-900/20 transition"
            @click="openAddDialog"
          >
            <span class="text-4xl text-green-400 dark:text-green-500 mb-2">＋</span>
            <span class="text-green-700 dark:text-green-400 font-semibold">添加新幻灯片</span>
          </div>
        </template>
      </draggable>
      <button
        v-if="isOrderChanged"
        class="mt-4 px-4 py-1 bg-blue-500 dark:bg-blue-600 text-white rounded shadow hover:bg-blue-600 dark:hover:bg-blue-700 transition"
        @click="saveOrder"
      >
        保存顺序
      </button>
    </div>
  </div>
  <teleport to="body">
    <div
      v-if="showAddDialog"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40 dark:bg-black dark:bg-opacity-60"
    >
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-lg p-6 max-w-2xl w-full relative">
        <button
          class="absolute top-2 right-2 text-gray-400 dark:text-gray-500 hover:text-gray-600 dark:hover:text-gray-300 text-xl"
          @click="closeAddDialog"
        >
          ×
        </button>
        <h2 class="text-lg font-bold mb-4 text-green-700 dark:text-green-400">
          选择要添加的幻灯片
        </h2>
        <div class="grid grid-cols-2 gap-4">
          <div
            v-for="slide in slidesStore.allSlides"
            :key="slide.id"
            class="p-2 rounded border border-gray-200 dark:border-gray-600 transition cursor-pointer hover:border-green-400 dark:hover:border-green-500"
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
  import { ref, onMounted, onUnmounted, watch } from 'vue'
  import draggable from 'vuedraggable'
  import SlideCard from './SlideCard.vue'
  import { useDeckStore } from '@/stores/useDeckStore'
  import { useSlidesStore } from '@/stores/useSlidesStore'
  import { addSlideToDeck, updateSlideDeck, reorderSlides } from '@/services/slideDeckService'
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
  const currentDeck = ref<SlideDeck | null>(null)

  onMounted(async () => {
    await slidesStore.refresh()
    deckStore.currentDeckId = props.deckId
    await deckStore.checkAndRefreshDeck()

    // 如果这个 deck 是当前播放的，设置到 store
    if (currentDeck.value && deckStore.currentDeckId === props.deckId) {
      deckStore.setCurrentDeck(currentDeck.value)
    }
  })

  // 监听 store 中的 deck 数据变化
  watch(
    () => deckStore.currentDeck,
    newDeck => {
      if (newDeck && newDeck.id === props.deckId) {
        // 如果 store 中的 deck 数据更新了，同步到本地
        currentDeck.value = newDeck
        deckSlides.value = newDeck.slides ?? []

        if (newDeck.transitionTime) {
          interval.value = newDeck.transitionTime * 1000
        }
      }
    },
    { deep: true }
  )

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
    await deckStore.checkAndRefreshDeck()
    closeAddDialog()
  }

  function onDragEnd() {
    isOrderChanged.value = true
  }

  async function saveOrder() {
    const newOrder = deckSlides.value.map(s => s.id)
    await reorderSlides(props.deckId, newOrder)
    isOrderChanged.value = false
    await deckStore.checkAndRefreshDeck()
  }

  // 当组件卸载时，如果这个 deck 是当前播放的，停止监听
  onUnmounted(() => {
    if (deckStore.currentDeckId === props.deckId) {
      deckStore.stopVersionCheck()
    }
  })
</script>
