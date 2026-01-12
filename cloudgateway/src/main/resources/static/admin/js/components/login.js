// Login Component
Vue.component('login-view', {
    props: {
        loading: Boolean,
        loginForm: Object
    },
    methods: {
        handleLogin() {
            this.$emit('login');
        }
    },
    template: `
        <div class="flex justify-center items-center h-screen bg-gray-900">
            <div class="bg-gray-800 border border-gray-700 rounded-lg p-8 w-full max-w-md shadow-2xl">
                <div class="text-center mb-6">
                    <h2 class="text-3xl font-bold mb-2">
                        <span class="material-icons text-4xl text-blue-400">public</span>
                    </h2>
                    <h1 class="text-3xl font-bold">Cloud Gateway</h1>
                    <p class="text-gray-400 mt-2">Admin Portal</p>
                </div>

                <div class="mb-4">
                    <label class="block text-gray-300 font-semibold mb-2">Username</label>
                    <input 
                        v-model="loginForm.username" 
                        type="text" 
                        placeholder="Enter username"
                        class="w-full px-4 py-2 bg-gray-700 border-2 border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-400 transition-colors">
                </div>

                <div class="mb-6">
                    <label class="block text-gray-300 font-semibold mb-2">Password</label>
                    <input 
                        v-model="loginForm.password" 
                        type="password" 
                        placeholder="Enter password"
                        class="w-full px-4 py-2 bg-gray-700 border-2 border-gray-600 rounded-lg text-white focus:outline-none focus:border-blue-400 transition-colors">
                </div>

                <button 
                    @click="handleLogin" 
                    :disabled="loading"
                    class="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-gray-600 text-white font-semibold py-2 px-4 rounded-lg transition-colors mb-4">
                    <span v-if="loading" class="flex items-center justify-center">
                        <span class="inline-block w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin mr-2"></span>
                        Logging in...
                    </span>
                    <span v-else>Login</span>
                </button>

                <p class="text-center text-gray-400 text-sm">
                    Demo: Use any username/password
                </p>
            </div>
        </div>
    `
});

