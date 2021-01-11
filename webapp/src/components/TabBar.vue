<template lang="pug">
.tabs.is-boxed
  ul
    li(v-for="tab in tabs" :class="{'is-active': tab.value == activeTab}")
      a(@click="tabSelect(tab.value)")
        span.icon.is-small
          i.fas.fa-image(aria-hidden="true")
        span {{tab.label}}
</template>

<script>
export default {
  name: 'TabBar',
  props: {
    tabs: {
      type: Array,
      validator: (val) => {
        const hasOwn = (self, propName) => {
          return Object.prototype.hasOwnProperty.call(self, propName)
        }
        val.forEach((item) => {
          if (hasOwn(item, "label") && hasOwn(item, "value")) return false
        })
        return true
      }
    },
    activeTab: String
  },
  // data: function () {
  //   return {
  //     activeTab: this.initialActiveTab
  //   }
  // },
  methods: {
    tabSelect: function (val) {
      if (val === this.activeTab) return
      // this.activeTab = val
      this.$emit("update:activeTab", val)
      this.$emit("tabSelect", val)
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
