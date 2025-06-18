export default {
  baseURL: 'http://api-gateway:8080',
  provider: {
    type: 'local',
    endpoints: {
      login: { url: '/api/auth/login', method: 'post' },
      logout: { url: '/api/auth/logout', method: 'post' },
      user: { url: '/api/auth/user', method: 'get' }
    }
  }
} 