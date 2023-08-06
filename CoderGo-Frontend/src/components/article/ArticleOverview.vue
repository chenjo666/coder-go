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
const router = useRouter();
/**************************************** 请求数据区 *****************************************/
// 搜索关键字
// 文章类型[all、discussion、help、sharing、tutorial、emotional、news、review、survey]
// 页数
const ARTICLE_TYPE = ['all', 'base', 'frontend', 'backend', 'testing', 'DevOps', 
        'architecture', 'design', 'database', 'tool', 'other']
const ARTICLE_TYPE_SHOW = ['全部', '基础', '前端', '后端', '测试', '运维', 
        '架构', '设计', '数据库', '工具', '其它']
const ARTICLE_SORT = ['default', 'new', 'hot', 'top']
const ARTICLE_SORT_SHOW = ['综合', '最新', '最热', '精华']
const PAGE_SIZE = 10

const keyInput = ref('')
const keyWord = ref('')
const currentPage = ref(1)
const activeArticleIndex = ref('0')
const activeArticleType = ref('all')
const activeArticleSort = ref('default')
// 在参数变化时刷新数据（示例代码，排序方式变为【综合】，页码为【第一页】）

// 界面展示视图-文章(avatar, articleTitle, isTop, isGem, content, views, likes, comments, publish_time, [articleTotal])
const articleOverviewVO = ref({
    "articles": [
            {
                "userId": "1676909673324552193",
                "userAvatar": "https://tse3-mm.cn.bing.net/th/id/OIP-C.7f-otWbJRToU6A6s0gNSOQAAAA?w=203&h=203&c=7&r=0&o=5&dpr=1.1&pid=1.7",
                "articleId": "1",
                "articleTitle": "分享markdown工具",
                "articleContent": "# HTML 代码转 MARKDOWN 代码示例\r\n\r\n段落\r\n\r\n## 用法如下：\r\n\r\n[链接1](https://www.ivtool.com) [链接2](https://www.ivtool.com \"链接2\")\r\n\r\n**加重1**\r\n\r\n**加重2**\r\n\r\n_强调_\r\n\r\n图片： ![alt text](/path/to/img.jpg \"Title\")\r\n\r\n* * *\r\n\r\n`代码`\r\n\r\n```\r\n //这是一段代码块\r\ncode.... \r\n```\r\n\r\n*   headingStyle (setext or atx)\r\n*   horizontalRule (\\*, -, or \\_)\r\n*   bullet (\\*, -, or +)\r\n*   codeBlockStyle (indented or fenced)\r\n*   fence (\\` or ~)\r\n*   emDelimiter (\\_ or \\*)\r\n*   strongDelimiter (\\*\\* or \\_\\_)\r\n*   linkStyle (inlined or referenced)\r\n*   linkReferenceStyle (full, collapsed, or shortcut)",
                "articleCreatedAt": "2023-03-20 20:21:55",
                "totalViews": "100",
                "totalLikes": "65",
                "totalReplies": "69",
                "gem": true,
                "top": true
            }
        ],
    "totalArticles":20
})

/********************************************************** 界面事件区 ***************************************************** **/
onMounted(() => {
    getArticleListRequest()
        .then(res => {
            if (res != null) {
                articleOverviewVO.value = res
            }
        })
})
// （1）搜索：
//  规则将关键字保存，并重新设定文章类型、文章排序规则、页码
const clickSearchEvent = () => {
    // 1. 关键字修改
    keyWord.value = keyInput.value
    activeArticleType.value = ARTICLE_TYPE[0]
    activeArticleSort.value = ARTICLE_SORT[0]
    currentPage.value = 1
    // 2. 发起请求
    getArticleListRequest()
        .then(res => {
            if (res != null) {
                articleOverviewVO.value = res
            }
        })
}
// （2）点击文章类型事件
const clickArticleTypeEvent = (newArticleType: string) => {
    activeArticleType.value = newArticleType
    activeArticleSort.value = ARTICLE_SORT[0]
    currentPage.value = 1

    getArticleListRequest()
        .then(res => {
            if (res != null) {
                articleOverviewVO.value = res
            }
        })
}
// （3）点击排序规则事件
const clickOrderModeEvent = (newOrderMode: string) => {
    activeArticleSort.value = newOrderMode
    currentPage.value = 1

    getArticleListRequest()
        .then(res => {
            if (res != null) {
                articleOverviewVO.value = res
            }
        })
}
// （4）点击分页按钮事件
const clickPageEvent = () => {
    getArticleListRequest()
        .then(res => {
            if (res != null) {
                articleOverviewVO.value = res
            }
        })
}
//  （5）点击文章事件
const clickArticleDetailEvent = (articleId) => {
    router.push(`/articleDetail/${articleId}`)
}

/******************************************************* 数据请求区 *******************************************************/
// （1）请求全部文章
const getArticleListRequest = () => {
    return axios.get("/article/v2/articles", {
        params: {
            'articleType': activeArticleType.value,
            'articleSort': activeArticleSort.value,
            'articleKey': keyWord.value,
            'articlePage': currentPage.value,
            'articleLimit': PAGE_SIZE
        }
    }).then(response => {
        if (response.status === 200 && response.data.code === 200) {
            return response.data.data
        }
        console.log(response.status)
        errorMsg(response.status !== 200 ? '网络出错' : '服务器出错')
        return null 
    }).catch(error => {
        console.error(error);
        return null
    });
}
//  (2) 请求提示词
const getSuggestionRequest = (queryString: string, cb: (arg: any) => void) => {
    axios.get('/article/v1/articles/suggestions/', { params: {queryString}})
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
                    <div class="article-search">
                        <el-input v-model="keyInput" placeholder="请输入" >
                            <template #append>
                                <el-button :icon="Search" @click="clickSearchEvent()" />
                            </template>
                        </el-input>
                    </div>
                </el-header>
                <!-- 主体区域 -->
                <el-main>
                    <!-- 文章菜单栏 -->
                    <div class="article-menu">
                        <div class="article-menu-type">
                            <el-menu :default-active="activeArticleIndex" mode="horizontal">
                                <el-menu-item v-for="(type, index) in ARTICLE_TYPE" 
                                    :key="index" :index="index.toString()" @click="clickArticleTypeEvent(type)">
                                    {{ ARTICLE_TYPE_SHOW[index] }}
                                </el-menu-item>
                            </el-menu>
                        </div>
                        <div class="article-menu-order">
                            <div>
                                <el-button text v-for="(sort, index) in ARTICLE_SORT" 
                                    :key="index" 
                                    :bg="activeArticleSort === sort"
                                    @click="clickOrderModeEvent(sort)">
                                    {{ ARTICLE_SORT_SHOW[index] }}
                                </el-button>
                            </div>
                            <p>共 {{ articleOverviewVO.totalArticles }} 条搜索结果</p>
                        </div>
                    </div>
                    <!-- 文章列表 -->
                    <div class="article-list">
                        <div class="article-component" v-for="(article, index) in articleOverviewVO.articles" :key="index">
                            <div class="article-info" @click="clickArticleDetailEvent(article.articleId)">
                                <div class="article-header">
                                    <el-avatar :size="30" :src="article.userAvatar" class="article-author" />
                                    <div class="article-articleTitle"  v-html="article.articleTitle">
                                    </div>
                                    <el-tag v-if="article.top">置顶</el-tag>
                                    <el-tag class="ml-2" type="success" v-if="article.gem">精选</el-tag>
                                </div>
                                <div class="article-body" v-html="article.articleContent"></div>
                                <div class="article-footer">
                                    <el-text><el-icon>
                                            <View />
                                        </el-icon>{{ article.totalViews }}</el-text>
                                    <el-text><el-icon>
                                            <Pointer />
                                        </el-icon>{{ article.totalLikes }}</el-text>
                                    <el-text><el-icon>
                                            <ChatRound />
                                        </el-icon>{{ article.totalReplies }}</el-text>
                                    <el-text><el-icon>
                                            <Timer />
                                        </el-icon>{{ article.articleCreatedAt }}</el-text>
                                </div>
                            </div>
                            <el-divider border-style="dotted" v-if="index !== articleOverviewVO.articles.length - 1" />
                        </div>
                    </div>
                </el-main>
                <el-footer>
                    <!-- 分页按钮 -->
                    <div class="center-pagination">
                        <el-pagination @click="clickPageEvent()" v-model:current-page="currentPage" background
                            layout="prev, pager, next" :total="articleOverviewVO.totalArticles" :default-page-size="PAGE_SIZE" />
                    </div>
                </el-footer>
            </el-container>
        </div>
    </el-card>
</template>

<style scoped>
.article-component {
    background-color: white;
    border-radius: 4px;
    margin-bottom: 10px;
}

.article-info:hover {
    cursor: pointer;
    background-color: rgb(245, 247, 249);
}

.article-header {
    display: flex;
    align-items: center;
}

.article-author,
.article-articleTitle {
    margin-right: 10px;
    font-weight: 600;
}

.article-search,
.article-search .el-input {
    height: 100%;
}

.el-menu-item {
    font-size: 20px;
}

.article-menu-order {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.article-isPerfect {
    background-color: rgb(222, 241, 212);
    color: rgb(90, 183, 38);
    border-radius: 4px;
}

.article-body {
    overflow: hidden;
    text-overflow: ellipsis;
    -webkit-line-clamp: 2;
    display: -webkit-box;
    -webkit-box-orient: vertical;
}

.article-footer {
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

.article-classify {
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