<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-100 via-white to-blue-200">
    <ClientOnly>
      <el-card class="w-full max-w-md shadow-2xl rounded-xl">
        <h2 class="text-4xl font-extrabold mb-8 text-center text-blue-700 tracking-wide drop-shadow">{{ $t('login') }}</h2>
        <el-form :model="form" @submit.prevent="onLogin">
          <el-form-item :label="$t('username')">
            <el-input v-model="form.username" autocomplete="username" size="large" />
          </el-form-item>
          <el-form-item :label="$t('password')">
            <el-input v-model="form.password" type="password" autocomplete="current-password" size="large" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="w-full rounded-full text-lg font-semibold transition-transform hover:scale-105" @click="onLogin" :loading="loading">
              {{ $t('login') }}
            </el-button>
          </el-form-item>
          <el-alert v-if="error" :title="error" type="error" show-icon class="mt-2" />
        </el-form>
      </el-card>
    </ClientOnly>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '#imports'
import { onMounted } from 'vue'

const form = ref({ username: '', password: '' })
const loading = ref(false)
const error = ref('')
const router = useRouter()
const { signIn } = useAuth()

onMounted(() => {
  // 这里可以放只在客户端执行的逻辑（如聚焦输入框等）
})

const onLogin = async () => {
  loading.value = true
  error.value = ''
  const result = await signIn({ username: form.value.username, password: form.value.password })
  if (result?.error) {
    error.value = result.error
  } else {
    router.push('/')
  }
  loading.value = false
}
</script> 