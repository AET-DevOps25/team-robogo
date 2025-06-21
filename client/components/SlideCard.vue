<!-- File: src/components/ContentCard.vue -->
<template>
    <div class="w-[300px] rounded overflow-hidden shadow-lg bg-white" :class="{
        'ring-4 ring-blue-500': selected,
        'bg-white hover:bg-gray-100': !selected
    }" @click="$emit('click')">
        <img class="w-[90%] h-40 object-cover mx-auto rounded" img :src="item.url" alt="preview">
        <div class="px-6 py-4">

            <p class="text-gray-700 text-base">
                {{ item.name }}
            </p>
        </div>
    </div>

</template>

<script setup lang="ts">
defineProps<{
    item: any,
    selected?: boolean
}>()
defineEmits(['click'])
</script>