<template lang="pug">
.audiobookDetails
  b-modal(v-model="showModal" width="500px" scroll="keep" @close="onClose")
    div(v-if="showModal && details")
      .card
        .card-image
          figure.image.is-1by1
            img(:src="details.albumArt.large")
          .card-content
            p.title.is-4 {{details.name}}
            .media(v-for="artist in details.artists")
              .media-left
                figure.image.is-64x64
                  img(:src="artist.artistImage.small")
              .media-content
                p.title.is-5 {{artist.name}}
            .content
              p Add some details...
</template>

<script>
export default {
  name: "AudiobookDetails",
  components: {},
  data: () => {
    return {
      showModal: false,
      details: {}
    }
  },
  methods: {
    show: function (details, onClose) {
      this.onClose = onClose

      this.details = details
      this.showModal = true
    },
    hide: function () {
      this.showModal = false
    }
  }
};
</script>