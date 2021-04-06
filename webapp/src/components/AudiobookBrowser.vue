<template lang="pug">
.AudiobookBrowser
  audiobookDetails(ref="detailsComponent")
  nav.notification.px-6
    .columns.is-multiline
      b-field.column.is-full
        b-input(
          icon="magnify",
          v-model="searchTerm",
          type="search",
          placeholder="Search audiobooks for...",
          expanded,
          @keyup.native="onKeyUp"
        )
        p.control
          b-button.is-primary(@click="search") Go!
      b-field.column
        b-switch(v-model="unabridgedOnly", @input="onClickSearchBtn") Show only unabridged audiobooks
    .level
      h3 Found
        strong {{ ` ${audioBooksFound} ` }}
        | audiobooks
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
          img(:src="item.albumArt.medium")
      .media-content
        p.title.is-4 {{ item.name }}
        p.subtitle {{ item.artists[0].name }}
</template>

<script>
import ItemList from "@/components/ItemList.vue"
import AudiobookDetails from "@/components/AudiobookDetails.vue"
import {updateProperties} from "@/lib/common"

export default {
  name: "AudiobookBrowser",
  components: {
    ItemList,
    AudiobookDetails
  },
  data: () => {
    return {
      searchTerm: "",
      unabridgedOnly: false,
      currentPage: 0,
      audioBooksFound: 0,
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
        params: {
          id
        },
        query: this.$route.query
      })
    },
    updateRoute: function () {
      this.$router.push({
        name: "audiobooks",
        query: {
          s: this.searchTerm,
          unabridgedOnly: this.unabridgedOnly,
          currentPage: this.currentPage
        }
      })
    },
    updateFromRoute: function () {
      console.log("Updating from route...")
      if (this.$route.params.id) {
        this.showDetails(this.$route.params.id)
      } else {
        // In case user is using her/his browser's navigation and is going back we want to also
        // hide the details modal
        this.hideDetails()
      }

      const q = this.$route.query
      const newParams = {
        "searchTerm": q.s || "",
        "unabridgedOnly": (q.unabridgedOnly || false) == "true",
        "currentPage": parseInt(q.currentPage) || 0
      }

      const changed = updateProperties(newParams, this)

      // We need to prevent programmatically updating the route from triggering another search
      if (changed) {
        this.fetchData(this.currentPage)
      }
    },
    showDetails: function (id) {
      this.$api.fetchSingleAudiobook(id).then((response) => {
        this.$refs.detailsComponent.show(response, () => {
          // Remove parameter after modal has been closed and
          // keep query params
          this.$router.push({
            name: "audiobooks",
            query: this.$route.query
          })
        })
      })
    },
    hideDetails: function () {
      this.$refs.detailsComponent.hide()
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
      return this.$api.fetchAudiobooks(this.searchTerm, this.unabridgedOnly, page).then((response) => {
        console.log(response)
        this.items = response.items
        this.totalPages = response.totalPages
        this.audioBooksFound = response.totalItems
      })
    }
  }
};
</script>