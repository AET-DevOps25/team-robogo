<template>
  <div class="min-h-screen flex items-center justify-center bg-background">
    <UCard class="w-full max-w-xl shadow-2xl rounded-3xl px-10 py-12">
      <template #header>
        <div class="flex flex-col items-center mb-6">
          <img src="/favicon.ico" class="w-16 h-16 mb-4" alt="logo" />
          <h2 class="text-4xl font-extrabold text-primary mb-2">{{ $t('loginTitle') }}</h2>
          <p class="text-gray-500 dark:text-gray-300 text-lg">{{ $t('loginSubtitle') }}</p>
        </div>
      </template>
      <UForm :state="form" @submit="onLogin" class="space-y-4">
        <div v-if="error" class="mt-2">
          <UAlert
            :title="error || $t('login_failed')"
            color="error"
            variant="soft"
            icon="i-heroicons-exclamation-circle"
            class="text-sm"
          >
            {{ error }}
          </UAlert>
        </div>
        <UFormField :label="$t('username')" name="username">
          <UInput
            v-model="form.username"
            size="xl"
            :placeholder="$t('usernamePlaceholder')"
            autocomplete="username"
            class="text-lg w-full"
            :error="error.length > 0"
          />
        </UFormField>
        <UFormField :label="$t('password')" name="password">
          <UInput
            v-model="form.password"
            type="password"
            size="xl"
            :placeholder="$t('passwordPlaceholder')"
            autocomplete="current-password"
            class="text-lg w-full"
            :error="error.length > 0"
          />
        </UFormField>
        <UButton
          type="submit"
          color="primary"
          size="xl"
          block
          :loading="loading"
          class="text-lg h-14 mt-6"
        >
          {{ $t('login') }}
        </UButton>
      </UForm>
    </UCard>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAuth } from '#imports'
import type { LoginRequestDTO, LoginResponseDTO } from '~/interfaces/dto'

const form = ref<LoginRequestDTO>({
  username: '',
  password: ''
})
const loading = ref(false)
const error = ref('')
const { signIn } = useAuth()

const onLogin = async () => {
  if (!form.value.username) {
    error.value = 'Please enter your username'
    return
  }
  
  if (!form.value.password) {
    error.value = 'Please enter your password'
    return
  }

  loading.value = true
  try {
    // 先直接获取登录响应
    const loginResponse = await $fetch<LoginResponseDTO>('/api/auth/login', {
      method: 'POST',
      body: {
        username: form.value.username,
        password: form.value.password
      }
    })

    console.log('Login API response:', loginResponse)

    if (!loginResponse.success) {
      error.value = loginResponse.error || 'Login failed, please try again'
      console.error('Login failed:', loginResponse)
      return
    }

    const authResponse = await signIn({
      username: form.value.username,
      password: form.value.password
    }, {
      redirect: false,
      token: loginResponse.token
    })


    if (authResponse?.error) {
      error.value = authResponse.error
      return
    }

    await navigateTo('/')
  } catch (err) {
    console.error('Login failed:', err)
    if (err instanceof Error) {
      error.value = err.message || 'Login failed, please try again'
    } else {
      error.value = 'Login failed, please try again'
    }
  } finally {
    loading.value = false
  }
}
</script> 