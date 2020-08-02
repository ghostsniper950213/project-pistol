<template>
  <div class="text-input">
    <img v-if="icon" :src="getImg()" />
    <input :type="type" v-model="innerValue" :style="icon ? {paddingLeft: '31px'} : {}" />
  </div>
</template>

<script>
export default {
  name: 'TextInput',
  props: {
    type: {
      type: String,
      default: 'text',
    },
    value: {},
    icon: String,
  },
  mounted() {
    this.innerValue = this.value
  },
  data() {
    return {
      innerValue: '',
    }
  },
  methods: {
    getImg() {
      return require('@/img/' + this.icon + '.png')
    },
  },
  watch: {
    value(next) {
      this.innerValue = next
    },
    innerValue(next) {
      this.$emit('input', next)
    },
  },
}
</script>

<style>
.text-input {
  position: relative;
  height: 40px;
}

.text-input img {
  position: absolute;
  left: 10px;
  top: 0;
  bottom: 0;
  margin: auto;
  width: 16px;
  height: 16px;
}

.text-input input {
  width: 100%;
  height: 100%;
  padding: 0 10px;
  border-radius: 5px;
  border: 1px solid #eee;
  background-color: #eee;
}

.text-input input:focus {
  border: 1px solid #aaa;
}
</style>