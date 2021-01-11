<template lang="pug">
.audiobooksBrowser Browser
  nav.level.notification
    b-field.level-item
      b-input(icon="magnify" expanded type="search" placeholder="Search audiobooks for...")
      p.control
        b-button.is-primary(@click="fetchData") Go!
    b-field.level-item
      b-switch Show only unabridged audiobooks
  keep-alive
    router-view
</template>

<script>
export default {
  name: "AudiobooksBrowser",
  components: {},
  data: () => {
    return {
      counter: 0
    }
  },
  methods: {
    // TODO: Fetch search params for route
    fetchData: function () {
      console.log(this.$api)
      this.$api.fetchData()
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">

</style>
