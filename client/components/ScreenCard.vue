<!-- File: src/components/ScreenCard.vue -->
<template>
  <div class="relative w-[300px] rounded overflow-hidden shadow-lg bg-white dark:bg-gray-800">
    <button
      class="absolute top-2 right-2 text-red-500 hover:text-red-700 dark:text-red-400 dark:hover:text-red-600 text-xl font-bold z-10"
      @click="$emit('requestDelete', screen)"
    >
      ×
    </button>

    <!-- Status Indicator -->
    <div class="absolute top-2 left-2 flex items-center gap-1">
      <div
        class="w-2 h-2 rounded-full"
        :class="{
          'bg-green-500': screen.status === 'ONLINE',
          'bg-red-500': screen.status === 'OFFLINE',
          'bg-yellow-500': screen.status === 'ERROR'
        }"
      />
      <span
        class="text-xs font-medium px-1 py-0.5 rounded"
        :class="{
          'text-green-700 bg-green-100': screen.status === 'ONLINE',
          'text-red-700 bg-red-100': screen.status === 'OFFLINE',
          'text-yellow-700 bg-yellow-100': screen.status === 'ERROR'
        }"
      >
        {{ screen.status }}
      </span>
    </div>

    <div v-if="currentSlide" class="w-[90%] h-40 mx-auto rounded overflow-hidden">
      <SlideCard :item="currentSlide" />
    </div>
    <div
      v-else
      class="w-[90%] h-40 object-cover mx-auto rounded bg-gray-200 dark:bg-gray-700 flex items-center justify-center"
    >
      <span class="text-gray-500 dark:text-gray-400">No Content</span>
    </div>
    <div class="px-6 py-4">
      <div class="font-bold text-xl mb-2 text-gray-900 dark:text-white">{{ screen.name }}</div>
      <p class="text-gray-700 dark:text-gray-300 text-base">
        Current Deck: {{ screen.slideDeck?.id ?? 'None' }}
      </p>
    </div>
    <div class="px-6 pt-4 pb-2">
      <USelectMenu
        :model-value="
          deckOptions.find(option => option.id === screen.slideDeck?.id) ?? deckOptions[0]
        "
        :items="deckOptions"
        item-value="id"
        item-text="name"
        class="w-full"
        @update:model-value="handleDeckChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
  import { computed, onMounted, onUnmounted, watch } from 'vue'
  import SlideCard from './SlideCard.vue'
  import { useDeckStore } from '@/stores/useDeckStore'
  import { assignSlideDeck } from '@/services/screenService'
  import type { SlideDeck, ScreenContent } from '@/interfaces/types'

  interface ScreenCardProps {
    screen: ScreenContent
    slideDecks: SlideDeck[]
  }

  const props = defineProps<ScreenCardProps>()
  const emit = defineEmits(['deckAssigned', 'requestDelete'])

  const deckStore = useDeckStore()

  // 根据 useDeckStore 的 currentSlideIndex 动态获取当前 slide
  const currentSlide = computed(() => {
    // 获取最新的 slideDeck 数据
    let slideDeck = props.screen.slideDeck

    // 如果当前 screen 的 slideDeck 是正在播放的 deck，从 store 中获取最新数据
    if (slideDeck && deckStore.currentDeckId === slideDeck.id && deckStore.currentDeck) {
      slideDeck = deckStore.currentDeck
    }

    if (!slideDeck?.slides || slideDeck.slides.length === 0) return null

    // 如果当前 screen 的 slideDeck 是正在播放的 deck
    if (deckStore.currentDeckId === slideDeck.id) {
      const slideIndex = deckStore.currentSlideIndex
      const slides = slideDeck.slides
      return slides[slideIndex] ?? null
    }

    // 否则显示第一个 slide
    return slideDeck.slides[0] ?? null
  })

  const deckOptions = computed(() => {
    const options = [
      { id: undefined, name: 'None' },
      ...(props.slideDecks?.map(deck => ({ id: deck.id, name: deck.name })) ?? [])
    ]
    return options
  })

  // 处理 slideDeck 选择
  async function handleDeckChange(selectedOption: { id: number | undefined; name: string }) {
    const deckId = selectedOption.id
    if (deckId === (props.screen.slideDeck?.id ?? undefined)) return

    try {
      if (deckId === undefined) {
        // 取消分配 slideDeck - 通过更新 screen 来设置 slideDeck 为 null
        const { updateScreen } = await import('@/services/screenService')
        await updateScreen(props.screen.id, {
          ...props.screen,
          slideDeck: null
        })
      } else {
        // 分配新的 slideDeck
        await assignSlideDeck(props.screen.id, deckId)
      }
      emit('deckAssigned') // 通知父组件刷新
    } catch (error) {
      console.error('Failed to assign slide deck:', error)
    }
  }

  // 当组件挂载时，如果这个 screen 的 slideDeck 是当前播放的，设置到 store
  onMounted(() => {
    if (props.screen.slideDeck && deckStore.currentDeckId === props.screen.slideDeck.id) {
      deckStore.setCurrentDeck(props.screen.slideDeck)
    }
  })

  // 监听 props.screen.slideDeck 的变化，自动更新 store
  const updateStoreDeck = () => {
    if (props.screen.slideDeck && deckStore.currentDeckId === props.screen.slideDeck.id) {
      deckStore.setCurrentDeck(props.screen.slideDeck)
    }
  }

  // 当 screen 的 slideDeck 变化时，更新 store
  watch(() => props.screen.slideDeck, updateStoreDeck, { deep: true })

  // 当组件卸载时，如果这个 screen 的 slideDeck 是当前播放的，停止监听
  onUnmounted(() => {
    if (props.screen.slideDeck && deckStore.currentDeckId === props.screen.slideDeck.id) {
      deckStore.stopVersionCheck()
    }
  })
</script>
