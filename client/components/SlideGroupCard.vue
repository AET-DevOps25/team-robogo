<!-- File: src/components/SlideGroupCard.vue -->
<template>
  <div class="w-full rounded-xl shadow-lg p-4 bg-white dark:bg-gray-800 space-y-3">
    <div class="flex gap-2 justify-between items-center">
      <h3 class="text-lg font-semibold text-gray-900 dark:text-white">{{ group.id }}</h3>
      <div class="flex items-center gap-2 mt-2" />
      <div class="flex items-center gap-2 mt-2">
        <span class="text-lg font-semibold text-gray-900 dark:text-white">Speed (s)</span>
        <SpeedControl v-model="editingGroup.speed" />
        <button
          class="ml-2 px-2 py-1 bg-blue-600 dark:bg-blue-500 rounded text-white hover:bg-blue-700 dark:hover:bg-blue-600"
          title="If you want to record the speed change to the backend, please click Save"
          @click="saveSpeed"
        >
          Save
        </button>
        <!-- ▶ Play 按钮 -->
        <button
          class="ml-2 px-2 py-1 bg-green-600 dark:bg-green-500 rounded text-white hover:bg-green-700 dark:hover:bg-green-600"
          title="Replay this group"
          @click="play()"
        >
          ▶
        </button>
      </div>
    </div>

    <div class="flex flex-wrap gap-4">
      <draggable
        v-model="editingGroup.slideIds"
        group="slides"
        item-key="id"
        class="flex flex-wrap gap-4"
        @end="onDragEnd"
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
  import { ref, computed, watch, reactive, toRaw } from 'vue'
  import draggable from 'vuedraggable'
  import SlideCard from './SlideCard.vue'
  import SpeedControl from './SpeedControl.vue'
  import { useSlides } from '@/composables/useSlides'
  import type { SlideGroup, SlideItem } from '@/interfaces/types'
  import { useScreenStore } from '@/stores/useScreenStore'
  import { saveGroup } from '@/services/groupService'
  const { slides, refresh } = useSlides()
  interface Slide {
    id: number
    name: string
    url: string
  }
  const slideIds = defineModel<number[]>('slide-ids', { required: true })
  const props = defineProps<{ group: SlideGroup; selectedContent?: SlideItem }>()
  const editingGroup = reactive<SlideGroup>(structuredClone(toRaw(props.group)))
  const store = useScreenStore()

  function play() {
    store.playGroup(props.group.id) // title 就是 groupId
  }

  defineEmits<{
    (e: 'select', item: Slide): void
    // (e: 'update:content', items: SlideItem[]): void
    (e: 'update:slide-ids', ids: number[]): void
  }>()

  /* 打开弹窗前刷新一次 */
  const showDialog = ref(false)
  async function openDialog() {
    await refresh()
    showDialog.value = true
  }
  const selectedToAdd = ref<number | null>(null)

  /* 拖拽时使用的临时数组 */
  const localSlideIds = ref<number[]>([...slideIds.value])
  watch(slideIds, ids => {
    localSlideIds.value = [...ids]
  })
  watch(
    () => props.group.version,
    () => {
      Object.assign(editingGroup, structuredClone(toRaw(props.group)))
    }
  )
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
  async function onDragEnd() {
    try {
      // toRaw
      const saved = await saveGroup(toRaw(editingGroup))
      Object.assign(editingGroup, saved)
      store.replaceGroup(saved)
    } catch (err: any) {
      const origin = store.slideGroups.find(g => g.id === editingGroup.id)!
      Object.assign(editingGroup, structuredClone(toRaw(origin)))
      alert(err.message ?? 'Save failed')
    }
  }

  async function saveSpeed() {
    try {
      const updatedGroup = await saveGroup(toRaw(editingGroup))
      Object.assign(editingGroup, updatedGroup)
      store.replaceGroup(updatedGroup)
      alert('Speed saved!')
    } catch (err: any) {
      alert(err.message ?? 'Failed to save speed')
    }
  }
</script>
