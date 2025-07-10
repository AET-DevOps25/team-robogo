<!-- File: src/components/SlideGroupCard.vue -->
<template>
  <div class="w-full rounded-xl shadow-lg p-4 bg-white dark:bg-gray-800 space-y-3">
    <div class="flex gap-2 justify-between items-center">
      <h3 class="text-lg font-semibold text-gray-900 dark:text-white">{{ title }}</h3>
      <div class="flex items-center gap-2 mt-2">
        <span class="text-lg font-semibold text-gray-900 dark:text-white">Speed</span>
        <SpeedControl v-model="speed" />
      </div>
    </div>

    <div class="flex flex-wrap gap-4">
      <draggable
        v-model="localSlideIds"
        group="slides"
        item-key="id"
        class="flex flex-wrap gap-4"
        @end="$emit('update:slide-ids', localSlideIds)"
      >
        <template #item="{ element }">
          <SlideCard
            v-if="id2Slide[element]"
            :item="id2Slide[element]"
            :selected="selectedContent?.id === element"
            @click="$emit('select', id2Slide[element])"
          />
        </template>
      </draggable>

      <!-- Add button -->

      <div
        class="w-[300px] h-[200px] rounded overflow-hidden shadow-lg bg-gray-100 dark:bg-gray-700 flex items-center justify-center text-5xl text-gray-400 dark:text-gray-500 hover:bg-gray-200 dark:hover:bg-gray-600 transition"
        @click="openDialog"
      >
        +
      </div>
    </div>

    <!-- Dialog -->
    <div
      v-if="showDialog"
      class="fixed inset-0 bg-black bg-opacity-40 dark:bg-black dark:bg-opacity-60 flex items-center justify-center z-50"
    >
      <div
        class="bg-white dark:bg-gray-800 p-6 rounded-lg w-[600px] max-h-[80vh] overflow-y-auto shadow-lg relative"
      >
        <h4 class="text-lg font-bold mb-4 text-gray-900 dark:text-white">Select Slides</h4>

        <!-- Slide Selection -->
        <div class="flex flex-wrap gap-4 mb-6">
          <SlideCard
            v-for="s in slides"
            :key="s.id"
            :item="s"
            :selected="s.id === selectedToAdd"
            @click="selectedToAdd = s.id"
          />
        </div>

        <!-- Actions -->
        <div class="flex justify-end gap-4">
          <button
            class="px-4 py-2 bg-gray-300 dark:bg-gray-600 text-gray-700 dark:text-gray-200 rounded hover:bg-gray-400 dark:hover:bg-gray-500"
            @click="cancelAdd"
          >
            Cancel
          </button>
          <button
            class="px-4 py-2 bg-blue-600 dark:bg-blue-500 text-white rounded hover:bg-blue-700 dark:hover:bg-blue-600 disabled:bg-gray-400 dark:disabled:bg-gray-600"
            :disabled="!selectedToAdd"
            @click="confirmAdd"
          >
            Confirm
          </button>
        </div>

        <button
          class="absolute top-2 right-2 text-gray-500 dark:text-gray-400 hover:text-gray-800 dark:hover:text-gray-200"
          @click="cancelAdd"
        >
          ✕
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, watch } from 'vue'
  import draggable from 'vuedraggable'
  import SlideCard from './SlideCard.vue'
  import SpeedControl from './SpeedControl.vue'
  import { useSlides } from '@/composables/useSlides'
  import type { SlideItem } from '@/interfaces/types'
  const { slides, refresh } = useSlides()
  interface Slide {
    id: number
    name: string
    url: string
  }
  const slideIds = defineModel<number[]>('slide-ids', { required: true })
  const speed = defineModel<number>('speed', { required: true })
  const props = defineProps<{ title:string; selectedContent?:SlideItem }>()

  defineEmits<{
    (e: 'select', item: Slide): void
    // (e: 'update:content', items: SlideItem[]): void
    (e: 'update:slide-ids', ids: number[]): void
  }>()

/* 打开弹窗前刷新一次 */
const showDialog = ref(false)
async function openDialog() {
  await refresh()
  console.log(slides)
  showDialog.value = true
}
  const selectedToAdd = ref<number | null>(null)

  /* 拖拽时使用的临时数组 */
  const localSlideIds = ref<number[]>([...slideIds.value])
  watch(slideIds, ids => {
    localSlideIds.value = [...ids]
  })



  const id2Slide = computed<Record<number, Slide>>(() => {
    const map: Record<number, Slide> = {}
      slides.value.forEach(s => {
      map[s.id] = s
    })
    return map
  })

  const confirmAdd = () => {
    if (selectedToAdd.value && !slideIds.value.includes(selectedToAdd.value)) {
      slideIds.value = [...slideIds.value, selectedToAdd.value]
      localSlideIds.value = [...slideIds.value]
    }
    resetDialog()
  }

  function cancelAdd() {
    resetDialog()
  }
  function resetDialog() {
    showDialog.value = false
    selectedToAdd.value = null
  }
</script>
