<template>
  <div class="landlord-layout">
    <landlord-navbar />
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
import LandlordNavbar from '@/components/layout/LandlordNavbar.vue'
import AppFooter from '@/components/layout/Footer.vue'
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.landlord-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  flex: 1;
  background: $bg-color;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>