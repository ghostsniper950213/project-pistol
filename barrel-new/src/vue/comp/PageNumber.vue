<template>
  <div class="page-number">
    <div>{{ pageNumber }}</div>
    <div>{{ totalPages }}</div>
  </div>
</template>

<script>
export default {
  name: 'PageNumber',
  props: ['pageNumber', 'totalPages']
}
</script>

<style>
.page-number {
  height: 40px;
  padding: 5px 0;
}

.page-number div {
  line-height: 15px;
  border-bottom: 1px solid #000;
  font-size: 12px;
}

.page-number div:last-of-type {
  border-bottom: none;
}
</style>