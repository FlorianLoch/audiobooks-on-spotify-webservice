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
      activeTab: "audiobooks"
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
      this.$router.push({ name: val })
    },
  },
};
</script>

<style lang="scss">
// Import Bulma's core
@import "~bulma/sass/utilities/_all";

// Set your colors
$primary: #2f7aa5;
$primary-light: findLightColor($primary);
$primary-dark: findDarkColor($primary);
$primary-invert: findColorInvert($primary);
$twitter: #4099ff;
$twitter-invert: findColorInvert($twitter);

// Lists and maps
$custom-colors: null !default;
$custom-shades: null !default;

// Setup $colors to use as bulma classes (e.g. 'is-twitter')
$colors: mergeColorMaps(
  (
    "white": (
      $white,
      $black,
    ),
    "black": (
      $black,
      $white,
    ),
    "light": (
      $light,
      $light-invert,
    ),
    "dark": (
      $dark,
      $dark-invert,
    ),
    "primary": (
      $primary,
      $primary-invert,
      $primary-light,
      $primary-dark,
    ),
    "link": (
      $link,
      $link-invert,
      $link-light,
      $link-dark,
    ),
    "info": (
      $info,
      $info-invert,
      $info-light,
      $info-dark,
    ),
    "success": (
      $success,
      $success-invert,
      $success-light,
      $success-dark,
    ),
    "warning": (
      $warning,
      $warning-invert,
      $warning-light,
      $warning-dark,
    ),
    "danger": (
      $danger,
      $danger-invert,
      $danger-light,
      $danger-dark,
    ),
  ),
  $custom-colors
);

// Links
$link: $primary;
$link-invert: $primary-invert;
$link-focus-border: $primary;

// Import Bulma and Buefy styles
@import "~bulma";
@import "~buefy/src/scss/buefy";
@import "~bulma/sass/utilities/_all";

$progress-bar: $primary-invert;

// Make the progressbar a little bolder
#nprogress .bar {
  height: 10px !important;
  background: $progress-bar !important;
}

#nprogress .peg {
  box-shadow: 0 0 10px $progress-bar, 0 0 5px $progress-bar !important;
}

// Spinner is currently deactivated
#nprogress .spinner-icon {
  border-top-color: $progress-bar !important;
  border-left-color: $progress-bar !important;
  border-radius: 50%;
}
</style>
