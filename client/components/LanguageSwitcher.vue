<template>
  <div class="relative">
    <select v-model="currentLocale" class="language-select" @change="handleLocaleChange">
      <option v-for="l in localeOptions" :key="l.code" :value="l.code" class="py-2">
        {{ getFlagEmoji(l.code) }} {{ l.name }}
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

  const getFlagEmoji = (languageCode: string) => {
    const countryCode = languageCode === 'en' ? 'GB' : languageCode.toUpperCase()

    const codePoints = countryCode.split('').map(char => 127397 + char.charCodeAt(0))

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

<style lang="postcss">
  .language-select {
    @apply appearance-none bg-white dark:bg-gray-800;
    @apply border border-gray-300 dark:border-gray-600;
    @apply rounded-lg px-4 py-2 pr-8;
    @apply hover:border-gray-400 dark:hover:border-gray-500;
    @apply focus:outline-none focus:ring-2 focus:ring-primary dark:focus:ring-primary;
    @apply focus:border-transparent;
    @apply transition-all duration-200;
    @apply cursor-pointer;
    @apply text-gray-700 dark:text-gray-200;
  }

  select::-ms-expand {
    display: none;
  }
</style>
