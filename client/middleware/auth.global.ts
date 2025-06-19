import { useAuth } from '#imports'

export default defineNuxtRouteMiddleware((to, from) => {
  const { status } = useAuth()
  if (to.meta.auth && to.path !== '/login' && status.value !== 'authenticated') {
    return navigateTo('/login')
  }
}) 