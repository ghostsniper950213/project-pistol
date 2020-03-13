<template>
  <button class="btn" :class="type" @click="$emit('click')">
    <slot />
  </button>
</template>

<script>
export default {
  name: 'Btn',
  props: ['type']
}
</script>

<style>
.btn {
  background: none;
  border-radius: 5px;
  height: 40px;
  line-height: 40px;
  padding: 0 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  background-color: #ddd;
}

.btn:active {
  background-color: #ccc;
  box-shadow: none;
}

.btn.blue {
  background-color: #2af;
  color: #fff;
}

.btn.blue:active {
  background-color: #08d;
}

.btn.red {
  background-color: #f55;
  color: #fff;
}

.btn.red:active {
  background-color: #d33;
}
</style>