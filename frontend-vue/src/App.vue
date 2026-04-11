<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

onMounted(() => {
  userStore.initializeUser()
})
</script>

<style lang="scss">
#app {
  width: 100%;
  min-height: 100vh;
}
</style>
