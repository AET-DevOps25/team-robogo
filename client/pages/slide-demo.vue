<template>
  <div class="slide-demo">
    <client-only>
      <swiper
        :slides-per-view="1"
        :pagination="{ clickable: true }"
        :navigation="true"
        class="mySwiper"
      >
        <swiper-slide v-for="img in images" :key="img.id">
          <img
            v-if="imageSrcs[img.id]"
            :src="imageSrcs[img.id]"
            :alt="img.name"
            style="width: 100%; height: auto; object-fit: contain"
          />
          <div class="caption">{{ img.name }}</div>
        </swiper-slide>
      </swiper>
    </client-only>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { Swiper, SwiperSlide } from 'swiper/vue'
  import 'swiper/css'
  import 'swiper/css/pagination'
  import 'swiper/css/navigation'
  import type { SlideImageMetaDTO } from '@/interfaces/types'
  import { useAuthFetch } from '@/composables/useAuthFetch'

  const images = ref<SlideImageMetaDTO[]>([])
  const imageSrcs = ref<{ [id: number]: string }>({})
  const { authFetch } = useAuthFetch()

  onMounted(async () => {
    const res = await authFetch<SlideImageMetaDTO[]>('/api/proxy/slide-images')
    images.value = res
    await Promise.all(
      images.value.map(async img => {
        const blob = await authFetch(`/api/proxy/slide-images/${img.id}`)
        imageSrcs.value[img.id] = URL.createObjectURL(blob)
      })
    )
  })
</script>

<style scoped>
  .mySwiper {
    width: 600px;
    max-width: 100vw;
    margin: 0 auto;
  }

  .caption {
    text-align: center;
    margin-top: 8px;
    color: #666;
  }
</style>
