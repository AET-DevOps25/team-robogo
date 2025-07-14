<!-- File: src/components/ScreenCard.vue -->
<template>
  <div class="relative w-[300px] rounded overflow-hidden shadow-lg bg-white dark:bg-gray-800">
    <button
      class="absolute top-2 right-2 text-red-500 hover:text-red-700 dark:text-red-400 dark:hover:text-red-600 text-xl font-bold z-10"
      @click="$emit('requestDelete', screen)"
    >
      ×
    </button>

    <img
      v-if="currentSlide"
      class="w-[90%] h-40 object-cover mx-auto rounded"
      :src="currentSlide.url"
      :alt="currentSlide.name"
    />
    <div
      v-else
      class="w-[90%] h-40 object-cover mx-auto rounded bg-gray-200 dark:bg-gray-700 flex items-center justify-center"
    >
      <span class="text-gray-500 dark:text-gray-400">No Content</span>
    </div>
    <div class="px-6 py-4">
      <div class="font-bold text-xl mb-2 text-gray-900 dark:text-white">{{ screen.name }}</div>
      <p class="text-gray-700 dark:text-gray-300 text-base">
        Current Group: {{ screen.groupId || 'None' }}
      </p>
      <p class="text-gray-500 dark:text-gray-400 text-sm">URL: {{ screen.urlPath }}</p>
    </div>
    <div class="px-6 pt-4 pb-2">
      <USelectMenu
        :model-value="screen.groupId"
        :items="groupOptions"
        class="w-full"
        @update:model-value="value => emit('updateGroup', { ...screen, groupId: value })"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
  import { computed } from 'vue'

  const props = defineProps<{
    screen: {
      id: string
      name: string
      status: string
      groupId: string
      currentContent: string // SlideId
      thumbnailUrl: string
      urlPath: string
    }
    slideGroups: { id: string; slideIds: number[]; speed: number; lastResetAt: number }[]
    allSlides: { id: number; name: string; url: string }[]
  }>()

  const emit = defineEmits(['updateGroup', 'requestDelete'])
  /** 把当前屏幕所属 group 找出来 */
  const group = computed(() => props.slideGroups.find(g => g.id === props.screen.groupId) ?? null)

  // 当前幻灯片对象
  const currentSlide = computed(() => {
    if (!group.value || !group.value.slideIds.length) return null
    const slideId = Number(props.screen.currentContent)
    return props.allSlides.find(s => s.id === slideId) ?? null
  })
  const groupOptions = computed(() => props.slideGroups.map(g => g.id))
</script>
