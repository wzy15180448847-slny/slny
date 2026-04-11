<template>
  <div class="main-layout">
    <app-navbar />
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    <app-footer />
  </div>
</template>

<script setup>
import AppNavbar from '@/components/layout/Navbar.vue'
import AppFooter from '@/components/layout/Footer.vue'
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.main-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  flex: 1;
  background: $bg-color;
}
</style>
