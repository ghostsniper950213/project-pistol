<template>
  <transition name="loading-trans">
    <div class="loading" v-show="show">
      <icon icon="circle-notch" class="fa-spin" />
    </div>
  </transition>
</template>

<script>
import Icon from '@/components/Icon'

export default {
  name: 'Loading',
  components: {
    Icon
  },
  props: ['show']
}
</script>

<style>
.loading {
  position: absolute;
  z-index: 999;
  top: 5px;
  left: 0;
  right: 0;
  margin: auto;
  padding-left: 0.5px;
  width: 30px;
  height: 30px;
  line-height: 26px;
  text-align: center;
  border-radius: 15px;
  background-color: #fff;
  border: 2px solid #567;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease;
}

.loading-trans-enter,
.loading-trans-leave-to {
  transform: translateY(-35px);
}
</style>