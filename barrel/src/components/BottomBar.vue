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
  box-shadow: 0 -1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  white-space: nowrap;
}

.bottom-bar-left {
  flex: 1;
  white-space: nowrap;
}

.bottom-bar-right {
  flex: 1;
  text-align: right;
  white-space: nowrap;
}

.bottom-bar button {
  background: none;
  line-height: 40px;
  padding: 0 10px;
}

.bottom-bar button:active {
  background-color: #eee;
}
</style>