<template>
  <div class="image-icon">
    <img
      :src="getImg()"
      :style="{width: size + 'px', height: size + 'px'}"
      :class="{'rotate': rotate}"
    />
  </div>
</template>

<script>
export default {
  name: 'ImageIcon',
  props: {
    icon: String,
    size: {
      type: [String, Number],
      default: 16,
    },
    rotate: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    getImg() {
      return require('@/img/' + this.icon + '.png')
    },
  },
}
</script>

<style>
.image-icon {
  display: inline-flex;
  align-items: center;
}

.image-icon img {
  vertical-align: middle;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.image-icon img.rotate {
  animation: rotate 4s linear infinite;
}
</style>