<template>
  <div class="min-h-screen flex flex-col bg-background">
    <AppHeader :show-logout="false" />
    <div class="flex-1 flex items-center justify-center px-4">
      <UCard class="w-full max-w-xl shadow-2xl rounded-3xl px-10 py-12">
        <template #header>
          <div class="flex flex-col items-center mb-6">
            <img src="/favicon.svg" class="w-16 h-16 mb-4" alt="RoboGo Logo" />
            <h2 class="text-4xl font-extrabold text-primary mb-2">
              {{ $t('signupTitle') }}
            </h2>
            <p class="text-gray-500 dark:text-gray-300 text-lg">
              {{ $t('signupSubtitle') }}
            </p>
          </div>
        </template>
        <UForm :state="form" class="space-y-4" @submit="onSignup">
          <div v-if="error" class="mt-2">
            <UAlert
              :title="error"
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
          <UFormField :label="$t('email')" name="email">
            <UInput
              v-model="form.email"
              type="email"
              size="xl"
              :placeholder="$t('emailPlaceholder')"
              autocomplete="email"
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
              autocomplete="new-password"
              class="text-lg w-full"
              :error="error.length > 0"
            />
          </UFormField>
          <UFormField :label="$t('confirmPassword')" name="confirmPassword">
            <UInput
              v-model="form.confirmPassword"
              type="password"
              size="xl"
              :placeholder="$t('confirmPasswordPlaceholder')"
              autocomplete="new-password"
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
            {{ $t('signup') }}
          </UButton>
          <div class="text-center mt-6">
            <p class="text-gray-600 dark:text-gray-400">
              {{ $t('hasAccount') }}
              <NuxtLink to="/login" class="text-primary hover:text-primary-dark font-medium">
                {{ $t('login') }}
              </NuxtLink>
            </p>
          </div>
        </UForm>
      </UCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAuth } from '#imports'
import type { SignupRequestDTO, LoginResponseDTO } from '~/interfaces/dto'

definePageMeta({
  auth: false
})

const form = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})
const error = ref('')
const { signIn } = useAuth()

const validateEmail = (email: string) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

const onSignup = async () => {
  if (!form.value.username) {
    error.value = 'Please enter your username'
    return
  }

  if (!form.value.email) {
    error.value = 'Please enter your email'
    return
  }

  if (!validateEmail(form.value.email)) {
    error.value = 'Please enter a valid email address'
    return
  }

  if (!form.value.password) {
    error.value = 'Please enter your password'
    return
  }

  if (form.value.password !== form.value.confirmPassword) {
    error.value = 'Passwords do not match'
    return
  }

  try {
    const signupResponse = await $fetch<{ success: boolean; error?: string }>('/api/auth/signup', {
      method: 'POST',
      body: {
        username: form.value.username,
        email: form.value.email,
        password: form.value.password
      }
    })

    if (!signupResponse.success) {
      error.value = signupResponse.error || 'Signup failed, please try again'
      return
    }

    const loginResponse = await $fetch<LoginResponseDTO>('/api/auth/login', {
      method: 'POST',
      body: {
        username: form.value.username,
        password: form.value.password
      }
    })

    if (!loginResponse.success) {
      error.value = loginResponse.error || 'Auto login failed after signup'
      return
    }

    const authResponse = await signIn(
      {
        username: form.value.username,
        password: form.value.password
      },
      {
        redirect: false,
        token: loginResponse.token
      }
    )

    if (authResponse?.error) {
      error.value = authResponse.error
      return
    }

    await navigateTo('/dashboard')
  } catch (err) {
    if (err instanceof Error) {
      error.value = err.message || 'Signup failed, please try again'
    } else {
      error.value = 'Signup failed, please try again'
    }
  }
}
</script>
