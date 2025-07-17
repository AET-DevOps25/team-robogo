<template>
  <div class="max-w-4xl mx-auto bg-white dark:bg-gray-800 rounded-xl shadow-md p-6 space-y-6">
    <!-- 标题 -->
    <div class="flex items-center justify-between">
      <h2 class="text-2xl font-bold text-gray-900 dark:text-white">{{ $t('teamManagement') }}</h2>
      <div class="flex gap-2">
        <button
          class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          @click="showAddTeamDialog = true"
        >
          {{ $t('addTeam') }}
        </button>
        <button
          class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
          @click="showAddCategoryDialog = true"
        >
          {{ $t('addCategory') }}
        </button>
      </div>
    </div>

    <!-- 团队列表 -->
    <div class="space-y-4">
      <h3 class="text-lg font-semibold text-gray-700 dark:text-gray-300">
        {{ $t('teams') }}
      </h3>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div
          v-for="team in teams"
          :key="team.id"
          class="p-4 border border-gray-200 dark:border-gray-600 rounded-lg hover:shadow-md transition-shadow"
        >
          <div class="flex items-center justify-between mb-2">
            <span class="font-semibold text-gray-900 dark:text-white">
              {{ team.name }}
            </span>
            <span class="text-sm text-gray-500 dark:text-gray-400">
              {{ getTeamCategoryName(team) }}
            </span>
          </div>
          <div class="mt-3 flex gap-2">
            <button
              class="flex-1 px-3 py-1 text-sm bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors"
              @click="editTeam(team)"
            >
              {{ $t('edit') }}
            </button>
            <button
              class="flex-1 px-3 py-1 text-sm bg-red-500 text-white rounded hover:bg-red-600 transition-colors"
              @click="deleteTeam(team)"
            >
              {{ $t('delete') }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 分类列表 -->
    <div class="space-y-4">
      <h3 class="text-lg font-semibold text-gray-700 dark:text-gray-300">
        {{ $t('categories') }}
      </h3>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div
          v-for="category in categories"
          :key="category.id"
          class="p-4 border border-gray-200 dark:border-gray-600 rounded-lg hover:shadow-md transition-shadow"
        >
          <div class="flex items-center justify-between mb-2">
            <span class="font-semibold text-gray-900 dark:text-white">
              {{ category.name }}
            </span>
            <span class="text-sm text-gray-500 dark:text-gray-400">
              {{ getCategoryTeamCount(category) }} {{ $t('teams') }}
            </span>
          </div>
          <div class="mt-3 flex gap-2">
            <button
              class="flex-1 px-3 py-1 text-sm bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors"
              @click="editCategory(category)"
            >
              {{ $t('edit') }}
            </button>
            <button
              class="flex-1 px-3 py-1 text-sm bg-red-500 text-white rounded hover:bg-red-600 transition-colors"
              @click="deleteCategory(category)"
            >
              {{ $t('delete') }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加/编辑团队对话框 -->
    <teleport to="body">
      <div
        v-if="showAddTeamDialog || showEditTeamDialog"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40 dark:bg-black dark:bg-opacity-60"
      >
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-lg p-6 max-w-md w-full mx-4">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-bold text-gray-900 dark:text-white">
              {{ showEditTeamDialog ? $t('editTeam') : $t('addTeam') }}
            </h3>
            <button
              class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
              @click="closeTeamDialog"
            >
              <span class="text-2xl">×</span>
            </button>
          </div>

          <form class="space-y-4" @submit.prevent="submitTeam">
            <!-- 团队名称 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                {{ $t('teamName') }}
              </label>
              <input
                v-model="teamFormData.name"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                :placeholder="$t('teamName')"
                required
              />
            </div>

            <!-- 选择分类 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                {{ $t('selectCategory') }}
              </label>
              <select
                v-model="teamFormData.categoryId"
                class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              >
                <option value="">{{ $t('selectCategory') }}</option>
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
                @click="closeTeamDialog"
              >
                {{ $t('cancel') }}
              </button>
              <button
                type="submit"
                class="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                :disabled="isSubmitting"
              >
                {{
                  isSubmitting ? $t('submitting') : showEditTeamDialog ? $t('update') : $t('add')
                }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </teleport>

    <!-- 添加/编辑分类对话框 -->
    <teleport to="body">
      <div
        v-if="showAddCategoryDialog || showEditCategoryDialog"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40 dark:bg-black dark:bg-opacity-60"
      >
        <div class="bg-white dark:bg-gray-800 rounded-lg shadow-lg p-6 max-w-md w-full mx-4">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-bold text-gray-900 dark:text-white">
              {{ showEditCategoryDialog ? $t('editCategory') : $t('addCategory') }}
            </h3>
            <button
              class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
              @click="closeCategoryDialog"
            >
              <span class="text-2xl">×</span>
            </button>
          </div>

          <form class="space-y-4" @submit.prevent="submitCategory">
            <!-- 分类名称 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                {{ $t('categoryName') }}
              </label>
              <input
                v-model="categoryFormData.name"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                :placeholder="$t('categoryName')"
                required
              />
            </div>

            <!-- 按钮组 -->
            <div class="flex gap-3 pt-4">
              <button
                type="button"
                class="flex-1 px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
                @click="closeCategoryDialog"
              >
                {{ $t('cancel') }}
              </button>
              <button
                type="submit"
                class="flex-1 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
                :disabled="isSubmitting"
              >
                {{
                  isSubmitting ? $t('submitting') : showEditCategoryDialog ? $t('update') : $t('add')
                }}
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
            {{ $t('confirmDelete') }}
          </h3>
          <p class="text-gray-600 dark:text-gray-400 mb-6">
            {{ $t('areYouSureDelete') }} {{ itemToDelete?.name || 'Unknown Item' }}？{{
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
  import { fetchTeams, createTeam, updateTeamCategory, deleteTeam } from '@/services/teamService'
  import { fetchCategories, createCategory, deleteCategory } from '@/services/categoryService'
  import type { Team, Category } from '@/interfaces/types'

  const { t } = useI18n()

  // 状态管理
  const teams = ref<Team[]>([])
  const categories = ref<Category[]>([])

  // 对话框状态
  const showAddTeamDialog = ref(false)
  const showEditTeamDialog = ref(false)
  const showAddCategoryDialog = ref(false)
  const showEditCategoryDialog = ref(false)
  const showDeleteConfirm = ref(false)
  const isSubmitting = ref(false)

  // 表单数据
  const teamFormData = reactive({
    name: '',
    categoryId: ''
  })

  const categoryFormData = reactive({
    name: ''
  })

  const editingTeam = ref<Team | null>(null)
  const editingCategory = ref<Category | null>(null)
  const itemToDelete = ref<Team | Category | null>(null)
  const deleteType = ref<'team' | 'category'>('team')

  // 生命周期
  onMounted(async () => {
    await loadData()
  })

  // 数据加载方法
  async function loadData() {
    try {
      await Promise.all([loadTeams(), loadCategories()])
    } catch (error) {
      console.error('Failed to load data:', error)
    }
  }

  async function loadTeams() {
    try {
      teams.value = await fetchTeams()
    } catch (error) {
      console.error('Failed to load teams:', error)
    }
  }

  async function loadCategories() {
    try {
      categories.value = await fetchCategories()
    } catch (error) {
      console.error('Failed to load categories:', error)
    }
  }

  // 辅助方法
  function getTeamCategoryName(team: Team): string {
    // 这里需要根据实际的Team数据结构来实现
    return 'Unknown Category'
  }

  function getCategoryTeamCount(category: Category): number {
    // 这里需要根据实际的Category数据结构来实现
    return 0
  }

  // 团队操作方法
  function editTeam(team: Team) {
    editingTeam.value = team
    teamFormData.name = team.name
    teamFormData.categoryId = '' // 需要根据实际数据结构设置
    showEditTeamDialog.value = true
  }

  function deleteTeam(team: Team) {
    itemToDelete.value = team
    deleteType.value = 'team'
    showDeleteConfirm.value = true
  }

  async function submitTeam() {
    if (!teamFormData.name || !teamFormData.categoryId) {
      return
    }

    isSubmitting.value = true
    try {
      if (showEditTeamDialog.value && editingTeam.value?.id) {
        // 更新团队分类
        await updateTeamCategory(editingTeam.value.id, parseInt(teamFormData.categoryId))
      } else {
        // 创建新团队
        await createTeam({ name: teamFormData.name })
      }

      closeTeamDialog()
      await loadTeams()
    } catch (error) {
      console.error('Failed to submit team:', error)
    } finally {
      isSubmitting.value = false
    }
  }

  function closeTeamDialog() {
    showAddTeamDialog.value = false
    showEditTeamDialog.value = false
    editingTeam.value = null
    teamFormData.name = ''
    teamFormData.categoryId = ''
  }

  // 分类操作方法
  function editCategory(category: Category) {
    editingCategory.value = category
    categoryFormData.name = category.name
    showEditCategoryDialog.value = true
  }

  function deleteCategory(category: Category) {
    itemToDelete.value = category
    deleteType.value = 'category'
    showDeleteConfirm.value = true
  }

  async function submitCategory() {
    if (!categoryFormData.name) {
      return
    }

    isSubmitting.value = true
    try {
      if (showEditCategoryDialog.value && editingCategory.value?.id) {
        // 更新分类
        // await updateCategory(editingCategory.value.id, { name: categoryFormData.name })
      } else {
        // 创建新分类
        await createCategory({ name: categoryFormData.name })
      }

      closeCategoryDialog()
      await loadCategories()
    } catch (error) {
      console.error('Failed to submit category:', error)
    } finally {
      isSubmitting.value = false
    }
  }

  function closeCategoryDialog() {
    showAddCategoryDialog.value = false
    showEditCategoryDialog.value = false
    editingCategory.value = null
    categoryFormData.name = ''
  }

  // 删除确认
  async function confirmDelete() {
    if (!itemToDelete.value?.id) return

    isSubmitting.value = true
    try {
      if (deleteType.value === 'team') {
        await deleteTeam(itemToDelete.value.id)
      } else {
        await deleteCategory(itemToDelete.value.id)
      }

      showDeleteConfirm.value = false
      itemToDelete.value = null
      await loadData()
    } catch (error) {
      console.error('Failed to delete item:', error)
    } finally {
      isSubmitting.value = false
    }
  }
</script> 