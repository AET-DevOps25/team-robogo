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
    <button
      class="absolute top-2 left-2 flex items-center gap-1 px-2 py-1 rounded border transition-colors hover:shadow-sm"
      :class="{
        'bg-green-100 border-green-300 text-green-700 hover:bg-green-200':
          screen.status === 'ONLINE',
        'bg-red-100 border-red-300 text-red-700 hover:bg-red-200': screen.status === 'OFFLINE',
        'bg-yellow-100 border-yellow-300 text-yellow-700 hover:bg-yellow-200':
          screen.status === 'ERROR'
      }"
      @click="toggleStatus"
    >
      <div
        class="w-2 h-2 rounded-full"
        :class="{
          'bg-green-500': screen.status === 'ONLINE',
          'bg-red-500': screen.status === 'OFFLINE',
          'bg-yellow-500': screen.status === 'ERROR'
        }"
      />
      <span class="text-xs font-medium">
        {{ screen.status }}
      </span>
    </button>

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
        {{ $t('currentDeck') }}: {{ screen.slideDeck?.name ?? $t('none') }}
      </p>
    </div>
    <div class="px-6 pt-4 pb-2">
      <select
        :value="selectedDeckOption?.id ?? null"
        class="w-full px-3 py-2 text-sm text-gray-900 dark:text-white bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 dark:focus:ring-blue-400"
        @change="handleSelectChange"
      >
        <option v-for="option in deckOptions" :key="String(option.id)" :value="option.id">
          {{ option.name }}
        </option>
      </select>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { computed, onMounted, onUnmounted, watch } from 'vue'
  import { useI18n } from 'vue-i18n'
  import SlideCard from './SlideCard.vue'
  import { useDeckStore } from '@/stores/useDeckStore'
  import { assignSlideDeck } from '@/services/screenService'
  import type { SlideDeck, ScreenContent } from '@/interfaces/types'
  import { useToast } from '@/composables/useToast'

  const { t } = useI18n()
  const { showError, showSuccess } = useToast()

  interface ScreenCardProps {
    screen: ScreenContent
    slideDecks: SlideDeck[]
  }

  const props = defineProps<ScreenCardProps>()
  const emit = defineEmits(['deckAssigned', 'requestDelete'])

  const deckStore = useDeckStore()

  const currentSlide = computed(() => {
    let slideDeck = props.screen.slideDeck

    if (slideDeck && deckStore.currentDeckId === slideDeck.id && deckStore.currentDeck) {
      slideDeck = deckStore.currentDeck
    }

    if (!slideDeck?.slides || slideDeck.slides.length === 0) return null

    if (deckStore.currentDeckId === slideDeck.id) {
      const slideIndex = deckStore.currentSlideIndex
      const slides = slideDeck.slides
      return slides[slideIndex] ?? null
    }

    return slideDeck.slides[0] ?? null
  })

  const deckOptions = computed(() => {
    const options = [
      { id: null, name: t('none') },
      ...(props.slideDecks?.map(deck => ({ id: deck.id, name: deck.name })) ?? [])
    ]
    return options
  })

  const selectedDeckOption = computed({
    get() {
      return (
        deckOptions.value.find(option => option.id === props.screen.slideDeck?.id) ??
        deckOptions.value[0]
      )
    },
    set(value) {
      if (value) {
        handleDeckChange(value)
      }
    }
  })

  async function handleDeckChange(selectedOption: { id: number | null; name: string }) {
    const deckId = selectedOption.id
    if (deckId === (props.screen.slideDeck?.id ?? null)) return

    try {
      if (deckId === null) {
        const { updateScreen } = await import('@/services/screenService')
        await updateScreen(props.screen.id, {
          ...props.screen,
          slideDeck: null
        })
      } else {
        await assignSlideDeck(props.screen.id, deckId)
      }
      emit('deckAssigned')
    } catch (error) {
      showError(t('failedToAssignDeck'))
    }
  }

  async function toggleStatus() {
    try {
      const newStatus = props.screen.status === 'ONLINE' ? 'OFFLINE' : 'ONLINE'
      const { updateScreenStatus } = await import('@/services/screenService')
      await updateScreenStatus(props.screen.id, newStatus)
      showSuccess(t('statusUpdated'))
      emit('deckAssigned')
    } catch (error) {
      showError(t('failedToUpdateStatus'))
    }
  }

  // 处理 select 元素的 change 事件
  function handleSelectChange(event: Event) {
    const selectedValue = (event.target as HTMLSelectElement).value
    const selectedOption = deckOptions.value.find(
      option => option.id === (selectedValue === 'null' ? null : parseInt(selectedValue))
    )
    if (selectedOption) {
      handleDeckChange(selectedOption)
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
