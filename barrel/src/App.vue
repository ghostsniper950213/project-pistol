<template>
  <div id="app">
    <!-- <keep-alive :include="keepAlives"> -->
    <navigation>
      <router-view id="main-view" />
    </navigation>
    <!-- </keep-alive> -->
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      // keepAlives: ['Gallery', 'Download'],
      keepAlives: ['Gallery', 'Download']
    }
  }
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
  box-shadow: -1px 0 3px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s cubic-bezier(1, 0, 0, 1);
}
</style>
