<template lang="pug">
.tabs.is-boxed
  ul
    li(v-for="tab in tabs" :class="{'is-active': tab.value == activeTab}")
      a(@click="tabSelect(tab.value)")
        span.icon.is-small
          i.mdi.mdi-24px(:class="tab.icon" aria-hidden="true")
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

        // TODO: Fix this loop, it's entirely broken
        val.forEach((item) => {
          if (hasOwn(item, "label") && hasOwn(item, "value") && hasOwn(item, "icon")) return true
        })
        return true
      }
    },
    activeTab: String
  },
  methods: {
    tabSelect: function (val) {
      if (val === this.activeTab) return
      
      this.$emit("update:activeTab", val)
      this.$emit("tabSelect", val)
    }
  }
}
</script>