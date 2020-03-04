<template>
  <div id="app">
    <navigation>
      <router-view id="main-view" />
    </navigation>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen,
    Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  border: none;
  outline: none;
  box-sizing: border-box;
  margin: 0;
  padding: 0;
  font-size: 16px;
  color: #234;
  -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
  -webkit-appearance: none;
}

body {
  overflow: hidden;
}

#main-view {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
}
</style>
