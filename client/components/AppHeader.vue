<template>
  <header
    class="w-full flex justify-between items-center gap-4 p-4 bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700"
  >
    <!-- 左侧：管理模块 -->
    <div v-if="isAuthenticated" class="flex items-center gap-2">
      <UButton
        color="primary"
        variant="soft"
        icon="i-heroicons-chart-bar"
        size="sm"
        @click="showScoreManagement = true"
      >
        {{ $t('scoreManagement') }}
      </UButton>
    </div>

    <!-- 右侧：语言切换和登出 -->
    <div class="flex items-center gap-4">
      <LanguageSwitcher />

      <UButton
        v-if="showLogout && isAuthenticated"
        color="error"
        variant="soft"
        icon="i-heroicons-arrow-right-on-rectangle"
        size="sm"
        loading-auto
        loading-icon="i-lucide-loader-2"
        @click="handleLogout"
      >
        {{ $t('logout') }}
      </UButton>
    </div>

    <!-- 分数管理对话框 -->
    <teleport to="body">
      <div
        v-if="showScoreManagement"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40 dark:bg-black dark:bg-opacity-60"
      >
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-lg w-full max-w-6xl h-5/6 mx-4 overflow-hidden">
          <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
            <h2 class="text-xl font-bold text-gray-900 dark:text-white">
              {{ $t('scoreManagement') }}
            </h2>
            <button
              class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
              @click="showScoreManagement = false"
            >
              <span class="text-2xl">×</span>
            </button>
          </div>
          <div class="p-4 h-full overflow-y-auto">
            <ScoreManagement />
          </div>
        </div>
      </div>
    </teleport>
  </header>
</template>

<script setup lang="ts">
  import { ref, computed } from 'vue'
  import ScoreManagement from './ScoreManagement.vue'
  import { useToast, useI18n, useAuth, navigateTo } from '#imports'

  defineProps({
    showLogout: {
      type: Boolean,
      default: true
    }
  })

  const { signOut, status } = useAuth()
  const toast = useToast()
  const { t } = useI18n()

  // 管理模块状态
  const showScoreManagement = ref(false)

  // 计算属性：是否已认证
  const isAuthenticated = computed(() => status.value === 'authenticated')

  const handleLogout = async () => {
    try {
      const result = await signOut({
        callbackUrl: '/login',
        redirect: true
      })

      if (result?.error) {
        toast.add({
          title: t('logoutFailed'),
          description: result.error,
          color: 'error'
        })
        return
      }
      toast.add({
        title: t('logoutSuccess'),
        color: 'success'
      })

      await navigateTo('/login')
    } catch (error) {
      toast.add({
        title: t('logoutFailed'),
        description: error instanceof Error ? error.message : String(error),
        color: 'error'
      })
    }
  }
</script>
