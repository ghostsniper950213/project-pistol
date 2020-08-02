<template>
  <div class="list-container" @scroll="$emit('scroll', $event)">
    <slot />
  </div>
</template>

<script>
export default {
  name: 'ListContainer'
}
</script>

<style>
.list-container {
  overflow-x: hidden;
  overflow-y: scroll;
}
</style>