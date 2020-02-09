import Vue from 'vue'
import VueRouter from 'vue-router'
import Gallery from '../views/Gallery'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'gallery',
    component: Gallery,
  },
  {
    path: '/search',
    name: 'search',
    component: () => import('../views/Search'),
  },
  {
    path: '/detail',
    name: 'detail',
    component: () => import('../views/Detail'),
  },
  {
    path: '/read',
    name: 'read',
    component: () => import('../views/Read'),
  },
  {
    path: '/user',
    name: 'user',
    component: () => import('../views/User'),
  },
  {
    path: '/download',
    name: 'download',
    component: () => import('../views/Download'),
  },
  {
    path: '/block-tag',
    name: 'blockTag',
    component: () => import('../views/BlockTag'),
  },
]

const router = new VueRouter({
  routes
})

export default router
