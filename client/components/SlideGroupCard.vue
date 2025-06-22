<template>
    <div class="w-full rounded-xl shadow-lg p-4 bg-white space-y-3">
        <div class="flex gap-2 justify-between items-center">
            <h3 class="text-lg font-semibold">{{ title }}</h3>
            <div class="flex items-center gap-2 mt-2">
                <span class="text-lg font-semibold">Speed</span>
                <SpeedControl v-model="speed" />
            </div>

        </div>

        <div class="flex flex-wrap gap-4">
            <draggable v-model="localContent" group="slides" item-key="id" class="flex flex-wrap gap-4"
                @end="$emit('update:content', localContent)">
                <template #item="{ element }">
                    <SlideCard :item="element" :selected="selectedContent?.id === element.id"
                        @click="$emit('select', element)" />
                </template>
            </draggable>

            <!-- Add button -->

            <div class="w-[300px] h-[200px] rounded overflow-hidden shadow-lg bg-gray-100 flex items-center justify-center text-5xl text-gray-400 hover:bg-gray-200 transition"
                @click="showDialog = true">
                +
            </div>
        </div>

        <!-- Dialog -->
        <div v-if="showDialog" class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <div class="bg-white p-6 rounded-lg w-[600px] max-h-[80vh] overflow-y-auto shadow-lg relative">
                <h4 class="text-lg font-bold mb-4">Select Slides</h4>

                <!-- Slide Selection -->
                <div class="flex flex-wrap gap-4 mb-6">
                    <SlideCard v-for="item in props.allSlides" :key="item.id + '-select'" :item="item"
                        :selected="item.id === selectedToAdd?.id" @click="selectedToAdd = item" />
                </div>

                <!-- Actions -->
                <div class="flex justify-end gap-4">
                    <button class="px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400" @click="cancelAdd">
                        取消
                    </button>
                    <button class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                        :disabled="!selectedToAdd" @click="confirmAdd">
                        确认添加
                    </button>
                </div>

                <button class="absolute top-2 right-2 text-gray-500 hover:text-gray-800" @click="cancelAdd">✕</button>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import draggable from 'vuedraggable'
import SlideCard from './SlideCard.vue'
import SpeedControl from './SpeedControl.vue'
const speed = ref(5)
const showDialog = ref(false)
interface SlideItem {
    id: number | string
    name: string
    url: string
}
const content = defineModel<SlideItem[]>('content', { required: true })
const props = defineProps<{
    title: string,
    selectedContent?: SlideItem,
    allSlides: SlideItem[]
}>()

defineEmits<{
    (e: 'select', item: SlideItem): void
    // (e: 'update:content', items: SlideItem[]): void
    (e: 'add', item: SlideItem): void
}>()
const localContent = ref<SlideItem[]>([...content.value])
watch(() => content.value, val => {
    localContent.value = [...val]
})
const handleAdd = (item: SlideItem) => {
    if (!content.value.some(i => i.id === item.id)) {
        content.value = [...content.value, item]
    }
    showDialog.value = false
}
const selectedToAdd = ref<SlideItem | null>(null)

const confirmAdd = () => {
    if (selectedToAdd.value) {
        content.value = [...content.value, selectedToAdd.value]
        localContent.value = [...content.value]
    }
    showDialog.value = false
    selectedToAdd.value = null
}

const cancelAdd = () => {
    showDialog.value = false
    selectedToAdd.value = null
}

</script>
