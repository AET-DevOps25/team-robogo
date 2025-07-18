// tests/components/ScreenCard.test.ts
import { describe, it, expect, vi } from 'vitest'
import { shallowMount } from '@vue/test-utils'
import ScreenCard from '@/components/ScreenCard.vue'
import type { SlideDeck, ScreenContent } from '@/interfaces/types'
import { SlideType } from '@/interfaces/types'
// Mock services
vi.mock('@/services/screenService', () => ({
  assignSlideDeck: vi.fn().mockResolvedValue(undefined),
  updateScreen: vi.fn().mockResolvedValue(undefined)
}))

vi.mock('@/stores/useDeckStore', () => ({
  useDeckStore: () => ({
    currentDeckId: 1,
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
          imageMeta: { id: 1, name: 'Slide 1', contentType: 'image/jpeg' }
        }
      ],
      transitionTime: 5000,
      version: 1,
      lastUpdate: new Date().toISOString()
    },
    currentSlideIndex: 0,
    setCurrentDeck: vi.fn(),
    stopVersionCheck: vi.fn()
  })
}))

vi.mock('@/composables/useToast', () => ({
  useToast: () => ({
    showError: vi.fn(),
    showSuccess: vi.fn()
  })
}))

const mockSlides = [
  {
    id: 1,
    index: 0,
    name: 'Slide 1',
    type: SlideType.IMAGE,
    imageMeta: { id: 1, name: 'Slide 1', contentType: 'image/jpeg' }
  }
]

const mockSlideDeck: SlideDeck = {
  id: 1,
  name: 'Test Deck',
  competitionId: 1,
  slides: mockSlides,
  transitionTime: 5000,
  version: 1,
  lastUpdate: new Date().toISOString()
}

const mockSlideDeck2: SlideDeck = {
  id: 2,
  name: 'Test Deck 2',
  competitionId: 1,
  slides: mockSlides,
  transitionTime: 5000,
  version: 1,
  lastUpdate: new Date().toISOString()
}

const mockGroups = [mockSlideDeck, mockSlideDeck2]

const baseScreen: ScreenContent = {
  id: 1,
  name: 'Hall Display',
  status: 'ONLINE',
  slideDeck: mockSlideDeck
}

vi.mock('@/composables/useToast', () => ({
  useToast: () => ({
    showError: vi.fn(),
    showSuccess: vi.fn()
  })
}))

const factory = (override: Partial<typeof baseScreen> = {}) =>
  shallowMount(ScreenCard, {
    props: {
      screen: { ...baseScreen, ...override },
      slideDecks: mockGroups
    },
    global: {
      stubs: {
        ClientOnly: { template: '<slot />' },
        'client-only': { template: '<slot />' },
        SlideCard: { template: '<div class="slide-card">Mock Slide</div>' }
      },
      mocks: {
        $t: (key: string) => key
      }
    }
  })

describe('ScreenCard', () => {
  it('mounts successfully', () => {
    expect(factory().exists()).toBe(true)
  })

  it('renders screen name', () => {
    expect(factory().text()).toContain('Hall Display')
  })

  it('renders current slide image when present', () => {
    const wrapper = factory()
    const slideCard = wrapper.find('.slide-card')
    expect(slideCard.exists()).toBe(true)
    expect(slideCard.text()).toContain('Mock Slide')
  })

  it('shows placeholder when slide not found', () => {
    const w = factory({ slideDeck: null })
    expect(w.findComponent({ name: 'SlideCard' }).exists()).toBe(false)
    expect(w.text()).toContain('No Content')
  })

  it('emits deckAssigned when select menu changes', async () => {
    const w = factory()

    const select = w.find('select')
    expect(select.exists()).toBe(true)

    await select.setValue('2') // 选择一个新的deck ID

    expect(w.emitted('deckAssigned')).toBeTruthy()
  })

  it('emits requestDelete on delete button click', async () => {
    const w = factory()
    await w.find('button').trigger('click')
    expect(w.emitted('requestDelete')![0][0]).toMatchObject({ id: 1 })
  })
})
