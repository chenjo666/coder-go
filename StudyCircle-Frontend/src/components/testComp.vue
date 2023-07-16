<template>
  <el-button text @click="dialogVisible = true">
    Click to open Dialog
  </el-button>

  <el-dialog v-model="dialogVisible" title="Tips" width="30%" draggable>
    <span>It's a draggable Dialog</span>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="dialogVisible = false">
          Confirm
        </el-button>
      </span>
    </template>
  </el-dialog>

  <v-md-editor v-model="text" height="400px"></v-md-editor>

  <div class="image-container">
    <el-avatar :src="'https://img.zcool.cn/community/01f0865d45230ba8012187f485a17b.jpg@1280w_1l_2o_100sh.jpg'"  class="image"
      alt="Your Image">
      </el-avatar>
      <el-icon class="icon"><Plus /></el-icon>
  </div>
</template>
  
<script lang="ts" setup>
import { ref } from 'vue'
import { Plus, Edit, ChatDotRound, DocumentCopy, RefreshRight, Delete, Setting, Present, Scissor, Timer, Position, More } from '@element-plus/icons-vue'
const dialogVisible = ref(false)
const text = ref('')
</script>

<style scoped>
.image-container {
  position: relative;
  display: inline-block;
  height: 40px;
}
.image-container .image {
  width: 40px;
  height: 40px;
}
.icon {
  position: absolute;
  bottom: 0;
  right: 0;
  border-radius: 50%;
  /* 设置图标的宽度 */
  /* 设置图标的高度 */
  background-image: url('your-icon-url');
  background-color: white;
  /* 设置图标的背景图片 */
  background-size: cover;
  /* 根据需要调整图标的大小 */
}</style>