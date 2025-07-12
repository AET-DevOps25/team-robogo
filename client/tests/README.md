# Frontend Mock Testing Guide

This project uses **MSW (Mock Service Worker)** + **Vitest** combination for frontend API service mock testing.

## Testing Technology Stack

- **Vitest**: Fast test runner
- **MSW**: Mock Service Worker for intercepting HTTP requests
- **@vue/test-utils**: Vue component testing utilities
- **Happy DOM**: Lightweight DOM environment

## Quick Start

### 1. Install Dependencies

```bash
cd client
yarn install
```

### 2. Run Tests

```bash
# Run all tests
yarn test

# Run tests and generate coverage report
yarn test:coverage

# Run test UI interface
yarn test:ui
```

## Test Structure

```
tests/
├── setup.ts              # Test setup file
├── mocks/
│   └── handlers.ts        # MSW request handlers
├── services/
│   └── aiService.test.ts  # AI service tests
└── components/
    └── AIServiceComponent.test.ts  # Component tests
```

## Mock API Overview

### AI Service Mock Endpoints

Our mock handlers provide the following API endpoints:

1. **Health Check**: `GET /api/proxy/genai/health`
2. **Get Suggestion**: `POST /api/proxy/genai/suggestion`
3. **Service Info**: `GET /api/proxy/genai/`
4. **Error Testing**: `GET /api/proxy/genai/error`

### Test Scenarios

Mock handlers support the following test scenarios:

- ✅ Normal responses
- ❌ Error responses (400 status code)
- ⏱️ Timeout simulation
- 🔌 Network errors
- 📊 Different service type responses

## Writing Tests

### 1. Service Test Example

```typescript
import { describe, it, expect } from 'vitest'
import { AIService } from '@/services/aiService'

describe('AIService', () => {
  it('should successfully get health check information', async () => {
    const result = await AIService.checkHealth()
    expect(result.status).toBe('healthy')
  })
})
```

### 2. Component Test Example

```typescript
import { mount } from '@vue/test-utils'
import { AIService } from '@/services/aiService'

const TestComponent = {
  template: \`<button @click="checkHealth">Check</button>\`,
  methods: {
    async checkHealth() {
      await AIService.checkHealth()
    }
  }
}

describe('Component Tests', () => {
  it('should be able to call AI service', async () => {
    const wrapper = mount(TestComponent)
    await wrapper.find('button').trigger('click')
    // Test component behavior
  })
})
```

## Advanced Testing Techniques

### 1. Custom Mock Responses

```typescript
import { server } from './setup'
import { http, HttpResponse } from 'msw'

// Override default response in specific tests
server.use(
  http.get('/api/proxy/genai/health', () => {
    return HttpResponse.json({ error: 'Service unavailable' }, { status: 503 })
  })
)
```

### 2. Async Testing

```typescript
it('should handle async operations', async () => {
  const promise = AIService.getSuggestion({ text: 'test' })
  const result = await promise
  expect(result.suggestion).toBeDefined()
})
```

### 3. Error Handling Tests

```typescript
it('should handle network errors', async () => {
  // Use special error trigger word
  await expect(AIService.getSuggestion({ text: 'error' })).rejects.toThrow(
    'HTTP error! status: 400'
  )
})
```

## Test Coverage

Running `yarn test:coverage` will generate coverage reports:

- 📊 **HTML Report**: `coverage/index.html`
- 📈 **Console Output**: Real-time coverage statistics
- 🎯 **Target**: Maintain 80%+ coverage

## Debugging Tests

### 1. Debug Individual Tests

```bash
# Run specific test file
yarn test aiService.test.ts

# Watch mode
yarn test --watch
```

### 2. Debug Failed Tests

```bash
# Show detailed error information
yarn test --reporter=verbose

# Stop at first failure
yarn test --bail=1
```

## Best Practices

1. **📝 Clear Test Descriptions**: Use descriptive test names
2. **🎯 Single Responsibility**: Each test should verify one functionality
3. **🔄 Independence**: Tests should not depend on each other
4. **📊 Coverage**: Important business logic should have 100% coverage
5. **🚀 Performance**: Avoid unnecessary DOM operations and async waits

## Common Questions

### Q: Why use MSW instead of other mock libraries?

A: MSW advantages:

- 🌐 Real HTTP request interception
- 🔧 Same mock setup works for both testing and development
- 🎯 Closer to real network environment
- 📱 Support for both browser and Node.js environments

### Q: How to mock complex API responses?

A: Create more complex response logic in `handlers.ts`:

```typescript
http.post('/api/complex', async ({ request }) => {
  const body = await request.json()

  // Return different responses based on request content
  if (body.type === 'special') {
    return HttpResponse.json({ special: true })
  }

  return HttpResponse.json({ normal: true })
})
```

### Q: How to test error scenarios?

A: Use special input values to trigger errors, or temporarily override mock responses in tests.

## Related Resources

- [Vitest Documentation](https://vitest.dev/)
- [MSW Documentation](https://mswjs.io/)
- [Vue Test Utils Documentation](https://vue-test-utils.vuejs.org/)
- [Nuxt Testing Documentation](https://nuxt.com/docs/getting-started/testing)
