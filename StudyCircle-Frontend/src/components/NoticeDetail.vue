<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { Delete } from '@element-plus/icons-vue'
import IconLike from "./icons/IconLike.vue"
import IconFavorite from "./icons/IconFavorite.vue"
import IconFollow from "./icons/IconFollow.vue"
import IconComment from './icons/IconComment.vue'
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
            "noticeTime": "2023-06-26 15:33:46",
            "postId": "1679041502378770434",
            "postTitle": "JVM之-内层管理",
            "read": true
        },
        {
            "userId": "1676909673391661094",
            "userAvatar": "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
            "userName": "用户FhG22y8e",
            "noticeId": "1679733498080731139",
            "noticeType": null,
            "noticeTime": "2023-06-14 20:57:36",
            "postId": "39",
            "postTitle": "求助标题13",
            "read": true
        },
        {
            "userId": "1676909673391661076",
            "userAvatar": "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
            "userName": "用户E4kvSBmj",
            "noticeId": "1679733498152034311",
            "noticeType": null,
            "noticeTime": "2023-05-27 20:56:37",
            "postId": "38",
            "postTitle": "讨论标题13",
            "read": true
        },
        {
            "userId": "1676909673454575623",
            "userAvatar": "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
            "userName": "用户CkebTqQi",
            "noticeId": "1679733498152034316",
            "noticeType": null,
            "noticeTime": "2023-05-14 19:58:08",
            "postId": "6",
            "postTitle": "设计",
            "read": true
        },
        {
            "userId": "1676909673324552202",
            "userAvatar": "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
            "userName": "用户doP25G0R",
            "noticeId": "1679733498152034307",
            "noticeType": null,
            "noticeTime": "2023-04-29 02:32:02",
            "postId": "33",
            "postTitle": "求助标题11",
            "read": true
        },
        {
            "userId": "1676909673391661095",
            "userAvatar": "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
            "userName": "用户l1TJkmHb",
            "noticeId": "1679733498122674178",
            "noticeType": null,
            "noticeTime": "2023-04-16 13:03:39",
            "postId": "33",
            "postTitle": "求助标题11",
            "read": true
        },
        {
            "userId": "1676909673391661075",
            "userAvatar": "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
            "userName": "用户fHCzQ78g",
            "noticeId": "1679733498080731138",
            "noticeType": null,
            "noticeTime": "2023-04-08 17:05:03",
            "postId": "1679041502378770434",
            "postTitle": "JVM之-内层管理",
            "read": true
        },
        {
            "userId": "1676909673391661083",
            "userAvatar": "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
            "userName": "用户jvhgSMYK",
            "noticeId": "1679733498198171649",
            "noticeType": null,
            "noticeTime": "2023-03-10 15:48:35",
            "postId": "14",
            "postTitle": "讨论标题5",
            "read": true
        },
        {
            "userId": "1676909673324552195",
            "userAvatar": "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
            "userName": "用户fU0RUVfZ",
            "noticeId": "1679733498152034306",
            "noticeType": null,
            "noticeTime": "2023-03-10 11:41:33",
            "postId": "18",
            "postTitle": "求助标题6",
            "read": true
        },
        {
            "userId": "1676909673391661069",
            "userAvatar": "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
            "userName": "用户x5kabXwF",
            "noticeId": "1679733498072342530",
            "noticeType": null,
            "noticeTime": "2023-03-01 12:16:23",
            "postId": "16",
            "postTitle": "分享标题6",
            "read": true
        }
    ],
    "noticeUnRead": 9,
    "noticeTotal": 30
})
const currentPage = ref(1)
const pageSize = ref(10)
const activeIndex = ref('1')
const router = useRouter()
// 1. 点赞帖子 2. 点赞评论  3. 评论帖子 4. 评论评论 5. 收藏帖子 6. 关注用户
const NOTICE_TYPE = ref([1, 2, 3, 4, 5, 6])
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
                // 反馈
                successMsg('删除成功！')
                // 重新请求此页消息
                clickPageEvent()
            }
        })
}

// (3) 消息分页
const clickPageEvent = () => {
    getNoticeRequest(currentPage.value, pageSize.value)
        .then(res => {
            if (res) {
                UIVO.value = res
            }
        })
}

// (4) 路由个人信息
const clickPersonEvent = (userId) => {
    router.push(`/person/${userId}`)
}

/******************************************* 请求区 *************************************************/
// (1) 删除全部通知
const deleteAllNoticeRequest = () => {
    return axios.delete(`/notifications`)
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
// (2) 删除单个通知
const deleteSingleNoticeRequest = (noticeId) => {
    return axios.delete(`/notifications/${noticeId}`)
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
// (3) 获取通知
const getNoticeRequest = (page, limit) => {
    return axios.get("/notifications", { params: { page, limit } })
        .then(response => {
            if (response.status !== 200) {
                errorMsg('网络请求出错!')
                return null
            }
            const ans = response.data

            if (ans.code !== 200) {
                errorMsg(ans.msg)
                return null
            }
            return ans.data
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
                                            <IconLike v-if="notice.noticeType == 1 || notice.noticeType == 2" />
                                            <IconComment v-if="notice.noticeType == 3 || notice.noticeType == 4" />
                                            <IconFavorite v-if="notice.noticeType == 5" />
                                            <IconFollow v-if="notice.noticeType == 6" />
                                        </el-icon>
                                    </div>
                                </div>
                            </el-col>
                            <!-- 用户名、时间、事件 -->
                            <el-col :span="22" class="notice-item-info">
                                <div class="grid-content ep-bg-purple">
                                    <div class="notice-time">
                                        <p>{{ notice.userName }} &emsp; {{ notice.noticeTime }}</p>
                                    </div>
                                    <div class="notice-event" v-if="notice.noticeType == 1">
                                        点赞了您的帖子
                                        <RouterLink :to="{ path: `/postDetail/${notice.postId}` }" class="notice-item-link">
                                            {{ notice.postTitle }}</RouterLink>
                                    </div>
                                    <div class="notice-event" v-if="notice.noticeType == 2">
                                        在
                                        <RouterLink :to="{ path: `/postDetail/${notice.postId}` }" class="notice-item-link">
                                            {{ notice.postTitle }}</RouterLink>
                                        点赞了您的评论
                                    </div>
                                    <div class="notice-event" v-if="notice.noticeType == 3">
                                        评论了您的帖子
                                        <RouterLink :to="{ path: `/postDetail/${notice.postId}` }" class="notice-item-link">
                                            {{ notice.postTitle }}</RouterLink>
                                    </div>
                                    <div class="notice-event" v-if="notice.noticeType == 4">
                                        在
                                        <RouterLink :to="{ path: `/postDetail/${notice.postId}` }" class="notice-item-link">
                                            {{ notice.postTitle }}</RouterLink>
                                        回复了您的评论
                                    </div>
                                    <div class="notice-event" v-if="notice.noticeType == 5">
                                        收藏了您的帖子
                                        <RouterLink :to="{ path: `/postDetail/${notice.postId}` }" class="notice-item-link">
                                            {{ notice.postTitle }}</RouterLink>
                                    </div>
                                    <div class="notice-event" v-if="notice.noticeType == 6">
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
                        layout="prev, pager, next" :total="UIVO.noticeTotal" :default-page-size="pageSize" />
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