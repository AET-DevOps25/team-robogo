// composables/useGroupVersionWatcher.ts
import { onMounted, onBeforeUnmount } from 'vue'
import { fetchGroupVersion, fetchGroups } from '@/services/groupService'
import { useScreenStore } from '@/stores/useScreenStore'

export function useGroupVersionWatcher(groupId: string, interval = 4000) {
  const store = useScreenStore()
  let timer: ReturnType<typeof setInterval> | null = null

  onMounted(() => {
    timer = setInterval(async () => {
      const local = store.slideGroups.find(g => g.id === groupId)
      if (!local) return

      try {
        const remoteVer = await fetchGroupVersion(groupId)
        if (remoteVer > local.version) {
          // 版本有变：重新拉整组数据
          const latest = (await fetchGroups()).find(g => g.id === groupId)
          latest && store.replaceGroup(latest)
        }
      } catch {/* 网络错误可忽略，下轮轮询再试 */}
    }, interval)
  })

  onBeforeUnmount(() => {
    timer && clearInterval(timer)
  })
}