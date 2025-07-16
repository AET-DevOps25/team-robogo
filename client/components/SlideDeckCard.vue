<template>
  <div class="w-full rounded-xl shadow-lg p-4 bg-white dark:bg-gray-800 space-y-3">
    <div class="flex gap-2 justify-between items-center">
      <h3 class="text-lg font-semibold text-gray-900 dark:text-white">{{ deck.id }}</h3>
      <div class="flex items-center gap-2 mt-2" />
      <h3 class="text-lg font-semibold text-gray-900 dark:text-white">{{ deck.name }}</h3>
      <div class="flex items-center gap-2 mt-2">
        <span class="text-lg font-semibold text-gray-900 dark:text-white">Speed (s)</span>
        <SpeedControl v-model="editingDeck.transitionTime" />
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
        v-model="editingDeck&&editingDeck.slides"
        item-key="id"
        class="flex flex-wrap gap-4"
        @end="onDragEnd"
      >
        <template v-if="editingDeck.slides && editingDeck.slides.length > 0">
          <template #item="{ element }">
            <template v-if="element">   <!-- check null -->
            <!-- 根据幻灯片类型使用不同的卡片组件 -->
            <ImageSlideCard
              v-if="element.type === SlideType.IMAGE"
              :item="element"
              :selected="selectedContent?.id === element.id"
              @click="$emit('select', element)"
            />
            <ScoreSlideCard
              v-else-if="element.type === SlideType.SCORE"
              :item="element"
              :selected="selectedContent?.id === element.id"
              @click="$emit('select', element)"
            />
            <div v-else class="text-gray-400">
              unknown slide type: {{ element.type }}
            </div>
          </template>
          </template>
        </template>
      </draggable>

      <!-- Add button -->
      <div
        v-if="editingDeck.id !== -1"
        class="w-[300px] h-[200px] rounded overflow-hidden shadow-lg bg-gray-100 dark:bg-gray-700 flex items-center justify-center text-5xl text-gray-400 dark:text-gray-500 hover:bg-gray-200 dark:hover:bg-gray-600 transition"
        @click="showDialog"
      >
        +
      </div>
      <div 
        v-if="!editingDeck?.slides || editingDeck.slides.length === 0"
        class="w-full text-center py-8 text-gray-500 dark:text-gray-400"
      >
        no slides in this deck
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
          <!-- 显示所有幻灯片元数据 -->
          <div 
            v-for="meta in allSlidesMeta" 
            :key="meta.id" 
            class="cursor-pointer p-2 border rounded"
            :class="{ 'border-blue-500': selectedToAdd === meta.id }"
            @click="selectedToAdd = meta.id"
          >
            <div v-if="isImageLoaded(meta.id)" class="w-full h-40 flex items-center justify-center">
              <img :src="getImageUrl(meta.id)" class="max-h-full max-w-full" />
            </div>
            <div v-else class="w-full h-40 flex items-center justify-center bg-gray-200">
              <div class="animate-spin w-6 h-6 border-2 border-gray-400 border-t-transparent rounded-full"></div>
            </div>
            <div class="mt-2 text-center truncate">{{ meta.name }}</div>
          </div>
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
  import { ref, computed, watch, reactive, toRaw, onUnmounted } from 'vue'
  import draggable from 'vuedraggable'
  import SpeedControl from './SpeedControl.vue'
  import ImageSlideCard from './ImageSlideCard.vue'
  import ScoreSlideCard from './ScoreSlideCard.vue'
  import { type LocalSlideDeck, type SlideItem, type ImageSlideMeta, SlideType } from '@/interfaces/types'
  import { useScreenStore } from '@/stores/useScreenStore'
  import { toLocalSlideDeck, updateSlideDeck } from '@/services/slideDeckService'
  import { fetchImageBlobById } from '@/services/slideImageService'

  const props = defineProps<{ 
    deck: LocalSlideDeck; 
    selectedContent?: SlideItem;
    allSlidesMeta: ImageSlideMeta[];
  }>()

  const emit = defineEmits<{
    (e: 'select', item: SlideItem): void
    (e: 'update:slide-ids', ids: number[]): void
  }>()

  const editingDeck = reactive<LocalSlideDeck>(structuredClone(toRaw(props.deck)))
  const store = useScreenStore()
  
  // 图片URL缓存
  const imageUrls = ref<Record<number, string>>({})
  const imageLoading = ref<Record<number, boolean>>({})

  // 获取图片URL
const getImageUrl = (id: number): string => {
  return imageUrls.value[id] || ''
}

  // 检查图片是否已加载
  const isImageLoaded = (id: number): boolean => {
    return !!imageUrls.value[id]
  }

  // 加载图片
  const loadImage = async (id: number) => {
    if (imageUrls.value[id]) return
    
    imageLoading.value[id] = true
    try {
      const blob = await fetchImageBlobById(id)
      const url = URL.createObjectURL(blob)
      imageUrls.value[id] = url
    } catch (error) {
      console.error('Error loading image:', error)
    } finally {
      imageLoading.value[id] = false
    }
  }

  // 在对话框显示时加载所有图片
  watch(
    () => showDialog.value,
    (show) => {
      if (show) {
        props.allSlidesMeta.forEach(meta => {
          loadImage(meta.id)
        })
      }
    }
  )

  // 确保slides数组存在
  watch(
    () => props.deck,
    () => {
      Object.assign(editingDeck, structuredClone(toRaw(props.deck)))
      if (editingDeck.slides === null) {
        editingDeck.slides = []
      }
    },
    { immediate: true, deep: true }
  )

  // 播放当前组
  function play() {
    store.playDeck(props.deck.id)
  }

  // 速度控制
  const speed = defineModel<number>('speed', { required: true })

  // 对话框相关
  const showDialog = ref(false)
  const selectedToAdd = ref<number | null>(null)

  const openDialog = () => {
    showDialog.value = true
  }

  const confirmAdd = () => {
    if (selectedToAdd.value) {
      const meta = props.allSlidesMeta.find(m => m.id === selectedToAdd.value)
            if (editingDeck.slides === null) {
        editingDeck.slides = []
      }
      if (meta) {
        const newSlide: SlideItem = {
          id: meta.id,
          index: editingDeck.slides.length,
          name: meta.name,
          type: SlideType.IMAGE,
          imageMeta: meta
        }
        
        editingDeck.slides.push(newSlide)
        emit('update:slides', [...editingDeck.slides])
      }
    }
    resetDialog()
  }

  const cancelAdd = () => {
    resetDialog()
  }

  const resetDialog = () => {
    showDialog.value = false
    selectedToAdd.value = null
  }

  // 拖拽结束处理
  async function onDragEnd() {
    try {
      const res = await updateSlideDeck(editingDeck.id, toRaw(editingDeck))
      const saved = toLocalSlideDeck(res)
      Object.assign(editingDeck, saved)
      store.replaceGroup(saved)
    } catch (err: any) {
      const origin = store.slideDecks.find(g => g.id === editingDeck.id)!
      Object.assign(editingDeck, structuredClone(toRaw(origin)))
      alert(err.message ?? 'Save failed')
    }
  }

  // 保存速度设置
  async function saveSpeed() {
    try {
      const res = await updateSlideDeck(editingDeck.id, toRaw(editingDeck))
      const saved = toLocalSlideDeck(res)
      Object.assign(editingDeck, saved)
      alert('Speed saved!')
    } catch (err: any) {
      alert(err.message ?? 'Failed to save speed')
    }
  }

  // 组件卸载时释放所有图片URL - 修复了此处的问题
  onUnmounted(() => {
    if (imageUrls.value) {
      Object.values(imageUrls.value).forEach(url => {
        URL.revokeObjectURL(url)
      })
    }
  })
</script>