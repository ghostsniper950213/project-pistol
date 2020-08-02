import Vue from 'vue'
import VuePageStack from 'vue-page-stack';
import VueLazyload from 'vue-lazyload'
import App from '@/vue/App.vue'

import router from '@/script/conf/router'
import '@/script/conf/regist'

// css
import '@/style/common.css'

// img
import bigImage from '@/img/bigImage.png'
import bigError from '@/img/bigError.png'

Vue.config.productionTip = false

Vue.use(VuePageStack, { router })
Vue.use(VueLazyload, {
  error: bigError,
  loading: bigImage
})

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
