<template>
  <div class="scroller" @scroll="handleScroll" ref="scroller">
    <slot class="scroller-content"/>
  </div>
</template>

<script>
export default {
  name: 'Scroller',
  activated() {
    this.$refs.scroller.scrollTop = this.scrollTop
  },
  data() {
    return {
      scrollTop: 0
    }
  },
  methods: {
    handleScroll(e, x) {
      this.$emit('scroll', e)
      this.scrollTop = this.$refs.scroller.scrollTop
    }
  }
}
</script>

<style>
.scroller {
  overflow-y: auto;
  overflow-x: hidden;
  background-color: #fff;
}
</style>