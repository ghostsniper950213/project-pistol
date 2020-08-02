<template>
  <button class="text-button" :class="type" @click="$emit('click')">
    <slot />
  </button>
</template>

<script>
export default {
  name: 'TextButton',
  props: {
    type: String,
  },
}
</script>

<style>
.text-button {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 35px;
  line-height: 35px;
  padding: 0 10px;
  border: none;
  border-radius: 5px;
  background-color: #eee;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.25);
}

.text-button:active {
  background-color: #ddd;
  box-shadow: none;
}

.text-button.blue {
  background-color: #48d;
  box-shadow: 0 1px 2px rgba(0, 20, 100, 0.35);
  color: #fff;
}

.text-button.blue:active {
  background-color: #37c;
  box-shadow: none;
}

.text-button.orange {
  background-color: #f84;
  box-shadow: 0 1px 2px rgba(100, 20, 0, 0.35);
  color: #fff;
}

.text-button.orange:active {
  background-color: #e73;
  box-shadow: none;
}
</style>