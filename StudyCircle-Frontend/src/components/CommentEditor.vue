<script setup lang="ts">
import axios from 'axios';
import { Edit } from '@element-plus/icons-vue'
import { ref, reactive } from 'vue'
import { ElMessage, emitChangeFn } from 'element-plus'

// 反馈工具
const successMsg = (msg) => {
  ElMessage({
    showClose: true,
    message: msg,
    type: 'success',
  })
}
const errorMsg = (msg) => {
  ElMessage({
    showClose: true,
    message: msg,
    type: 'error',
  })
}
const commentContent = ref('')
const emit = defineEmits(['commentResult'])
const object = defineProps({
  objectId: {
    type: String
  },
  objectType: {
    type: String
  },
  parentIndex: {
    type: Number
  },
  childIndex: {
    type: Number
  }
})

// 发送评论事件
const clickSendCommentEvent = () => {
  console.log('send comment:' + object.objectId + ", " + object.objectType)
  console.log('commentEditor: ' + object.parentIndex + ", " + object.childIndex)
  addCommentRequest(object.objectId, object.objectType, commentContent.value)
  .then(res => {
    if (res != null) {
      commentContent.value = ''
      successMsg('评论成功！')
      emit('commentResult', object.parentIndex, object.childIndex)
    }
  }).catch(err => {
    console.log(err)
  })
}

// 发送评论请求
const addCommentRequest = (objectId : string | undefined, objectType : string | undefined, content: string) => {
  return axios.post("/comments", {"objectId" : objectId, "objectType" : objectType, "content" : content})
    .then(response => {
      if (response.status !== 200) {
        errorMsg('网络请求出错!')
        return false
      }
      const ans = response.data

      if (ans.code !== 200) {
        errorMsg(ans.msg)
        return false
      }
      return true
    })
    .catch(error => {
      console.error(error);
      return false
    });
}

</script>

<template>
  <div class="comment-editor-container">
    <div class="border-wrapper">
      <div class="editor-buttons-wrapper">
        <v-md-editor v-model="commentContent" height="200px" width="100%" mode="edit"></v-md-editor>
        <div class="button-wrapper">
          <el-button type="primary" :icon="Edit" @click="clickSendCommentEvent()">评论</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.comment-editor-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.border-wrapper {
  border-radius: 5px;
  padding: 8px;
}

.editor-buttons-wrapper {
  display: flex;
  flex-direction: column;
}

.button-wrapper {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  background-color: white;
  box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.1);
  margin-top: 1px;
  height: 40px;
  padding-right: 10px;
}

.button-wrapper .el-button {
  margin-right: 5px;
  /* 将按钮从右边靠近一点 */
}
</style>