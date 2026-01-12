// Sidebar Component
Vue.component('app-sidebar', {
    props: {
        currentView: String
    },
    methods: {
        navigate(view) {
            this.$emit('navigate', view);
        },
        isActive(view) {
            return this.currentView === view;
        }
    },
    template: `
        <div class="w-64 bg-gray-800 border-r border-gray-700 overflow-y-auto">
            <ul class="list-none p-0 m-0">
                <li>
                    <a @click="navigate('dashboard')" 
                       :class="['block px-6 py-3 cursor-pointer transition-all border-l-4', 
                                isActive('dashboard') ? 'bg-blue-900 text-blue-400 border-blue-400 font-semibold' : 'text-gray-400 border-transparent hover:bg-gray-700 hover:text-gray-100']">
                        <span class="material-icons inline mr-2 text-lg">dashboard</span>
                        Dashboard
                    </a>
                </li>
                <li>
                    <a @click="navigate('routes')" 
                       :class="['block px-6 py-3 cursor-pointer transition-all border-l-4', 
                                isActive('routes') ? 'bg-blue-900 text-blue-400 border-blue-400 font-semibold' : 'text-gray-400 border-transparent hover:bg-gray-700 hover:text-gray-100']">
                        <span class="material-icons inline mr-2 text-lg">route</span>
                        Routes
                    </a>
                </li>
                <li>
                    <a @click="navigate('audit')" 
                       :class="['block px-6 py-3 cursor-pointer transition-all border-l-4', 
                                isActive('audit') ? 'bg-blue-900 text-blue-400 border-blue-400 font-semibold' : 'text-gray-400 border-transparent hover:bg-gray-700 hover:text-gray-100']">
                        <span class="material-icons inline mr-2 text-lg">history</span>
                        Audit Logs
                    </a>
                </li>
                <li>
                    <a @click="navigate('health')" 
                       :class="['block px-6 py-3 cursor-pointer transition-all border-l-4', 
                                isActive('health') ? 'bg-blue-900 text-blue-400 border-blue-400 font-semibold' : 'text-gray-400 border-transparent hover:bg-gray-700 hover:text-gray-100']">
                        <span class="material-icons inline mr-2 text-lg">favorite</span>
                        Health Check
                    </a>
                </li>
            </ul>
        </div>
    `
});

