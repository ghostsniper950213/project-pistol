<template>
  <div class="tool-bar-item" :style="width ? {width: width + 'px'} : {flex: 1}">
    <slot />
  </div>
</template>

<script>
export default {
  name: 'ToolBarItem',
  props: {
    width: [String, Number],
  },
}
</script>

<style>
.tool-bar-item {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}
</style>