import { ref, markRaw } from 'vue'
import type { Component } from 'vue'
import Toast from '@/components/Toast.vue'

interface ToastOptions {
  message: string
  type?: 'success' | 'error' | 'info' | 'warning'
  duration?: number
}

const toasts = ref<Array<{ id: number; component: Component; props: ToastOptions }>>([])
let nextId = 1

export function useToast() {
  function showToast(options: ToastOptions) {
    const id = nextId++
    const toast = {
      id,
      component: markRaw(Toast),
      props: options
    }

    toasts.value.push(toast)

    // 自动移除 toast
    if (options.duration !== 0) {
      setTimeout(() => {
        removeToast(id)
      }, options.duration || 5000)
    }

    return id
  }

  function removeToast(id: number) {
    const index = toasts.value.findIndex(toast => toast.id === id)
    if (index > -1) {
      toasts.value.splice(index, 1)
    }
  }

  function showSuccess(message: string, duration = 5000) {
    return showToast({ message, type: 'success', duration })
  }

  function showError(message: string, duration = 5000) {
    return showToast({ message, type: 'error', duration })
  }

  function showInfo(message: string, duration = 5000) {
    return showToast({ message, type: 'info', duration })
  }

  function showWarning(message: string, duration = 5000) {
    return showToast({ message, type: 'warning', duration })
  }

  return {
    toasts,
    showToast,
    showSuccess,
    showError,
    showInfo,
    showWarning,
    removeToast
  }
}
