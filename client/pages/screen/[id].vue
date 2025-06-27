<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()
const screenId = Number(route.params.id)

// 可通过 screenId 查找对应屏幕配置
</script>

<template>
    <div class="w-full h-screen bg-black text-white flex items-center justify-center text-3xl">
        Screen ID: {{ screenId }}
    </div>
</template>
