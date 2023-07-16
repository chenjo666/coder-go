<script setup lang="ts">
import axios from 'axios';
import { ref, reactive, nextTick } from 'vue'
import { ElMessage, ElInput } from 'element-plus'
/******************************************* 工具区 ***********************************************/
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
const emit = defineEmits(['postResult'])
/******************************************* 数据区 ***********************************************/
// 帖子类型[all、discussion、help、sharing、tutorial、emotional、news、review、survey]
const postForm = ref({
    postTitle: '',
    postContent: '',
    postType: 'discussion',
    postTags: ['并发编程']
})

const inputValue = ref('')
const inputVisible = ref(false)
const InputRef = ref<InstanceType<typeof ElInput>>()
const radio = ref(1)

const map = ['', 'discussion', 'help', 'sharing', 'tutorial', 'emotional', 'news', 'review', 'survey', 'other']
const handleClose = (tag: string) => {
    postForm.value.postTags.splice(postForm.value.postTags.indexOf(tag), 1)
}
const showInput = () => {
    inputVisible.value = true
    nextTick(() => {
        InputRef.value!.input!.focus()
    })
}
const handleInputConfirm = () => {
    if (inputValue.value) {
        postForm.value.postTags.push(inputValue.value)
    }
    inputVisible.value = false
    inputValue.value = ''
}
/******************************************* 事件区 ***********************************************/
const clickPublishPostEvent = () => {
    postForm.value.postType = map[radio.value]
    addPostRequest().then(res => {
        if (res) {
            emit('postResult', res);
            successMsg("发布成功！")
        }
    })
}
/******************************************* 请求区 ***********************************************/
// 发布帖子
const addPostRequest = () => {
    return axios.post('/posts', {"postTitle" : postForm.value.postTitle, 
        "postContent" : postForm.value.postContent, 
        "postType" : postForm.value.postType, 
        "postTags" : postForm.value.postTags
    }).then((response) => {
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
    }).catch(error => {
        console.error(error);
        return false
    });
}
</script>

<template>
    <el-dialog width="80%" center>
        <span>
            <el-text>标题</el-text>
            <el-input v-model="postForm.postTitle"></el-input>
        </span>
        <span>
            <el-text>类型</el-text>
            <div>
                <el-radio-group v-model="radio">
                    <el-radio :label="1">讨论</el-radio>
                    <el-radio :label="2">求助</el-radio>
                    <el-radio :label="3">分享</el-radio>
                    <el-radio :label="4">教程</el-radio>
                    <el-radio :label="5">情感</el-radio>
                    <el-radio :label="6">新闻</el-radio>
                    <el-radio :label="7">评价</el-radio>
                    <el-radio :label="8">调研</el-radio>
                    <el-radio :label="9">其它</el-radio>
                </el-radio-group>
            </div>
        </span>
        <div>
            <p>标签</p>
            <div>
                <el-tag v-for="tag in postForm.postTags" :key="tag" class="mx-1" closable :disable-transitions="false"
                    @close="handleClose(tag)">
                    {{ tag }}
                </el-tag>
                <el-input v-if="inputVisible" ref="InputRef" v-model="inputValue" class="ml-1 w-20" size="small"
                    @keyup.enter="handleInputConfirm" @blur="handleInputConfirm" />
                <el-button v-else class="button-new-tag ml-1" size="small" @click="showInput">
                    + 新增标签
                </el-button>
            </div>
        </div>
        <span>
            <el-text>内容</el-text>
            <v-md-editor v-model="postForm.postContent" height="400px" width="100%"></v-md-editor>
        </span>
        <template #footer>
            <span class="dialog-footer">
                <el-button type="primary" @click="clickPublishPostEvent()">
                    发布
                </el-button>
            </span>
        </template>
    </el-dialog>
</template>