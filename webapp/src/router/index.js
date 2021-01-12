import Vue from 'vue'
import VueRouter from 'vue-router'
import AuthorBrowser from '@/components/AuthorBrowser.vue'
import AudiobookBrowser from '@/components/AudiobookBrowser.vue'

Vue.use(VueRouter)

const routes = [{
    name: "audiobooks",
    path: "/audiobooks/:id?",
    component: AudiobookBrowser,
    alias: "/",
    meta: {
      "tabValue": "audiobooks"
    },
  },
  {
    name: "authors",
    path: "/authors",
    component: AuthorBrowser,
    meta: {
      "tabValue": "authors"
    }
  }
]

const router = new VueRouter({
  // mode: 'history',
  routes
})

export default router
