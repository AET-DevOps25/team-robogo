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
  import { ref, onMounted, watch } from 'vue'
  import { useI18n } from 'vue-i18n'
  import { SlideType, type SlideItem } from '@/interfaces/types'
  import { fetchImageBlobById } from '@/services/slideImageService'

  const props = defineProps<{ item: SlideItem }>()
  const imageUrl = ref<string | null>(null)
  const { t } = useI18n()

  function getImageMetaId(item: SlideItem): number | undefined {
    if (item.type === SlideType.IMAGE && 'imageMeta' in item && item.imageMeta) {
      return Number(item.imageMeta.id)
    }
    return undefined
  }

  async function loadImage() {
    imageUrl.value = null
    const imageId = getImageMetaId(props.item)
    if (imageId !== undefined) {
      try {
        const blob = await fetchImageBlobById(imageId)
        imageUrl.value = URL.createObjectURL(blob)
      } catch (e) {
        imageUrl.value = ''
      }
    }
  }

  onMounted(loadImage)
  watch(() => getImageMetaId(props.item), loadImage)
</script>

<style scoped>
  .image-slide-card {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
</style>
