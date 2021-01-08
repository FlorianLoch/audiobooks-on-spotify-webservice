<template>
  <div class="home">
    <img alt="Vue logo" src="../assets/logo.png">
    <AudiobooksBrowser msg="Welcome to Your Vue.js App"/>
  </div>
</template>

<script>
// @ is an alias to /src
import AudiobooksBrowser from '@/components/AudiobooksBrowser.vue'

export default {
  name: 'Audiobooks',
  components: {
    AudiobooksBrowser
  }
}
</script>
