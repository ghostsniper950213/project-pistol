<template>
  <div class="list-item">
    <slot />
  </div>
</template>

<script>
export default {
  name: 'ListItem',
}
</script>

<style>
.list-item {
  display: flex;
  align-items: center;
  border-bottom: 1px solid #ddd;
}
</style>