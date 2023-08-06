<script setup>
import { RouterLink, RouterView } from 'vue-router'
import WebsiteHeader from './components/WebsiteHeader.vue'
import WebsiteFooter from './components/WebsiteFooter.vue'
</script>

<template>
  <div class="common-layout">
    <el-container>
      <el-header >
        <WebsiteHeader></WebsiteHeader>
      </el-header>
      <el-main>
        <RouterView/>
      </el-main>
      <el-footer>
        <WebsiteFooter></WebsiteFooter>
      </el-footer>
    </el-container>
  </div>
</template>
<style scoped>
.el-header {
  background-color: white;
  height: 60px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}
.el-main {
  flex-grow: 1;
  overflow: auto;
}
</style>
