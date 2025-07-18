<template>
  <div class="image-slide-card">
    <img
      v-if="imageUrl"
      :src="imageUrl"
      :alt="item.name"
      class="w-full h-40 object-cover rounded mb-2"
    />
    <div v-else class="w-full h-40 flex items-center justify-center text-gray-400">
      {{ t('loading') }}
    </div>
    <div class="font-semibold text-gray-900 dark:text-white">{{ item.name }}</div>
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
  }
</style>
