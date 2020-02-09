import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const defaultSearchParams = {
  page: 0,
  f_cats: 0,
  f_search: '',
  f_spf: '',
  f_spt: '',
  f_srdd: 2,
  advsearch: 1,
  f_sr: 'on',
  f_sp: 'on',
  f_sname: 'on',
  f_stags: 'on',
  f_sdesc: 'on'
}

export default new Vuex.Store({
  state: {
    defaultSearchParams: defaultSearchParams
  },
  mutations: {
  },
  actions: {
  },
  modules: {
  }
})
