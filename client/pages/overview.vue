<!-- File: src/pages/overview.vue -->
<template>
    <div class="p-6 space-y-6">
        <!--Screens Monitor -->
        <section>
            <h2 class="text-2xl font-bold mb-4">Screens Monitor</h2>
            <div
                class="max-h-[50vh] overflow-y-auto scrollbar-thin scrollbar-thumb-gray-400 scrollbar-track-gray-100 rounded p-2">

                <div class="flex flex-wrap gap-6 justify-start">
                    <ScreenCard v-for="screen in screens" :key="screen.id" :screen="screen" :slideGroups="slideGroups"
                        :all-slides="contentList" @updateGroup="onUpdateScreenGroup"
                        @requestDelete="(screen) => { showDeleteConfirm = true; screenToDelete = screen }" />
                    <div class="w-[300px] h-[260px] flex items-center justify-center rounded-xl bg-gray-100 text-5xl text-gray-400 hover:bg-gray-200 cursor-pointer"
                        @click="showAddScreenDialog = true">
                        +
                    </div>

                </div>
            </div>
        </section>
        <!-- Add Screen Dialog -->
        <div v-if="showAddScreenDialog"
            class="fixed inset-0 bg-black bg-opacity-40 z-50 flex items-center justify-center">
            <div class="bg-white rounded-lg p-6 shadow-lg w-[400px] relative">
                <h3 class="text-lg font-bold mb-4">Add New Screen</h3>
                <input v-model="newScreenName" placeholder="Screen Name" class="mb-2 w-full border p-2 rounded" />
                <input v-model="newScreenUrl" placeholder="Thumbnail URL (optional)"
                    class="mb-4 w-full border p-2 rounded" />

                <div class="flex justify-end gap-2">
                    <button @click="showAddScreenDialog = false"
                        class="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400 text-gray-800">
                        Cancel
                    </button>
                    <button @click="addNewScreen" class="px-4 py-2 bg-blue-600 rounded hover:bg-blue-700 text-white">
                        Add
                    </button>
                </div>
                <button class="absolute top-2 right-2 text-gray-400 hover:text-gray-600"
                    @click="showAddScreenDialog = false">✕</button>
            </div>
        </div>
        <!-- Confirm Delete Dialog  -->
        <div v-if="showDeleteConfirm"
            class="fixed inset-0 bg-black bg-opacity-40 z-50 flex items-center justify-center">
            <div class="bg-white p-6 rounded-xl shadow-lg w-[400px] relative">
                <h3 class="text-lg font-semibold mb-4">Confirm Delete</h3>
                <p>Are you sure you want to delete <strong>{{ screenToDelete?.name }}</strong>?</p>
                <div class="flex justify-end gap-2 mt-4">
                    <button class="px-4 py-2 bg-gray-300 rounded" @click="showDeleteConfirm = false">Cancel</button>
                    <button class="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700"
                        @click="confirmDeleteScreen">Delete</button>
                </div>
                <button class="absolute top-2 right-2 text-gray-400 hover:text-gray-600"
                    @click="showDeleteConfirm = false">✕</button>
            </div>
        </div>

        <section class="grid grid-cols-[2fr_1fr] gap-6 bg-gray-50 p-6 rounded-xl shadow">
            <!-- Left: Slide Group -->

            <div>
                <div class="flex items-center justify-between mb-2">
                    <h2 class="text-lg font-semibold">Slide Groups</h2>

                    <button class="text-sm px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
                        @click="triggerFileInput">
                        Upload Slide
                    </button>
                    <!-- 在 Upload Slide 按钮后加一个文件输入框 -->
                    <input ref="fileInput" type="file" accept="image/*" class="hidden" @change="handleFileUpload" />


                </div>
                <div>
                    <USelectMenu v-model="selectedGroupId" :items="slideGroups.map(g => g.id)" class="w-48" />
                    <UButton color="neutral" variant="outline" @click="showAddGroupDialog = true">Add Group</UButton>

                </div>
                <div class="h-[400px] w-full overflow-y-auto border-gray-200  rounded p-2 flex flex-wrap gap-6 justify-start
                    scrollbar-thin scrollbar-thumb-gray-400 scrollbar-track-gray-100">
                    <SlideGroupCard v-if="currentGroup && currentGroup.id !== 'None'" :key="currentGroup.id"
                        :title="currentGroup.id" v-model:slide-ids="currentGroup.slideIds"
                        v-model:speed="currentGroup.speed" :allSlides="contentList" :selected-content="selectedContent"
                        :slides="currentGroupSlides" @select="selectContent" />
                </div>
            </div>

            <!-- Right: Groups -->
            <div class="flex flex-col gap-4">



                <!-- Scores Update -->
                <div class="bg-white p-4 rounded-xl shadow-md flex flex-col sm:flex-row items-center gap-4">

                    <input v-model="scoreTarget" placeholder="Team ID" class="border p-2 rounded w-full sm:w-auto" />
                    <input v-model="scoreValue" placeholder="Score" class="border p-2 rounded w-full sm:w-auto" />
                    <button class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700" @click="submitScore">
                        Update Scores
                    </button>
                </div>
                <!-- Chat Box -->
                <div class="bg-white p-4 rounded-xl shadow-md flex flex-col gap-3 h-[300px]">
                    <h3 class="text-lg font-semibold">Chat with AI</h3>

                    <div class="flex-1 overflow-y-auto border rounded p-2 text-sm text-gray-700 bg-gray-50">
                        <div v-for="(msg, index) in chatHistory" :key="index" class="mb-2">
                            <strong>{{ msg.role }}:</strong> {{ msg.text }}
                        </div>
                    </div>

                    <div class="flex gap-2 pt-2">
                        <input v-model="userMessage" @keyup.enter="sendMessage" placeholder="Type a message..."
                            class="flex-1 border p-2 rounded" />
                        <button class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700" @click="sendMessage">
                            Send
                        </button>
                    </div>
                </div>
            </div>
        </section>
        <!-- Add Group Dialog -->
        <div v-if="showAddGroupDialog"
            class="fixed inset-0 bg-black bg-opacity-40 z-50 flex items-center justify-center">
            <div class="bg-white p-6 rounded-xl shadow-lg w-[400px] relative">
                <h3 class="text-lg font-semibold mb-4">Add New Group</h3>

                <input v-model="newGroupName" placeholder="Group Name"
                    class="w-full border border-gray-300 p-2 rounded mb-4" />

                <div class="flex justify-end gap-2">
                    <button class="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400" @click="showAddGroupDialog = false">
                        Cancel
                    </button>
                    <button class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700" @click="addGroup">
                        Add
                    </button>
                </div>

                <button class="absolute top-2 right-2 text-gray-400 hover:text-gray-600"
                    @click="showAddGroupDialog = false">✕</button>
            </div>
        </div>

        <section class="bg-white p-4 rounded-xl shadow-md flex flex-col sm:flex-row items-center gap-4">
            <button class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700" @click="callTestApi">
                Test
            </button>
            <p class="mt-2 text-gray-700">{{ testResponse }}</p>
        </section>

    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { computed } from 'vue'
import { onMounted } from 'vue'
import { v4 as uuidv4 } from 'uuid'
import { useScreenStore } from '@/stores/useScreenStore'
import { watchOnce } from '@vueuse/core'
import ScreenCard from '../components/ScreenCard.vue';
import SlideGroupCard from '../components/SlideGroupCard.vue'
const scoreTarget = ref('')
const scoreValue = ref('')

const testResponse = ref('') // display Returned Text from API

const store = useScreenStore()

watchOnce(
    () => store.slideGroups.length > 0 || store.screens.length > 0,
    () => {
        // 数据已从 localStorage 加载
        if (!store.slideTimerStarted) {
            store.startSlideTimer()
            store.slideTimerStarted = true
        }
    }
)

// 延迟初始化空状态
watchOnce(
    () => store.slideGroups.length === 0 && store.screens.length === 0,
    () => {
        store.initStore()
    }
)

const value = ref('')
interface SlideItem {
    id: number
    name: string
    url: string
}
const selectedContent = ref<SlideItem | null>(null)

function selectContent(item: SlideItem) {
    selectedContent.value = item
}

const callTestApi = async () => {
    try {
        const res = await fetch('/api/test')
        const text = await res.text()
        testResponse.value = text
    } catch (error) {
        console.error('API error', error)
        testResponse.value = 'Error calling API'
    }
};


const screens = computed(() => store.screens)

const contentList = computed(() => store.contentList)

const slideGroups = computed(() => store.slideGroups)


const selectedGroupId = ref(store.slideGroups[0]?.id || 'None')

const currentGroup = computed(() =>
    slideGroups.value.find(g => g.id === selectedGroupId.value)
)
const currentGroupSlides = computed(() =>
    currentGroup.value
        ? store.contentList.filter(s => currentGroup.value!.slideIds.includes(s.id))
        : []
)
const onUpdateScreenGroup = (updatedScreen) => {
    store.updateScreenGroup(updatedScreen.id, updatedScreen.groupId)
}


const showAddScreenDialog = ref(false)
const newScreenName = ref('')
const newScreenUrl = ref('')
const addNewScreen = () => {
    store.addScreen(newScreenName.value, newScreenUrl.value)
    showAddScreenDialog.value = false
    newScreenName.value = ''
    newScreenUrl.value = ''
}

/*
for GenAI
*/
const userMessage = ref('')
const chatHistory = ref<{ role: string; text: string }[]>([])

const sendMessage = async () => {
    if (!userMessage.value.trim()) return

    // Append user message
    chatHistory.value.push({ role: 'You', text: userMessage.value })

    // Placeholder GenAI response (replace with real API call later)
    const responseText = `🤖 (Placeholder response to): "${userMessage.value}"`

    // Simulate response
    setTimeout(() => {
        chatHistory.value.push({ role: 'AI', text: responseText })
    }, 500)

    userMessage.value = ''
}

const showAddGroupDialog = ref(false)
const newGroupName = ref('')

// 添加新分组
const addGroup = () => {
    store.addGroup(newGroupName.value)
    selectedGroupId.value = newGroupName.value
    newGroupName.value = ''
    showAddGroupDialog.value = false
}

//delete screen
const showDeleteConfirm = ref(false)
const screenToDelete = ref(null)

const confirmDeleteScreen = () => {
    if (!screenToDelete.value) return
    store.screens = store.screens.filter(s => s.id !== screenToDelete.value.id)
    showDeleteConfirm.value = false
    screenToDelete.value = null
}

//add slide in slide deck
const fileInput = ref<HTMLInputElement | null>(null)

const triggerFileInput = () => {
    fileInput.value?.click()
}

const handleFileUpload = (event: Event) => {
    const files = (event.target as HTMLInputElement).files
    if (!files || files.length === 0) return

    const file = files[0]
    //replace with API later
    const reader = new FileReader()

    reader.onload = () => {
        const imageUrl = reader.result as string
        const newId = Date.now()

        const newSlide = {
            id: newId,
            name: file.name,
            url: imageUrl,
        }

        contentList.value.push(newSlide)
        store.contentList.push(newSlide)

        const targetGroup = store.slideGroups.find(g => g.id === selectedGroupId.value)
        if (targetGroup && targetGroup.id !== 'None') {
            targetGroup.slideIds.push(newSlide.id)
        }
    }

    reader.readAsDataURL(file)
}
</script>
