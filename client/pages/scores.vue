<template>
  <div class="p-6 space-y-6 bg-gray-50 dark:bg-gray-900 min-h-screen">
    <div class="max-w-7xl mx-auto">
      <!-- 页面标题 -->
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-gray-900 dark:text-white">
          {{ $t('scoreManagement') }}
        </h1>
        <p class="text-gray-600 dark:text-gray-400 mt-2">
          {{ $t('scoreManagementDesc') }}
        </p>
      </div>

      <!-- 分数管理组件 -->
      <ScoreManagement />
    </div>
  </div>
</template>

<script setup lang="ts">
  import ScoreManagement from '@/components/ScoreManagement.vue'
</script>
