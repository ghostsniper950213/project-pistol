<template>
  <div class="modal">
    <transition name="modal-cover-trans">
      <div class="modal-cover" v-show="show" @click="handleClose"></div>
    </transition>
    <transition name="modal-panel-trans">
      <div class="modal-panel" v-show="show">
        <slot />
      </div>
    </transition>
  </div>
</template>

<script>
export default {
  name: 'Modal',
  props: ['show'],
  methods: {
    handleClose() {
      this.$emit('close')
    }
  }
}
</script>

<style>
.modal-cover {
  position: absolute;
  z-index: 99;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(0, 0, 0, 0.1);
  opacity: 1;
  transition: opacity 0.2s ease-in-out;
}

.modal-cover-trans-enter,
.modal-cover-trans-leave-to {
  opacity: 0;
}

.modal-panel {
  position: absolute;
  z-index: 100;
  bottom: 20px;
  left: 20px;
  right: 20px;
  background-color: #fff;
  box-shadow: 0 5px 10px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
  padding: 10px;
  transition: transform 0.2s ease-in-out;
  text-align: center;
}

.modal-panel-trans-enter,
.modal-panel-trans-leave-to {
  transform: translateY(calc(100% + 20px));
}
</style>