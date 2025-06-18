import { useAuth } from '#imports'

export default defineNuxtRouteMiddleware(async (to, from) => {
  const { status } = useAuth()
  if (to.meta.auth && status.value !== 'authenticated') {
    return navigateTo('/login')
  }
}) 