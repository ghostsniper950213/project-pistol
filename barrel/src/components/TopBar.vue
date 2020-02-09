<template>
  <div class="top-bar">
    <div class="top-bar-left">
      <button class="top-bar-back" @click="$router.back()">
        <icon icon="arrow-left" />
      </button>
    </div>
    <div class="top-bar-center">
      <span>{{ title }}</span>
    </div>
    <div class="top-bar-right"></div>
  </div>
</template>

<script>
import Icon from '@/components/Icon'

export default {
  name: 'TopBar',
  components: {
    Icon
  },
  props: ['title']
}
</script>

<style>
.top-bar {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 40px;
  line-height: 40px;
  display: flex;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

.top-bar-left {
  width: 40px;
  text-align: center;
}

.top-bar-left button {
  background: none;
  width: 40px;
  height: 40px;
}

.top-bar-left button:active {
  background-color: #eee;
}

.top-bar-center {
  flex: 1;
  overflow: hidden;
  white-space: nowrap;
  text-align: center;
}

.top-bar-right {
  width: 40px;
}
</style>