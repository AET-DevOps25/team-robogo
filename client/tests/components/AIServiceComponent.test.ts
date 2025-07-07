import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { AIService } from '@/services/aiService'

// 创建一个简单的测试组件
const TestComponent = {
  template: `
    <div>
      <button @click="checkHealth" data-testid="health-button">
        检查健康状态
      </button>
      <div v-if="healthStatus" data-testid="health-status">
        {{ healthStatus.status }}
      </div>
      <div v-if="error" data-testid="error">
        {{ error }}
      </div>
    </div>
  `,
  data() {
    return {
      healthStatus: null,
      error: null
    }
  },
  methods: {
    async checkHealth() {
      try {
        this.healthStatus = await AIService.checkHealth()
        this.error = null
      } catch (err) {
        this.error = err instanceof Error ? err.message : '未知错误'
        this.healthStatus = null
      }
    }
  }
}

describe('使用AIService的组件测试', () => {
  it('应该成功显示健康状态', async () => {
    const wrapper = mount(TestComponent)
    
    // 点击健康检查按钮
    const healthButton = wrapper.find('[data-testid="health-button"]')
    await healthButton.trigger('click')
    
    // 等待异步操作完成
    await wrapper.vm.$nextTick()
    
    // 检查健康状态是否显示
    const healthStatus = wrapper.find('[data-testid="health-status"]')
    expect(healthStatus.exists()).toBe(true)
    expect(healthStatus.text()).toBe('healthy')
  })

  it('应该显示错误信息当API调用失败时', async () => {
    // Mock AIService.checkHealth 方法返回错误
    const mockCheckHealth = vi.spyOn(AIService, 'checkHealth')
    mockCheckHealth.mockRejectedValueOnce(new Error('API调用失败'))
    
    const wrapper = mount(TestComponent)
    
    // 点击健康检查按钮
    const healthButton = wrapper.find('[data-testid="health-button"]')
    await healthButton.trigger('click')
    
    // 等待异步操作完成
    await wrapper.vm.$nextTick()
    
    // 检查错误信息是否显示
    const error = wrapper.find('[data-testid="error"]')
    expect(error.exists()).toBe(true)
    expect(error.text()).toBe('API调用失败')
    
    // 恢复原始方法
    mockCheckHealth.mockRestore()
  })

  it('应该在错误后能够重新获取健康状态', async () => {
    const wrapper = mount(TestComponent)
    
    // 第一次调用失败
    const mockCheckHealth = vi.spyOn(AIService, 'checkHealth')
    mockCheckHealth.mockRejectedValueOnce(new Error('临时错误'))
    
    const healthButton = wrapper.find('[data-testid="health-button"]')
    await healthButton.trigger('click')
    await wrapper.vm.$nextTick()
    
    // 确认显示错误
    expect(wrapper.find('[data-testid="error"]').exists()).toBe(true)
    
    // 恢复正常响应
    mockCheckHealth.mockRestore()
    
    // 再次点击
    await healthButton.trigger('click')
    await wrapper.vm.$nextTick()
    
    // 确认显示正常状态
    expect(wrapper.find('[data-testid="health-status"]').exists()).toBe(true)
    expect(wrapper.find('[data-testid="error"]').exists()).toBe(false)
  })
}) 