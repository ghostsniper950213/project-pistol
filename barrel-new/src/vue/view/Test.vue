<template>
  <div>
    <div id="input-test">
      <text-input icon="search" v-model="value" />
    </div>
    <div id="list-test">
      <list-container>
        <list-item>
      </list-item>
        <list-item>312</list-item>
      </list-container>
    </div>
    <text-button style="margin: 40px">
      <image-icon icon="reload" />确认
    </text-button>
    <tool-bar>
      <tool-bar-item width="60">
        <image-icon icon="reload" />
      </tool-bar-item>
      <tool-bar-item>2</tool-bar-item>
      <tool-bar-item width="60">3</tool-bar-item>
    </tool-bar>
  </div>
</template>

<script>
export default {
  name: 'Test',
  data() {
    return {
      value: '',
    }
  },
}
</script>

<style scoped>
#input-test {
  margin: 20px;
}

#list-test {
  margin-top: 20px;
}
</style>