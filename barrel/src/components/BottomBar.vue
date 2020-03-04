<template>
  <div class="bottom-bar">
    <slot />
  </div>
</template>

<script>
export default {
  name: 'BottomBar'
}
</script>

<style>
.bottom-bar {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 40px;
  white-space: nowrap;
  background-color: #fff;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  text-align: center;
  z-index: 10;
}

.bottom-bar button {
  background: none;
  line-height: 40px;
  width: 60px;
}

.bottom-bar button:active {
  background-color: #ddd;
}
</style>