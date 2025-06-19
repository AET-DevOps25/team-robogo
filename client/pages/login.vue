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
            :title="$t('login_failed')"
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
import { de } from '../src/proto/proto'

type LoginRequest = de.fll.core.proto.ILoginRequest
type LoginResponse = de.fll.core.proto.ILoginResponse
const { LoginRequest, LoginResponse } = de.fll.core.proto

const form = ref<LoginRequest>({
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
    const response = await signIn({
      username: form.value.username,
      password: form.value.password
    }, {
      redirect: false
    })

    if (!response?.success) {
      error.value = response?.error || 'Username or password is incorrect'
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