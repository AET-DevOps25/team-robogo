<!-- File: src/pages/screen/[id].vue -->
<script setup lang="ts">
import { useRoute } from 'vue-router'
import { computed } from 'vue'
import { useStartTimeStore } from '@/stores/useStartTimeStore'
import { useSlides } from '@/composables/useSlides'
import { fetchAllImageMetas } from '~/services/slideImageService'
import { fetchScreenById } from '~/services/screenService'
import { ScreenContent } from '@interfaces/types'
import SlideCard from '@components/SlideCard'
const AllStartTime = useStartTimeStore()
const screenId = useRoute().params.id as number
const screen = await fetchScreenById(screenId)
const currentDeck = screen.slideDeck
const startTime = getStartTime(screen.slideDeck.id)
//
const currentSlide = computed(() => {




})
</script>

<template>
  <SlideCard :item="slide" class="w-full" />
</template>
