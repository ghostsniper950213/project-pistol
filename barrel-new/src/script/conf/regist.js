import Vue from 'vue'

Vue.component('ToolBar', () => import('@/vue/comp/ToolBar'))
Vue.component('ToolBarItem', () => import('@/vue/comp/ToolBarItem'))
Vue.component('ImageIcon', () => import('@/vue/comp/ImageIcon'))
Vue.component('TextInput', () => import('@/vue/comp/TextInput'))
Vue.component('TextButton', () => import('@/vue/comp/TextButton'))
Vue.component('TextDialog', () => import('@/vue/comp/TextDialog'))
Vue.component('TextDialogRow', () => import('@/vue/comp/TextDialogRow'))
Vue.component('ListContainer', () => import('@/vue/comp/ListContainer'))
Vue.component('ListItem', () => import('@/vue/comp/ListItem'))
Vue.component('PageNumber', () => import('@/vue/comp/PageNumber'))