import { describe, it, expect } from 'vitest'
import { shallowMount } from '@vue/test-utils'
import { defineComponent } from 'vue'
import SpeedControl from '@/components/SpeedControl.vue'

const SliderStub = defineComponent({
  name: 'Slider',
  props: ['modelValue', 'min', 'max'],
  emits: ['update:modelValue'],
  template: '<div />'
})

const factory = (value = 50) =>
  shallowMount(SpeedControl, {
    props: { modelValue: value },
    global: {
      stubs: {
        ClientOnly: { template: '<slot />' },
        'client-only': { template: '<slot />' },

        slider: SliderStub,
        Slider: SliderStub
      }
    }
  })

describe('SpeedControl', () => {
  it('renders and shows current speed', () => {
    const w = factory(42)
    expect(w.text()).toContain('42')
  })

  it('emits update:modelValue when slider changes', async () => {
    const w = factory(10)

    const slider = w.findComponent(SliderStub)
    expect(slider.exists()).toBe(true)

    await slider.vm.$emit('update:modelValue', 80)

    expect(w.emitted('update:modelValue')![0][0]).toBe(80)

    expect(w.text()).toContain('80')
  })
})
