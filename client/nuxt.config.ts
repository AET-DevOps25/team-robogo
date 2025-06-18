// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-05-15',
  devtools: { enabled: true },
  css: ['@/assets/tailwind.css'],
  modules: [
    '@sidebase/nuxt-auth',
    '@nuxtjs/i18n',
    '@pinia/nuxt'
  ]
})
