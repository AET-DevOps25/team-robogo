<template>
  <div class="min-h-screen bg-background flex flex-col items-center justify-center px-4 py-12">
    <div class="flex flex-col items-center mb-10">
      <img src="/favicon.ico" class="w-20 h-20 mb-4" alt="logo" />
      <h1 class="text-4xl md:text-5xl font-extrabold text-primary mb-2 text-center drop-shadow">
        {{ $t('homeTitle') }}
      </h1>
      <p class="text-lg md:text-xl text-gray-600 dark:text-gray-300 text-center max-w-2xl">
        {{ $t('homeSubtitle') }}
      </p>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-6 w-full max-w-5xl mb-12">
      <UCard class="flex flex-col items-center py-8">
        <Icon name="i-heroicons-device-tablet" class="w-10 h-10 text-primary mb-2" />
        <h3 class="text-xl font-bold mb-1">{{ $t('featureGroup') }}</h3>
        <p class="text-gray-500 dark:text-gray-400 text-center text-sm">
          {{ $t('featureGroupDesc') }}
        </p>
      </UCard>
      <UCard class="flex flex-col items-center py-8">
        <Icon name="i-heroicons-bolt" class="w-10 h-10 text-primary mb-2" />
        <h3 class="text-xl font-bold mb-1">{{ $t('featureRealtime') }}</h3>
        <p class="text-gray-500 dark:text-gray-400 text-center text-sm">
          {{ $t('featureRealtimeDesc') }}
        </p>
      </UCard>
      <UCard class="flex flex-col items-center py-8">
        <Icon name="i-heroicons-sparkles" class="w-10 h-10 text-primary mb-2" />
        <h3 class="text-xl font-bold mb-1">{{ $t('featureAI') }}</h3>
        <p class="text-gray-500 dark:text-gray-400 text-center text-sm">
          {{ $t('featureAIDesc') }}
        </p>
      </UCard>
    </div>

    <div class="max-w-3xl text-center mb-10">
      <h2 class="text-2xl font-semibold mb-2">{{ $t('projectGoalTitle') }}</h2>
      <p class="text-gray-700 dark:text-gray-200 text-base">
        {{ $t('projectGoalDesc') }}
      </p>
    </div>

    <UButton to="/login" size="xl" color="primary" class="px-10 py-4 text-lg font-bold rounded-full shadow-lg">
      {{ $t('tryNow') }}
    </UButton>
  </div>
</template>

<script setup lang="ts">
import { Icon } from '#components'
</script>