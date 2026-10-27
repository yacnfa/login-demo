import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { House, User, Plus } from '@element-plus/icons-vue'

const app = createApp(App)
app.component('House', House)
app.component('User', User)
app.component('Plus', Plus)
app.use(store).use(router).use(ElementPlus).mount('#app')
