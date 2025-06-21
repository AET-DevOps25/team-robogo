<!-- File: src/components/ScreenCard.vue -->
<template>
    <div class="w-[300px] rounded overflow-hidden shadow-lg bg-white">
        <img class="w-[90%] h-40 object-cover mx-auto rounded" img :src="screen.thumbnailUrl || placeholder"
            alt="screen preview">
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
            <button class="mt-2 px-3 py-1 text-sm bg-blue-500 text-white rounded hover:bg-blue-600"
                @click="$emit('updateScreen', screen)">
                Update Screen
            </button>
        </div>
    </div>
</template>

<script setup>
defineProps({
    screen: {
        type: Object,
        required: true,
    },
});

const placeholder = 'https://via.placeholder.com/300x200?text=Preview';
</script>
