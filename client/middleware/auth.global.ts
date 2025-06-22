import { useAuth } from '#imports'

export default defineNuxtRouteMiddleware((to) => {
  const { status } = useAuth()
  if (
    to.meta.auth &&
    !['/', '/login', '/signup'].includes(to.path) &&
    status.value !== 'authenticated'
  ) {
    return navigateTo('/login')
  }
})
