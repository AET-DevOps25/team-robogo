<!-- File: src/components/SlideCard.vue -->
<template>
  <component
    :is="getSlideComponent(item.type)"
    v-if="getSlideComponent(item.type)"
    :item="item"
    v-bind="$attrs"
  />
  <div v-else class="text-gray-400">
    {{ t('unknownSlideType', { type: item.type }) }}
  </div>
</template>

<script setup lang="ts">
  import { useI18n } from 'vue-i18n'
  import ImageSlideCard from './ImageSlideCard.vue'
  import ScoreSlideCard from './ScoreSlideCard.vue'
  import { SlideType, type SlideItem } from '@/interfaces/types'

  const _ = defineProps<{ item: SlideItem }>()
  const { t } = useI18n()

  function getSlideComponent(type: SlideType) {
    switch (type) {
      case SlideType.IMAGE:
        return ImageSlideCard
      case SlideType.SCORE:
        return ScoreSlideCard
      default:
        return null
    }
  }
</script>
