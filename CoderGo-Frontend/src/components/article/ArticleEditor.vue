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
const emit = defineEmits(['articleResult'])
/******************************************* 数据区 ***********************************************/
// 文章类型[all、discussion、help、sharing、tutorial、emotional、news、review、survey]
const articleForm = ref({
    articleTitle: '',
    articleContent: '',
    articleType: 'operating-system',
    articleTags: ['并发编程']
})

const inputValue = ref('')
const inputVisible = ref(false)
const InputRef = ref<InstanceType<typeof ElInput>>()
const radio = ref(0)

const map = ['base', 'frontend', 'backend', 'testing', 'DevOps',
    'architecture', 'design', 'database', 'tool', 'other']
const map_SHOW = ['计算机基础', '前端', '后端', '测试', '运维', '架构', '设计', '数据库', '工具', '其它']

const handleClose = (tag: string) => {
    articleForm.value.articleTags.splice(articleForm.value.articleTags.indexOf(tag), 1)
}
const showInput = () => {
    inputVisible.value = true
    nextTick(() => {
        InputRef.value!.input!.focus()
    })
}
const handleInputConfirm = () => {
    if (inputValue.value) {
        articleForm.value.articleTags.push(inputValue.value)
    }
    inputVisible.value = false
    inputValue.value = ''
}
/******************************************* 事件区 ***********************************************/
const clickPublishArticleEvent = () => {
    articleForm.value.articleType = map[radio.value]
    addArticleRequest().then(res => {
        if (res) {
            emit('articleResult', res);
            successMsg("发布成功！")
        }
    })
}
/******************************************* 请求区 ***********************************************/
// 发布文章
const addArticleRequest = () => {
    return axios.post('/article/v1/articles', {
        "articleTitle": articleForm.value.articleTitle,
        "articleContent": articleForm.value.articleContent,
        "articleType": articleForm.value.articleType,
        "articleTags": articleForm.value.articleTags
    }).then((response) => {
        if (response.status === 200 && (response.data.code >= 200 && response.data.code <= 299)) {
            return true;
        }
        errorMsg(response.status === 200 ? '网络错误' : response.data.msg);
        return false;

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
            <el-input v-model="articleForm.articleTitle"></el-input>
        </span>
        <span>
            <el-text>类型</el-text>
            <div>
                <el-radio-group v-model="radio">
                    <el-radio :label="index" v-for="(tag, index) in map" :key="index">{{ map_SHOW[index] }}</el-radio>
                </el-radio-group>
            </div>
        </span>
        <div>
            <p>标签</p>
            <div>
                <el-tag v-for="tag in articleForm.articleTags" :key="tag" class="mx-1" closable :disable-transitions="false"
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
            <v-md-editor v-model="articleForm.articleContent" height="400px" width="100%"></v-md-editor>
        </span>
        <template #footer>
            <span class="dialog-footer">
                <el-button type="primary" @click="clickPublishArticleEvent()">
                    发布
                </el-button>
            </span>
        </template>
    </el-dialog>
</template>