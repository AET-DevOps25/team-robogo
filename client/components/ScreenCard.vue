<!-- File: src/components/ScreenCard.vue -->
<template>
    <div class="w-[300px] rounded overflow-hidden shadow-lg bg-white">
        <img v-if="screen.currentContent !== 'BLACK_SCREEN'" class="w-[90%] h-40 object-cover mx-auto rounded" img
            :src="screen.thumbnailUrl || placeholder" alt="screen preview" />
        <div v-else class="w-[90%] h-40 object-cover mx-auto rounded bg-black ">
            No Content
        </div>
        <div class="px-6 py-4">
            <div class="font-bold text-xl mb-2">{{ screen.name }}</div>
            <span
                class="inline-block bg-red-400 rounded-full px-3 py-1 text-sm font-semibold text-gray-700 mr-2 mb-2">{{
                    screen.status }}</span>
            <p class="text-gray-700 text-base">
                Current Group: {{ screen.currentContent || 'none' }}
            </p>
        </div>
        <div class="px-6 pt-4 pb-2">
            <USelectMenu v-model="screen.groupId" :items="groupOptions" class="w-full"
                @update:modelValue="emit('updateGroup', screen)" />
        </div>
    </div>
</template>

<script setup lang="ts">
const props = defineProps<{
    screen: {
        id: number,
        name: string,
        status: string,
        groupId: string,
        currentContent: string,
        thumbnailUrl: string
    },
    slideGroups: { id: string, content: any[] }[]
}>()

const emit = defineEmits(['updateGroup'])
const placeholder = 'https://via.placeholder.com/300x200?text=Preview';
const groupOptions = ['None', ...props.slideGroups.map(g => g.id)]
</script>
