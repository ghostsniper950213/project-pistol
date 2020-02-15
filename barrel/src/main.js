import Vue from 'vue'
import App from './App.vue'
import router from './router'
import VueLazyload from 'vue-lazyload'
import Navigation from 'vue-navigation'

import errorImage from './assets/error.png'
import loadingImage from './assets/loading.png'

Vue.config.productionTip = false

Vue.use(Navigation, {router})

Vue.use(VueLazyload, {
  error: errorImage,
  loading: loadingImage,
  attempt: 3
})

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')