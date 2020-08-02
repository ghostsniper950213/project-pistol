import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '@/vue/view/Home'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  // {
  //   path: '/search',
  //   name: 'Search',
  //   component: () => import('@/vue/view/Search')
  // },
  // {
  //   path: '/detail',
  //   name: 'Detail',
  //   component: () => import('@/vue/view/Detail')
  // },
  {
    path: '/test',
    name: 'Test',
    component: () => import('@/vue/view/Test')
  },
]

const router = new VueRouter({
  routes
})

export default router