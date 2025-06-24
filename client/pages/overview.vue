<template>
    <div class="p-6 space-y-6">
        <!--Screens Monitor -->
        <section>
            <h2 class="text-2xl font-bold mb-4">Screens Monitor</h2>
            <div
                class="max-h-[50vh] overflow-y-auto scrollbar-thin scrollbar-thumb-gray-400 scrollbar-track-gray-100 rounded p-2">

                <div class="flex flex-wrap gap-6 justify-start">
                    <ScreenCard v-for="screen in screens" :key="screen.id" :screen="screen" :slideGroups="slideGroups"
                        @updateGroup="onUpdateScreenGroup"
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
            <!-- Left: SlidesWH -->

            <div>
                <div class="flex items-center justify-between mb-2">
                    <h2 class="text-lg font-semibold">Slide Groups</h2>

                    <button class="text-sm px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
                        @click="uploadSlide">
                        Upload Slide
                    </button>

                </div>
                <div>
                    <USelectMenu v-model="selectedGroupId" :items="slideGroups.map(g => g.id)" class="w-48" />
                    <UButton color="neutral" variant="outline" @click="showAddGroupDialog = true">Add Group</UButton>

                </div>
                <div class="h-[400px] w-full overflow-y-auto border-gray-200  rounded p-2 flex flex-wrap gap-6 justify-start
                    scrollbar-thin scrollbar-thumb-gray-400 scrollbar-track-gray-100">
                    <SlideGroupCard v-if="currentGroup && currentGroup.id !== 'None'" :key="currentGroup.id"
                        v-model:content="currentGroup.content" :allSlides="contentList"
                        :selectedContent="selectedContent" @select="selectContent"
                        @add="(item) => currentGroup.content.push(item)" />

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


import ScreenCard from '../components/ScreenCard.vue';
import SlideCard from '../components/SlideCard.vue';
import SlideGroupCard from '../components/SlideGroupCard.vue'

const testResponse = ref('') // display Returned Text from API
const items = ref(['Backlog', 'Todo', 'In Progress', 'Done'])
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
const uploadSlide = () => {
    console.log('Upload Slide button clicked');
    // TODO: Open file input or modal
};


const id1 = uuidv4()
const id2 = uuidv4()
const id3 = uuidv4()
const id4 = uuidv4()
const screens = ref([]);

const contentList = ref([
    {
        id: 1,
        name: 'Slide A',
        url: './5.png'
    },
    {
        id: 2,
        name: 'Slide B',
        url: './Folie3-BIHZ0bEZ.PNG'
    },
    {
        id: 3,
        name: 'Slide C',
        url: './Folie4-CbnN4Bxf.PNG'
    },
    {
        id: 4,
        name: 'Slide D',
        url: './Folie5-CcbTgpDC.PNG'
    },
    {
        id: 5,
        name: 'Slide E',
        url: './Folie6-B8wXdOXm.PNG'
    }
]);
const slideGroups = ref([
    {
        id: 'None',
        content: []
    }
])


const selectedGroupId = ref(slideGroups.value[0]?.id || 'None')
const currentGroup = computed(() =>
    slideGroups.value.find(g => g.id === selectedGroupId.value)
)
const onUpdateScreenGroup = (updatedScreen) => {
    const index = screens.value.findIndex(s => s.id === updatedScreen.id)
    if (index !== -1) {
        screens.value[index].groupId = updatedScreen.groupId // could be 'None'
    }
}

onMounted(() => {
    setInterval(() => {
        screens.value.forEach(screen => {
            if (!group || !group.content || group.content.length === 0) {
                screen.currentContent = 'BLACK_SCREEN'
                return
            }

            const selectedGroupId = ref(slideGroups.value[0]?.id || 'Default')
            if (group && group.content.length > 0) {
                const currentIndex = group.content.findIndex(s => s.name === screen.currentContent)
                const nextSlide = group.content[(currentIndex + 1) % group.content.length]
                screen.currentContent = nextSlide.name
            }
        })
    }, 5000)
})
const showAddScreenDialog = ref(false)
const newScreenName = ref('')
const newScreenUrl = ref('')
const addNewScreen = () => {
    const newId = uuidv4()
    screens.value.push({
        id: newId,
        name: newScreenName.value || 'Unnamed Screen',
        status: 'offline',
        groupId: 'None',
        currentContent: 'BLACK_SCREEN',
        thumbnailUrl: newScreenUrl.value || 'https://via.placeholder.com/300x200?text=Preview',
        urlPath: `/screen/${newId}`,
    })
    showAddScreenDialog.value = false
    newScreenName.value = ''
    newScreenUrl.value = ''
}


/*
for GenAI
*/
const userMessage = ref('')
const chatHistory = ref<{ role: string, text: string }[]>([])

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
    const name = newGroupName.value.trim()
    if (!name) return
    // 避免重复
    if (slideGroups.value.find(g => g.id === name)) return

    slideGroups.value.push({
        id: name,
        content: []
    })
    selectedGroupId.value = name
    newGroupName.value = ''
    showAddGroupDialog.value = false
}

//delete screen
const showDeleteConfirm = ref(false)
const screenToDelete = ref(null)

const confirmDeleteScreen = () => {
    if (!screenToDelete.value) return
    screens.value = screens.value.filter(s => s.id !== screenToDelete.value.id)
    showDeleteConfirm.value = false
    screenToDelete.value = null
}

</script>
