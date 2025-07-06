// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-05-15',
  devtools: { enabled: true },
  modules: [
    '@sidebase/nuxt-auth',
    '@nuxt/ui',
    '@nuxtjs/i18n',
    '@pinia/nuxt',
    'pinia-plugin-persistedstate/nuxt'
  ],
  runtimeConfig: {
    public: {
      apiBase: process.env.NUXT_PUBLIC_API_BASE || '/'
    }
  },
  auth: {
    baseURL: process.env.NUXT_PUBLIC_API_BASE || '/',
    provider: {
      type: 'local',
      endpoints: {
        signIn: {
          path: '/api/proxy/auth/login',
          method: 'post'
        },
        signOut: {
          path: '/api/proxy/auth/logout',
          method: 'post'
        },
        getSession: {
          path: '/api/proxy/auth/session',
          method: 'get'
        }
      },
      token: {
        type: 'Bearer',
        headerName: 'Authorization',
        maxAgeInSeconds: 3600
      },
      pages: {
        login: '/login'
      }
    },
    globalAppMiddleware: {
      isEnabled: true,
      addDefaultCallbackUrl: true
    }
  },
  i18n: {
    locales: [
      { code: 'en', name: 'English', file: 'en.json' },
      { code: 'de', name: 'Deutsch', file: 'de.json' }
    ],
    defaultLocale: 'en',
    lazy: true,
    strategy: 'no_prefix'
  },
  css: ['~/assets/css/main.css'],
  app: {
    head: {
      link: [{ rel: 'icon', type: 'image/svg+xml', href: '/favicon.svg' }]
    }
  }
})
