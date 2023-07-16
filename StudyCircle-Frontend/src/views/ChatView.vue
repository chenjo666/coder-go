<script lang="ts" setup>
import AIChat from '../components/AIChat.vue'
</script>

<template>
  <el-row :gutter="10" class="main">
    <el-col :span="3" class="main-content">
      <div class="grid-content ep-bg-purple core-content">
        <el-card>广告栏出租</el-card>
      </div>
    </el-col>
    <!-- 核心 -->
    <el-col :span="18" class="main-content">
      <div class="grid-content ep-bg-purple core-content">
        <AIChat></AIChat>
      </div>
    </el-col>
    <el-col :span="3" class="main-content">
      <div class="grid-content ep-bg-purple core-content">
        <el-card>统计栏拓展</el-card>
      </div>
    </el-col>
  </el-row>
</template>

<style>
</style>



<template>

</template>