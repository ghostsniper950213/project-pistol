<template>
  <div class="text-dialog-row">
    <slot />
  </div>
</template>

<script>
export default {
  name: 'TextDialogRow',
}
</script>

<style>
.text-dialog-row {
  margin: 10px;
  display: flex;
  align-items: center;
}

.text-dialog-row > * {
  flex: 1;
}
</style>