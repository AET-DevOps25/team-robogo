<template>
  <div class="min-h-screen bg-background flex flex-col items-center px-4 py-12">
    <div class="flex flex-col items-center mb-10">
      <img src="/favicon.ico" class="w-20 h-20 mb-4" alt="logo" />
      <h1 class="text-4xl md:text-5xl font-extrabold text-primary mb-2 text-center drop-shadow">
        {{ $t('dashboardTitle') }}
      </h1>
      <p class="text-lg md:text-xl text-gray-600 dark:text-gray-300 text-center max-w-2xl">
        {{ $t('dashboardSubtitle') }}
      </p>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-8 w-full max-w-6xl mb-12">
      <UCard class="flex flex-col py-6">
        <h2 class="text-2xl font-bold mb-2">{{ $t('screenOverview') }}</h2>
        <p class="text-gray-500 dark:text-gray-400 mb-4">{{ $t('screenOverviewDesc') }}</p>
        <div class="h-24 flex items-center justify-center text-gray-400">{{ $t('placeholder') }}</div>
      </UCard>
      <UCard class="flex flex-col py-6">
        <h2 class="text-2xl font-bold mb-2">{{ $t('leaderboard') }}</h2>
        <p class="text-gray-500 dark:text-gray-400 mb-4">{{ $t('leaderboardDesc') }}</p>
        <div class="h-24 flex items-center justify-center text-gray-400">{{ $t('placeholder') }}</div>
      </UCard>
      <UCard class="flex flex-col py-6">
        <h2 class="text-2xl font-bold mb-2">{{ $t('contentManagement') }}</h2>
        <p class="text-gray-500 dark:text-gray-400 mb-4">{{ $t('contentManagementDesc') }}</p>
        <div class="h-24 flex items-center justify-center text-gray-400">{{ $t('placeholder') }}</div>
      </UCard>
      <UCard class="flex flex-col py-6">
        <h2 class="text-2xl font-bold mb-2">{{ $t('aiRecommendation') }}</h2>
        <p class="text-gray-500 dark:text-gray-400 mb-4">{{ $t('aiRecommendationDesc') }}</p>
        <div class="h-24 flex items-center justify-center text-gray-400">{{ $t('placeholder') }}</div>
      </UCard>
      <UCard class="flex flex-col py-6">
        <h2 class="text-2xl font-bold mb-2">{{ $t('imageUpload') }}</h2>
        <p class="text-gray-500 dark:text-gray-400 mb-4">{{ $t('imageUploadDesc') }}</p>
        <div class="h-24 flex items-center justify-center text-gray-400">{{ $t('placeholder') }}</div>
      </UCard>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  auth: true,
})
</script> 