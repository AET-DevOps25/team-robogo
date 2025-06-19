<template>
  <header class="w-full flex justify-end items-center gap-4 p-4">
    <LanguageSwitcher />
    <UButton
      v-if="showLogout"
      color="error"
      variant="soft"
      icon="i-heroicons-arrow-right-on-rectangle"
      size="sm"
      loading-auto
      loading-icon="i-lucide-loader-2"
      @click="handleLogout"
    >
      {{ $t('logout') }}
    </UButton>
  </header>
</template>

<script setup lang="ts">
import { useToast, useI18n } from '#imports'
import { useAuth } from '#imports'

defineProps({
  showLogout: {
    type: Boolean,
    default: true
  }
})

const { signOut } = useAuth()
const toast = useToast()
const { t } = useI18n()

const handleLogout = async () => {
  try {
    await signOut()
    toast.add({
      title: t('logoutSuccess'),
      color: 'success'
    })
  } catch (error) {
    toast.add({
      title: t('logoutFailed'),
      color: 'error'
    })
  }
}
</script> 