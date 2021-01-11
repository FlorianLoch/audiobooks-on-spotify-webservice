<template lang="pug">
.audiobooksDetails Details
  a(@click="toAudiobooksBrowser") Back
  br
  a(@click="counter++") {{ counter }}
</template>

<script>
export default {
  name: "AudiobooksDetails",
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
