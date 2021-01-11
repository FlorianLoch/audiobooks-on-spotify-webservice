<template lang="pug">
.audiobooksList List
  a(@click="counter++") {{ counter }}
  br
  router-link(to="/audiobooks/DETAILS_ID") Show Details
</template>

<script>
export default {
  name: "AudiobooksList",
  components: {},
  data: () => {
    return {
      counter: 0
    }
  },
  methods: {
    toAudiobooksBrowser: function () {
      this.$router.push({name: 'audiobooks'})
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">

</style>
