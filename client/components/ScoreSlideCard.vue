<template>
  <div class="score-slide-card w-full">
    <template v-if="item.type === 'SCORE' && 'scores' in item">
      <div class="font-semibold text-gray-900 dark:text-white mb-4 text-center text-lg">
        {{ item.name }}
      </div>
      <div class="overflow-x-auto w-full">
        <table class="w-full text-sm border-separate border-spacing-0 rounded-lg shadow-lg">
          <thead>
            <tr>
              <th
                class="bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200 font-semibold px-6 py-3 first:rounded-tl-lg last:rounded-tr-lg border-b"
              >
                {{ t('teamName') }}
              </th>
              <th
                class="bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200 font-semibold px-6 py-3 border-b"
              >
                {{ t('scoreUnit') }}
              </th>
              <th
                class="bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200 font-semibold px-6 py-3 border-b"
              >
                {{ t('time') }}
              </th>
              <th
                class="bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200 font-semibold px-6 py-3 border-b"
              >
                {{ t('category') }}
              </th>
              <th
                class="bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200 font-semibold px-6 py-3 last:rounded-tr-lg border-b"
              >
                {{ t('rank') }}
              </th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(score, index) in item.scores"
              :key="score.team?.id || index"
              :class="[
                'transition-colors',
                score.highlight
                  ? 'bg-yellow-100 dark:bg-yellow-900/80 hover:bg-yellow-200 dark:hover:bg-yellow-800'
                  : 'hover:bg-gray-50 dark:hover:bg-gray-800',
                'border-b last:border-b-0'
              ]"
            >
              <td class="px-6 py-3 border-b">{{ score.team?.name || '' }}</td>
              <td class="px-6 py-3 border-b">{{ score.points }}</td>
              <td class="px-6 py-3 border-b">{{ score.time }}</td>
              <td class="px-6 py-3 border-b">{{ item.category?.name }}</td>
              <td class="px-6 py-3 border-b">{{ score.rank }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>
    <template v-else>
      <div class="text-center text-gray-400 pt-8 pb-8">
        <div class="text-2xl mb-2">📊</div>
        <div>{{ t('notScoreType') }}</div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
  import { useI18n } from 'vue-i18n'
  import type { SlideItem } from '@/interfaces/types'

  defineProps<{ item: SlideItem }>()
  const { t } = useI18n()
</script>

<style scoped>
  .score-slide-card {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 100%;
  }

  table {
    border-radius: 0.5rem;
    overflow: hidden;
    box-shadow: 0 4px 12px 0 rgba(0, 0, 0, 0.1);
  }

  th,
  td {
    white-space: nowrap;
  }
</style>
