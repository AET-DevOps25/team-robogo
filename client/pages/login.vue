<template>
  <div class="flex items-center justify-center min-h-screen bg-gray-50">
    <el-card class="w-96">
      <h2 class="text-2xl font-bold mb-6 text-center">{{ $t('login') }}</h2>
      <el-form :model="form" @submit.prevent="onLogin">
        <el-form-item :label="$t('username')">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item :label="$t('password')">
          <el-input v-model="form.password" type="password" autocomplete="current-password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="w-full" @click="onLogin" :loading="loading">{{ $t('login') }}</el-button>
        </el-form-item>
        <el-alert v-if="error" :title="error" type="error" show-icon class="mt-2" />
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '#imports'

const form = ref({ username: '', password: '' })
const loading = ref(false)
const error = ref('')
const router = useRouter()
const { signIn } = useAuth()

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