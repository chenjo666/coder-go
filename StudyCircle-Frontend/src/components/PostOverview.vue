<script lang="ts" setup>
import { Timer, Pointer, ChatRound, View, Search } from '@element-plus/icons-vue'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage } from 'element-plus'
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
/**************************************** 请求数据区 *****************************************/
// 搜索关键字
// 帖子类型[all、discussion、help、sharing、tutorial、emotional、news、review、survey]
// 排序规则[normal、newest、hotest]
// 页数
const keyInput = ref('')
const keyWord = ref('')
const pageSize = ref(10)
const currentPage = ref(1)
const activeIndex = ref('1')
const postType = ref('all')
const orderMode = ref('normal')
const router = useRouter();
// 在参数变化时刷新数据（示例代码，排序方式变为【综合】，页码为【第一页】）

// 界面展示视图-帖子(avatar, postTitle, isTop, isGem, content, views, likes, comments, publish_time, [postTotal])
const postOverviewVO = ref({
    "posts": [
            {
                "userId": "1676909673324552193",
                "userAvatar": "https://tse3-mm.cn.bing.net/th/id/OIP-C.7f-otWbJRToU6A6s0gNSOQAAAA?w=203&h=203&c=7&r=0&o=5&dpr=1.1&pid=1.7",
                "postId": "1",
                "postTitle": "分享markdown工具",
                "postContent": "# HTML 代码转 MARKDOWN 代码示例\r\n\r\n段落\r\n\r\n## 用法如下：\r\n\r\n[链接1](https://www.ivtool.com) [链接2](https://www.ivtool.com \"链接2\")\r\n\r\n**加重1**\r\n\r\n**加重2**\r\n\r\n_强调_\r\n\r\n图片： ![alt text](/path/to/img.jpg \"Title\")\r\n\r\n* * *\r\n\r\n`代码`\r\n\r\n```\r\n //这是一段代码块\r\ncode.... \r\n```\r\n\r\n*   headingStyle (setext or atx)\r\n*   horizontalRule (\\*, -, or \\_)\r\n*   bullet (\\*, -, or +)\r\n*   codeBlockStyle (indented or fenced)\r\n*   fence (\\` or ~)\r\n*   emDelimiter (\\_ or \\*)\r\n*   strongDelimiter (\\*\\* or \\_\\_)\r\n*   linkStyle (inlined or referenced)\r\n*   linkReferenceStyle (full, collapsed, or shortcut)",
                "postTime": "2023-03-20 20:21:55",
                "postViews": "100",
                "postLikes": "65",
                "postReplies": "69",
                "gem": true,
                "top": true
            }
        ],
    "postTotal":20
})
const keywordSuggestions = ref({
    
})

/********************************************************** 界面事件区 ***************************************************** **/
onMounted(() => {
    getPostListRequest()
        .then(res => {
            if (res != null) {
                postOverviewVO.value = res
            }
        })
})
// （1）搜索：
//  规则将关键字保存，并重新设定帖子类型、帖子排序规则、页码
const clickSearchEvent = () => {
    // 1. 关键字修改
    keyWord.value = keyInput.value
    postType.value = 'all'
    orderMode.value = 'normal'
    currentPage.value = 1
    // 2. 发起请求
    getPostListRequest()
        .then(res => {
            if (res != null) {
                postOverviewVO.value = res
            }
        })
}
// （2）点击帖子类型事件
const clickPostTypeEvent = (newPostType: string) => {
    postType.value = newPostType
    orderMode.value = 'normal'
    currentPage.value = 1

    getPostListRequest()
        .then(res => {
            if (res != null) {
                postOverviewVO.value = res
            }
        })
}
// （3）点击排序规则事件
const clickOrderModeEvent = (newOrderMode: string) => {
    orderMode.value = newOrderMode
    currentPage.value = 1

    getPostListRequest()
        .then(res => {
            if (res != null) {
                postOverviewVO.value = res
            }
        })
}
// （4）点击分页按钮事件
const clickPageEvent = () => {
    getPostListRequest()
        .then(res => {
            if (res != null) {
                postOverviewVO.value = res
            }
        })
}
//  （5）点击帖子事件
const clickPostDetailEvent = (postId) => {
    router.push(`/postDetail/${postId}`)
}

const handleSelect = (item: string) => {
  keyInput.value = item
}
/******************************************************* 数据请求区 *******************************************************/
// （1）请求全部帖子
const getPostListRequest = () => {
    return axios.get("/posts/v2", {
        params: {
            'postType': postType.value,
            'orderMode': orderMode.value,
            'keyWord': keyWord.value,
            'currentPage': currentPage.value,
            'pageSize': pageSize.value
        }
    }).then(response => {
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

    }).catch(error => {
        console.error(error);
        return null
    });
}
//  (2) 请求提示词
const getSuggestionRequest = (queryString: string, cb: (arg: any) => void) => {
    axios.get('/posts/v1/suggestions/', { params: {queryString}})
    .then(res => {
        if (res.status === 200) {
            return null
        }
        const result = res.data.data
        cb(result)
    }).catch(err => {
        console.log(err)
    })
}
</script>

<template>
    <el-card class="box-card">
        <div class="common-layout">
            <el-container>
                <el-header>
                    <div class="post-search">
                        <el-input placeholder="请输入">
                            <template #append>
                                <el-button :icon="Search" @click="clickSearchEvent()" />
                            </template>
                        </el-input>
                    </div>
                </el-header>
                <!-- 主体区域 -->
                <el-main>
                    <!-- 帖子菜单栏 -->
                    <div class="post-menu">
                        <div class="post-menu-type">
                            <el-menu :default-active="activeIndex" mode="horizontal">
                                <el-menu-item index="1" @click="clickPostTypeEvent('all')">全部</el-menu-item>
                                <el-menu-item index="2" @click="clickPostTypeEvent('discussion')">讨论</el-menu-item>
                                <el-menu-item index="3" @click="clickPostTypeEvent('help')">求助</el-menu-item>
                                <el-menu-item index="4" @click="clickPostTypeEvent('sharing')">分享</el-menu-item>
                                <el-menu-item index="5" @click="clickPostTypeEvent('tutorial')">教程</el-menu-item>
                                <el-menu-item index="6" @click="clickPostTypeEvent('emotional')">情感</el-menu-item>
                                <el-menu-item index="7" @click="clickPostTypeEvent('news')">新闻</el-menu-item>
                                <el-menu-item index="8" @click="clickPostTypeEvent('review')">评价</el-menu-item>
                                <el-menu-item index="9" @click="clickPostTypeEvent('survey')">调查</el-menu-item>
                                <el-menu-item index="10" @click="clickPostTypeEvent('other')">其它</el-menu-item>
                            </el-menu>
                        </div>
                        <div class="post-menu-order">
                            <div>
                                <el-button text :bg="orderMode === 'normal'"
                                    @click="clickOrderModeEvent('normal')">综合</el-button>
                                <el-button text :bg="orderMode === 'newest'"
                                    @click="clickOrderModeEvent('newest')">最新</el-button>
                                <el-button text :bg="orderMode === 'hotest'"
                                    @click="clickOrderModeEvent('hotest')">最热</el-button>
                            </div>
                            <p>共 {{ postOverviewVO.postTotal }} 条搜索结果</p>
                        </div>
                    </div>
                    <!-- 帖子列表 -->
                    <div class="post-list">
                        <div class="post-component" v-for="(post, index) in postOverviewVO.posts" :key="index">
                            <div class="post-info" @click="clickPostDetailEvent(post.postId)">
                                <div class="post-header">
                                    <el-avatar :size="30" :src="post.userAvatar" class="post-author" />
                                    <div class="post-postTitle"  v-html="post.postTitle">
                                    </div>
                                    <el-tag v-if="post.top">置顶</el-tag>
                                    <el-tag class="ml-2" type="success" v-if="post.gem">精选</el-tag>
                                </div>
                                <div class="post-body" v-html="post.postContent"></div>
                                <div class="post-footer">
                                    <el-text><el-icon>
                                            <View />
                                        </el-icon>{{ post.postViews }}</el-text>
                                    <el-text><el-icon>
                                            <Pointer />
                                        </el-icon>{{ post.postLikes }}</el-text>
                                    <el-text><el-icon>
                                            <ChatRound />
                                        </el-icon>{{ post.postReplies }}</el-text>
                                    <el-text><el-icon>
                                            <Timer />
                                        </el-icon>{{ post.postTime }}</el-text>
                                </div>
                            </div>
                            <el-divider border-style="dotted" v-if="index !== postOverviewVO.posts.length - 1" />
                        </div>
                    </div>
                </el-main>
                <el-footer>
                    <!-- 分页按钮 -->
                    <div class="center-pagination">
                        <el-pagination @click="clickPageEvent()" v-model:current-page="currentPage" background
                            layout="prev, pager, next" :total="postOverviewVO.postTotal" :default-page-size="pageSize" />
                    </div>
                </el-footer>
            </el-container>
        </div>
    </el-card>
</template>

<style scoped>
.post-component {
    background-color: white;
    border-radius: 4px;
    margin-bottom: 10px;
}

.post-info:hover {
    cursor: pointer;
    background-color: rgb(245, 247, 249);
}

.post-header {
    display: flex;
    align-items: center;
}

.post-author,
.post-postTitle {
    margin-right: 10px;
    font-weight: 600;
}

.post-search,
.post-search .el-input {
    height: 100%;
}

.el-menu-item {
    font-size: 20px;
}

.post-menu-order {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.post-isPerfect {
    background-color: rgb(222, 241, 212);
    color: rgb(90, 183, 38);
    border-radius: 4px;
}

.post-body {
    overflow: hidden;
    text-overflow: ellipsis;
    -webkit-line-clamp: 2;
    display: -webkit-box;
    -webkit-box-orient: vertical;
}

.post-footer {
    width: 100%;
}

.el-text:nth-child(-n + 3) {
    margin-right: 10px;
}

.el-text:nth-child(4) {
    float: right;
}

.row-bg {
    height: 100%;
}

.post-classify {
    display: flex;
    align-items: center;
    /* justify-content: center; */
}

.center-pagination {
    display: flex;
    justify-content: center;
}

.menu-font {
    color: black;
}

.menu-font:hover {
    color: blue;
    cursor: pointer;
}

.active {
    text-decoration: solid;
    color: blue;
}

.menu-container {
    display: flex;
    justify-content: center;
    align-items: center;
}

.el-header>.el-row {
    height: 100%;
}

.el-header>.el-row>.el-col {
    height: 100%;
}
</style>