<template>
  <div class="w-full flex justify-end items-center p-4">
    <select v-model="currentLocale" class="border rounded px-2 py-1" @change="handleLocaleChange">
      <option v-for="l in localeOptions" :key="l.code" :value="l.code">
        {{ l.name }}
      </option>
    </select>
  </div>
</template>

<script setup lang="ts">
// Language switcher for i18n, can be used on every page
import { useI18n } from 'vue-i18n'
import { computed, ref } from 'vue'

const i18n = useI18n()
const currentLocale = ref(i18n.locale.value)

const localeOptions = computed(() => {
  return i18n.locales.value.map(locale => ({
    code: locale.code,
    name: locale.name
  }))
})

const handleLocaleChange = async () => {
  await i18n.setLocale(currentLocale.value)
}
</script>
