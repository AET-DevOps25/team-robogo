<template>
  <div class="p-6 space-y-6 bg-gray-50 dark:bg-gray-900 min-h-screen">
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

      <!-- Right: Groups -->
      <div class="flex flex-col gap-4">
        <!-- AI Chat Box -->
        <AIChatBox />
      </div>
    </section>
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
</template>

<script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { useI18n } from 'vue-i18n'
  import SlideDeckCard from '@/components/SlideDeckCard.vue'
  import AIChatBox from '@/components/AIChatBox.vue'
  import type { SlideDeck } from '@/interfaces/types'
  import { fetchSlideDecks, createSlideDeck } from '@/services/slideDeckService'
  import { COMPETITIONS, TEAMS } from '@/data/competitions'

  const { t } = useI18n()

  const selectedDeckId = ref<number>()
  onMounted(async () => {
    const backendDecks = await fetchSlideDecks()
    selectedDeckId.value = backendDecks[0].id
  })

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
    selectedDeckId.value = response.id
    newDeckName.value = ''
    showaddDeckDialog.value = false
  }

  // delete screen
  const showDeleteConfirm = ref<boolean>(false)
  const screenToDelete = ref<any | null>(null) // Changed to any as ScreenContent is removed

  const confirmDeleteScreen = () => {
    if (!screenToDelete.value) return
    // store.screens = store.screens.filter((s: any) => s.id !== screenToDelete.value!.id) // This line is removed
    showDeleteConfirm.value = false
    screenToDelete.value = null
  }

  const showaddDeckDialog = ref(false)
  const newDeckName = ref('')
</script>
