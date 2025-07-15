import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createI18n } from 'vue-i18n'
import SlideCard from '@/components/SlideCard.vue'
import en from '@/i18n/locales/en.json'
import { SlideType } from '@/interfaces/types'

const i18n = createI18n({
  legacy: false,
  locale: 'en',
  messages: { en }
})

const mockItem = {
  id: 1,
  index: 0,
  name: 'Test Slide',
  type: SlideType.IMAGE,
  imageMeta: { id: 1, name: 'img', contentType: 'image/png' }
}

const factory = (selected = false) =>
  mount(SlideCard, {
    props: { item: mockItem, selected },
    global: { plugins: [i18n] }
  })

describe('SlideCard', () => {
  it('renders child component or fallback', () => {
    const wrapper = factory()
    expect(wrapper.text()).toContain(mockItem.name)
  })

  it('emits click', async () => {
    const wrapper = factory()
    await wrapper.trigger('click')
    expect(wrapper.emitted('click')).toHaveLength(1)
  })
})
