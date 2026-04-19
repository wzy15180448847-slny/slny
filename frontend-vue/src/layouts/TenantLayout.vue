<template>
  <div class="tenant-layout">
    <tenant-navbar />
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
import TenantNavbar from '@/components/layout/TenantNavbar.vue'
import AppFooter from '@/components/layout/Footer.vue'
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.tenant-layout {
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