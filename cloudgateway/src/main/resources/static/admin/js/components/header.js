// Header Component
Vue.component('app-header', {
    props: {
        currentUser: String
    },
    methods: {
        logout() {
            this.$emit('logout');
        }
    },
    template: `
        <div class="bg-gray-800 border-b border-gray-700 px-6 py-4 flex justify-between items-center shadow-md">
            <h1 class="text-2xl font-bold flex items-center gap-2">
                <span class="material-icons text-blue-400">public</span>
                Cloud Gateway Admin
            </h1>
            <div class="flex items-center gap-4">
                <div class="flex items-center gap-2">
                    <span class="material-icons">account_circle</span>
                    <span>{{ currentUser }}</span>
                </div>
                <button @click="logout" class="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-lg transition-colors">
                    Logout
                </button>
            </div>
        </div>
    `
});

