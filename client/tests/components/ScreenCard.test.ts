// tests/components/ScreenCard.test.ts
import { describe, it, expect } from 'vitest'
import { shallowMount } from '@vue/test-utils'
import { defineComponent, h } from 'vue'
import ScreenCard from '@/components/ScreenCard.vue'

const SelectMenuStub = defineComponent({
  name: 'SelectMenu', // 组件名
  emits: ['update:model-value'], // 声明会发出的事件
  setup(_, { slots }) {
    return () => h('div', slots.default?.())
  }
})
const mockSlides = [{ id: 1, name: 'Slide 1', url: 'https://example.com/slide1.jpg' }]
const mockGroups = [{ id: 'G-1', slideIds: [1], speed: 50, lastResetAt: Date.now() }]
const baseScreen = {
  id: 'SCR-1',
  name: 'Hall Display',
  status: 'ONLINE',
  groupId: 'G-1',
  currentContent: '1',
  thumbnailUrl: '',
  urlPath: '/screens/1'
}

const factory = (override: Partial<typeof baseScreen> = {}) =>
  shallowMount(ScreenCard, {
    props: {
      screen: { ...baseScreen, ...override },
      slideGroups: mockGroups,
      allSlides: mockSlides
    },
    global: {
      stubs: {
        ClientOnly: { template: '<slot />' },
        'client-only': { template: '<slot />' },

        /* 👇 用刚才的组件替换 Nuxt UI 的 <select-menu> */
        'select-menu': SelectMenuStub
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
    const img = factory().find('img')
    expect(img.exists()).toBe(true)
  })

  it('shows placeholder when slide not found', () => {
    const w = factory({ currentContent: '999' })
    expect(w.find('img').exists()).toBe(false)
    expect(w.text()).toContain('No Content')
  })

  it('emits updateGroup when select menu changes', async () => {
    const w = factory()

    const select = w.findComponent(SelectMenuStub) // VueWrapper ✔
    expect(select.exists()).toBe(true)

    await select.vm.$emit('update:model-value', 'G-NEW')

    expect(w.emitted('updateGroup')![0][0]).toMatchObject({
      id: 'SCR-1',
      groupId: 'G-NEW'
    })
  })

  it('emits requestDelete on delete button click', async () => {
    const w = factory()
    await w.find('button').trigger('click')
    expect(w.emitted('requestDelete')![0][0]).toMatchObject({ id: 'SCR-1' })
  })
})
