export default {
  baseURL: 'http://localhost:8080',
  provider: {
    type: 'local',
    endpoints: {
      login: { url: '/auth/login', method: 'post' },
      logout: { url: '/auth/logout', method: 'post' },
      user: { url: '/auth/user', method: 'get' },
      session: { url: '/auth/session', method: 'get' }
    }
  }
} 