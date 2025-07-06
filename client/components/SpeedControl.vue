<!-- components/SpeedControl.vue -->
<script setup lang="ts">
  const modelValue = defineModel<number>()
</script>

<template>
  <div class="flex items-center gap-2">
    <USlider v-model="modelValue" :min="1" :max="100" class="w-40" />
    <span class="text-sm text-gray-500 w-10 text-right">{{ modelValue }}</span>
  </div>
</template>
