<script setup>
import { ref } from 'vue'

const testResponse = ref('') // display Returned Text from API

const callTestApi = async () => {
  try {
    const res = await fetch('/api/test')
    const text = await res.text()
    testResponse.value = text
  } catch (error) {
    console.error('API error', error)
    testResponse.value = 'Error calling API'
  }
}
</script>


<template>
  <div class="p-6 space-y-6">
    <!--Screens Preview -->
    <section>
      <h2 class="text-2xl font-bold mb-4">Screens Preview</h2>
      <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
        <ScreenCard v-for="screen in screens" :key="screen.id" :screen="screen" />
      </div>
    </section>

    <section class="grid grid-cols-1 md:grid-cols-2 gap-6 bg-gray-50 p-6 rounded-xl shadow">
      <!-- Left: SlidesWH -->
      <div>
        <h2 class="text-lg font-semibold mb-2">Select Content</h2>
        <div class="h-60 overflow-y-auto border rounded p-2 space-y-2">
          <div v-for="item in contentList" :key="item.id" @click="selectContent(item)" :class="[
            'cursor-pointer p-2 rounded hover:bg-blue-100',
            selectedContent?.id === item.id ? 'bg-blue-200 font-semibold' : ''
          ]">
            {{ item.name }}
          </div>
        </div>
      </div>


      <!-- Right: Groups -->
      <div>
        <h2 class="text-lg font-semibold mb-2">Select Group</h2>
        <div class="h-60 overflow-y-auto border rounded p-2 space-y-2">
          <div v-for="group in groups" :key="group.id" @click="selectGroup(group)" :class="[
            'cursor-pointer p-2 rounded hover:bg-green-100',
            selectedGroup?.id === group.id ? 'bg-green-200 font-semibold' : ''
          ]">
            {{ group.name }}
          </div>
        </div>
      </div>
      <div class="md:col-span-2 flex justify-end">
        <button class="mt-4 px-6 py-2 bg-blue-600 text-white rounded hover:bg-blue-700" @click="updateGroupContent">
          Apply
        </button>
      </div>
    </section>

    <!-- Scores Updata -->
    <section class="bg-white p-4 rounded-xl shadow-md flex flex-col sm:flex-row items-center gap-4">
      <input v-model="scoreTarget" placeholder="Screen ID or Group ID" class="border p-2 rounded w-full sm:w-auto" />
      <input v-model="scoreValue" placeholder="Score" class="border p-2 rounded w-full sm:w-auto" />
      <button class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700" @click="submitScore">
        Update Scores
      </button>
    </section>
    <section class="bg-white p-4 rounded-xl shadow-md flex flex-col sm:flex-row items-center gap-4">
      <button class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700" @click="callTestApi">
        Test
      </button>
      <p class="mt-2 text-gray-700">{{ testResponse }}</p>
    </section>
  </div>
</template>
