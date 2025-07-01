<!-- File: src/pages/screen/[id].vue -->
<script setup lang="ts">
import { useRoute } from 'vue-router'
import { computed } from 'vue'
import { useScreenStore } from '@/stores/useScreenStore'
import { onMounted } from 'vue'
import { toRef } from 'vue'
import { watch } from 'vue'

const route = useRoute()
const screenId = route.params.id as string
const store = useScreenStore()

const screen = computed(() => store.screens.find(s => s.id === screenId))
const group = computed(() => store.slideGroups.find(g => g.id === screen.value?.groupId))

const currentIndex = computed(() =>
    group.value?._currentSlideIndex ?? 0
)

const currentSlide = computed(() => {
    const grp = group.value
    if (!grp || !grp.slideIds.length) return null
    const id = grp.slideIds[currentIndex.value]
    return store.contentList.find(s => s.id === id) ?? null
})

onMounted(() => {
    store.startSlideTimer()   // 不再关心 slideTimerStarted
})


</script>

<template>
    <div class="w-full h-screen bg-black flex items-center justify-center">
        <div v-if="currentSlide">
            <img :src="currentSlide.url" class="max-w-full max-h-full object-contain" :alt="currentSlide.name" />
        </div>
        <div v-else class="text-white text-3xl">No Slide</div>
    </div>
</template>
