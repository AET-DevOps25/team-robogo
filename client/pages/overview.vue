<script setup>
import { ref } from 'vue'
import ScreenCard from '../components/ScreenCard.vue';
import SlideCard from '../components/SlideCard.vue';
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
};
const uploadSlide = () => {
    console.log('Upload Slide button clicked');
    // TODO: Open file input or modal
};

const screens = ref([
    {
        id: 1,
        name: 'Screen 1',
        status: 'online',
        currentContent: 'Image A',
        thumbnailUrl: './5.png'
    },
    {
        id: 2,
        name: 'Screen 2',
        status: 'offline',
        currentContent: 'Image B',
        thumbnailUrl: './5.png'
    }
    ,
    {
        id: 3,
        name: 'Screen 3',
        status: 'offline',
        currentContent: 'Image C',
        thumbnailUrl: './5.png'
    }
    ,
    {
        id: 4,
        name: 'Screen 4',
        status: 'offline',
        currentContent: 'Image 4',
        thumbnailUrl: './5.png'
    }
]);

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
</script>


<template>
    <div class="p-6 space-y-6">
        <!--Screens Preview -->
        <section>
            <h2 class="text-2xl font-bold mb-4">Screens Preview</h2>
            <div class="flex flex-wrap gap-6 justify-start">
                <ScreenCard v-for="screen in screens" :key="screen.id" :screen="screen" />
            </div>
        </section>

        <section class="grid grid-cols-2 gap-6 bg-gray-50 p-6 rounded-xl shadow">
            <!-- Left: SlidesWH -->

            <div>
                <div class="flex items-center justify-between mb-2">
                    <h2 class="text-lg font-semibold">Slides</h2>
                    <button class="text-sm px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
                        @click="uploadSlide">
                        Upload Slide
                    </button>
                </div>
                <div class="h-[400px] overflow-y-auto border-gray-200  rounded p-2 flex flex-wrap gap-6 justify-start
                    scrollbar-thin scrollbar-thumb-gray-400 scrollbar-track-gray-100">
                    <SlideCard v-for="item in contentList" :key="item.id" :item="item"
                        :selected="selectedContent?.id === item.id" @click="selectContent(item)" />
                </div>
            </div>

            <!-- Right: Groups -->
            <div class="flex flex-col gap-4">
                <div>
                    <h2 class="text-lg font-semibold mb-2">Select Group</h2>
                    <div class="h-60 overflow-y-auto rounded p-2 space-y-2">
                        <div v-for="group in groups" :key="group.id" @click="selectGroup(group)" :class="[
                            'cursor-pointer p-2 rounded hover:bg-green-100',
                            selectedGroup?.id === group.id ? 'bg-green-200 font-semibold' : ''
                        ]">
                            {{ group.name }}
                        </div>
                    </div>

                    <button class="px-6 py-2 bg-purple-600 text-white rounded hover:bg-purple-700 mb-2"
                        @click="addSlidesGroup">
                        Add Slides Group
                    </button>
                    <button class="mt-4 px-6 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                        @click="updateGroupContent">
                        Delete Slides Group
                    </button>
                </div>


                <!-- Scores Update -->
                <div class="bg-white p-4 rounded-xl shadow-md flex flex-col sm:flex-row items-center gap-4">
                    <input v-model="scoreTarget" placeholder="Screen ID or Group ID"
                        class="border p-2 rounded w-full sm:w-auto" />
                    <input v-model="scoreValue" placeholder="Score" class="border p-2 rounded w-full sm:w-auto" />
                    <button class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700" @click="submitScore">
                        Update Scores
                    </button>
                </div>
            </div>
        </section>
        <section class="bg-white p-4 rounded-xl shadow-md flex flex-col sm:flex-row items-center gap-4">
            <button class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700" @click="callTestApi">
                Test
            </button>
            <p class="mt-2 text-gray-700">{{ testResponse }}</p>
        </section>

    </div>
</template>
