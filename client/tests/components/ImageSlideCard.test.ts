import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { nextTick } from 'vue'
import ImageSlideCard from '@/components/ImageSlideCard.vue'
import { SlideType } from '@/interfaces/types'

// Mock services - 使用 setup 中的 useAuthFetch
vi.mock('@/services/slideImageService', () => ({
  fetchImageBlobById: vi.fn().mockImplementation(async (_id: number) => {
    // 模拟 useAuthFetch 的行为
    await Promise.resolve() // 添加 await 表达式
    return new Blob(['mock image data'], { type: 'image/jpeg' })
  })
}))

// Mock composables
vi.mock('vue-i18n', () => ({
  useI18n: () => ({
    t: vi.fn((key: string) => key)
  })
}))

describe('ImageSlideCard', () => {
  let wrapper: any

  const mockImageSlide = {
    id: 1,
    index: 0,
    name: 'Test Image',
    type: SlideType.IMAGE,
    imageMeta: {
      id: 1,
      name: 'test.jpg',
      contentType: 'image/jpeg'
    }
  }

  beforeEach(() => {
    vi.clearAllMocks()

    // Mock URL methods
    global.URL.createObjectURL = vi.fn().mockReturnValue('blob:mock-url')
    global.URL.revokeObjectURL = vi.fn()
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  const createWrapper = (props = {}) => {
    return mount(ImageSlideCard, {
      props: {
        item: mockImageSlide,
        ...props
      }
    })
  }

  describe('rendering', () => {
    it('renders correctly with image slide', () => {
      wrapper = createWrapper()

      expect(wrapper.exists()).toBe(true)
      expect(wrapper.find('.image-slide-card').exists()).toBe(true)
      expect(wrapper.text()).toContain('Test Image')
    })

    it('shows loading state initially', async () => {
      wrapper = createWrapper()

      // 等待组件挂载和异步操作
      await nextTick()

      // 检查加载状态
      expect(wrapper.vm.isLoading).toBe(true)
      expect(wrapper.text()).toContain('loading')
    })

    it('displays image when loaded successfully', async () => {
      wrapper = createWrapper()

      // 等待图片加载完成
      await nextTick()
      await wrapper.vm.$nextTick()

      // 模拟图片加载完成
      wrapper.vm.isLoading = false
      wrapper.vm.imageUrl = 'blob:mock-url'
      await nextTick()

      expect(wrapper.find('img').exists()).toBe(true)
      expect(wrapper.find('img').attributes('alt')).toBe('Test Image')
    })

    it('shows error state when image fails to load', async () => {
      const { fetchImageBlobById } = await import('@/services/slideImageService')
      ;(fetchImageBlobById as any).mockRejectedValue(new Error('Image load failed'))
      wrapper = createWrapper()

      await nextTick()
      await wrapper.vm.$nextTick()

      expect(wrapper.text()).toContain('imageLoadError')
      expect(wrapper.find('img').exists()).toBe(false)
    })
  })

  describe('image events', () => {
    it('handles image load success', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {})
      wrapper = createWrapper()

      await nextTick()
      await wrapper.vm.$nextTick()

      // 确保图片元素存在
      wrapper.vm.imageUrl = 'blob:mock-url'
      wrapper.vm.isLoading = false
      await nextTick()

      const img = wrapper.find('img')
      if (img.exists()) {
        await img.trigger('load')
        expect(consoleSpy).toHaveBeenCalledWith('Image loaded successfully:', 'Test Image')
        expect(wrapper.vm.isLoading).toBe(false)
      }

      consoleSpy.mockRestore()
    })

    it('handles image load error', async () => {
      const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})
      wrapper = createWrapper()

      await nextTick()
      await wrapper.vm.$nextTick()

      // 确保图片元素存在
      wrapper.vm.imageUrl = 'blob:mock-url'
      wrapper.vm.isLoading = false
      await nextTick()

      const img = wrapper.find('img')
      if (img.exists()) {
        await img.trigger('error')
        expect(consoleSpy).toHaveBeenCalledWith('Image load error for:', 'Test Image')
        expect(wrapper.vm.isLoading).toBe(false)
        expect(wrapper.vm.imageUrl).toBeNull()
      }

      consoleSpy.mockRestore()
    })
  })

  describe('cleanup', () => {
    it('revokes object URL on component unmount', async () => {
      wrapper = createWrapper()

      await nextTick()
      await wrapper.vm.$nextTick()

      // 设置 imageUrl
      wrapper.vm.imageUrl = 'blob:mock-url'

      wrapper.unmount()

      expect(global.URL.revokeObjectURL).toHaveBeenCalledWith('blob:mock-url')
    })

    it('revokes object URL when imageId changes', async () => {
      wrapper = createWrapper()

      await nextTick()
      await wrapper.vm.$nextTick()

      // 设置初始 imageUrl
      wrapper.vm.imageUrl = 'blob:mock-url'

      // 更新 props 触发重新加载
      await wrapper.setProps({
        item: {
          ...mockImageSlide,
          imageMeta: {
            id: 2,
            name: 'new.jpg',
            contentType: 'image/jpeg'
          }
        }
      })

      await nextTick()

      // Should revoke the old URL
      expect(global.URL.revokeObjectURL).toHaveBeenCalledWith('blob:mock-url')
    })
  })
})
