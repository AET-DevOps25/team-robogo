<template>
  <div class="w-full bg-white dark:bg-gray-800 rounded-xl shadow-md p-6 space-y-6">
    <!-- 播放速度调整 -->
    <div class="space-y-3">
      <div class="flex items-center justify-between">
        <label class="font-semibold text-gray-700 dark:text-gray-300">
          {{ $t('playbackSpeed') }}:
        </label>
        <span class="text-sm text-gray-600 dark:text-gray-400">{{ interval }}ms</span>
      </div>

      <!-- 动态速度滑块 -->
      <div class="relative">
        <input
          v-model="interval"
          type="range"
          min="500"
          max="5000"
          step="100"
          class="w-full h-2 bg-gray-200 dark:bg-gray-700 rounded-lg appearance-none cursor-pointer slider"
          @input="onSpeedChange"
          @change="updateInterval"
        />

        <!-- 速度指示器 -->
        <div class="flex justify-between text-xs text-gray-500 dark:text-gray-400 mt-1">
          <span>{{ $t('fast') }}</span>
          <span>{{ $t('normal') }}</span>
          <span>{{ $t('slow') }}</span>
        </div>
      </div>

      <!-- 预设速度按钮 -->
      <div class="flex gap-2 flex-wrap">
        <button
          v-for="preset in speedPresets"
          :key="preset.value"
          class="px-3 py-1 text-xs rounded-full transition-colors duration-200"
          :class="
            interval === preset.value
              ? 'bg-blue-500 text-white'
              : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'
          "
          @click="setSpeed(preset.value)"
        >
          {{ preset.label }}
        </button>
      </div>
    </div>
    <!-- 当前 deck slides -->
    <div>
      <h3 class="text-lg font-bold text-blue-700 dark:text-blue-400 mb-4 flex items-center gap-2">
        <span class="inline-block w-2 h-2 bg-blue-400 rounded-full" />
        {{ $t('currentSlides') }}
      </h3>
      <draggable
        v-model="deckSlides"
        item-key="id"
        class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6"
        :animation="300"
        ghost-class="drag-ghost"
        chosen-class="drag-chosen"
        drag-class="drag-active"
        handle=".drag-handle"
        @end="onDragEnd"
        @start="onDragStart"
        @change="onDragChange"
      >
        <template #item="{ element, index }">
          <div
            class="slide-item relative p-4 rounded-lg border border-blue-200 dark:border-blue-600 transition-all duration-200 hover:shadow-lg dark:hover:shadow-xl hover:border-blue-300 dark:hover:border-blue-500 group bg-white dark:bg-gray-700"
            :class="{ 'opacity-50': isDragging && draggedIndex === index }"
          >
            <!-- 拖拽手柄 -->
            <div
              class="drag-handle absolute top-2 left-2 w-6 h-6 flex items-center justify-center cursor-move opacity-0 group-hover:opacity-100 transition-opacity duration-200 z-10"
            >
              <div class="w-4 h-4 flex flex-col justify-center items-center">
                <div class="w-3 h-0.5 bg-gray-400 dark:bg-gray-500 mb-0.5" />
                <div class="w-3 h-0.5 bg-gray-400 dark:bg-gray-500 mb-0.5" />
                <div class="w-3 h-0.5 bg-gray-400 dark:bg-gray-500" />
              </div>
            </div>

            <!-- 删除按钮 -->
            <button
              v-if="false"
              class="absolute top-2 right-2 w-6 h-6 flex items-center justify-center text-red-500 hover:text-red-700 bg-white dark:bg-gray-800 rounded-full shadow z-20"
              :title="$t('deleteSlide')"
              @click.stop="handleRemoveSlide(element)"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-4 w-4"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M6 18L18 6M6 6l12 12"
                />
              </svg>
            </button>

            <!-- 位置指示器 -->
            <div
              class="absolute bottom-2 left-2 bg-blue-500 dark:bg-blue-600 text-white text-xs px-2 py-1 rounded-full opacity-90 font-medium"
            >
              {{ index + 1 }}
            </div>

            <SlideCard :item="element" class="w-full" />
          </div>
        </template>
        <template #footer>
          <div
            v-if="false"
            class="flex flex-col items-center justify-center p-6 border-2 border-dashed border-green-400 dark:border-green-500 rounded cursor-pointer hover:bg-green-50 dark:hover:bg-green-900/20 transition-colors duration-200"
            @click="openAddDialog"
          >
            <span class="text-4xl text-green-400 dark:text-green-500 mb-2">＋</span>
            <span class="text-green-700 dark:text-green-400 font-semibold">
              {{ $t('addNewSlide') }}
            </span>
          </div>
        </template>
      </draggable>

      <!-- 操作按钮 -->
      <div class="flex gap-2 mt-4">
        <button
          v-if="isOrderChanged"
          class="px-4 py-2 bg-blue-500 dark:bg-blue-600 text-white rounded shadow hover:bg-blue-600 dark:hover:bg-blue-700 transition-colors duration-200 flex items-center gap-2"
          @click="saveOrder"
        >
          <span class="text-sm">💾</span>
          {{ $t('saveOrder') }}
        </button>
        <button
          v-if="isOrderChanged"
          class="px-4 py-2 bg-gray-500 dark:bg-gray-600 text-white rounded shadow hover:bg-gray-600 dark:hover:bg-gray-700 transition-colors duration-200 flex items-center gap-2"
          @click="resetOrder"
        >
          <span class="text-sm">↺</span>
          {{ $t('resetOrder') }}
        </button>
      </div>
    </div>
  </div>

  <!-- 添加幻灯片对话框 -->
  <teleport to="body">
    <div
      v-if="showAddDialog"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40 dark:bg-black dark:bg-opacity-60"
      @click="closeAddDialog"
    >
      <div
        class="bg-white dark:bg-gray-800 rounded-lg shadow-lg p-6 max-w-2xl w-full relative"
        @click.stop
      >
        <button
          class="absolute top-2 right-2 text-gray-400 dark:text-gray-500 hover:text-gray-600 dark:hover:text-gray-300 text-xl"
          @click="closeAddDialog"
        >
          ×
        </button>
        <h2 class="text-lg font-bold mb-4 text-green-700 dark:text-green-400">
          {{ $t('selectSlideToAdd') }}
        </h2>
        <div class="grid grid-cols-2 gap-4 max-h-96 overflow-y-auto">
          <div
            v-for="slide in slidesStore.allSlides"
            :key="slide.id"
            class="p-2 rounded border border-gray-200 dark:border-gray-600 transition cursor-pointer hover:border-green-400 dark:hover:border-green-500 hover:shadow-md"
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
  import { useI18n } from 'vue-i18n'
  import draggable from 'vuedraggable'
  import SlideCard from './SlideCard.vue'
  import { useDeckStore } from '@/stores/useDeckStore'
  import { useSlidesStore } from '@/stores/useSlidesStore'
  import {
    addSlideToDeck,
    reorderSlides,
    updateSlideDeckSpeed,
    removeSlideFromDeck
  } from '@/services/slideDeckService'
  import type { SlideItem, SlideDeck } from '@/interfaces/types'
  import { useToast } from '@/composables/useToast'

  interface SlideDeckCardProps {
    deckId: number
  }

  const props = defineProps<SlideDeckCardProps>()
  const { t } = useI18n()
  const deckStore = useDeckStore()
  const slidesStore = useSlidesStore()
  const { showSuccess, showError } = useToast()

  const interval = ref(deckStore.interval)
  const userSetSpeed = ref(false) // 标记用户是否手动设置了速度
  const deckSlides = ref<SlideItem[]>([])
  const showAddDialog = ref(false)
  const isOrderChanged = ref(false)
  const currentDeck = ref<SlideDeck | null>(null)
  const isDragging = ref(false)
  const draggedIndex = ref(-1)
  const originalOrder = ref<SlideItem[]>([])

  // 速度预设
  const speedPresets = ref([
    { label: '0.5s', value: 500 },
    { label: '1s', value: 1000 },
    { label: '2s', value: 2000 },
    { label: '3s', value: 3000 },
    { label: '5s', value: 5000 }
  ])

  onMounted(async () => {
    await slidesStore.refresh()
    deckStore.currentDeckId = props.deckId
    await deckStore.checkAndRefreshDeck()

    // 如果这个 deck 是当前播放的，设置到 store
    if (currentDeck.value && deckStore.currentDeckId === props.deckId) {
      deckStore.setCurrentDeck(currentDeck.value)
    }

    // 重置用户设置标记
    userSetSpeed.value = false
  })

  // 监听 store 中的 deck 数据变化
  watch(
    () => deckStore.currentDeck,
    newDeck => {
      if (newDeck && newDeck.id === props.deckId) {
        // 如果 store 中的 deck 数据更新了，同步到本地
        currentDeck.value = newDeck
        deckSlides.value = newDeck.slides ?? []
        originalOrder.value = [...deckSlides.value]

        // 只有在用户没有手动设置速度时才更新速度
        if (newDeck.transitionTime && !userSetSpeed.value) {
          // 如果检测到异常大的值（可能是数据错误），重置为默认值
          if (newDeck.transitionTime > 10000) {
            // 大于10秒
            interval.value = 2000 // 默认2秒
            // 自动修复后端数据
            updateSlideDeckSpeed(props.deckId, 2.0)
          } else {
            interval.value = newDeck.transitionTime
          }
        }
      }
    },
    { deep: true }
  )

  // 使用专门的更新速度接口
  async function updateInterval() {
    try {
      userSetSpeed.value = true // 标记用户已手动设置速度
      deckStore.setIntervalMs(interval.value)
      // 使用专门的更新速度接口，将毫秒转换为秒
      await updateSlideDeckSpeed(props.deckId, interval.value / 1000)
      showSuccess(t('intervalUpdated'))
    } catch (error) {
      showError(t('failedToUpdateInterval'))
    }
  }

  function openAddDialog() {
    showAddDialog.value = true
  }

  function closeAddDialog() {
    showAddDialog.value = false
  }

  function onSpeedChange() {
    // 实时更新store中的速度，但不立即保存到后端
    userSetSpeed.value = true // 标记用户已手动设置速度
    deckStore.setIntervalMs(interval.value)
  }

  function setSpeed(value: number) {
    interval.value = value
    updateInterval()
  }

  async function handleAddSlide(slide: SlideItem) {
    try {
      await addSlideToDeck(props.deckId, slide)
      await deckStore.checkAndRefreshDeck()
      closeAddDialog()
      showSuccess(t('slideAdded'))
    } catch (error) {
      showError(t('failedToAddSlide'))
    }
  }

  async function handleRemoveSlide(slide: SlideItem) {
    try {
      await removeSlideFromDeck(props.deckId, slide.id)
      await deckStore.checkAndRefreshDeck()
      showSuccess(t('slideRemoved') || 'Slide removed')
    } catch (error) {
      showError(t('failedToRemoveSlide') || 'Failed to remove slide')
    }
  }

  function onDragStart(evt: any) {
    isDragging.value = true
    draggedIndex.value = evt.oldIndex
  }

  function onDragChange(_evt: any) {
    // 检查顺序是否改变
    const hasChanged = deckSlides.value.some(
      (slide, index) => originalOrder.value[index]?.id !== slide.id
    )
    isOrderChanged.value = hasChanged
  }

  function onDragEnd() {
    isDragging.value = false
    draggedIndex.value = -1
    // 检查顺序是否改变
    const hasChanged = deckSlides.value.some(
      (slide, index) => originalOrder.value[index]?.id !== slide.id
    )
    isOrderChanged.value = hasChanged
  }

  function resetOrder() {
    deckSlides.value = [...originalOrder.value]
    isOrderChanged.value = false
  }

  async function saveOrder() {
    try {
      const newOrder = deckSlides.value.map(s => s.id)
      await reorderSlides(props.deckId, newOrder)
      isOrderChanged.value = false
      originalOrder.value = [...deckSlides.value]
      await deckStore.checkAndRefreshDeck()
      showSuccess(t('orderSaved'))
    } catch (error) {
      showError(t('failedToSaveOrder'))
    }
  }

  // 当组件卸载时，如果这个 deck 是当前播放的，停止监听
  onUnmounted(() => {
    if (deckStore.currentDeckId === props.deckId) {
      deckStore.stopVersionCheck()
    }
  })
</script>

<style scoped>
  .drag-ghost {
    opacity: 0.5;
    transform: rotate(5deg);
    background: rgba(59, 130, 246, 0.1);
    border: 2px dashed #3b82f6;
  }

  .drag-chosen {
    transform: scale(1.05);
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
    z-index: 10;
  }

  .drag-active {
    transform: rotate(5deg) scale(1.1);
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
    z-index: 20;
  }

  .slide-item {
    transition: all 0.2s ease;
  }

  .slide-item:hover {
    transform: translateY(-2px);
  }

  /* 暗色模式下的拖拽样式 */
  .dark .drag-ghost {
    background: rgba(59, 130, 246, 0.2);
    border-color: #60a5fa;
  }

  .dark .drag-chosen {
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.4);
  }

  .dark .drag-active {
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.5);
  }

  /* 滑块样式 */
  .slider {
    -webkit-appearance: none;
    appearance: none;
    background: transparent;
  }

  .slider::-webkit-slider-track {
    background: #e5e7eb;
    height: 8px;
    border-radius: 4px;
  }

  .slider::-webkit-slider-thumb {
    -webkit-appearance: none;
    appearance: none;
    background: #3b82f6;
    height: 20px;
    width: 20px;
    border-radius: 50%;
    cursor: pointer;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    transition: all 0.2s ease;
  }

  .slider::-webkit-slider-thumb:hover {
    background: #2563eb;
    transform: scale(1.1);
  }

  .slider::-moz-range-track {
    background: #e5e7eb;
    height: 8px;
    border-radius: 4px;
    border: none;
  }

  .slider::-moz-range-thumb {
    background: #3b82f6;
    height: 20px;
    width: 20px;
    border-radius: 50%;
    cursor: pointer;
    border: none;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    transition: all 0.2s ease;
  }

  .slider::-moz-range-thumb:hover {
    background: #2563eb;
    transform: scale(1.1);
  }

  /* 暗色模式滑块样式 */
  .dark .slider::-webkit-slider-track {
    background: #374151;
  }

  .dark .slider::-webkit-slider-thumb {
    background: #60a5fa;
  }

  .dark .slider::-webkit-slider-thumb:hover {
    background: #3b82f6;
  }

  .dark .slider::-moz-range-track {
    background: #374151;
  }

  .dark .slider::-moz-range-thumb {
    background: #60a5fa;
  }

  .dark .slider::-moz-range-thumb:hover {
    background: #3b82f6;
  }
</style>
