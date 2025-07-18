<template>
  <div class="w-full h-full flex items-center justify-center text-white">
    <div class="text-center">
      <h1 class="text-6xl font-bold mb-4">{{ slide.name }}</h1>
      <p class="text-2xl opacity-70">{{ t('slideType') }}: {{ slide.type }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { useI18n } from 'vue-i18n'
  import type { SlideItem } from '@/interfaces/types'

  defineProps<{ slide: SlideItem }>()
  const { t } = useI18n()
</script> 