<template>
  <div class="tool-bar">
    <slot />
  </div>
</template>

<script>
export default {
  name: 'ToolBar',
}
</script>

<style>
.tool-bar {
  position: fixed;
  left: 10px;
  right: 10px;
  bottom: 10px;
  height: 40px;
  display: flex;
  overflow: hidden;
  align-items: center;
  justify-content: center;
  background-color: #f6f6f6;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}
</style>