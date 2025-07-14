import { describe, it, expect } from 'vitest'
import { fetchAllImageMetas, fetchImageBlobById, uploadImage } from '@/services/slideImageService'

// 集成测试：需保证后端 /api/proxy/slide-images 可访问

describe('slideImageService', () => {
  it('should fetch all image metas', async () => {
    const metas = await fetchAllImageMetas()
    expect(Array.isArray(metas)).toBe(true)
    if (metas.length > 0) {
      expect(metas[0]).toHaveProperty('id')
      expect(metas[0]).toHaveProperty('name')
      expect(metas[0]).toHaveProperty('contentType')
    }
  })

  it('should fetch image blob by id if exists', async () => {
    const metas = await fetchAllImageMetas()
    if (metas.length > 0) {
      const blob = await fetchImageBlobById(metas[0].id)
      expect(blob).toBeInstanceOf(Blob)
      expect(blob.size).toBeGreaterThan(0)
    } else {
      // 跳过
      expect(true).toBe(true)
    }
  })

  it('should upload image and return meta', async () => {
    const file = new File([new Uint8Array([1, 2, 3, 4])], 'test.png', { type: 'image/png' })
    const meta = await uploadImage(file)
    expect(meta).toHaveProperty('id')
    expect(meta).toHaveProperty('name')
    expect(meta.name).toBe('test.png')
    expect(meta.contentType).toBe('image/png')
  })
})
