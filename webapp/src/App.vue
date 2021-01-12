<template lang="pug">
.app
  .hero.is-primary
    .hero-body
      .container
        h1.title Hörbuchspion
        h2.subtitle Making it easier to find audiobooks on Spotify!
  .container.mt-4
    TabBar(:tabs="tabs", :activeTab.sync="activeTab", @tabSelect="tabSelect")
    keep-alive
      router-view
  .footer.mt-6
      .content.has-text-centered
        p Hoerbuchspion, WebUI v0.1
        p << THIS WOULD BE A NEAT PLACE FOR SOME CRAWL STATS >>
</template>

<script>
import TabBar from "@/components/TabBar.vue";

export default {
  name: "App",
  components: {
    TabBar
  },
  data: () => {
    return {
      tabs: [
        {
          label: "Audiobooks",
          value: "audiobooks",
          icon: "mdi-book-open-variant"
        },
        {
          label: "Authors",
          value: "authors",
          icon: "mdi-account"
        },
      ],
      activeTab: "audiobooks",
    };
  },
  watch: {
    $route: "onRouteChanged",
  },
  created: function () {
    this.onRouteChanged()
  },
  methods: {
    onRouteChanged: function () {
      // console.log(this.$route.path);
      // console.log(this.$route.meta.tabValue);
      this.activeTab = this.$route.meta.tabValue
    },
    tabSelect: function (val) {
      this.$router.push({ name: val });
    },
  },
};
</script>

<style lang="scss">

</style>
