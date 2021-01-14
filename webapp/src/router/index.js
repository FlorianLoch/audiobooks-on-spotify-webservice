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

/*
  As we are using the "history" mode we have to make sure the webserver serving
  this app is configured correctly. With the current webservice implementation
  this the case - but is has to be ensured, that the routes of the webapp and
  the server do not overlapp. Until they don't the server will always serve the
  webapp and things should work smoothly.
*/

const router = new VueRouter({
  mode: "history",
  routes
})

export default router
