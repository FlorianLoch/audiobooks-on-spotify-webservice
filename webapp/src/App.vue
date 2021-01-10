<template lang="pug">
.app
  TabBar(:tabs="tabs", :activeTab.sync="activeTab", @tabSelect="tabSelect")
  router-view
</template>

<script>
import TabBar from "@/components/TabBar.vue";

export default {
  name: "AudiobooksBrowser",
  components: {
    TabBar,
  },
  data: () => {
    return {
      tabs: [
        {
          label: "Audiobooks",
          value: "audiobooks",
        },
        {
          label: "Authors",
          value: "authors",
        },
      ],
      activeTab: "audiobooks",
    };
  },
  watch: {
    $route: "routeChanged",
  },
  methods: {
    routeChanged: function () {
      console.log(this.$route.path);
      this.activeTab = this.$route.path.substr(1)
    },
    tabSelect: function (val) {
      this.$router.push({ path: val });
    },
  },
};
</script>

<style lang="scss">
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

#nav {
  padding: 30px;

  a {
    font-weight: bold;
    color: #2c3e50;

    &.router-link-exact-active {
      color: #42b983;
    }
  }
}
</style>
