<template lang="pug">
.audiobookDetails
  b-modal(v-model="showModal" width="640px" scroll="keep" @close="onClose")
    div(v-if="showModal && details")
      .card
        .card-image
          figure.image.is-1by1
            img(:src="details.albumArt.large")
          .card-content
            p.title.is-4 {{details.name}}
            .media
              .media-left
                figure.image.is-64x64
                  img(:src="details.artist.artistImage.small")
              .media-content
                p.title.is-5 {{details.artist.name}}
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
    }
  }
};
</script>