<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { Delete } from '@element-plus/icons-vue'
import IconLike from "../icons/IconLike.vue"
import IconFavorite from "../icons/IconFavorite.vue"
import IconFollow from "../icons/IconFollow.vue"
import IconComment from '../icons/IconComment.vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router';
/******************************************* 工具区 *************************************************/
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

/******************************************* 数据区 *************************************************/
const UIVO = ref({
    "noticeVOList": [
        {
            "userId": "1676909673324552207",
            "userAvatar": "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
            "userName": "用户KpDY9Rxs",
            "noticeId": "1679733498152034310",
            "noticeType": null,
            "noticeCreatedAt": "2023-06-26 15:33:46",
            "articleId": "1679041502378770434",
            "articleTitle": "JVM之-内层管理",
            "read": true
        }
    ],
    "noticeUnRead": 9,
    "noticeTotal": 30
})
const currentPage = ref(1)
const PAGE_SIZE = 10
const activeIndex = ref('1')
const router = useRouter()
const NOTICE_TYPE = {
    "ARTICLE_LIKE": 1,
    "ARTICLE_REPLY": 2,
    "ARTICLE_FAVORITE": 3,
    "ARTICLE_COMMENT_LIKE": 4,
    "ARTICLE_COMMENT_REPLY": 5,
    "USER_FOLLOW": 6
}
/******************************************* 事件区 *************************************************/
onMounted(() => {
    clickPageEvent()
})
// (1) 删除全部通知
const deleteAllNoticeEvent = () => {
    deleteAllNoticeRequest()
        .then(res => {
            if (res) {
                UIVO.value.noticeTotal = 0
                UIVO.value.noticeUnRead = 0
                UIVO.value.noticeVOList = []
            }
        })
}
// (2) 删除单个通知
const deleteSingleNoticeEvent = (index) => {
    deleteSingleNoticeRequest(UIVO.value.noticeVOList[index].noticeId)
        .then(res => {
            if (res) {
                // 重新请求此页消息
                clickPageEvent()
            }
        })
}

// (3) 消息分页
const clickPageEvent = () => {
    getNoticeRequest(currentPage.value, PAGE_SIZE)
        .then(res => {
            UIVO.value = res?.data
        })
}

// (4) 路由个人信息
const clickPersonEvent = (userId) => {
    router.push(`/person/${userId}`)
}

/******************************************* 请求区 *************************************************/
// (1) 删除全部通知
const deleteAllNoticeRequest = () => {
    return axios.delete(`/notice/v1/notices`)
        .then(response => {
            if (response.status === 200 && (response.data.code >= 200 || response.data.code <= 299)) {
                successMsg('删除成功')
                return true;
            }
            errorMsg(response.status === 200 ? '网络错误' : response.data.msg);
            return false;
        })
        .catch(error => {
            console.error(error);
            return false
        });
}
// (2) 删除单个通知
const deleteSingleNoticeRequest = (noticeId) => {
    return axios.delete(`/notice/v1/notices/${noticeId}`)
        .then(response => {
            if (response.status === 200 && (response.data.code >= 200 || response.data.code <= 299)) {
                successMsg('删除成功')
                return true;
            }
            errorMsg(response.status === 200 ? '网络错误' : response.data.msg);
            return false;
        })
        .catch(error => {
            console.error(error);
            return false
        });
}
// (3) 获取通知
const getNoticeRequest = (page, limit) => {
    return axios.get("/notice/v1/notices", { params: { page, limit } })
        .then(response => {
            if (response.status === 200 && (response.data.code >= 200 || response.data.code <= 299)) {
                return response.data;
            }
            errorMsg(response.status === 200 ? '网络错误' : response.data.msg);
            return null;
        })
        .catch(error => {
            console.error(error);
            return null
        });
}

</script>

<template>
    <el-card>
        <div class="notice-container">
            <div class="notice-core">
                <div class="notice-nav">
                    <el-row>
                        <el-col :span="4" class="notice-nav-info">
                            <div class="grid-content ep-bg-purple notice-nav-left">
                                <!-- <h2>通知</h2> -->
                                <el-menu :default-active="activeIndex" :ellipsis="false" class="el-menu-demo"
                                    mode="horizontal">
                                    <el-menu-item index="1">通知</el-menu-item>
                                </el-menu>
                            </div>
                        </el-col>
                        <el-col :span="12">
                            <div class="grid-content ep-bg-purple"></div>
                        </el-col>
                        <el-col :span="8" class="notice-nav-info">
                            <div class="grid-content ep-bg-purple notice-nav-right">
                                <el-text>共 {{ UIVO.noticeTotal }} 条通知，还剩 {{ UIVO.noticeUnRead }} 条未读&emsp;</el-text>
                                <!-- <el-button type="primary" :icon="Delete">全部清空</el-button> -->
                                <el-popconfirm title="是否全部删除？" @confirm="deleteAllNoticeEvent()">
                                    <template #reference>
                                        <el-button type="primary" :icon="Delete">全部清空</el-button>
                                    </template>
                                </el-popconfirm>
                            </div>
                        </el-col>
                    </el-row>
                </div>
                <div class="notice-list">
                    <div v-for="(notice, index) in UIVO.noticeVOList" :key="index" class="notice-item">
                        <el-row>
                            <!-- 用户头像、事件类型 -->
                            <el-col :span="1" class="notice-item-info">
                                <div class="grid-content ep-bg-purple notice-item-avatar">
                                    <div class="notice-avatar-container">
                                        <el-badge is-dot value="新" class="item" :hidden="notice.read">
                                                <el-avatar :src="notice.userAvatar" class="notice-avatar" 
                                                    @click="clickPersonEvent(notice.userId)" alt="Your Image">
                                                </el-avatar>
                                        </el-badge>
                                        <el-icon class="notice-icon">
                                            <IconLike v-if="notice.noticeType == NOTICE_TYPE.ARTICLE_LIKE 
                                                || notice.noticeType == NOTICE_TYPE.ARTICLE_COMMENT_LIKE" />
                                            <IconComment v-if="notice.noticeType == NOTICE_TYPE.ARTICLE_REPLY 
                                                || notice.noticeType == NOTICE_TYPE.ARTICLE_COMMENT_REPLY" />
                                            <IconFavorite v-if="notice.noticeType == NOTICE_TYPE.ARTICLE_FAVORITE" />
                                            <IconFollow v-if="notice.noticeType == NOTICE_TYPE.USER_FOLLOW" />
                                        </el-icon>
                                    </div>
                                </div>
                            </el-col>
                            <!-- 用户名、时间、事件 -->
                            <el-col :span="22" class="notice-item-info">
                                <div class="grid-content ep-bg-purple">
                                    <div class="notice-time">
                                        <p>{{ notice.userName }} &emsp; {{ notice.noticeCreatedAt }}</p>
                                    </div>
                                    <div class="notice-event" v-if="notice.noticeType == NOTICE_TYPE.ARTICLE_LIKE">
                                        点赞了您的文章
                                        <RouterLink :to="{ path: `/articleDetail/${notice.articleId}` }" class="notice-item-link">
                                            {{ notice.articleTitle }}</RouterLink>
                                    </div>
                                    <div class="notice-event" v-if="notice.noticeType == NOTICE_TYPE.ARTICLE_COMMENT_LIKE">
                                        在
                                        <RouterLink :to="{ path: `/articleDetail/${notice.articleId}` }" class="notice-item-link">
                                            {{ notice.articleTitle }}</RouterLink>
                                        点赞了您的评论
                                    </div>
                                    <div class="notice-event" v-if="notice.noticeType == NOTICE_TYPE.ARTICLE_REPLY">
                                        评论了您的文章
                                        <RouterLink :to="{ path: `/articleDetail/${notice.articleId}` }" class="notice-item-link">
                                            {{ notice.articleTitle }}</RouterLink>
                                    </div>
                                    <div class="notice-event" v-if="notice.noticeType == NOTICE_TYPE.ARTICLE_COMMENT_REPLY">
                                        在
                                        <RouterLink :to="{ path: `/articleDetail/${notice.articleId}` }" class="notice-item-link">
                                            {{ notice.articleTitle }}</RouterLink>
                                        回复了您的评论
                                    </div>
                                    <div class="notice-event" v-if="notice.noticeType == NOTICE_TYPE.ARTICLE_FAVORITE">
                                        收藏了您的文章
                                        <RouterLink :to="{ path: `/articleDetail/${notice.articleId}` }" class="notice-item-link">
                                            {{ notice.articleTitle }}</RouterLink>
                                    </div>
                                    <div class="notice-event" v-if="notice.noticeType == NOTICE_TYPE.USER_FOLLOW">
                                        关注了您
                                    </div>
                                </div>
                            </el-col>
                            <!-- 删除图标 -->
                            <el-col :span="1" class="notice-item-info">
                                <div class="grid-content ep-bg-purple notice-delete-container">
                                    <el-popconfirm title="是否删除此通知？" @confirm="deleteSingleNoticeEvent(index)">
                                        <template #reference>
                                            <el-icon size="20" class="notice-delete">
                                                <Delete />
                                            </el-icon>
                                        </template>
                                    </el-popconfirm>
                                </div>
                            </el-col>
                        </el-row>
                    </div>
                </div>
                <div class="notice-page">
                    <el-pagination @click="clickPageEvent()" v-model:current-page="currentPage" background
                        layout="prev, pager, next" :total="UIVO.noticeTotal" :default-page-size="PAGE_SIZE" />
                </div>
            </div>
        </div>
    </el-card>
</template>


<style scoped>
/** 头部信息栏：通知字体 + 显示通知数量字体 + 删除按钮 */
.notice-nav .el-row {
    height: 80px;
}

.notice-nav-info {
    display: flex;
    align-items: center;
}

.notice-nav-left {
    display: flex;
    justify-content: flex-start;
}

.notice-nav-right {
    display: flex;
    margin-left: auto;
    /* 使用 margin-left: auto 将内容推向右边 */
}


/**  */
.notice-item {
    border-bottom: 2px solid rgb(245, 245, 245);
}

.notice-item .el-row {
    height: 80px;
}

.notice-item-info {
    display: flex;
    align-items: center;
}

.notice-item-avatar,
.notice-page {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 60px;
}

.notice-page {
    margin-top: 20px;
}

/** 头像 */
.notice-avatar-container {
    position: relative;
    display: inline-block;
    height: 50px;
}

.notice-avatar {
    /* height: 90%;
    width: 90%; */
    width: 50px;
    height: 50px;
}

.notice-avatar:hover {
    cursor: pointer;
}

.notice-icon {
    position: absolute;
    bottom: 0;
    right: 0;
    border-radius: 50%;
    background-color: white;
    /* 设置图标的背景图片 */
    background-size: cover;
    /* 根据需要调整图标的大小 */
}

.notice-item-link {
    color: blue;
}

.notice-item-link:hover {
    background-color: white;
}
.notice-event {
    font-size: 18px;
}
/** 删除图标 */
.notice-delete-container {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    /* 将内容推向右边 */
}

.notice-delete:hover {
    cursor: pointer;
    color: blue;
}</style>