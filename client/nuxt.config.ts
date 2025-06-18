// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-05-15',
  devtools: { enabled: true },
  css: ['@/assets/tailwind.css'],
  modules: [
    '@sidebase/nuxt-auth',
    '@nuxtjs/i18n',
    '@pinia/nuxt'
  ],
  runtimeConfig: {
    public: {
      apiBase: process.env.VITE_API_BASE_URL || 'http://localhost:8080'
    }
  },
  auth: {
    baseURL: process.env.VITE_API_BASE_URL || 'http://localhost:8080',
    provider: {
      type: 'local',
      endpoints: {
        signIn: { path: '/api/auth/login', method: 'post' },
        signOut: { path: '/api/auth/logout', method: 'post' },
        getSession: { path: '/api/auth/session', method: 'get' }
      }
    },
  },
  i18n: {
    locales: [
      { code: 'en', name: 'English', file: 'en.json' },
      { code: 'de', name: 'Deutsch', file: 'de.json' }
    ],
    defaultLocale: 'en',
  }
})
