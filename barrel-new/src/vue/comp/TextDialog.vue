<template>
  <div class="text-dialog">
    <transition name="text-dialog-cover-trans">
      <div v-show="show" class="text-dialog-cover"></div>
    </transition>
    <transition name="text-dialog-panel-trans">
      <div v-show="show" class="text-dialog-panel">
        <slot />
      </div>
    </transition>
  </div>
</template>

<script>
export default {
  name: 'TextDialog',
  props: {
    show: {
      default: false
    }
  }
}
</script>

<style>
.text-dialog-cover {
  position: fixed;
  z-index: 20;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.2);
}

.text-dialog-panel {
  position: fixed;
  z-index: 30;
  left: 10px;
  right: 10px;
  bottom: 60px;
  margin: auto;
  border-radius: 10px;
  background-color: #fff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.25);
}

.text-dialog-cover-trans-enter,
.text-dialog-cover-trans-leave-to {
  opacity: 0;
}

.text-dialog-cover-trans-enter-active,
.text-dialog-cover-trans-leave-active {
  transition: opacity 0.2s ease;
}

.text-dialog-panel-trans-enter,
.text-dialog-panel-trans-leave-to {
  transform: translateY(calc(100% + 60px));
}

.text-dialog-panel-trans-enter-active,
.text-dialog-panel-trans-leave-active {
  transition: transform 0.2s ease;
}
</style>