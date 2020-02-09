<template>
  <div class="ipt">
    <icon :icon="icon" />
    <input :type="type" v-model="val"/>
  </div>
</template>

<script>
import Icon from '@/components/Icon'

export default {
  name: 'Ipt',
  components: {
    Icon
  },
  props: ['icon', 'value', 'type'],
  data() {
    return {
      val: this.value
    }
  },
  watch: {
    value(next) {
      this.val = next
    },
    val(next) {
      this.$emit('input', next)
    }
  }
}
</script>

<style>
.ipt {
  position: relative;
}

.ipt input {
  width: 100%;
  height: 40px;
  border-radius: 10px;
  outline: none;
  background-color: rgba(0, 0, 0, 0.1);
  opacity: 1;
  padding: 0 10px 0 40px;
}

.ipt .icon {
  position: absolute;
  left: 12px;
  top: 12px;
}
</style>