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
    appearance: none;
    background-color: white;
    border: 1px solid #d1d5db;
    border-radius: 0.5rem;
    padding: 0.5rem 2rem 0.5rem 1rem;
    cursor: pointer;
    color: #374151;
    transition: all 0.2s;
  }

  .language-select:hover {
    border-color: #9ca3af;
  }

  .language-select:focus {
    outline: none;
    border-color: transparent;
    box-shadow: 0 0 0 2px #3b82f6;
  }

  /* Dark mode styles */
  .dark .language-select {
    background-color: #1f2937;
    border-color: #4b5563;
    color: #e5e7eb;
  }

  .dark .language-select:hover {
    border-color: #6b7280;
  }

  .dark .language-select:focus {
    box-shadow: 0 0 0 2px #3b82f6;
  }

  select::-ms-expand {
    display: none;
  }
</style>
