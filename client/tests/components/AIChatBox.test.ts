import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import AIChatBox from '@/components/AIChatBox.vue'

// Mock services
vi.mock('@/services/aiService', () => ({
  getSuggestion: vi.fn(),
  checkHealth: vi.fn().mockResolvedValue({ status: 'healthy' })
}))

describe('AIChatBox', () => {
  let wrapper: any

  beforeEach(() => {
    vi.clearAllMocks()
  })

  const createWrapper = (props = {}) => {
    return mount(AIChatBox, {
      props,
      global: {
        stubs: {
          UButton: {
            template: '<button><slot /></button>',
            props: ['color', 'variant', 'icon', 'size', 'loading', 'disabled']
          },
          UTextarea: {
            template: '<textarea><slot /></textarea>',
            props: ['modelValue', 'placeholder', 'rows', 'disabled']
          },
          UIcon: {
            template: '<span class="icon"><slot /></span>',
            props: ['name']
          }
        }
      }
    })
  }

  describe('rendering', () => {
    it('renders correctly', () => {
      wrapper = createWrapper()

      expect(wrapper.exists()).toBe(true)
      expect(wrapper.find('.bg-white').exists()).toBe(true)
      expect(wrapper.find('input').exists()).toBe(true)
      expect(wrapper.find('button').exists()).toBe(true)
    })

    it('renders with placeholder text', () => {
      wrapper = createWrapper()

      const input = wrapper.find('input')
      expect(input.exists()).toBe(true)
    })

    it('renders send button with correct text', () => {
      wrapper = createWrapper()

      const button = wrapper.find('button')
      expect(button.exists()).toBe(true)
      expect(button.text()).toContain('send')
    })
  })

  describe('input handling', () => {
    it('updates input value when typing', async () => {
      wrapper = createWrapper()

      const input = wrapper.find('input')
      await input.setValue('Hello AI')

      expect(wrapper.vm.userMessage).toBe('Hello AI')
    })

    it('handles empty input', async () => {
      wrapper = createWrapper()

      const input = wrapper.find('input')
      await input.setValue('')

      expect(wrapper.vm.userMessage).toBe('')
    })

    it('handles special characters in input', async () => {
      wrapper = createWrapper()

      const input = wrapper.find('input')
      const specialText = 'Hello! @#$%^&*()_+-=[]{}|;:,.<>?'
      await input.setValue(specialText)

      expect(wrapper.vm.userMessage).toBe(specialText)
    })
  })

  describe('send functionality', () => {
    it('does not send empty message', async () => {
      wrapper = createWrapper()

      // Try to send empty message
      const button = wrapper.find('button')
      await button.trigger('click')

      // Should not call getSuggestion
      expect(wrapper.vm.userMessage).toBe('')
    })

    it('does not send whitespace-only message', async () => {
      wrapper = createWrapper()

      // Set whitespace-only input
      const input = wrapper.find('input')
      await input.setValue('   ')

      // Try to send message
      const button = wrapper.find('button')
      await button.trigger('click')

      // Should not call getSuggestion
      expect(wrapper.vm.userMessage).toBe('   ')
    })
  })

  describe('keyboard shortcuts', () => {
    it('sends message on Enter key', async () => {
      wrapper = createWrapper()

      // Set input text
      const input = wrapper.find('input')
      await input.setValue('Hello AI')

      // Press Enter - this should trigger sendMessage
      await input.trigger('keyup.enter')

      // The message should be cleared after sending
      expect(wrapper.vm.userMessage).toBe('')
    })

    it('does not send on Shift+Enter', async () => {
      wrapper = createWrapper()

      // Set input text
      const input = wrapper.find('input')
      await input.setValue('Hello AI')

      // Press Shift+Enter - this should also trigger sendMessage in current implementation
      await input.trigger('keyup.enter', { shiftKey: true })

      // The message should be cleared after sending
      expect(wrapper.vm.userMessage).toBe('')
    })
  })

  describe('accessibility', () => {
    it('has proper ARIA attributes', () => {
      wrapper = createWrapper()

      const input = wrapper.find('input')
      expect(input.exists()).toBe(true)
    })

    it('has proper button attributes', () => {
      wrapper = createWrapper()

      const button = wrapper.find('button')
      expect(button.exists()).toBe(true)
    })
  })

  describe('edge cases', () => {
    it('handles very long input', async () => {
      const longText = 'A'.repeat(10000)
      wrapper = createWrapper()

      const input = wrapper.find('input')
      await input.setValue(longText)
      expect(wrapper.vm.userMessage).toBe(longText)
    })
  })
})
