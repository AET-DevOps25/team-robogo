//This function handles uploaded slides (not slide deck!!)
import { ref, watch } from 'vue'
import type { ImageSlideMeta, LocalImageSlideUrl } from '@/interfaces/types'
import { fetchAllImageMetas, fetchImageBlobById } from '@/services/slideImageService'
export function useSlides() {
  // 本地 slides 响应式副本
  const allSlides = ref<LocalImageSlideUrl[]>([])
  // convert blob to url
  async function getAllSlidesUrl() {
    try {
      const slidesMeta = await fetchAllImageMetas() //get Meta data
      //temp array to store new loaded images
      const newSlides: LocalImageSlideUrl[] = []

      await Promise.all(
        slidesMeta.map(async meta => {
          try {
            const blob = await fetchImageBlobById(meta.id)
            const url = URL.createObjectURL(blob)
            newSlides.push({ id: meta.id, name: meta.name, url })
          } catch (error) {
            console.error(`failed to load image  ${meta.name} `, error)
            newSlides.push({ id: meta.id, name: meta.name, url: '' })
          }
        })
      )

      releaseOldResources()

      // update slides
      allSlides.value = newSlides
    } catch (error) {
      console.error('刷新图片失败:', error)
    }
  }
  function releaseOldResources() {
      allSlides.value.forEach(slide => {
        if (slide.url) {
          URL.revokeObjectURL(slide.url)
        }
      })
    }

    /**This function adds a slide in local slides list, 
     * first upload and post a slide to server, get blob from server, use blob to convert the new slide's url to replace previewUrl in LocalImageSlideUrl,
     * slides order is not important */
    function add(newSlide: LocalImageSlideUrl) {

      allSlides.value.push(newSlide)
    }

    function remove(slideId: number) {
      allSlides.value = allSlides.value.filter((s: LocalImageSlideUrl) => s.id !== slideId)
    }

  return { allSlides, getAllSlidesUrl, add, remove }
}
