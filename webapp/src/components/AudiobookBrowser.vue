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
          placeholder="Search audiobooks for..."
          expanded
          @keyup.native="onKeyUp"
        )
        p.control
          b-button.is-primary(@click="search") Go!
      b-field.column
        b-switch(v-model="unabridgedOnly" @input="search") Show only unabridged audiobooks
    .level
      h3 Found
        strong {{ ` ${audioBooksFound} `}}
        | audiobooks
  AudiobookList(:items="items" :totalPages="totalPages" :currentPage="currentPage + 1" @pageChange="onPageChange" @itemSelected="onItemSelected")
</template>

<script>
import AudiobookList from '@/components/AudiobookList.vue'
import AudiobookDetails from '@/components/AudiobookDetails.vue'

export default {
  name: "AudiobookBrowser",
  components: {
    AudiobookList,
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
  watch: {
    $route: "readParametersFromRoute",
  },
  mounted: function () {
    if (this.$route.params.id) {
      this.showDetails(this.$route.params.id)
    }

    this.readParametersFromRoute()
    this.search()
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
      })
    },
    onItemSelected: function (id) {
      this.showDetails(id)
    },
    updateRoute: function () {
      this.$router.push({params: {
        id: ""
      }, query: {
        s: this.searchTerm,
        unabridgedOnly: this.unabridgedOnly,
        currentPage: this.currentPage
      }})
    },
    readParametersFromRoute: function () {
      const q = this.$route.query
      this.searchTerm = q.s || ""
      this.unabridgedOnly = (q.unabridgedOnly || false) == "true"
      this.currentPage = parseInt(q.currentPage || 0)
    },
    showDetails: function (id) {
      this.$api.fetchSingleAudiobook(id).then((response) => {
        this.$router.push({name: "audiobooks", params: {
          id
        }})

        this.$refs.detailsComponent.show(response)
      })
    },
    search: function () {
      this.updateRoute()

      this.currentPage = 0
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

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
</style>
