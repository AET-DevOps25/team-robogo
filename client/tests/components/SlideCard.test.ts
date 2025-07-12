import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import SlideCard from '@/components/SlideCard.vue'

const mockItem = {
  id: 1,
  name: 'Test Slide',
  url: 'https://example.com/slide.jpg'
}

const factory = (selected = false) => mount(SlideCard, { props: { item: mockItem, selected } })

describe('SlideCard', () => {
  it('renders image & name', () => {
    const wrapper = factory()

    // img
    const img = wrapper.get('img')
    expect(img.attributes('src')).toBe(mockItem.url)
    expect(img.attributes('alt')).toBe('preview')

    // text
    expect(wrapper.text()).toContain(mockItem.name)
  })

  it('emits click', async () => {
    const wrapper = factory()
    await wrapper.trigger('click')
    expect(wrapper.emitted('click')).toHaveLength(1)
  })

  it('applies selected classes', () => {
    const wrapper = factory(true)
    expect(wrapper.classes()).toContain('ring-4')
    expect(wrapper.classes()).toContain('ring-blue-500')
    // 不应再有 hover 灰背景类
    expect(wrapper.classes()).not.toContain('hover:bg-gray-100')
  })

  it('applies un-selected hover classes', () => {
    const wrapper = factory(false)
    expect(wrapper.classes()).toContain('hover:bg-gray-100')
    // 不应出现选中圈
    expect(wrapper.classes()).not.toContain('ring-4')
  })
})
