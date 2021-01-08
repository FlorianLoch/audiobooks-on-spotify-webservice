<template lang="pug">
.audiobooksBrowser
  h3 Hello World
  TabBar
</template>

<script>
import TabBar from '@/components/TabBar.vue'

export default {
  name: "AudiobooksBrowser",
  components: {
    TabBar,
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
h3 {
  margin: 40px 0 0;
  color: #42b983;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
