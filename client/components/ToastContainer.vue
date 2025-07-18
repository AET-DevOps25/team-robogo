<template>
  <div class="fixed top-4 right-4 z-50 space-y-2">
    <Toast
      v-for="toast in toasts"
      :key="toast.id"
      :message="toast.props.message"
      :type="toast.props.type"
      @close="removeToast(toast.id)"
    />
  </div>
</template>

<script setup lang="ts">
  import Toast from './Toast.vue'
  import { useToast } from '@/composables/useToast'

  const { toasts, removeToast } = useToast()
</script>
