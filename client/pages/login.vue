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
      <UForm :state="form" @submit="onLogin" class="space-y-6">
        <UFormField :label="$t('username')" name="username">
          <UInput
            v-model="form.username"
            size="xl"
            :placeholder="$t('usernamePlaceholder')"
            autocomplete="username"
            class="text-lg w-full"
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
          />
        </UFormField>
        <UButton
          type="submit"
          color="primary"
          size="xl"
          block
          :loading="loading"
          class="text-lg h-14"
        >
          {{ $t('login') }}
        </UButton>
        <UAlert v-if="error" color="red" variant="subtle" class="mt-2 text-base">
          {{ error }}
        </UAlert>
      </UForm>
    </UCard>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAuth } from '#imports'

const form = ref({ username: '', password: '' })
const loading = ref(false)
const error = ref('')
const { signIn } = useAuth()

const onLogin = async () => {
  loading.value = true
  error.value = ''
  try {
    const result = await signIn({
      username: form.value.username,
      password: form.value.password,
    }, {
      redirect: false
    })
    if (result?.error) {
      error.value = result.error
    } else {
      await navigateTo('/')
    }
  } catch (err) {
    console.error('Unexpected error:', err)
    error.value = 'Unknown error occurred during login'
  } finally {
    loading.value = false
  }
}
</script> 