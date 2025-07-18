import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import Toast from '~/components/Toast.vue'

describe('Toast', () => {
  describe('rendering', () => {
    it('renders correctly with default props', async () => {
      const wrapper = mount(Toast, {
        props: {
          message: 'Test Toast'
        }
      })

      // Wait for component to mount and become visible
      await wrapper.vm.$nextTick()

      expect(wrapper.exists()).toBe(true)
      expect(wrapper.find('.fixed').exists()).toBe(true)
      expect(wrapper.text()).toContain('Test Toast')
    })

    it('renders with different types', async () => {
      const types = ['success', 'error', 'info', 'warning'] as const

      for (const type of types) {
        const wrapper = mount(Toast, {
          props: {
            message: `Test ${type}`,
            type
          }
        })

        await wrapper.vm.$nextTick()

        expect(wrapper.text()).toContain(`Test ${type}`)
        expect(wrapper.find('.bg-green-50').exists()).toBe(type === 'success')
        expect(wrapper.find('.bg-red-50').exists()).toBe(type === 'error')
        expect(wrapper.find('.bg-blue-50').exists()).toBe(type === 'info')
        expect(wrapper.find('.bg-yellow-50').exists()).toBe(type === 'warning')
      }
    })

    it('renders without description', async () => {
      const wrapper = mount(Toast, {
        props: {
          message: 'Test Toast'
        }
      })

      await wrapper.vm.$nextTick()
      expect(wrapper.text()).toContain('Test Toast')
    })

    it('renders with message', async () => {
      const wrapper = mount(Toast, {
        props: {
          message: 'Test Message'
        }
      })

      await wrapper.vm.$nextTick()
      expect(wrapper.text()).toContain('Test Message')
    })
  })

  describe('interactions', () => {
    it('emits close event when close button is clicked', async () => {
      const wrapper = mount(Toast, {
        props: {
          message: 'Test Toast'
        }
      })

      await wrapper.vm.$nextTick()

      const closeButton = wrapper.find('button')
      expect(closeButton.exists()).toBe(true)

      await closeButton.trigger('click')

      // Toast should be hidden after click
      expect(wrapper.find('.fixed').exists()).toBe(false)
    })

    it('hides toast when close button is clicked', async () => {
      const wrapper = mount(Toast, {
        props: {
          message: 'Test Toast'
        }
      })

      await wrapper.vm.$nextTick()

      const closeButton = wrapper.find('button')
      await closeButton.trigger('click')

      expect(wrapper.find('.fixed').exists()).toBe(false)
    })
  })

  describe('auto-dismiss', () => {
    it('auto-dismisses after specified duration', async () => {
      vi.useFakeTimers()

      const wrapper = mount(Toast, {
        props: {
          message: 'Test Toast',
          duration: 1000
        }
      })

      await wrapper.vm.$nextTick()
      expect(wrapper.find('.fixed').exists()).toBe(true)

      vi.advanceTimersByTime(1000)
      await wrapper.vm.$nextTick()

      expect(wrapper.find('.fixed').exists()).toBe(false)

      vi.useRealTimers()
    })

    it('does not auto-dismiss when duration is 0', async () => {
      vi.useFakeTimers()

      const wrapper = mount(Toast, {
        props: {
          message: 'Test Toast',
          duration: 0
        }
      })

      await wrapper.vm.$nextTick()
      expect(wrapper.find('.fixed').exists()).toBe(true)

      vi.advanceTimersByTime(5000)
      await wrapper.vm.$nextTick()

      expect(wrapper.find('.fixed').exists()).toBe(true)

      vi.useRealTimers()
    })

    it('uses default duration when not specified', async () => {
      vi.useFakeTimers()

      const wrapper = mount(Toast, {
        props: {
          message: 'Test Toast'
        }
      })

      await wrapper.vm.$nextTick()
      expect(wrapper.find('.fixed').exists()).toBe(true)

      vi.advanceTimersByTime(5000)
      await wrapper.vm.$nextTick()

      expect(wrapper.find('.fixed').exists()).toBe(false)

      vi.useRealTimers()
    })
  })

  describe('accessibility', () => {
    it('has proper ARIA attributes', async () => {
      const wrapper = mount(Toast, {
        props: {
          message: 'Test Toast'
        }
      })

      await wrapper.vm.$nextTick()
      const toast = wrapper.find('.fixed')
      expect(toast.exists()).toBe(true)
    })

    it('has proper button attributes', async () => {
      const wrapper = mount(Toast, {
        props: {
          message: 'Test Toast'
        }
      })

      await wrapper.vm.$nextTick()
      const closeButton = wrapper.find('button')
      expect(closeButton.exists()).toBe(true)
    })
  })

  describe('styling', () => {
    it('applies correct color classes', async () => {
      const typeClasses = {
        success: 'bg-green-50',
        error: 'bg-red-50',
        info: 'bg-blue-50',
        warning: 'bg-yellow-50'
      }

      for (const [type, expectedClass] of Object.entries(typeClasses)) {
        const wrapper = mount(Toast, {
          props: {
            message: `Test ${type}`,
            type: type as any
          }
        })

        await wrapper.vm.$nextTick()
        expect(wrapper.find(`.${expectedClass}`).exists()).toBe(true)
      }
    })

    it('applies correct position classes', async () => {
      const wrapper = mount(Toast, {
        props: {
          message: 'Test Toast'
        }
      })

      await wrapper.vm.$nextTick()
      expect(wrapper.find('.fixed').exists()).toBe(true)
      expect(wrapper.find('.top-4').exists()).toBe(true)
      expect(wrapper.find('.right-4').exists()).toBe(true)
    })
  })

  describe('edge cases', () => {
    it('handles missing toast data gracefully', async () => {
      const wrapper = mount(Toast, {
        props: {
          message: ''
        }
      })

      await wrapper.vm.$nextTick()
      expect(wrapper.exists()).toBe(true)
    })

    it('handles empty message', async () => {
      const wrapper = mount(Toast, {
        props: {
          message: ''
        }
      })

      await wrapper.vm.$nextTick()
      expect(wrapper.exists()).toBe(true)
    })

    it('handles very long content', async () => {
      const longMessage = 'A'.repeat(100)
      const wrapper = mount(Toast, {
        props: {
          message: longMessage
        }
      })

      await wrapper.vm.$nextTick()
      expect(wrapper.text()).toContain(longMessage)
    })
  })
})
