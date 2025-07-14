<template>
  <div class="score-slide-card">
    <template v-if="item.type === 'SCORE' && 'scores' in item">
      <div class="font-semibold text-gray-900 dark:text-white mb-2">{{ item.name }}</div>
      <div v-if="item.category?.categoryScoring" class="text-base font-bold mb-2">
        {{ t('categoryScoring.' + item.category.categoryScoring) }}
      </div>
      <div class="overflow-x-auto w-full">
        <table class="w-full text-sm border-separate border-spacing-0 rounded-lg shadow">
          <thead>
            <tr>
              <th
                class="bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200 font-semibold px-4 py-2 first:rounded-tl-lg last:rounded-tr-lg border-b"
              >
                {{ t('teamName') }}
              </th>
              <th
                class="bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200 font-semibold px-4 py-2 border-b"
              >
                {{ t('scoreUnit') }}
              </th>
              <th
                class="bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200 font-semibold px-4 py-2 border-b"
              >
                {{ t('time') }}
              </th>
              <th
                class="bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200 font-semibold px-4 py-2 border-b"
              >
                {{ t('category') }}
              </th>
              <th
                class="bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200 font-semibold px-4 py-2 border-b"
              >
                {{ t('rank') }}
              </th>
              <th
                class="bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200 font-semibold px-4 py-2 last:rounded-tr-lg border-b"
              >
                {{ t('categoryScoring') }}
              </th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="score in item.scores"
              :key="score.team.id"
              :class="[
                'transition-colors',
                score.highlight
                  ? 'bg-yellow-100 dark:bg-yellow-900/80'
                  : 'hover:bg-gray-50 dark:hover:bg-gray-800',
                'border-b last:border-b-0'
              ]"
            >
              <td class="px-4 py-2 border-b">{{ score.team.name }}</td>
              <td class="px-4 py-2 border-b">{{ score.points }}</td>
              <td class="px-4 py-2 border-b">{{ score.time }}</td>
              <td class="px-4 py-2 border-b">{{ item.category?.name }}</td>
              <td class="px-4 py-2 border-b">{{ score.rank }}</td>
              <td class="px-4 py-2 border-b">
                <span v-if="item.category?.categoryScoring">
                  {{ t('categoryScoring.' + item.category.categoryScoring) }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>
    <template v-else>
      <div class="text-xs text-gray-400 mt-1">{{ t('notScoreType') }}</div>
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
    align-items: flex-start;
  }

  table {
    border-radius: 0.5rem;
    overflow: hidden;
    box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.04);
  }

  th,
  td {
    white-space: nowrap;
  }
</style>
