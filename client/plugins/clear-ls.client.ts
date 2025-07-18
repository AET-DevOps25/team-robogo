// plugins/clear-ls.client.ts
// cleanup storage right before dev
import { defineNuxtPlugin } from '#app'

export default defineNuxtPlugin({
  name: 'clear-localstorage-dev',
  enforce: 'pre', // before Pinia
  setup() {
    if (import.meta.env.DEV) {
      localStorage.removeItem('pinia-screen')
    }
  }
})
