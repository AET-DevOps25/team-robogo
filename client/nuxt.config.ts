// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-05-15',
  devtools: { enabled: true },
  modules: ['@sidebase/nuxt-auth', '@nuxt/ui', '@nuxtjs/i18n', '@pinia/nuxt', 'pinia-plugin-persistedstate/nuxt'],
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
        signIn: {
          path: '/api/auth/login',
          method: 'post'
        },
        signOut: {
          path: '/api/auth/logout',
          method: 'post'
        },
        getSession: {
          path: '/api/auth/session',
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
  vite: {
    server: {
      proxy: {
        '/api/auth': { target: 'http://localhost:8080', changeOrigin: true },
        '/api/server': { target: 'http://localhost:8080', changeOrigin: true },
        '/api/genai': { target: 'http://localhost:8080', changeOrigin: true }
      }
    }
  },
  css: ['~/assets/css/main.css'],
  app: {
    head: {
      link: [{ rel: 'icon', type: 'image/svg+xml', href: '/favicon.svg' }]
    }
  }
})
