<template lang="pug">
.AuthorBrowser
  nav.notification.px-6
    .columns.is-multiline
      b-field.column.is-full
        b-input(
          icon="magnify",
          v-model="searchTerm",
          type="search",
          placeholder="Search authors for...",
          expanded,
          @keyup.native="onKeyUp"
        )
        p.control
          b-button.is-primary(@click="onClickSearchBtn") Go!
    .level
      h3 Found
        strong {{ ` ${authorsFound} ` }}
        | authors
  ItemList(
    :items="items",
    :totalPages="totalPages",
    :currentPage="currentPage + 1",
    @pageChange="onPageChange",
    @itemSelected="onItemSelected"
  )
    template(v-slot="item")
      .media-left
        figure.image.is-128x128
          img(:src="item.artistImage.medium")
      .media-content
        p.title.is-4 {{ item.name }}
        p.subtitle {{ item.popularity }}
</template>

<script>
import ItemList from "@/components/ItemList.vue"
import { updateProperties } from "@/lib/common"

export default {
  name: "AuthorBrowser",
  components: {
    ItemList
  },
  data: () => {
    return {
      searchTerm: "",
      currentPage: 0,
      authorsFound: 0,
      totalPages: 0,
      items: []
    }
  },
  mounted: function () {
    this.search()
  },
  watch: {
    $route: function () {
      this.updateFromRoute()
    }
  },
  methods: {
    onKeyUp: function (e) {
      if (e.keyCode === 13) {
        this.search()
      }
    },
    onPageChange: function (page) {
      // Pagination component is not 0-based
      page--
      this.fetchData(page).then(() => {
        this.currentPage = page
        this.updateRoute()
      })
    },
    onItemSelected: function (id) {
      this.$router.push({
        name: "audiobooks",
        query: {
          s: `artistID:"${id}"`
        }
      })
    },
    updateRoute: function () {
      this.$router.push({
        query: {
          s: this.searchTerm,
          currentPage: this.currentPage
        }
      })
    },
    updateFromRoute: function () {
      const q = this.$route.query
      const newParams = {
        "searchTerm": q.s || "",
        "currentPage": parseInt(q.currentPage) || 0
      }

      const changed = updateProperties(newParams, this)

      // We need to prevent programmatically updating the route from triggering another search
      if (changed) {
        this.fetchData(this.currentPage)
      }
    },
    onClickSearchBtn: function () {
      // Reset page before performing a fresh search
      this.currentPage = 0
      this.search()
    },
    search: function () {
      this.updateRoute()

      this.fetchData(this.currentPage)
    },
    fetchData: function (page) {
      return this.$api.fetchAuthors(this.searchTerm, page).then((response) => {
        console.log(response)
        this.items = response.items
        this.totalPages = response.totalPages
        this.authorsFound = response.totalItems
      })
    }
  }
};
</script>