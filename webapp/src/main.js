import Vue from 'vue'
import App from './App.vue'
import router from './router'
import Buefy from 'buefy'
import API from './lib/API'
import axios from "axios"
import NProgress from "nprogress"

import './styles.scss'

Vue.use(Buefy)
Vue.use(API)

NProgress.configure({ showSpinner: false });

axios.interceptors.request.use((config) => {
  NProgress.start()
  return config
})
axios.interceptors.response.use((response) => {
  NProgress.done()
  return response
}, (err) => {
  NProgress.done()
  return Promise.reject(err)
})

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')