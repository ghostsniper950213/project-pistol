<template>
  <transition name="loading-trans">
    <div class="loading" v-show="show">
      <icon icon="circle-notch" class="fa-spin"/>
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
  width: 30.333333333px;
  height: 30.333333333px;
  line-height: 26.166666666px;
  text-align: center;
  border-radius: 20px;
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