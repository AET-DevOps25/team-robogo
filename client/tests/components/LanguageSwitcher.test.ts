import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import LanguageSwitcher from '@/components/LanguageSwitcher.vue'

// Mock vue-i18n
const mockSetLocale = vi.fn()
const mockLocales = [
  { code: 'en', name: 'English' },
  { code: 'de', name: 'Deutsch' }
]

vi.mock('vue-i18n', () => ({
  useI18n: () => ({
    locale: { value: 'en' },
    locales: { value: mockLocales },
    setLocale: mockSetLocale
  })
}))

describe('LanguageSwitcher', () => {
  let wrapper: any

  beforeEach(() => {
    wrapper = mount(LanguageSwitcher)
  })

  it('renders correctly', () => {
    expect(wrapper.exists()).toBe(true)
    expect(wrapper.find('select').exists()).toBe(true)
  })

  it('displays all language options', () => {
    const options = wrapper.findAll('option')
    expect(options).toHaveLength(2)
    expect(options[0].text()).toContain('English')
    expect(options[1].text()).toContain('Deutsch')
  })

  it('shows current locale as selected', () => {
    const select = wrapper.find('select')
    expect(select.element.value).toBe('en')
  })

  it('calls setLocale when language is changed', async () => {
    const select = wrapper.find('select')
    await select.setValue('de')
    await select.trigger('change')
    expect(mockSetLocale).toHaveBeenCalledWith('de')
  })

  it('generates correct flag emoji for language codes', () => {
    const component = wrapper.vm
    expect(component.getFlagEmoji('en')).toBe('🇬🇧')
    expect(component.getFlagEmoji('de')).toBe('🇩🇪')
  })

  it('has correct CSS classes', () => {
    const select = wrapper.find('select')
    expect(select.classes()).toContain('language-select')
  })
})
