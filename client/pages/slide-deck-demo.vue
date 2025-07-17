<script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { useI18n } from 'vue-i18n'
  import { fetchSlideDecks } from '@/services/slideDeckService'
  import { fetchScreens } from '@/services/screenService'
  import type { SlideDeck, ScreenContent } from '@/interfaces/types'
  import SlideCard from '@/components/SlideCard.vue'
  import { uploadImage, fetchImageBlobById } from '@/services/slideImageService'

  const { t } = useI18n()
  const slideDecks = ref<SlideDeck[]>([])
  const screens = ref<ScreenContent[]>([])
  const loading = ref(false)
  const error = ref('')

  // 图片上传相关
  const fileInput = ref<HTMLInputElement | null>(null)
  const selectedFile = ref<File | null>(null)
  const previewUrl = ref<string | null>(null)
  const uploading = ref(false)
  const uploadSuccess = ref(false)
  const uploadError = ref('')

  onMounted(async () => {
    loading.value = true
    try {
      slideDecks.value = await fetchSlideDecks()
      screens.value = await fetchScreens()
    } catch (e: any) {
      error.value = e?.message || t('fetchFailed')
    } finally {
      loading.value = false
    }
  })

  function onFileChange(e: Event) {
    const files = (e.target as HTMLInputElement).files
    if (files && files[0]) {
      setFile(files[0])
    }
  }

  function onDrop(e: DragEvent) {
    if (e.dataTransfer?.files && e.dataTransfer.files[0]) {
      setFile(e.dataTransfer.files[0])
    }
  }

  function setFile(file: File) {
    selectedFile.value = file
    previewUrl.value = URL.createObjectURL(file)
    uploadSuccess.value = false
    uploadError.value = ''
  }

  function removeImage() {
    selectedFile.value = null
    previewUrl.value = null
    uploadSuccess.value = false
    uploadError.value = ''
    if (fileInput.value) fileInput.value.value = ''
  }

  async function upload() {
    if (!selectedFile.value) return
    uploading.value = true
    uploadSuccess.value = false
    uploadError.value = ''
    try {
      await uploadImage(selectedFile.value)
      uploadSuccess.value = true
      removeImage()
    } catch (e: any) {
      uploadError.value = e?.message || '上传失败'
    } finally {
      uploading.value = false
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
      <div class="upload-container">
        <label class="upload-dropzone" @dragover.prevent @drop.prevent="onDrop">
          <input
            ref="fileInput"
            class="hidden"
            type="file"
            accept="image/*"
            @change="onFileChange"
          />
          <div v-if="!previewUrl" class="upload-placeholder">
            <span class="icon">📷</span>
            <span>点击或拖拽图片到此处上传</span>
          </div>
          <div v-else class="upload-preview">
            <img :src="previewUrl" alt="预览" />
            <button class="remove-btn" @click.stop="removeImage">移除</button>
          </div>
        </label>
        <button class="upload-btn" :disabled="!selectedFile || uploading" @click="upload">
          {{ uploading ? '上传中...' : '上传图片' }}
        </button>
        <div v-if="uploadSuccess" class="upload-success">上传成功！</div>
        <div v-if="uploadError" class="upload-error">{{ uploadError }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
  .upload-container {
    margin-top: 2rem;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  .upload-dropzone {
    border: 2px dashed #a0aec0;
    border-radius: 8px;
    padding: 2rem;
    width: 320px;
    text-align: center;
    cursor: pointer;
    background: #f9fafb;
    transition: border-color 0.2s;
    margin-bottom: 1rem;
    position: relative;
  }
  .upload-dropzone:hover {
    border-color: #3182ce;
  }
  .upload-placeholder {
    color: #a0aec0;
    font-size: 1.1rem;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  .upload-placeholder .icon {
    font-size: 2.5rem;
    margin-bottom: 0.5rem;
  }
  .upload-preview {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  .upload-preview img {
    max-width: 200px;
    max-height: 120px;
    border-radius: 6px;
    margin-bottom: 0.5rem;
    box-shadow: 0 2px 8px #0001;
  }
  .remove-btn {
    background: #e53e3e;
    color: #fff;
    border: none;
    border-radius: 4px;
    padding: 0.3rem 0.8rem;
    cursor: pointer;
    font-size: 0.9rem;
  }
  .upload-btn {
    background: #3182ce;
    color: #fff;
    border: none;
    border-radius: 6px;
    padding: 0.6rem 1.5rem;
    font-size: 1rem;
    cursor: pointer;
    margin-top: 0.5rem;
    transition: background 0.2s;
  }
  .upload-btn:disabled {
    background: #a0aec0;
    cursor: not-allowed;
  }
  .upload-success {
    color: #38a169;
    margin-top: 0.5rem;
  }
  .upload-error {
    color: #e53e3e;
    margin-top: 0.5rem;
  }
</style>
