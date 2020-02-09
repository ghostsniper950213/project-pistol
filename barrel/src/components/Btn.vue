<template>
  <button class="btn" @click="$emit('click')">
    <slot />
  </button>
</template>

<script>
export default {
  name: 'Btn'
}
</script>

<style>
.btn {
  background: none;
  border-radius: 10px;
  height: 40px;
  padding: 0 10px;
}

.btn:active {
  background-color: #eee;
}
</style>