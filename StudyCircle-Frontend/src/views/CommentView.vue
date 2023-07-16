<script lang="ts" setup>
import PostDetail from '../components/PostDetail.vue';
</script>


<template>
    <el-row :gutter="10" class="main">
        <el-col :span="3" class="main-content">
            <div class="grid-content ep-bg-purple core-content"><el-card>广告栏出租</el-card></div>
        </el-col>
        <!-- 核心 -->
        <el-col :span="18" class="main-content">
            <div class="grid-content ep-bg-purple core-content">
                <PostDetail></PostDetail>
            </div>
        </el-col>
        <el-col :span="3" class="main-content">
            <div class="grid-content ep-bg-purple core-content">
                <el-card>统计栏拓展</el-card>
            </div>
        </el-col>
    </el-row>
</template>

<style scoped>
.main-content {
  height: 100%;
  flex: 1;
}
.core-content {
  background-color: white;
  height: 100%;
}

.grid-content {
    border-radius: 4px;
    height: 100%;
}
</style>