<template>
  <div class="score-slide-card">
    <div class="font-semibold text-gray-900 dark:text-white mb-2">{{ item.name }}</div>
    <template v-if="isScoreSlide(item)">
      <div
        v-for="score in item.scores"
        :key="score.time"
        class="text-sm text-gray-700 dark:text-gray-300"
      >
        {{ score.points }} {{ t('scoreUnit') }} ({{ score.time }}s)
        <span v-if="score.highlight" class="text-red-500 font-bold ml-2">★</span>
      </div>
      <div class="text-xs text-gray-400 mt-1">{{ t('category') }}: {{ item.categoryId }}</div>
    </template>
    <template v-else>
      <div class="text-xs text-gray-400 mt-1">{{ t('notScoreType') }}</div>
    </template>
  </div>
</template>

<script setup lang="ts">
  import { useI18n } from 'vue-i18n'
  import { SlideType, type SlideItem } from '@/interfaces/types'
  function isScoreSlide(item: SlideItem): item is Extract<SlideItem, { type: SlideType.SCORE }> {
    return item.type === SlideType.SCORE && 'scores' in item && 'categoryId' in item
  }
  defineProps<{ item: SlideItem }>()
  const { t } = useI18n()
</script>

<style scoped>
  .score-slide-card {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }
</style>
