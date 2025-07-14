import { describe, it, expect } from 'vitest'
import { shallowMount } from '@vue/test-utils'
import SlideDeckCard from '@/components/SlideDeckCard.vue'

describe('SlideDeckCard', () => {
  const mockAllSlides = [
    {
      id: 1,
      index: 0,
      name: 'Slide 1',
      type: 'image',
      imageMeta: { id: 1, name: 'img1', contentType: 'image/jpeg' }
    },
    {
      id: 2,
      index: 1,
      name: 'Slide 2',
      type: 'image',
      imageMeta: { id: 2, name: 'img2', contentType: 'image/jpeg' }
    },
    {
      id: 3,
      index: 2,
      name: 'Slide 3',
      type: 'image',
      imageMeta: { id: 3, name: 'img3', contentType: 'image/jpeg' }
    },
    {
      id: 4,
      index: 3,
      name: 'Score Slide',
      type: 'score',
      scores: [
        { points: 10, time: 60, highlight: false },
        { points: 20, time: 120, highlight: true }
      ],
      categoryId: 100
    }
  ]

  const defaultProps = {
    title: 'Test Deck',
    slides: mockAllSlides,
    slideIds: [1, 2],
    speed: 50
  }

  it('renders correctly', () => {
    const wrapper = shallowMount(SlideDeckCard, {
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
    const wrapper = shallowMount(SlideDeckCard, {
      props: defaultProps,
      global: {
        stubs: {
          SpeedControl: true,
          SlideCard: true,
          draggable: true
        }
      }
    })

    expect(wrapper.text()).toContain('Test Deck')
  })

  it('displays Speed label', () => {
    const wrapper = shallowMount(SlideDeckCard, {
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
    const wrapper = shallowMount(SlideDeckCard, {
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
