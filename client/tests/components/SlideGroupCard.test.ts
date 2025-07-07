import { describe, it, expect } from 'vitest'
import { shallowMount } from '@vue/test-utils'
import SlideGroupCard from '@/components/SlideGroupCard.vue'

describe('SlideGroupCard', () => {
  const mockAllSlides = [
    { id: 1, name: 'Slide 1', url: 'https://example.com/slide1.jpg' },
    { id: 2, name: 'Slide 2', url: 'https://example.com/slide2.jpg' },
    { id: 3, name: 'Slide 3', url: 'https://example.com/slide3.jpg' }
  ]

  const defaultProps = {
    title: 'Test Group',
    allSlides: mockAllSlides,
    slideIds: [1, 2],
    speed: 50
  }

  it('renders correctly', () => {
    const wrapper = shallowMount(SlideGroupCard, {
      props: defaultProps,
      global: {
        stubs: {
          SpeedControl: true,
          SlideCard: true,
          draggable: true
        }
      }
    })

    expect(wrapper.exists()).toBe(true)
    expect(wrapper.classes()).toContain('w-full')
  })

  it('displays title', () => {
    const wrapper = shallowMount(SlideGroupCard, {
      props: defaultProps,
      global: {
        stubs: {
          SpeedControl: true,
          SlideCard: true,
          draggable: true
        }
      }
    })

    expect(wrapper.text()).toContain('Test Group')
  })

  it('displays Speed label', () => {
    const wrapper = shallowMount(SlideGroupCard, {
      props: defaultProps,
      global: {
        stubs: {
          SpeedControl: true,
          SlideCard: true,
          draggable: true
        }
      }
    })

    expect(wrapper.text()).toContain('Speed')
  })

  it('renders SpeedControl component', () => {
    const wrapper = shallowMount(SlideGroupCard, {
      props: defaultProps,
      global: {
        stubs: {
          SpeedControl: true,
          SlideCard: true,
          draggable: true
        }
      }
    })

    expect(wrapper.findComponent({ name: 'SpeedControl' }).exists()).toBe(true)
  })
}) 