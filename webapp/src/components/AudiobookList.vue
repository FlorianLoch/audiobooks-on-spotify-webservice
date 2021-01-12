<template lang="pug">
.AudiobookList
  div(v-if="items.length > 0")
    .media(v-for="(item, index) in items" @click="onItemSelected(item.id)")
      .media-left
        figure.image.is-128x128
          img(:src="item.albumArtUrl")
      .media-content
        p.title.is-4 {{item.name}}
        p.subtitle {{item.artist.name}}
  div(v-else).my-6.py-6
    p.title.is-3.has-text-centered Nothing here. Start a search.
  div(v-if="totalPages > 0").mt-4
    b-pagination(
      :total="totalPages",
      :current="currentPage",
      range-before="3",
      order="is-centered",
      per-page="1",
      aria-next-label="Next page",
      aria-previous-label="Previous page",
      aria-page-label="Page",
      aria-current-label="Current page"
      @change="onPageChange"
    )
</template>

<script>
export default {
  name: "AudiobookList",
  components: {},
  props: {
    items: Array,
    currentPage: Number,
    totalPages: Number
  },
  methods: {
    onPageChange: function (page) {
      this.$emit("pageChange", page)
    },
    onItemSelected: function (id) {
      this.$emit("itemSelected", id)
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
</style>
