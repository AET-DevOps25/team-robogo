import { beforeAll, afterEach, afterAll } from 'vitest'
import { setupServer } from 'msw/node'
import { handlers } from './mocks/handlers'

// 创建MSW服务器
const server = setupServer(...handlers)

// 在所有测试开始前启动服务器
beforeAll(() => server.listen())

// 每个测试后重置handlers
afterEach(() => server.resetHandlers())

// 所有测试完成后关闭服务器
afterAll(() => server.close()) 