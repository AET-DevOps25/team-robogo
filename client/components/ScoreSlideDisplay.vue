<template>
  <div class="w-full h-full flex items-center justify-center text-white">
    <div class="text-center max-w-4xl mx-auto px-8">
      <h1 class="text-6xl font-bold mb-8">{{ slide.name }}</h1>
      <div
        v-if="slide.type === 'SCORE' && 'scores' in slide && slide.scores.length > 0"
        class="space-y-4"
      >
        <div
          v-for="score in slide.scores"
          :key="score.team.id"
          class="text-4xl p-6 rounded-lg transition-all duration-300"
          :class="[
            score.highlight
              ? 'bg-yellow-500/20 text-yellow-300 font-bold border-2 border-yellow-400'
              : 'bg-white/10 hover:bg-white/20'
          ]"
        >
          <div class="flex justify-between items-center">
            <span class="font-semibold">{{ score.team.name }}</span>
            <span class="text-5xl font-bold">{{ score.points }}</span>
          </div>
          <div class="flex justify-between items-center mt-2 text-2xl opacity-80">
            <span>{{ score.time }} {{ t('seconds') }}</span>
            <span>{{ t('rankWithNumber', { number: score.rank }) }}</span>
          </div>
        </div>
      </div>
      <div v-else class="text-3xl opacity-70">{{ t('noScoreData') }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { useI18n } from 'vue-i18n'
  import type { SlideItem } from '@/interfaces/types'

  defineProps<{ slide: SlideItem }>()
  const { t } = useI18n()
</script> 