<template>
  <font-awesome-icon class="icon" :icon="icon" fixed-width @click="$emit('click')" />
</template>

<script>
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import {
  faTags,
  faStar,
  faBook,
  faImages,
  faSearch,
  faAngleUp,
  faUser,
  faDownload,
  faEyeSlash,
  faClock,
  faCircleNotch,
  faArrowLeft,
  faAtlas,
  faHeart,
  faPause,
  faUpload,
  faLock,
  faTrashAlt,
  faTasks,
  faRedo,
  faPlay,
  faBookReader
} from '@fortawesome/free-solid-svg-icons'
library.add(
  faTags,
  faStar,
  faBook,
  faImages,
  faSearch,
  faAngleUp,
  faUser,
  faDownload,
  faEyeSlash,
  faClock,
  faCircleNotch,
  faArrowLeft,
  faAtlas,
  faHeart,
  faPause,
  faUpload,
  faLock,
  faTrashAlt,
  faTasks,
  faRedo,
  faPlay,
  faBookReader
)

export default {
  name: 'Icon',
  components: {
    FontAwesomeIcon
  },
  props: ['icon'],
  methods: {
    test() {
      alert(1)
    }
  }
}
</script>

<style>
.icon {
  color: #567;
  text-align: center;
  padding: 0;
  margin: 0;
}

.icon svg {
  text-align: center;
  padding: 0;
  margin: 0;
}

.icon path {
  color: inherit;
  padding: 0;
  margin: 0;
}
</style>