import { fileURLToPath } from 'node:url'
import { defineVitestConfig } from '@nuxt/test-utils/config'

export default defineVitestConfig({
  test: {
    environment: 'happy-dom',
    setupFiles: ['./tests/setup.ts'],
    globals: true,
    root: fileURLToPath(new URL('./', import.meta.url)),
    coverage: {
      reporter: ['text', 'json', 'html'],
      exclude: [
        'node_modules/',
        'tests/',
        '**/*.d.ts',
        '**/*.config.ts',
        '**/*.config.js',
        'nuxt.config.ts',
        '.nuxt/**',
        // exclude page files
        'pages/**',
        // exclude middleware and plugins
        'middleware/**',
        'plugins/**',
        // exclude server-side code
        'server/**',
        // exclude type definitions
        'interfaces/**',
        'types/**',
        // exclude root component
        'app.vue',
        // exclude state management (if not needed for testing)
        'stores/**',
        // exclude static resources
        'assets/**',
        'public/**',
        // exclude internationalization files
        'i18n/**',
        // exclude build files
        'dist/**',
        '.output/**'
      ]
    }
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./', import.meta.url))
    }
  }
})
