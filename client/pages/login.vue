<template>
  <div class="min-h-screen flex flex-col bg-background">
    <AppHeader :show-logout="false" />
    <div class="flex-1 flex items-center justify-center">
      <UCard class="w-full max-w-xl shadow-2xl rounded-3xl px-10 py-12">
        <template #header>
          <div class="flex flex-col items-center mb-6">
            <img src="/favicon.svg" class="w-16 h-16 mb-4" alt="RoboGo Logo" />
            <h2 class="text-4xl font-extrabold text-primary mb-2">
              {{ $t('loginTitle') }}
            </h2>
            <p class="text-gray-500 dark:text-gray-300 text-lg">
              {{ $t('loginSubtitle') }}
            </p>
          </div>
        </template>
        <UForm :state="form" class="space-y-4" @submit="onLogin">
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
            loading-auto
            loading-icon="i-lucide-loader-2"
            class="text-lg h-14 mt-6"
          >
            {{ $t('login') }}
          </UButton>
          <div class="text-center mt-6">
            <p class="text-gray-600 dark:text-gray-400">
              {{ $t('noAccount') }}
              <NuxtLink to="/signup" class="text-primary hover:text-primary-dark font-medium">
                {{ $t('signup') }}
              </NuxtLink>
            </p>
          </div>
        </UForm>
      </UCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuth } from '#imports'
import type { LoginRequestDTO } from '~/interfaces/dto'

definePageMeta({
  auth: false
})

const form = ref<LoginRequestDTO>({
  username: '',
  password: ''
})
const error = ref('')
const { signIn, status } = useAuth()

onMounted(async () => {
  if (status.value === 'authenticated') {
    await navigateTo('/dashboard')
  }
})

const onLogin = async () => {
  error.value = ''
  if (!form.value.username) {
    error.value = 'Please enter your username'
    return
  }

  if (!form.value.password) {
    error.value = 'Please enter your password'
    return
  }

  try {
    const response = await signIn(
      {
        username: form.value.username,
        password: form.value.password
      },
      {
        redirect: false
      }
    )

    if (response?.error) {
      error.value = response.error || 'Login failed, please try again.'
      return
    }

    await navigateTo('/dashboard')
  } catch (err: any) {
    error.value = err.data?.message || err.message || 'An unexpected error occurred.'
  }
}
</script>
