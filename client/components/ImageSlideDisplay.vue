<template>
  <div class="w-full h-full flex items-center justify-center">
    <img v-if="imageUrl" :src="imageUrl" :alt="slide.name" class="w-full h-full object-contain" />
    <div v-else class="w-full h-full flex items-center justify-center text-white text-2xl">
      {{ t('imageLoading') }}
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted, watch, onUnmounted } from 'vue'
  import { useI18n } from 'vue-i18n'
  import { SlideType, type SlideItem } from '@/interfaces/types'
  import { fetchImageBlobById } from '@/services/slideImageService'

  const props = defineProps<{ slide: SlideItem }>()
  const imageUrl = ref<string | null>(null)
  const currentImageId = ref<number | null>(null)
  const { t } = useI18n()

  function getImageMetaId(slide: SlideItem): number | undefined {
    if (slide.type === SlideType.IMAGE && 'imageMeta' in slide && slide.imageMeta) {
      return Number(slide.imageMeta.id)
    }
    return undefined
  }

  async function loadImage() {
    const imageId = getImageMetaId(props.slide)

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
      try {
        const blob = await fetchImageBlobById(imageId)
        imageUrl.value = URL.createObjectURL(blob)
        currentImageId.value = imageId
      } catch (e) {
        imageUrl.value = ''
        currentImageId.value = null
      }
    } else {
      currentImageId.value = null
    }
  }

  onMounted(loadImage)

  // 只在图片ID真正变化时才重新加载
  watch(
    () => getImageMetaId(props.slide),
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