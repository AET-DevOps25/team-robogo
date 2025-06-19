<template>
  <div class="w-full flex justify-end items-center p-4">
    <div class="relative">
      <select 
        v-model="currentLocale" 
        @change="handleLocaleChange"
        class="appearance-none bg-white border border-gray-300 rounded-lg px-4 py-2 pr-8 hover:border-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 cursor-pointer text-gray-700"
      >
        <option v-for="l in localeOptions" :key="l.code" :value="l.code" class="py-2">
          {{ getFlagEmoji(l.code) }} {{ l.name }}
        </option>
      </select>
      <div class="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
        <svg class="h-4 w-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
        </svg>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// Language switcher for i18n, can be used on every page
import { useI18n } from 'vue-i18n'
import { computed, ref } from 'vue'

const i18n = useI18n()
const currentLocale = ref(i18n.locale.value)

const getFlagEmoji = (languageCode: string) => {
  const countryCode = languageCode === 'en' ? 'GB' : languageCode.toUpperCase()
  
  const codePoints = countryCode
    .split('')
    .map(char => 127397 + char.charCodeAt(0))
  
  return String.fromCodePoint(...codePoints)
}

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

<style>
select {
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
}

select::-ms-expand {
  display: none;
}
</style>
