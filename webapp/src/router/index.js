import Vue from 'vue'
import VueRouter from 'vue-router'
import AudiobookBrowser from '@/components/AudiobookBrowser.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: "/authors/:id?",
    name: "authors",
    component: undefined, // TODO: Replace this
    meta: {
      "tabValue": "authors"
    }
  }, {
    name: "audiobooks",
    path: "/audiobooks/:id?",
    component: AudiobookBrowser,
    meta: {
      "tabValue": "audiobooks"
    },
    alias: "/",
  },
  {
    path: '/about',
    name: 'About',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/About.vue')
  }
]

const router = new VueRouter({
  // mode: 'history',
  routes
})



// router.beforeEach((to, from, next) => {
//   function hasQueryParams(route) {
//     return !!Object.keys(route.query).length
//   }

//   // Drop the query params in case the entity changes (i.e., query params for searching audiobooks do not make sense when switching to author tab)
//   function sameEntity() {
//     console.log(to)
//     return from.name === to.name
//   }

//   if (!hasQueryParams(to) && hasQueryParams(from) && sameEntity()) {
//     next(Object.assign({}, to, { query: from.query }))
//   } else {
//     next()
//   }
// })

// router.beforeEach((to, from, next) => {
//   next(Object.assign({}, to, {query: from.query}))
// })

export default router
