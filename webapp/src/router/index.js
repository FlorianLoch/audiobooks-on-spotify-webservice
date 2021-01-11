import Vue from 'vue'
import VueRouter from 'vue-router'
import AudiobooksBrowser from '@/components/AudiobooksBrowser.vue'
import AudiobooksDetails from '@/components/AudiobooksDetails.vue'
import AudiobooksList from '@/components/AudiobooksList.vue'


Vue.use(VueRouter)

const routes = [
  {
    path: "/audiobooks",
    component: AudiobooksBrowser,
    meta: {
      "tabValue": "audiobooks"
    },
    alias: "/",
    children: [{
      path: ":id",
      component: AudiobooksDetails,
      meta: {
        "tabValue": "audiobooks"
      }
    }, {
      path: "",
      name: "audiobooks",
      component: AudiobooksList,
      meta: {
        "tabValue": "audiobooks"
      }
    }]
  },
  {
    path: "/authors/:id?",
    name: "authors",
    component: AudiobooksBrowser, // TODO: Replace this
    meta: {
      "tabValue": "authors"
    }
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
  mode: 'history',
  routes
})

function hasQueryParams(route) {
  return !!Object.keys(route.query).length
}

router.beforeEach((to, from, next) => {
   if(!hasQueryParams(to) && hasQueryParams(from)){
    next(Object.assign({}, to, {query: from.query}))
  } else {
    next()
  }
})

// router.beforeEach((to, from, next) => {
//   next(Object.assign({}, to, {query: from.query}))
// })

export default router
