import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import SlideCard from '@/components/SlideCard.vue'

describe('SlideCard', () => {
  const mockItem = {
    id: 1,
    name: 'Test Slide',
    url: 'https://example.com/slide.jpg'
  }

  it('renders correctly', () => {
    const wrapper = mount(SlideCard, {
      props: {
        item: mockItem
      }
    })

    expect(wrapper.find('div').exists()).toBe(true)
    expect(wrapper.find('div').classes()).toContain('w-[300px]')
    expect(wrapper.find('div').classes()).toContain('rounded')
    expect(wrapper.find('div').classes()).toContain('shadow-lg')
  })

  it('displays item name', () => {
    const wrapper = mount(SlideCard, {
      props: {
        item: mockItem
      }
    })

    expect(wrapper.text()).toContain('Test Slide')
  })

  it('displays item image', () => {
    const wrapper = mount(SlideCard, {
      props: {
        item: mockItem
      }
    })

    const img = wrapper.find('img')
    expect(img.exists()).toBe(true)
    expect(img.attributes('src')).toBe('https://example.com/slide.jpg')
    expect(img.attributes('alt')).toBe('preview')
  })

  it('applies selected styling when selected', () => {
    const wrapper = mount(SlideCard, {
      props: {
        item: mockItem,
        selected: true
      }
    })

    expect(wrapper.find('div').classes()).toContain('ring-4')
    expect(wrapper.find('div').classes()).toContain('ring-blue-500')
  })

  it('applies non-selected styling when not selected', () => {
    const wrapper = mount(SlideCard, {
      props: {
        item: mockItem,
        selected: false
      }
    })

    expect(wrapper.find('div').classes()).toContain('bg-white')
    expect(wrapper.find('div').classes()).toContain('hover:bg-gray-100')
  })

  it('emits click event when clicked', async () => {
    const wrapper = mount(SlideCard, {
      props: {
        item: mockItem
      }
    })

    await wrapper.find('div').trigger('click')

    expect(wrapper.emitted('click')).toBeTruthy()
    expect(wrapper.emitted('click')).toHaveLength(1)
  })

  it('has correct image styling', () => {
    const wrapper = mount(SlideCard, {
      props: {
        item: mockItem
      }
    })

    const img = wrapper.find('img')
    expect(img.classes()).toContain('w-[90%]')
    expect(img.classes()).toContain('h-40')
    expect(img.classes()).toContain('object-cover')
    expect(img.classes()).toContain('mx-auto')
    expect(img.classes()).toContain('rounded')
  })

  it('has correct text styling', () => {
    const wrapper = mount(SlideCard, {
      props: {
        item: mockItem
      }
    })

    const text = wrapper.find('p')
    expect(text.classes()).toContain('text-gray-700')
    expect(text.classes()).toContain('text-base')
  })
}) 