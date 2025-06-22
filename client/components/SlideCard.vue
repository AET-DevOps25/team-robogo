<!-- File: src/components/ContentCard.vue -->
<template>
    <div class="w-[300px] rounded overflow-hidden shadow-lg bg-white">
        <img class="w-[90%] h-40 object-cover mx-auto rounded" img :src="item.url" alt="preview">
        <div class="px-6 py-4">

            <p class="text-gray-700 text-base">
                {{ item.name }}
            </p>
        </div>
    </div>

</template>

<script setup>
defineProps({
    item: {
        type: Object,
        required: true,
    },
    selected: {
        type: Boolean,
        default: false,
    },
});
</script>