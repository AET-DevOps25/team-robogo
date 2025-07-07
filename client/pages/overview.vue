<template>
  <div class="p-6 space-y-6 bg-gray-50 dark:bg-gray-900 min-h-screen">
    <!--Screens Monitor -->
    <section>
      <h2 class="text-2xl font-bold mb-4 text-gray-900 dark:text-white">
        {{ t('screensMonitor') }}
      </h2>
      <div
        class="max-h-[50vh] overflow-y-auto scrollbar-thin scrollbar-thumb-gray-400 dark:scrollbar-thumb-gray-600 scrollbar-track-gray-100 dark:scrollbar-track-gray-800 rounded p-2"
      >
        <div class="flex flex-wrap gap-6 justify-start">
          <ScreenCard
            v-for="screen in screens"
            :key="screen.id"
            :screen="screen"
            :slide-groups="slideGroups"
            :all-slides="contentList"
            @update-group="onUpdateScreenGroup"
            @request-delete="
              (screen: Screen) => {
                showDeleteConfirm = true
                screenToDelete = screen
              }
            "
          />
          <div
            class="w-[300px] h-[260px] flex items-center justify-center rounded-xl bg-gray-100 dark:bg-gray-700 text-5xl text-gray-400 dark:text-gray-500 hover:bg-gray-200 dark:hover:bg-gray-600 cursor-pointer"
            @click="showAddScreenDialog = true"
          >
            +
          </div>
        </div>
      </div>
    </section>
    <!-- Add Screen Dialog -->
    <div
      v-if="showAddScreenDialog"
      class="fixed inset-0 bg-black bg-opacity-40 dark:bg-black dark:bg-opacity-60 z-50 flex items-center justify-center"
    >
      <div class="bg-white dark:bg-gray-800 rounded-lg p-6 shadow-lg w-[400px] relative">
        <h3 class="text-lg font-bold mb-4 text-gray-900 dark:text-white">
          {{ t('addNewScreen') }}
        </h3>
        <input
          v-model="newScreenName"
          :placeholder="t('screenName')"
          class="mb-2 w-full border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white p-2 rounded"
        />
        <input
          v-model="newScreenUrl"
          :placeholder="t('thumbnailUrl')"
          class="mb-4 w-full border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white p-2 rounded"
        />

        <div class="flex justify-end gap-2">
          <button
            class="px-4 py-2 bg-gray-300 dark:bg-gray-600 rounded hover:bg-gray-400 dark:hover:bg-gray-500 text-gray-800 dark:text-gray-200"
            @click="showAddScreenDialog = false"
          >
            {{ t('cancel') }}
          </button>
          <button
            class="px-4 py-2 bg-blue-600 dark:bg-blue-500 rounded hover:bg-blue-700 dark:hover:bg-blue-600 text-white"
            @click="addNewScreen"
          >
            {{ t('add') }}
          </button>
        </div>
        <button
          class="absolute top-2 right-2 text-gray-400 dark:text-gray-500 hover:text-gray-600 dark:hover:text-gray-300"
          @click="showAddScreenDialog = false"
        >
          ✕
        </button>
      </div>
    </div>
    <!-- Confirm Delete Dialog  -->
    <div
      v-if="showDeleteConfirm"
      class="fixed inset-0 bg-black bg-opacity-40 dark:bg-black dark:bg-opacity-60 z-50 flex items-center justify-center"
    >
      <div class="bg-white dark:bg-gray-800 p-6 rounded-xl shadow-lg w-[400px] relative">
        <h3 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">
          {{ t('confirmDelete') }}
        </h3>
        <p class="text-gray-700 dark:text-gray-300">
          {{ t('areYouSureDelete') }}
          <strong>{{ screenToDelete?.name }}</strong>
          ?
        </p>
        <div class="flex justify-end gap-2 mt-4">
          <button
            class="px-4 py-2 bg-gray-300 dark:bg-gray-600 rounded text-gray-800 dark:text-gray-200 hover:bg-gray-400 dark:hover:bg-gray-500"
            @click="showDeleteConfirm = false"
          >
            {{ t('cancel') }}
          </button>
          <button
            class="px-4 py-2 bg-red-600 dark:bg-red-500 text-white rounded hover:bg-red-700 dark:hover:bg-red-600"
            @click="confirmDeleteScreen"
          >
            {{ t('delete') }}
          </button>
        </div>
        <button
          class="absolute top-2 right-2 text-gray-400 dark:text-gray-500 hover:text-gray-600 dark:hover:text-gray-300"
          @click="showDeleteConfirm = false"
        >
          ✕
        </button>
      </div>
    </div>

    <section
      class="grid grid-cols-[2fr_1fr] gap-6 bg-gray-50 dark:bg-gray-800 p-6 rounded-xl shadow"
    >
      <!-- Left: Slide Group -->

      <div>
        <div class="flex items-center justify-between mb-2">
          <h2 class="text-lg font-semibold text-gray-900 dark:text-white">
            {{ t('slideGroups') }}
          </h2>

          <button
            class="text-sm px-3 py-1 bg-blue-500 dark:bg-blue-600 text-white rounded hover:bg-blue-600 dark:hover:bg-blue-700"
            @click="triggerFileInput"
          >
            {{ t('uploadSlide') }}
          </button>
          <input
            ref="fileInput"
            type="file"
            accept="image/*"
            class="hidden"
            @change="handleFileUpload"
          />
        </div>
        <div>
          <USelectMenu
            v-model="selectedGroupId"
            :items="slideGroups.map((g: SlideGroup) => g.id)"
            class="w-48"
          />
          <UButton color="neutral" variant="outline" @click="showAddGroupDialog = true">
            {{ t('addGroup') }}
          </UButton>
        </div>
        <div
          class="h-[400px] w-full overflow-y-auto border-gray-200 dark:border-gray-600 rounded p-2 flex flex-wrap gap-6 justify-start scrollbar-thin scrollbar-thumb-gray-400 dark:scrollbar-thumb-gray-600 scrollbar-track-gray-100 dark:scrollbar-track-gray-800"
        >
          <SlideGroupCard
            v-if="currentGroup && currentGroup.id !== 'None'"
            :key="currentGroup.id"
            v-model:slide-ids="currentGroup.slideIds"
            v-model:speed="currentGroup.speed"
            :title="currentGroup.id"
            :all-slides="contentList"
            :selected-content="selectedContent"
            :slides="currentGroupSlides"
            @select="selectContent"
          />
        </div>
      </div>

      <!-- Right: Groups -->
      <div class="flex flex-col gap-4">
        <!-- Scores Update -->
        <div
          class="bg-white dark:bg-gray-700 p-4 rounded-xl shadow-md flex flex-col sm:flex-row items-center gap-4"
        >
          <input
            v-model="scoreTarget"
            :placeholder="t('teamId')"
            class="border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-white p-2 rounded w-full sm:w-auto"
          />
          <input
            v-model="scoreValue"
            :placeholder="t('score')"
            class="border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-white p-2 rounded w-full sm:w-auto"
          />
          <button
            class="px-4 py-2 bg-green-600 dark:bg-green-500 text-white rounded hover:bg-green-700 dark:hover:bg-green-600"
            @click="submitScore"
          >
            {{ t('updateScores') }}
          </button>
        </div>
        <!-- AI Chat Box -->
        <div
          class="bg-white dark:bg-gray-700 p-4 rounded-xl shadow-md flex flex-col gap-3 h-[350px]"
        >
          <div class="flex items-center justify-between">
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white">
              {{ t('chatWithAI') }}
            </h3>
            <div class="flex items-center gap-2">
              <div
                :class="['w-2 h-2 rounded-full', aiServiceStatus ? 'bg-green-500' : 'bg-red-500']"
              />
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

            <!-- Loading indicator -->
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

            <!-- Error message -->
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
      </div>
    </section>
    <!-- Add Group Dialog -->
    <div
      v-if="showAddGroupDialog"
      class="fixed inset-0 bg-black bg-opacity-40 dark:bg-black dark:bg-opacity-60 z-50 flex items-center justify-center"
    >
      <div class="bg-white dark:bg-gray-800 p-6 rounded-xl shadow-lg w-[400px] relative">
        <h3 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">
          {{ t('addNewGroup') }}
        </h3>

        <input
          v-model="newGroupName"
          :placeholder="t('groupName')"
          class="w-full border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white p-2 rounded mb-4"
        />

        <div class="flex justify-end gap-2">
          <button
            class="px-4 py-2 bg-gray-300 dark:bg-gray-600 rounded hover:bg-gray-400 dark:hover:bg-gray-500 text-gray-800 dark:text-gray-200"
            @click="showAddGroupDialog = false"
          >
            {{ t('cancel') }}
          </button>
          <button
            class="px-4 py-2 bg-blue-600 dark:bg-blue-500 text-white rounded hover:bg-blue-700 dark:hover:bg-blue-600"
            @click="addGroup"
          >
            {{ t('add') }}
          </button>
        </div>

        <button
          class="absolute top-2 right-2 text-gray-400 dark:text-gray-500 hover:text-gray-600 dark:hover:text-gray-300"
          @click="showAddGroupDialog = false"
        >
          ✕
        </button>
      </div>
    </div>

    <section
      class="bg-white dark:bg-gray-700 p-4 rounded-xl shadow-md flex flex-col sm:flex-row items-center gap-4"
    >
      <button
        class="px-4 py-2 bg-green-600 dark:bg-green-500 text-white rounded hover:bg-green-700 dark:hover:bg-green-600"
        @click="callTestApi"
      >
        {{ t('test') }}
      </button>
      <p class="mt-2 text-gray-700 dark:text-gray-300">{{ testResponse }}</p>
    </section>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, onMounted } from 'vue'
  import { watchOnce } from '@vueuse/core'
  import { useI18n } from 'vue-i18n'
  import ScreenCard from '../components/ScreenCard.vue'
  import SlideGroupCard from '../components/SlideGroupCard.vue'
  import { useScreenStore } from '@/stores/useScreenStore'
  import { AIService } from '@/services/aiService'
  import type { Screen, SlideItem, ChatMessage, SlideGroup } from '@/interfaces/types'
  import type { SuggestionRequestDTO } from '@/interfaces/dto'

  const { t } = useI18n()

  const scoreTarget = ref('')
  const scoreValue = ref('')

  const testResponse = ref('') // display Returned Text from API

  const store = useScreenStore()

  // Submit score function
  const submitScore = () => {
    if (!scoreTarget.value || !scoreValue.value) return

    try {
      // TODO: Replace with actual API call
      console.log('Submitting score:', {
        teamId: scoreTarget.value,
        score: scoreValue.value
      })

      // Clear inputs after submission
      scoreTarget.value = ''
      scoreValue.value = ''
    } catch (error) {
      console.error('Error submitting score:', error)
    }
  }

  watchOnce(
    () => store.slideGroups.length > 0 || store.screens.length > 0,
    () => {
      // 数据已从 localStorage 加载
      if (!store.slideTimerStarted) {
        store.startSlideTimer()
        store.slideTimerStarted = true
      }
    }
  )

  // 延迟初始化空状态
  watchOnce(
    () => store.slideGroups.length === 0 && store.screens.length === 0,
    () => {
      store.initStore()
    }
  )

  const selectedContent = ref<SlideItem | undefined>(undefined)

  function selectContent(item: SlideItem) {
    selectedContent.value = item
  }

  const callTestApi = async () => {
    try {
      const res = await fetch('/api/test')
      const text = await res.text()
      testResponse.value = text
    } catch (error) {
      console.error('API error', error)
      testResponse.value = 'Error calling API'
    }
  }

  const screens = computed(() => store.screens)

  const contentList = computed(() => store.contentList)

  const slideGroups = computed(() => store.slideGroups)

  const selectedGroupId = ref(store.slideGroups[0]?.id || 'None')

  const currentGroup = computed(() => slideGroups.value.find(g => g.id === selectedGroupId.value))
  const currentGroupSlides = computed(() =>
    currentGroup.value
      ? store.contentList.filter(s => currentGroup.value!.slideIds.includes(s.id))
      : []
  )
  const onUpdateScreenGroup = (updatedScreen: Screen) => {
    store.updateScreenGroup(updatedScreen.id, updatedScreen.groupId)
  }

  const showAddScreenDialog = ref(false)
  const newScreenName = ref('')
  const newScreenUrl = ref('')
  const addNewScreen = () => {
    store.addScreen(newScreenName.value, newScreenUrl.value)
    showAddScreenDialog.value = false
    newScreenName.value = ''
    newScreenUrl.value = ''
  }

  /*
  AI Chat Integration
  */
  const userMessage = ref('')
  const chatHistory = ref<ChatMessage[]>([])
  const isAiLoading = ref(false)
  const aiError = ref('')
  const selectedAiService = ref<'openwebui' | 'openai'>('openwebui')
  const aiServiceStatus = ref(false)

  // Check AI service health on component mount
  onMounted(async () => {
    try {
      const health = await AIService.checkHealth()
      aiServiceStatus.value = health.status === 'healthy'
    } catch (error) {
      aiServiceStatus.value = false
      console.error('AI service health check failed:', error)
    }
  })

  const sendMessage = async () => {
    if (!userMessage.value.trim()) return

    const messageText = userMessage.value.trim()
    userMessage.value = ''

    // Add user message to chat history
    chatHistory.value.push({
      role: 'You',
      text: messageText
    })

    // Show loading state
    isAiLoading.value = true
    aiError.value = ''

    try {
      const request: SuggestionRequestDTO = {
        text: messageText,
        service: selectedAiService.value
      }

      const response = await AIService.getSuggestion(request)

      // Add AI response to chat history
      chatHistory.value.push({
        role: 'AI',
        text: response.suggestion
      })
    } catch (error: any) {
      aiError.value = error.message || t('aiServiceFailed')
      console.error('AI service error:', error)
    } finally {
      isAiLoading.value = false
    }
  }

  const showAddGroupDialog = ref(false)
  const newGroupName = ref('')

  // 添加新分组
  const addGroup = () => {
    store.addGroup(newGroupName.value)
    selectedGroupId.value = newGroupName.value
    newGroupName.value = ''
    showAddGroupDialog.value = false
  }

  // delete screen
  const showDeleteConfirm = ref<boolean>(false)
  const screenToDelete = ref<Screen | null>(null)

  const confirmDeleteScreen = () => {
    if (!screenToDelete.value) return
    store.screens = store.screens.filter(s => s.id !== screenToDelete.value!.id)
    showDeleteConfirm.value = false
    screenToDelete.value = null
  }

  const fileInput = ref<HTMLInputElement | null>(null)

  const triggerFileInput = () => {
    fileInput.value?.click()
  }

  const handleFileUpload = (event: Event) => {
    const files = (event.target as HTMLInputElement).files
    if (!files || files.length === 0) return

    const file = files[0]
    // replace with API later
    const reader = new FileReader()

    reader.onload = () => {
      const imageUrl = reader.result as string
      const newId = Date.now()

      const newSlide = {
        id: newId,
        name: file.name,
        url: imageUrl
      }

      contentList.value.push(newSlide)
      store.contentList.push(newSlide)

      const targetGroup = store.slideGroups.find(g => g.id === selectedGroupId.value)
      if (targetGroup && targetGroup.id !== 'None') {
        targetGroup.slideIds.push(newSlide.id)
      }
    }

    reader.readAsDataURL(file)
  }
</script>
