<template>
  <div class="max-w-4xl mx-auto bg-white dark:bg-gray-800 rounded-xl shadow-md p-6 space-y-6">
    <!-- 标题 -->
    <div class="flex items-center justify-between">
      <h2 class="text-2xl font-bold text-gray-900 dark:text-white">{{ $t('scoreManagement') }}</h2>
      <div class="flex gap-2">
        <button
          class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
          @click="showChangeTeamCategoryDialog = true"
        >
          {{ $t('changeTeamCategory') }}
        </button>
        <button
          class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          @click="showAddScoreDialog = true"
        >
          {{ $t('addScore') }}
        </button>
      </div>
    </div>

    <!-- 分类选择 -->
    <div class="space-y-4">
      <h3 class="text-lg font-semibold text-gray-700 dark:text-gray-300">
        {{ $t('selectCategory') }}
      </h3>
      <select
        v-model="selectedCategoryId"
        class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        @change="loadScoresByCategory"
      >
        <option value="">{{ $t('selectCategory') }}</option>
        <option v-for="category in categories" :key="category.id" :value="category.id">
          {{ category.name }}
        </option>
      </select>
    </div>

    <!-- 分数列表 -->
    <div v-if="selectedCategoryId" class="space-y-4">
      <h3 class="text-lg font-semibold text-gray-700 dark:text-gray-300">
        {{ $t('currentScores') }}
      </h3>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div
          v-for="score in scores"
          :key="`${score.team?.id || score.id}-${score.points}`"
          class="p-4 border border-gray-200 dark:border-gray-600 rounded-lg hover:shadow-md transition-shadow"
          :class="{ 'border-green-500 bg-green-50 dark:bg-green-900/20': score.highlight }"
        >
          <div class="flex items-center justify-between mb-2">
            <span class="font-semibold text-gray-900 dark:text-white">
              {{ score.team?.name || 'Unknown Team' }}
            </span>
            <span
              v-if="score.rank"
              class="px-2 py-1 text-xs font-bold rounded-full"
              :class="
                score.rank === 1
                  ? 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-200'
                  : 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200'
              "
            >
              {{ $t('rank') }} {{ score.rank }}
            </span>
          </div>
          <div class="space-y-1">
            <div class="flex justify-between">
              <span class="text-sm text-gray-600 dark:text-gray-400">{{ $t('points') }}:</span>
              <span class="font-semibold text-gray-900 dark:text-white">{{ score.points }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-sm text-gray-600 dark:text-gray-400">{{ $t('time') }}:</span>
              <span class="font-semibold text-gray-900 dark:text-white">{{ score.time }}秒</span>
            </div>
          </div>
          <div class="mt-3 flex gap-2">
            <button
              class="flex-1 px-3 py-1 text-sm bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors"
              @click="editScore(score)"
            >
              {{ $t('editScore') }}
            </button>
            <button
              class="flex-1 px-3 py-1 text-sm bg-red-500 text-white rounded hover:bg-red-600 transition-colors"
              @click="deleteScore(score)"
            >
              {{ $t('delete') }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加/编辑分数对话框 -->
    <teleport to="body">
      <div
        v-if="showAddScoreDialog || showEditScoreDialog"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40 dark:bg-black dark:bg-opacity-60"
      >
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-lg p-6 max-w-md w-full mx-4">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-bold text-gray-900 dark:text-white">
              {{ showEditScoreDialog ? $t('editScore') : $t('addScore') }}
            </h3>
            <button
              class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
              @click="closeDialog"
            >
              <span class="text-2xl">×</span>
            </button>
          </div>

          <form class="space-y-4" @submit.prevent="submitScore">
            <!-- 选择团队 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                {{ $t('selectTeam') }}
              </label>
              <select
                v-model="formData.teamId"
                class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              >
                <option value="">{{ $t('selectTeam') }}</option>
                <option v-for="team in teams" :key="team.id" :value="team.id">
                  {{ team.name }}
                </option>
              </select>
            </div>

            <!-- 分数输入 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                {{ $t('points') }}
              </label>
              <input
                v-model.number="formData.points"
                type="number"
                step="0.1"
                min="0"
                class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                :placeholder="$t('points')"
                required
              />
            </div>

            <!-- 时间输入 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                {{ $t('time') }}
              </label>
              <input
                v-model.number="formData.time"
                type="number"
                min="0"
                class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                :placeholder="$t('time')"
                required
              />
            </div>

            <!-- 按钮组 -->
            <div class="flex gap-3 pt-4">
              <button
                type="button"
                class="flex-1 px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
                @click="closeDialog"
              >
                {{ $t('cancel') }}
              </button>
              <button
                type="submit"
                class="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                :disabled="isSubmitting"
              >
                {{
                  isSubmitting ? $t('submitting') : showEditScoreDialog ? $t('update') : $t('add')
                }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </teleport>

    <!-- 改变团队分类对话框 -->
    <teleport to="body">
      <div
        v-if="showChangeTeamCategoryDialog"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40 dark:bg-black dark:bg-opacity-60"
      >
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-lg p-6 max-w-md w-full mx-4">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-bold text-gray-900 dark:text-white">
              {{ $t('changeTeamCategory') }}
            </h3>
            <button
              class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
              @click="closeChangeTeamCategoryDialog"
            >
              <span class="text-2xl">×</span>
            </button>
          </div>

          <form class="space-y-4" @submit.prevent="submitChangeTeamCategory">
            <!-- 选择团队 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                {{ $t('selectTeam') }}
              </label>
              <select
                v-model="changeTeamCategoryData.teamId"
                class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              >
                <option value="">{{ $t('selectTeam') }}</option>
                <option v-for="team in teams" :key="team.id" :value="team.id">
                  {{ team.name }}
                </option>
              </select>
            </div>

            <!-- 选择新分类 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                {{ $t('selectNewCategory') }}
              </label>
              <select
                v-model="changeTeamCategoryData.categoryId"
                class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              >
                <option value="">{{ $t('selectNewCategory') }}</option>
                <option v-for="category in categories" :key="category.id" :value="category.id">
                  {{ category.name }}
                </option>
              </select>
            </div>

            <!-- 按钮组 -->
            <div class="flex gap-3 pt-4">
              <button
                type="button"
                class="flex-1 px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
                @click="closeChangeTeamCategoryDialog"
              >
                {{ $t('cancel') }}
              </button>
              <button
                type="submit"
                class="flex-1 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
                :disabled="isSubmitting"
              >
                {{ isSubmitting ? $t('submitting') : $t('change') }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </teleport>

    <!-- 删除确认对话框 -->
    <teleport to="body">
      <div
        v-if="showDeleteConfirm"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40 dark:bg-black dark:bg-opacity-60"
      >
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-lg p-6 max-w-md w-full mx-4">
          <h3 class="text-lg font-bold text-gray-900 dark:text-white mb-4">
            {{ $t('confirmDeleteScore') }}
          </h3>
          <p class="text-gray-600 dark:text-gray-400 mb-6">
            {{ $t('areYouSureDeleteScore') }} {{ scoreToDelete?.team?.name || 'Unknown Team' }}？{{
              $t('confirmDelete')
            }}
          </p>
          <div class="flex gap-3">
            <button
              class="flex-1 px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
              @click="showDeleteConfirm = false"
            >
              {{ $t('cancel') }}
            </button>
            <button
              class="flex-1 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
              :disabled="isSubmitting"
              @click="confirmDelete"
            >
              {{ isSubmitting ? $t('deleting') : $t('delete') }}
            </button>
          </div>
        </div>
      </div>
    </teleport>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted, reactive } from 'vue'
  import { useI18n } from 'vue-i18n'
  import { fetchTeams, updateTeamCategory } from '@/services/teamService'
  import { fetchCategories } from '@/services/categoryService'
  import {
    addScore,
    updateScore,
    deleteScore as deleteScoreApi,
    fetchScoresByCategory
  } from '@/services/scoreService'
  import type { Team, Score, Category } from '@/interfaces/types'
  import { useToast } from '@/composables/useToast'

  const { t } = useI18n()
  const { showSuccess, showError } = useToast()

  // 状态管理
  const categories = ref<Category[]>([])
  const teams = ref<Team[]>([])
  const scores = ref<Score[]>([])
  const selectedCategoryId = ref<string>('')

  // 对话框状态
  const showAddScoreDialog = ref(false)
  const showEditScoreDialog = ref(false)
  const showDeleteConfirm = ref(false)
  const showChangeTeamCategoryDialog = ref(false)
  const isSubmitting = ref(false)

  // 表单数据
  const formData = reactive({
    teamId: '',
    points: 0,
    time: 0
  })

  const changeTeamCategoryData = reactive({
    teamId: '',
    categoryId: ''
  })

  const editingScore = ref<Score | null>(null)
  const scoreToDelete = ref<Score | null>(null)

  // 生命周期
  onMounted(async () => {
    await loadCategories()
    await loadTeams()
  })

  // 数据加载方法
  async function loadCategories() {
    try {
      categories.value = await fetchCategories()
    } catch (error) {
      showError(t('loadCategoriesFailed'))
    }
  }

  async function loadTeams() {
    try {
      teams.value = await fetchTeams()
    } catch (error) {
      showError(t('loadTeamsFailed'))
    }
  }

  async function loadScoresByCategory() {
    if (!selectedCategoryId.value) {
      scores.value = []
      return
    }

    try {
      const categoryId = parseInt(selectedCategoryId.value)
      scores.value = await fetchScoresByCategory(categoryId)
    } catch (error) {
      scores.value = []
      showError(t('loadScoresFailed'))
    }
  }

  // 表单操作方法
  function editScore(score: Score) {
    editingScore.value = score
    formData.teamId = score.team?.id.toString() || ''
    formData.points = score.points
    formData.time = score.time
    showEditScoreDialog.value = true
  }

  function deleteScore(score: Score) {
    scoreToDelete.value = score
    showDeleteConfirm.value = true
  }

  async function submitScore() {
    if (!formData.teamId) {
      showError(t('selectTeam'))
      return
    }

    isSubmitting.value = true
    try {
      const teamId = parseInt(formData.teamId)
      const selectedTeam = teams.value.find(t => t.id === teamId)

      if (!selectedTeam) {
        showError(t('teamNotFound'))
        return
      }

      const scoreData: Partial<Score> = {
        team: selectedTeam,
        points: formData.points,
        time: formData.time
      }

      if (showEditScoreDialog.value && editingScore.value?.id) {
        await updateScore(editingScore.value.id, scoreData)
        showSuccess(t('scoreUpdated'))
      } else {
        await addScore(scoreData as Score)
        showSuccess(t('scoreAdded'))
      }

      closeDialog()
      await loadScoresByCategory()
    } catch (error) {
      showError(t('submitFailed'))
    } finally {
      isSubmitting.value = false
    }
  }

  async function submitChangeTeamCategory() {
    if (!changeTeamCategoryData.teamId || !changeTeamCategoryData.categoryId) {
      showError(t('selectTeamAndCategory'))
      return
    }

    isSubmitting.value = true
    try {
      const teamId = parseInt(changeTeamCategoryData.teamId)
      const categoryId = parseInt(changeTeamCategoryData.categoryId)

      await updateTeamCategory(teamId, categoryId)
      showSuccess(t('changeSuccess'))
      closeChangeTeamCategoryDialog()

      // 重新加载teams和scores
      await loadTeams()
      if (selectedCategoryId.value) {
        await loadScoresByCategory()
      }
    } catch (error) {
      showError(t('changeFailed'))
    } finally {
      isSubmitting.value = false
    }
  }

  async function confirmDelete() {
    if (!scoreToDelete.value?.id) return

    isSubmitting.value = true
    try {
      await deleteScoreApi(scoreToDelete.value.id)
      showSuccess(t('scoreDeleted'))
      showDeleteConfirm.value = false
      scoreToDelete.value = null
      await loadScoresByCategory()
    } catch (error) {
      showError(t('deleteFailed'))
    } finally {
      isSubmitting.value = false
    }
  }

  function closeDialog() {
    showAddScoreDialog.value = false
    showEditScoreDialog.value = false
    editingScore.value = null
    formData.teamId = ''
    formData.points = 0
    formData.time = 0
  }

  function closeChangeTeamCategoryDialog() {
    showChangeTeamCategoryDialog.value = false
    changeTeamCategoryData.teamId = ''
    changeTeamCategoryData.categoryId = ''
  }
</script>
