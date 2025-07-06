<!-- File: src/components/ScreenCard.vue -->
<template>
  <div class="relative w-[300px] rounded overflow-hidden shadow-lg bg-white">
    <button
      class="absolute top-2 right-2 text-red-500 hover:text-red-700 text-xl font-bold z-10"
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
    <div v-else class="w-[90%] h-40 object-cover mx-auto rounded bg-black">No Content</div>
    <div class="px-6 py-4">
      <div class="font-bold text-xl mb-2">{{ screen.name }}</div>
      <span
        class="inline-block bg-red-400 rounded-full px-3 py-1 text-sm font-semibold text-gray-700 mr-2 mb-2"
      >
        {{ screen.status }}
      </span>
      <p class="text-gray-700 text-base">Current Group: {{ currentSlide?.name ?? 'none' }}</p>
      <p class="text-gray-500 text-sm">URL: {{ screen.urlPath }}</p>
    </div>
    <div class="px-6 pt-4 pb-2">
      <USelectMenu
        v-model="screen.groupId"
        :items="groupOptions"
        class="w-full"
        @update:model-value="emit('updateGroup', screen)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
  import { computed } from 'vue'

  const props = defineProps<{
    screen: {
      id: number
      name: string
      status: string
      groupId: string
      currentContent: string // SlideId
      thumbnailUrl: string
      urlPath: string
    }
    slideGroups: { id: string; slideIds: number[] }[]
    allSlides: { id: number; name: string; url: string }[]
  }>()

  const emit = defineEmits(['updateGroup'])
  /** 把当前屏幕所属 group 找出来 */
  const group = computed(() => props.slideGroups.find(g => g.id === props.screen.groupId) ?? null)
  // 组名（没有则显示 'none'）
  const groupName = computed(() => group.value?.id ?? 'none')

  // 当前幻灯片对象
  const currentSlide = computed(() => {
    if (!group.value || !group.value.slideIds.length) return null
    const slideId = Number(props.screen.currentContent)
    return props.allSlides.find(s => s.id === slideId) ?? null
  })
  const groupOptions = computed(() => props.slideGroups.map(g => g.id))
</script>
