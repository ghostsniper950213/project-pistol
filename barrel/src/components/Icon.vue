<template>
  <font-awesome-icon class="icon" :icon="icon" fixed-width @click="$emit('click')"/>
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
  faUpload,
  faClock,
  faCircleNotch,
  faArrowRight,
  faArrowLeft,
  faRedo,
  faAtlas,
  faHeart,
  faCheck,
  faTimes,
  faLock,
  faSignInAlt,
  faSignOutAlt,
  faSave,
  faPause
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
  faUpload,
  faClock,
  faCircleNotch,
  faArrowRight,
  faArrowLeft,
  faRedo,
  faAtlas,
  faHeart,
  faCheck,
  faTimes,
  faLock,
  faSignInAlt,
  faSignOutAlt,
  faSave,
  faPause
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
  color: inherit;
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