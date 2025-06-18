import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: '' as string,
    user: null as null | Record<string, any>
  }),
  actions: {
    setToken(token: string) {
      this.token = token
    },
    setUser(user: any) {
      this.user = user
    },
    logout() {
      this.token = ''
      this.user = null
    }
  },
  persist: true
})