import { describe, it, expect, vi, beforeEach } from 'vitest'
import { shallowMount, type VueWrapper } from '@vue/test-utils'
import { nextTick } from 'vue'
import SlideDeckCard from '@/components/SlideDeckCard.vue'

const { mockSaveGroup, mockPlayGroup, mockReplaceGroup, mockRefresh } = vi.hoisted(() => ({
  mockSaveGroup: vi.fn(),
  mockPlayGroup: vi.fn(),
  mockReplaceGroup: vi.fn(),
  mockRefresh: vi.fn()
}))

vi.mock('@/composables/useSlides', () => ({
  useSlides: () => ({
    slides: [
      { id: 1, name: 'Slide 1', url: 'https://example.com/slide1.jpg' },
      { id: 2, name: 'Slide 2', url: 'https://example.com/slide2.jpg' },
      { id: 3, name: 'Slide 3', url: 'https://example.com/slide3.jpg' }
    ],
    refresh: mockRefresh
  })
}))

vi.mock('@/stores/useScreenStore', () => ({
  useScreenStore: () => ({
    playGroup: mockPlayGroup,
    replaceGroup: mockReplaceGroup,
    slideDecks: [{ id: 'Group-A', slideIds: [1, 2], speed: 5, version: 0 }]
  })
}))

vi.mock('@/services/groupService', () => ({
  saveGroup: mockSaveGroup
}))

// 模拟全局 alert 函数
vi.stubGlobal('alert', vi.fn())

describe('SlideDeckCard', () => {
  const mockSlideDeck = {
    id: 'Group-A',
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
    speed: 5,
    version: 0
  }

  const defaultProps = {
    group: mockSlideDeck,
    selectedContent: null,
    'slide-ids': [1, 2],
    speed: 5
  }

  let wrapper: VueWrapper<any>

  beforeEach(() => {
    vi.resetAllMocks()

    wrapper = shallowMount(SlideDeckCard, {
  it('renders correctly', () => {
    const wrapper = shallowMount(SlideDeckCard, {
      props: defaultProps,
      global: {
        stubs: {
          // 修复 SpeedControl 存根
          SpeedControl: {
            template: '<div>SpeedControl</div>',
            props: ['modelValue']
          },
          SlideCard: true,
          draggable: true
        }
      }
    })
  })

  it('renders correctly', () => {
    expect(wrapper.exists()).toBe(true)
    expect(wrapper.classes()).toContain('w-full')
  })

  it('displays group id', () => {
    expect(wrapper.find('h3').text()).toBe('Group-A')
  })

  it('calls playGroup when play button is clicked', async () => {
    await wrapper.find('button.bg-green-600').trigger('click')
    expect(mockPlayGroup).toHaveBeenCalledWith('Group-A')
  })

  it('updates group info after successful drag-and-drop', async () => {
    const savedResponse = {
      ...mockSlideDeck,
      slideIds: [2, 1],
      version: 1
    }
    mockSaveGroup.mockResolvedValue(savedResponse)

    wrapper.vm.editingGroup.slideIds = [2, 1]
    wrapper.vm.onDragEnd()
    await nextTick()

    // Check key fields, ignore version
    expect(mockSaveGroup).toHaveBeenCalledWith(
      expect.objectContaining({
        id: 'Group-A',
        slideIds: [2, 1]
      })
    )

    // Verify version update
    expect(wrapper.vm.editingGroup.version).toBe(1)
    expect(mockReplaceGroup).toHaveBeenCalledWith(
      expect.objectContaining({
        id: 'Group-A',
        slideIds: [2, 1],
        version: 1
      })
    )
  })

  it('rolls back state on drag failure', async () => {
    // Simulate save failure
    mockSaveGroup.mockRejectedValue(new Error('Save failed'))

    // Original state
    const originalSlideIds = [...wrapper.vm.editingGroup.slideIds]

    // Modify local state (simulate drag)c
    wrapper.vm.editingGroup.slideIds = [2, 1]

    // Trigger drag end event
    wrapper.vm.onDragEnd()
    await nextTick()

    // Check save function was called
    expect(mockSaveGroup).toHaveBeenCalled()

    // Check rollback to original state
    expect(wrapper.vm.editingGroup.slideIds).toEqual(originalSlideIds)

    // Check alert was called
    expect(alert).toHaveBeenCalled()
  })

  it('opens the add slide dialog', async () => {
    expect(wrapper.find('.fixed').exists()).toBe(false)
    await wrapper.find('.w-\\[300px\\]').trigger('click')
    expect(mockRefresh).toHaveBeenCalled()
    expect(wrapper.find('.fixed').exists()).toBe(true)
  })

  it('adds a slide to the group', async () => {
    await wrapper.find('.w-\\[300px\\]').trigger('click')
    wrapper.vm.selectedToAdd = 3
    wrapper.vm.confirmAdd()
    await nextTick()

    expect(wrapper.vm.slideIds).toEqual([1, 2, 3])
    expect(wrapper.vm.showDialog).toBe(false)
  })

  it('cancels adding a slide', async () => {
    await wrapper.find('.w-\\[300px\\]').trigger('click')
    wrapper.vm.selectedToAdd = 3
    wrapper.vm.cancelAdd()
    await nextTick()

    expect(wrapper.vm.slideIds).toEqual([1, 2])
    expect(wrapper.vm.showDialog).toBe(false)
    expect(wrapper.vm.selectedToAdd).toBeNull()
  })

  it('resets editing group when version updates', async () => {
    wrapper.vm.editingGroup.slideIds = [2, 1]
    await wrapper.setProps({
      group: {
        ...mockSlideDeck,
        version: 1
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

    expect(wrapper.vm.editingGroup.slideIds).toEqual([1, 2])
    expect(wrapper.vm.editingGroup.version).toBe(1)
  })

  it('saves speed when save button is clicked', async () => {
    const updatedGroup = {
      ...mockSlideDeck,
      speed: 42,
      version: 1
    }
    mockSaveGroup.mockResolvedValue(updatedGroup)

    // mock editing speed（v-model）
    wrapper.vm.speed = 42

    // saveSpeed 方法
    await wrapper.vm.saveSpeed()
    await nextTick()

    expect(mockSaveGroup).toHaveBeenCalledWith(
      expect.objectContaining({
        id: 'Group-A',
        speed: 42
      })
    )
    expect(mockReplaceGroup).toHaveBeenCalledWith(updatedGroup)
    expect(wrapper.vm.editingGroup.speed).toBe(42)
    expect(wrapper.vm.editingGroup.version).toBe(1)
  })
})
