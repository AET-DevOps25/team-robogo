<script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { useI18n } from 'vue-i18n'
  import { fetchSlideDecks } from '@/services/slideDeckService'
  import { fetchScreens } from '@/services/screenService'
  import type { SlideDeck, ScreenContent } from '@/interfaces/types'
  import SlideCard from '@/components/SlideCard.vue'
  import { uploadImage } from '@/services/slideImageService'
  import type { ImageSlideMeta } from '@/interfaces/types'

  const { t } = useI18n()
  const slideDecks = ref<SlideDeck[]>([])
  const screens = ref<ScreenContent[]>([])
  const loading = ref(false)
  const error = ref('')

  const previewUrl = ref<string | null>(null)
  const uploadResult = ref<ImageSlideMeta | null>(null)
  const uploadError = ref('')

  onMounted(async () => {
    loading.value = true
    try {
      slideDecks.value = await fetchSlideDecks()
      screens.value = await fetchScreens()
      console.log('fetchScreens result:', screens.value)
    } catch (e: any) {
      error.value = e?.message || t('fetchFailed')
    } finally {
      loading.value = false
    }
  })

  async function onImageChange(e: Event) {
    const file = (e.target as HTMLInputElement).files?.[0]
    if (file) {
      previewUrl.value = URL.createObjectURL(file)
      try {
        uploadError.value = ''
        uploadResult.value = await uploadImage(file)
      } catch (err: any) {
        uploadError.value = err?.message || '上传失败'
      }
    }
  }
</script>

<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4">{{ t('slideDeckDemo') }}</h1>
    <div v-if="loading">{{ t('loading') }}</div>
    <div v-else-if="error" class="text-red-500">{{ error }}</div>
    <ul v-else>
      <li v-for="deck in slideDecks" :key="deck.id" class="mb-8 p-4 border rounded">
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
        <div
          v-if="deck.slides && deck.slides.length"
          class="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4"
        >
          <SlideCard v-for="slide in deck.slides" :key="slide.id" :item="slide" />
        </div>
      </li>
    </ul>
    <div v-if="screens.length" class="mt-10">
      <h2 class="text-xl font-bold mb-2">{{ t('screensMonitor') }}</h2>
      <ul>
        <li v-for="screen in screens" :key="screen.id" class="mb-2">
          <b>{{ t('id') }}:</b>
          {{ screen.id }},
          <b>{{ t('name') }}:</b>
          {{ screen.name }},
          <b>{{ t('status') }}:</b>
          {{ screen.status }},
          <b>{{ t('slideDeck') }}:</b>
          {{ screen.slideDeck?.name || '-' }}
          <details v-if="screen.slideDeck">
            <summary class="cursor-pointer text-blue-600 underline mt-1">
              Show SlideDeck Details
            </summary>
            <div class="ml-4 mt-2 p-2 border rounded bg-gray-50 dark:bg-gray-800">
              <div>
                <b>ID:</b>
                {{ screen.slideDeck.id }}
              </div>
              <div>
                <b>Name:</b>
                {{ screen.slideDeck.name }}
              </div>
              <div>
                <b>Version:</b>
                {{ screen.slideDeck.version }}
              </div>
              <div>
                <b>Transition Time:</b>
                {{ screen.slideDeck.transitionTime }}
              </div>
              <div>
                <b>Competition ID:</b>
                {{ screen.slideDeck.competitionId }}
              </div>
              <div>
                <b>Slides:</b>
                {{ screen.slideDeck.slides?.length || 0 }}
              </div>
              <ul v-if="screen.slideDeck.slides && screen.slideDeck.slides.length">
                <li v-for="slide in screen.slideDeck.slides" :key="slide.id">
                  <b>{{ t('id') }}:</b>
                  {{ slide.id }},
                  <b>{{ t('name') }}:</b>
                  {{ slide.name }},
                  <b>{{ t('type') }}:</b>
                  {{ slide.type }},
                  <b>{{ t('index') }}:</b>
                  {{ slide.index }}
                </li>
              </ul>
            </div>
          </details>
        </li>
      </ul>
    </div>
    <div class="mt-16 border-t pt-8">
      <h2 class="text-lg font-bold mb-2">图片上传测试</h2>
      <input type="file" class="mb-4" accept="image/*" @change="onImageChange" />
      <img v-if="previewUrl" :src="previewUrl" alt="Preview" class="max-w-xs mt-2 rounded shadow" />
      <div v-if="uploadResult" class="text-green-600 mt-2">
        {{ t('uploadSuccess') }}: {{ uploadResult.name }}
      </div>
      <div v-if="uploadError" class="text-red-600 mt-2">
        {{ uploadError }}
      </div>
    </div>
  </div>
</template>
