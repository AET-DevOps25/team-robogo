<template>
  <div class="flex flex-col items-center justify-center min-h-screen bg-gradient-to-br from-blue-100 via-white to-blue-200">
    <h1 class="text-4xl font-extrabold text-blue-700 mb-4 drop-shadow">欢迎来到新前端！</h1>
    <p class="mt-2 text-lg text-gray-600">Nuxt 3 + TypeScript + Vue 3 项目已重置</p>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  auth: true
})
</script>