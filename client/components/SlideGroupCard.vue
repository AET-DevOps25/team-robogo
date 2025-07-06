<!-- File: src/components/SlideGroupCard.vue -->
<template>
  <div class="w-full rounded-xl shadow-lg p-4 bg-white space-y-3">
    <div class="flex gap-2 justify-between items-center">
      <h3 class="text-lg font-semibold">{{ title }}</h3>
      <div class="flex items-center gap-2 mt-2">
        <span class="text-lg font-semibold">Speed</span>
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
        class="w-[300px] h-[200px] rounded overflow-hidden shadow-lg bg-gray-100 flex items-center justify-center text-5xl text-gray-400 hover:bg-gray-200 transition"
        @click="showDialog = true"
      >
        +
      </div>
    </div>

    <!-- Dialog -->
    <div
      v-if="showDialog"
      class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50"
    >
      <div
        class="bg-white p-6 rounded-lg w-[600px] max-h-[80vh] overflow-y-auto shadow-lg relative"
      >
        <h4 class="text-lg font-bold mb-4">Select Slides</h4>

        <!-- Slide Selection -->
        <div class="flex flex-wrap gap-4 mb-6">
          <SlideCard
            v-for="s in allSlides"
            :key="s.id"
            :item="s"
            :selected="s.id === selectedToAdd"
            @click="selectedToAdd = s.id"
          />
        </div>

        <!-- Actions -->
        <div class="flex justify-end gap-4">
          <button
            class="px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400"
            @click="cancelAdd"
          >
            取消
          </button>
          <button
            class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
            :disabled="!selectedToAdd"
            @click="confirmAdd"
          >
            确认添加
          </button>
        </div>

        <button class="absolute top-2 right-2 text-gray-500 hover:text-gray-800" @click="cancelAdd">
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

  interface Slide {
    id: number
    name: string
    url: string
  }
  const slideIds = defineModel<number[]>('slide-ids', { required: true })
  const speed = defineModel<number>('speed', { required: true })
  const props = defineProps<{
    title: string
    allSlides: Slide[]
    selectedContent?: Slide
  }>()

  defineEmits<{
    (e: 'select', item: Slide): void
    // (e: 'update:content', items: SlideItem[]): void
    (e: 'update:slide-ids', ids: number[]): void
  }>()

  const showDialog = ref(false)
  const selectedToAdd = ref<number | null>(null)

  /* 拖拽时使用的临时数组 */
  const localSlideIds = ref<number[]>([...slideIds.value])
  watch(slideIds, ids => {
    localSlideIds.value = [...ids]
  })

  const id2Slide = computed<Record<number, Slide>>(() => {
    const map: Record<number, Slide> = {}
    props.allSlides.forEach(s => {
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
