<template>
  <div class="bg-white dark:bg-gray-700 p-4 rounded-xl shadow-md flex flex-col gap-3 h-[350px]">
    <div class="flex items-center justify-between">
      <h3 class="text-lg font-semibold text-gray-900 dark:text-white">
        {{ t('chatWithAI') }}
      </h3>
      <div class="flex items-center gap-2">
        <div :class="['w-2 h-2 rounded-full', aiServiceStatus ? 'bg-green-500' : 'bg-red-500']" />
        <select
          v-model="selectedAiService"
          class="text-xs px-2 py-1 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-white rounded"
        >
          <option value="openwebui">OpenWebUI</option>
          <option value="openai">OpenAI</option>
        </select>
      </div>
    </div>
    <div
      class="flex-1 overflow-y-auto border border-gray-200 dark:border-gray-600 rounded p-2 text-sm text-gray-700 dark:text-gray-300 bg-gray-50 dark:bg-gray-800"
    >
      <div v-for="(msg, index) in chatHistory" :key="index" class="mb-3">
        <div
          :class="[
            'p-2 rounded',
            msg.role === 'You'
              ? 'bg-blue-100 dark:bg-blue-900/30 ml-8'
              : 'bg-gray-100 dark:bg-gray-700 mr-8'
          ]"
        >
          <div class="flex items-center gap-2 mb-1">
            <strong
              :class="
                msg.role === 'You'
                  ? 'text-blue-700 dark:text-blue-300'
                  : 'text-gray-700 dark:text-gray-300'
              "
            >
              {{ msg.role === 'You' ? '👤' : '🤖' }} {{ msg.role }}
            </strong>
          </div>
          <div class="whitespace-pre-wrap">{{ msg.text }}</div>
        </div>
      </div>
      <div v-if="isAiLoading" class="mb-3">
        <div class="bg-gray-100 dark:bg-gray-700 mr-8 p-2 rounded">
          <div class="flex items-center gap-2">
            <div
              class="animate-spin w-4 h-4 border-2 border-gray-400 border-t-transparent rounded-full"
            />
            <span class="text-gray-600 dark:text-gray-400">{{ t('aiThinking') }}</span>
          </div>
        </div>
      </div>
      <div v-if="aiError" class="mb-3">
        <div class="bg-red-100 dark:bg-red-900/30 p-2 rounded">
          <div class="flex items-center gap-2">
            <span class="text-red-600 dark:text-red-400">❌ {{ aiError }}</span>
          </div>
        </div>
      </div>
    </div>
    <div class="flex gap-2 pt-2">
      <input
        v-model="userMessage"
        :disabled="isAiLoading"
        :placeholder="t('enterQuestion')"
        class="flex-1 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-white p-2 rounded disabled:opacity-50"
        @keyup.enter="sendMessage"
      />
      <button
        :disabled="isAiLoading || !userMessage.trim()"
        class="px-4 py-2 bg-blue-600 dark:bg-blue-500 text-white rounded hover:bg-blue-700 dark:hover:bg-blue-600 disabled:opacity-50 disabled:cursor-not-allowed"
        @click="sendMessage"
      >
        {{ isAiLoading ? t('sending') : t('send') }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref } from 'vue'
  import { useI18n } from 'vue-i18n'
  import type { ChatMessage } from '@/interfaces/types'
  import { getSuggestion, checkHealth } from '@/services/aiService'

  const { t } = useI18n()

  const userMessage = ref('')
  const chatHistory = ref<ChatMessage[]>([])
  const isAiLoading = ref(false)
  const aiError = ref('')
  const selectedAiService = ref<'openwebui' | 'openai'>('openwebui')
  const aiServiceStatus = ref(false)

  // 检查AI服务健康
  checkHealth()
    .then(health => {
      aiServiceStatus.value = health.status === 'healthy'
    })
    .catch(() => {
      aiServiceStatus.value = false
    })

  async function sendMessage() {
    if (!userMessage.value.trim()) return
    const messageText = userMessage.value.trim()
    userMessage.value = ''
    chatHistory.value.push({ role: 'You', text: messageText })
    isAiLoading.value = true
    aiError.value = ''
    try {
      const response = await getSuggestion({
        text: messageText,
        service: selectedAiService.value
      })
      chatHistory.value.push({ role: 'AI', text: response.suggestion })
    } catch (error: any) {
      aiError.value = error.message || t('aiServiceFailed')
      // eslint-disable-next-line no-console
      console.error('AI service error:', error)
    } finally {
      isAiLoading.value = false
    }
  }
</script>
