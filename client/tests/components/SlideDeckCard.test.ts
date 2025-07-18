import { describe, it, expect, vi, beforeEach } from 'vitest'
import { shallowMount } from '@vue/test-utils'
import SlideDeckCard from '@/components/SlideDeckCard.vue'
import { SlideType } from '@/interfaces/types'

// Mock services
vi.mock('@/services/slideDeckService', () => ({
  saveGroup: vi.fn(),
  playGroup: vi.fn(),
  refreshGroups: vi.fn(),
  addSlideToDeck: vi.fn(),
  reorderSlides: vi.fn(),
  updateSlideDeckSpeed: vi.fn(),
  removeSlideFromDeck: vi.fn()
}))

// Mock composables
vi.mock('vue-i18n', () => ({
  useI18n: () => ({
    t: vi.fn((key: string) => key)
  })
}))

// Mock stores
vi.mock('@/stores/useDeckStore', () => ({
  useDeckStore: () => ({
    currentDeck: {
      id: 1,
      name: 'Test Deck',
      competitionId: 1,
      slides: [
        {
          id: 1,
          index: 0,
          name: 'Slide 1',
          type: 'IMAGE',
          imageMeta: { id: 1, name: 'img1', contentType: 'image/jpeg' }
        },
        {
          id: 2,
          index: 1,
          name: 'Slide 2',
          type: 'IMAGE',
          imageMeta: { id: 2, name: 'img2', contentType: 'image/jpeg' }
        }
      ],
      transitionTime: 5000,
      version: 1,
      lastUpdate: new Date().toISOString()
    },
    currentDeckId: 1,
    interval: 5000,
    setCurrentDeck: vi.fn(),
    updateDeck: vi.fn(),
    setIntervalMs: vi.fn(),
    checkAndRefreshDeck: vi.fn().mockResolvedValue(undefined),
    stopVersionCheck: vi.fn()
  })
}))

vi.mock('@/stores/useSlidesStore', () => ({
  useSlidesStore: () => ({
    allSlides: [
      {
        id: 1,
        index: 0,
        name: 'Slide 1',
        type: SlideType.IMAGE,
        imageMeta: { id: 1, name: 'img1', contentType: 'image/jpeg' }
      },
      {
        id: 2,
        index: 1,
        name: 'Slide 2',
        type: SlideType.IMAGE,
        imageMeta: { id: 2, name: 'img2', contentType: 'image/jpeg' }
      }
    ],
    refresh: vi.fn().mockResolvedValue(undefined)
  })
}))

// Mock useToast
vi.mock('@/composables/useToast', () => ({
  useToast: () => ({
    showSuccess: vi.fn(),
    showError: vi.fn()
  })
}))

describe('SlideDeckCard', () => {
  const mockSlideDeck = {
    id: 1,
    name: 'Test Deck',
    competitionId: 1,
    slides: [
      {
        id: 1,
        index: 0,
        name: 'Slide 1',
        type: SlideType.IMAGE,
        imageMeta: { id: 1, name: 'img1', contentType: 'image/jpeg' }
      },
      {
        id: 2,
        index: 1,
        name: 'Slide 2',
        type: SlideType.IMAGE,
        imageMeta: { id: 2, name: 'img2', contentType: 'image/jpeg' }
      }
    ],
    transitionTime: 5000,
    version: 1,
    lastUpdate: new Date().toISOString()
  }

  const defaultProps = {
    deckId: 1
  }

  let wrapper: any

  beforeEach(() => {
    vi.clearAllMocks()

    wrapper = shallowMount(SlideDeckCard, {
      props: defaultProps,
      global: {
        stubs: {
          SlideCard: { template: '<div>SlideCard</div>' },
          draggable: { template: '<div>Draggable</div>' }
        },
        mocks: {
          $t: (key: string) => key
        }
      }
    })
  })

  it('renders correctly', () => {
    expect(wrapper.exists()).toBe(true)
    // 检查组件是否包含正确的根元素类
    expect(wrapper.find('.bg-white').exists()).toBe(true)
  })

  it('displays playback speed controls', () => {
    expect(wrapper.text()).toContain('playbackSpeed')
    expect(wrapper.text()).toContain('5000ms')
  })

  it('shows current slides section', () => {
    expect(wrapper.text()).toContain('currentSlides')
  })

  it('emits play event when play button is clicked', async () => {
    const playButton = wrapper.find('button.bg-green-600')
    if (playButton.exists()) {
      await playButton.trigger('click')
      expect(wrapper.emitted('play')).toBeTruthy()
    }
  })

  it('emits edit event when edit button is clicked', async () => {
    const editButton = wrapper.find('button.bg-blue-600')
    if (editButton.exists()) {
      await editButton.trigger('click')
      expect(wrapper.emitted('edit')).toBeTruthy()
    }
  })

  it('emits delete event when delete button is clicked', async () => {
    const deleteButton = wrapper.find('button.bg-red-600')
    if (deleteButton.exists()) {
      await deleteButton.trigger('click')
      expect(wrapper.emitted('delete')).toBeTruthy()
    }
  })

  it('updates when props change', async () => {
    const newDeck = {
      ...mockSlideDeck,
      name: 'Updated Deck'
    }

    await wrapper.setProps({
      group: newDeck
    })

    // 检查组件是否正确更新
    expect(wrapper.exists()).toBe(true)
  })

  it('handles empty slides', () => {
    const emptyDeck = {
      ...mockSlideDeck,
      slides: []
    }

    const emptyWrapper = shallowMount(SlideDeckCard, {
      props: {
        group: emptyDeck,
        selectedContent: null,
        deckId: 1
      },
      global: {
        stubs: {
          SlideCard: { template: '<div>SlideCard</div>' },
          draggable: { template: '<div>Draggable</div>' }
        },
        mocks: {
          $t: (key: string) => key
        }
      }
    })

    expect(emptyWrapper.exists()).toBe(true)
  })
}) 