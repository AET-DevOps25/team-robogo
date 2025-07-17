<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900">
    <!-- 使用AppHeader -->
    <AppHeader />

    <div class="p-6 space-y-6">
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
          <SlideDeckCard v-if="selectedDeckId" :deck-id="selectedDeckId" />
        </div>

        <!-- Right: Screens Management -->
        <div class="flex flex-col gap-4">
          <!-- Screens Section -->
          <div class="bg-white dark:bg-gray-700 rounded-lg p-4 shadow">
            <h3 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">
              {{ t('screensMonitor') }}
            </h3>

            <!-- Screens List -->
            <div class="space-y-4 max-h-96 overflow-y-auto">
              <ScreenCard
                v-for="screen in screens"
                :key="screen.id"
                :screen="screen"
                :slide-decks="slideDecks"
                @deck-assigned="handleDeckAssigned"
                @request-delete="handleRequestDelete"
              />
            </div>

            <!-- Add Screen Button -->
            <button
              class="mt-4 w-full px-4 py-2 bg-blue-600 dark:bg-blue-500 text-white rounded hover:bg-blue-700 dark:hover:bg-blue-600 transition"
              @click="showAddScreenDialog = true"
            >
              {{ t('addNewScreen') }}
            </button>
          </div>

          <!-- AI Chat Box -->
          <AIChatBox />
        </div>
      </section>

      <!-- Add Screen Dialog -->
      <div
        v-if="showAddScreenDialog"
        class="fixed inset-0 bg-black bg-opacity-40 dark:bg-black dark:bg-opacity-60 z-50 flex items-center justify-center"
      >
        <div class="bg-white dark:bg-gray-800 p-6 rounded-xl shadow-lg w-[400px] relative">
          <h3 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">
            {{ t('addNewScreen') }}
          </h3>

          <input
            v-model="newScreenName"
            :placeholder="t('screenName')"
            class="w-full border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white p-2 rounded mb-4"
          />

          <div class="flex justify-end gap-2">
            <button
              class="px-4 py-2 bg-gray-300 dark:bg-gray-600 rounded hover:bg-gray-400 dark:hover:bg-gray-500 text-gray-800 dark:text-gray-200"
              @click="showAddScreenDialog = false"
            >
              {{ t('cancel') }}
            </button>
            <button
              class="px-4 py-2 bg-blue-600 dark:bg-blue-500 text-white rounded hover:bg-blue-700 dark:hover:bg-blue-600"
              @click="addScreen"
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

      <!-- Add Group Dialog -->
      <div
        v-if="showaddDeckDialog"
        class="fixed inset-0 bg-black bg-opacity-40 dark:bg-black dark:bg-opacity-60 z-50 flex items-center justify-center"
      >
        <div class="bg-white dark:bg-gray-800 p-6 rounded-xl shadow-lg w-[400px] relative">
          <h3 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">
            {{ t('addNewGroup') }}
          </h3>

          <input
            v-model="newDeckName"
            :placeholder="t('groupName')"
            class="w-full border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white p-2 rounded mb-4"
          />
          <USelectMenu
            v-model="selectedCompetitionDeck"
            :items="TEAMS"
            item-value="id"
            item-text="name"
            class="w-full border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white p-2 rounded mb-4"
          />

          <div class="flex justify-end gap-2">
            <button
              class="px-4 py-2 bg-gray-300 dark:bg-gray-600 rounded hover:bg-gray-400 dark:hover:bg-gray-500 text-gray-800 dark:text-gray-200"
              @click="showaddDeckDialog = false"
            >
              {{ t('cancel') }}
            </button>
            <button
              class="px-4 py-2 bg-blue-600 dark:bg-blue-500 text-white rounded hover:bg-blue-700 dark:hover:bg-blue-600"
              @click="addDeck"
            >
              {{ t('add') }}
            </button>
          </div>

          <button
            class="absolute top-2 right-2 text-gray-400 dark:text-gray-500 hover:text-gray-600 dark:hover:text-gray-300"
            @click="showaddDeckDialog = false"
          >
            ✕
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { useI18n } from 'vue-i18n'
  import AppHeader from '@/components/AppHeader.vue'
  import SlideDeckCard from '@/components/SlideDeckCard.vue'
  import ScreenCard from '@/components/ScreenCard.vue'
  import AIChatBox from '@/components/AIChatBox.vue'
  import type { SlideDeck, ScreenContent } from '@/interfaces/types'
  import { fetchSlideDecks, createSlideDeck } from '@/services/slideDeckService'
  import { fetchScreens, createScreen, deleteScreen } from '@/services/screenService'
  import { COMPETITIONS, TEAMS } from '@/data/competitions'

  const { t } = useI18n()

  const selectedDeckId = ref<number>()
  const screens = ref<ScreenContent[]>([])
  const slideDecks = ref<SlideDeck[]>([])

  onMounted(async () => {
    await loadData()
  })

  async function loadData() {
    try {
      const [backendDecks, backendScreens] = await Promise.all([fetchSlideDecks(), fetchScreens()])

      slideDecks.value = backendDecks
      screens.value = backendScreens

      if (backendDecks.length > 0) {
        selectedDeckId.value = backendDecks[0].id
      }
    } catch (error) {
      console.error('Failed to load data:', error)
    }
  }

  // 处理 screen 分配 slideDeck 后的事件
  async function handleDeckAssigned() {
    // 重新加载 screens 数据
    try {
      screens.value = await fetchScreens()
    } catch (error) {
      console.error('Failed to refresh screens:', error)
    }
  }

  // 处理删除 screen 请求
  function handleRequestDelete(screen: ScreenContent) {
    screenToDelete.value = screen
    showDeleteConfirm.value = true
  }

  // 确认删除 screen
  async function confirmDeleteScreen() {
    if (!screenToDelete.value) return

    try {
      await deleteScreen(screenToDelete.value.id)
      screens.value = screens.value.filter(s => s.id !== screenToDelete.value!.id)
    } catch (error) {
      console.error('Failed to delete screen:', error)
    } finally {
      showDeleteConfirm.value = false
      screenToDelete.value = null
    }
  }

  // 添加新 screen
  const showAddScreenDialog = ref(false)
  const newScreenName = ref('')

  async function addScreen() {
    if (!newScreenName.value.trim()) return

    try {
      const newScreen: Partial<ScreenContent> = {
        name: newScreenName.value,
        status: 'OFFLINE',
        slideDeck: null
      }

      const createdScreen = await createScreen(newScreen as ScreenContent)
      screens.value.push(createdScreen)
      newScreenName.value = ''
      showAddScreenDialog.value = false
    } catch (error) {
      console.error('Failed to create screen:', error)
    }
  }

  const selectedCompetitionDeck = ref(COMPETITIONS[0])

  // 添加新分组
  const addDeck = async () => {
    const newDeck: SlideDeck = {
      id: 0,
      name: newDeckName.value,
      competitionId: selectedCompetitionDeck.value.id,
      slides: null,
      transitionTime: 5,
      version: 0
    }
    const response = await createSlideDeck(newDeck)
    slideDecks.value.push(response)
    selectedDeckId.value = response.id
    newDeckName.value = ''
    showaddDeckDialog.value = false
  }

  // delete screen
  const showDeleteConfirm = ref<boolean>(false)
  const screenToDelete = ref<ScreenContent | null>(null)

  const showaddDeckDialog = ref(false)
  const newDeckName = ref('')
</script>
