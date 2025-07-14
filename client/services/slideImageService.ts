import { useAuthFetch } from '@/composables/useAuthFetch'
import type { ImageSlideMeta } from '@/interfaces/types'

const BASE_URL = '/api/proxy/slide-images'

/**
 * 获取所有图片元数据（不含二进制内容）
 */
export async function fetchAllImageMetas(): Promise<ImageSlideMeta[]> {
  const { authFetch } = useAuthFetch()
  return await authFetch<ImageSlideMeta[]>(BASE_URL)
}

/**
 * 获取图片二进制内容（可用于 <img :src="..." />）
 * 返回 Blob，可用 URL.createObjectURL(blob) 转为图片 src
 */
export async function fetchImageBlobById(id: string | number): Promise<Blob> {
  const { authFetch } = useAuthFetch()
  return await authFetch<Blob>(`${BASE_URL}/${id}`)
}

/**
 * 上传图片（multipart/form-data）
 * file: File 对象
 */
export async function uploadImage(file: File): Promise<ImageSlideMeta> {
  const { authFetch } = useAuthFetch()
  const formData = new FormData()
  formData.append('file', file)
  // 不能加 Content-Type，浏览器会自动设置
  return await authFetch<ImageSlideMeta>(BASE_URL, {
    method: 'POST',
    body: formData
  })
} 