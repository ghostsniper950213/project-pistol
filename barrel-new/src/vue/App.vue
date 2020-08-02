<template>
  <div id="app">
    <vue-page-stack>
      <router-view id="view"/>
    </vue-page-stack>
  </div>
</template>

<style>
#app {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: auto;
}

#view {
  width: 100%;
  height: 100%;
}
</style>
