# 前端Mock测试指南

这个项目使用了**MSW (Mock Service Worker)** + **Vitest**的组合来进行前端API服务的Mock测试。

## 测试技术栈

- **Vitest**: 快速的测试运行器
- **MSW**: Mock Service Worker，用于拦截HTTP请求
- **@vue/test-utils**: Vue组件测试工具
- **Happy DOM**: 轻量级DOM环境

## 快速开始

### 1. 安装依赖

```bash
cd client
yarn install
```

### 2. 运行测试

```bash
# 运行所有测试
yarn test

# 运行测试并生成覆盖率报告
yarn test:coverage

# 运行测试UI界面
yarn test:ui
```

## 测试结构

```
tests/
├── setup.ts              # 测试设置文件
├── mocks/
│   └── handlers.ts        # MSW请求处理器
├── services/
│   └── aiService.test.ts  # AI服务测试
└── components/
    └── AIServiceComponent.test.ts  # 组件测试
```

## Mock API说明

### AI服务Mock接口

我们的Mock处理器提供了以下API端点：

1. **健康检查**: `GET /api/proxy/genai/health`
2. **获取建议**: `POST /api/proxy/genai/suggestion`
3. **服务信息**: `GET /api/proxy/genai/`
4. **错误测试**: `GET /api/proxy/genai/error`

### 测试场景

Mock处理器支持以下测试场景：

- ✅ 正常响应
- ❌ 错误响应（400状态码）
- ⏱️ 超时模拟
- 🔌 网络错误
- 📊 不同服务类型响应

## 编写测试

### 1. 服务测试示例

```typescript
import { describe, it, expect } from 'vitest'
import { AIService } from '@/services/aiService'

describe('AIService', () => {
  it('应该成功获取健康检查信息', async () => {
    const result = await AIService.checkHealth()
    expect(result.status).toBe('healthy')
  })
})
```

### 2. 组件测试示例

```typescript
import { mount } from '@vue/test-utils'
import { AIService } from '@/services/aiService'

const TestComponent = {
  template: \`<button @click="checkHealth">检查</button>\`,
  methods: {
    async checkHealth() {
      await AIService.checkHealth()
    }
  }
}

describe('组件测试', () => {
  it('应该能够调用AI服务', async () => {
    const wrapper = mount(TestComponent)
    await wrapper.find('button').trigger('click')
    // 测试组件行为
  })
})
```

## 高级测试技巧

### 1. 自定义Mock响应

```typescript
import { server } from './setup'
import { http, HttpResponse } from 'msw'

// 在特定测试中覆盖默认响应
server.use(
  http.get('/api/proxy/genai/health', () => {
    return HttpResponse.json(
      { error: '服务不可用' },
      { status: 503 }
    )
  })
)
```

### 2. 异步测试

```typescript
it('应该处理异步操作', async () => {
  const promise = AIService.getSuggestion({ text: 'test' })
  const result = await promise
  expect(result.suggestion).toBeDefined()
})
```

### 3. 错误处理测试

```typescript
it('应该处理网络错误', async () => {
  // 使用特殊的错误触发词
  await expect(
    AIService.getSuggestion({ text: 'error' })
  ).rejects.toThrow('HTTP error! status: 400')
})
```

## 测试覆盖率

运行 `yarn test:coverage` 会生成覆盖率报告：

- 📊 **HTML报告**: `coverage/index.html`
- 📈 **控制台输出**: 实时显示覆盖率统计
- 🎯 **目标**: 保持80%以上的覆盖率

## 调试测试

### 1. 调试单个测试

```bash
# 运行特定测试文件
yarn test aiService.test.ts

# 观察模式
yarn test --watch
```

### 2. 调试失败的测试

```bash
# 显示详细错误信息
yarn test --reporter=verbose

# 在第一个失败时停止
yarn test --bail=1
```

## 最佳实践

1. **📝 清晰的测试描述**: 使用中文描述测试目的
2. **🎯 单一职责**: 每个测试只验证一个功能
3. **🔄 独立性**: 测试之间不应相互依赖
4. **📊 覆盖率**: 重要的业务逻辑应该有100%覆盖率
5. **🚀 性能**: 避免不必要的DOM操作和异步等待

## 常见问题

### Q: 为什么使用MSW而不是其他Mock库？

A: MSW的优势：
- 🌐 真实的HTTP请求拦截
- 🔧 同一套Mock既可用于测试也可用于开发
- 🎯 更接近真实的网络环境
- 📱 支持浏览器和Node.js环境

### Q: 如何Mock复杂的API响应？

A: 在 `handlers.ts` 中创建更复杂的响应逻辑：

```typescript
http.post('/api/complex', async ({ request }) => {
  const body = await request.json()
  
  // 根据请求内容返回不同响应
  if (body.type === 'special') {
    return HttpResponse.json({ special: true })
  }
  
  return HttpResponse.json({ normal: true })
})
```

### Q: 如何测试错误场景？

A: 使用特殊的输入值触发错误，或者在测试中临时覆盖Mock响应。

## 相关资源

- [Vitest官方文档](https://vitest.dev/)
- [MSW官方文档](https://mswjs.io/)
- [Vue测试工具文档](https://vue-test-utils.vuejs.org/)
- [Nuxt测试文档](https://nuxt.com/docs/getting-started/testing) 