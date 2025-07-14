<script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { useI18n } from 'vue-i18n'
  import { fetchSlideDecks } from '@/services/slideDeckService'
  import type { SlideDeck } from '@/interfaces/types'

  const { t } = useI18n()
  const slideDecks = ref<SlideDeck[]>([])
  const loading = ref(false)
  const error = ref('')

  onMounted(async () => {
    loading.value = true
    try {
      slideDecks.value = await fetchSlideDecks()
    } catch (e: any) {
      error.value = e?.message || t('fetchFailed')
    } finally {
      loading.value = false
    }
  })
</script>

<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4">{{ t('slideDeckDemo') }}</h1>
    <div v-if="loading">{{ t('loading') }}</div>
    <div v-else-if="error" class="text-red-500">{{ error }}</div>
    <ul v-else>
      <li v-for="deck in slideDecks" :key="deck.id" class="mb-4 p-4 border rounded">
        <div>
          <b>{{ t('id') }}:</b>
          {{ deck.id }}
        </div>
        <div>
          <b>{{ t('name') }}:</b>
          {{ deck.name }}
        </div>
        <div>
          <b>{{ t('slides') }}:</b>
          {{ deck.slides?.length || 0 }}
        </div>
      </li>
    </ul>
  </div>
</template>
