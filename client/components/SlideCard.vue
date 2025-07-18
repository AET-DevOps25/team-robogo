<!-- File: src/components/SlideCard.vue -->
<template>
  <div class="w-full max-w-4xl mx-auto">
    <component
      :is="getSlideComponent(item.type)"
      v-if="getSlideComponent(item.type)"
      :item="item"
      v-bind="$attrs"
    />
    <div v-else class="text-gray-400 text-center pt-8 pb-8">
      {{ t('unknownSlideType', { type: item.type }) }}
    </div>
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

<style scoped>
  /* 添加加载动画样式 */
  .loading {
    display: flex;
    align-items: center;
    justify-content: center;
    padding-top: 2rem;
    padding-bottom: 2rem;
  }

  .loading::after {
    content: '';
    width: 2rem;
    height: 2rem;
    border: 4px solid #e5e7eb;
    border-top: 4px solid #3b82f6;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }

  @keyframes spin {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }
</style>
