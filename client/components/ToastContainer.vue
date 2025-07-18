<template>
  <div class="fixed top-4 right-4 z-50 space-y-2">
    <component
      :is="toast.component"
      v-for="toast in toasts"
      :key="toast.id"
      v-bind="toast.props"
      @close="removeToast(toast.id)"
    />
  </div>
</template>

<script setup lang="ts">
  import { useToast } from '@/composables/useToast'

  const { toasts, removeToast } = useToast()
</script>
