<!-- File: src/pages/screen/[id].vue -->
<script setup lang="ts">
import { useRoute } from 'vue-router'
import { computed, onMounted } from 'vue'
import { useScreenStore } from '@/stores/useScreenStore'
import { useSlides } from '@/composables/useSlides'

const { slides, refresh } = useSlides()
await refresh()

const store = useScreenStore()
const screenId = useRoute().params.id as string
const screen = computed(() => store.screens.find(s => s.id === screenId))
const group = computed(() => store.slideGroups.find(g => g.id === screen.value?.groupId))


const currentSlide = computed(() => {
  const id = Number(screen.value?.currentContent)
  if (!id) return null
  return slides.value.find(s => s.id === id) ?? null
})


</script>

<template>
  <div class="w-full h-screen bg-black flex items-center justify-center">
    <div v-if="currentSlide">
      <img :src="currentSlide.url" :key="currentSlide.id" class="max-w-full max-h-full object-contain"
        :alt="currentSlide.name" />
    </div>
    <div v-else class="text-white text-3xl">No Slide</div>
  </div>
</template>
