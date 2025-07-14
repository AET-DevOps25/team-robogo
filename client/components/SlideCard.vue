<!-- File: src/components/SlideCard.vue -->
<template>
  <ImageSlideCard v-if="item.type === 'image'" :item="item" v-bind="$attrs" />
  <ScoreSlideCard v-else-if="item.type === 'score'" :item="item" v-bind="$attrs" />
  <div v-else class="text-gray-400">未知类型: {{ item.type }}</div>
</template>

<script setup lang="ts">
  import type { SlideItem } from '@/interfaces/types'
  import ImageSlideCard from './ImageSlideCard.vue'
  import ScoreSlideCard from './ScoreSlideCard.vue'
  const props = defineProps<{ item: SlideItem }>()
</script>
