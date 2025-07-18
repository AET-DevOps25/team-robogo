<template>
  <div class="image-slide-card w-full">
    <div
      v-if="isLoading"
      class="w-full h-48 flex items-center justify-center bg-gray-100 dark:bg-gray-800 rounded-lg mb-3"
    >
      <div class="loading-spinner" />
      <span class="ml-2 text-gray-500">{{ t('loading') }}</span>
    </div>
    <img
      v-else-if="imageUrl"
      :src="imageUrl"
      :alt="item.name"
      class="w-full h-48 object-cover rounded-lg mb-3 shadow-md"
      @load="handleImageLoad"
      @error="handleImageError"
    />
    <div
      v-else
      class="w-full h-48 flex items-center justify-center bg-gray-100 dark:bg-gray-800 rounded-lg mb-3"
    >
      <div class="text-gray-400 text-center">
        <div class="text-2xl mb-2">📷</div>
        <div>{{ t('imageLoadError') }}</div>
      </div>
    </div>
    <div class="font-semibold text-gray-900 dark:text-white text-center">{{ item.name }}</div>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted, watch, onUnmounted } from 'vue'
  import { useI18n } from 'vue-i18n'
  import { SlideType, type SlideItem } from '@/interfaces/types'
  import { fetchImageBlobById } from '@/services/slideImageService'

  const props = defineProps<{ item: SlideItem }>()
  const imageUrl = ref<string | null>(null)
  const currentImageId = ref<number | null>(null)
  const isLoading = ref(false)
  const { t } = useI18n()

  function getImageMetaId(item: SlideItem): number | undefined {
    if (item.type === SlideType.IMAGE && 'imageMeta' in item && item.imageMeta) {
      return Number(item.imageMeta.id)
    }
    return undefined
  }

  async function loadImage() {
    const imageId = getImageMetaId(props.item)

    // 如果图片ID没有变化，不需要重新加载
    if (imageId === currentImageId.value && imageUrl.value) {
      return
    }

    // 释放之前的URL对象
    if (imageUrl.value) {
      URL.revokeObjectURL(imageUrl.value)
      imageUrl.value = null
    }

    if (imageId !== undefined) {
      isLoading.value = true
      try {
        const blob = await fetchImageBlobById(imageId)
        const url = URL.createObjectURL(blob)
        imageUrl.value = url
        currentImageId.value = imageId

        // 预加载图片以确保能正确触发load事件
        const img = new Image()
        img.onload = () => {
          console.log('Image preloaded successfully:', props.item.name)
          isLoading.value = false
        }
        img.onerror = () => {
          console.error('Image preload failed:', props.item.name)
          isLoading.value = false
          imageUrl.value = null
        }
        img.src = url
      } catch (e) {
        console.error('Failed to load image:', e)
        imageUrl.value = null
        currentImageId.value = null
        isLoading.value = false
      }
    } else {
      currentImageId.value = null
      isLoading.value = false
    }
  }

  function handleImageError() {
    console.error('Image load error for:', props.item.name)
    isLoading.value = false
    imageUrl.value = null
  }

  function handleImageLoad() {
    console.log('Image loaded successfully:', props.item.name)
    isLoading.value = false
  }

  onMounted(loadImage)

  // 只在图片ID真正变化时才重新加载
  watch(
    () => getImageMetaId(props.item),
    (newId, oldId) => {
      if (newId !== oldId) {
        loadImage()
      }
    }
  )

  // 组件卸载时释放URL对象
  onUnmounted(() => {
    if (imageUrl.value) {
      URL.revokeObjectURL(imageUrl.value)
    }
  })
</script>

<style scoped>
  .image-slide-card {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 100%;
  }

  .loading-spinner {
    width: 24px;
    height: 24px;
    border: 3px solid #e5e7eb;
    border-top: 3px solid #3b82f6;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }

  @keyframes spin {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }
</style>
