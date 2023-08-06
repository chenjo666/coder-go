<script lang="ts" setup>
import { Timer, Pointer, View, Plus, Star, Edit, Message } from '@element-plus/icons-vue'
import axios from 'axios';
import type { TabsPaneContext } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router';
const activeName = ref('first')

const handleClick = (tab: TabsPaneContext, event: Event) => {
    console.log(tab, event)
}
const count = ref(10)
const load = () => {
    count.value += 2
}
const route = useRoute()
/**************************************************数据**************************************************/
const UIVO = ref({
    "userId": "1",
    "username": "cal",
    "userAvatar": "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png",
    "totalFollowings": 100,
    "totalFollowers": 100,
    "totalViews": 100,
    "totalLikes": 100,
    "totalFavorites": 100,
    "articles": [
        {
            "articleId": "1",
            "articleTitle": "ta",
            "articleCreatedAt": "2023-08-06",
            "totalViews": 1,
            "totalLikes": 100
        } 
    ],
})
const userId = ref('1')
let user = JSON.parse(localStorage.getItem('user') || '');

/**************************************************请求**************************************************/
onMounted(() => {
    userId.value = Array.isArray(route.params.personId) ? route.params.personId[0] : route.params.personId
    console.log(userId.value)
    getUserDetailRequest(userId.value)
    .then(res => {
            UIVO.value = res?.data
         })
})

// 查询用户详情
const getUserDetailRequest = (userId) => {
    return axios.get(`/user/v1/${userId}`)
    .then(response => {
        if (response.status === 200 && response.data.code === 200) {
            return response.data
        }
        return null 
    }).catch(error => {
        console.error(error);
        return null
    });
}
// 查询用户关注列表
const getFollowingsRequest = (userId) => {
    return axios.get(`/social/v1/${userId}/followings`)
    .then(response => {
        if (response.status === 200 && response.data.code === 200) {
            return response.data.data
        }
        return null 
    }).catch(error => {
        console.error(error);
        return null
    });
}
// 查询用户粉丝列表
const getFollowersRequest = (userId) => {
    return axios.get(`/social/v1/${userId}/followers`)
    .then(response => {
        if (response.status === 200 && response.data.code === 200) {
            return response.data.data
        }
        return null 
    }).catch(error => {
        console.error(error);
        return null
    });
}
// 查询用户收藏列表
const getFavoriteRequest = () => {
    return axios.get(`/article/v1/articles/favorites/${userId}`)
    .then(response => {
        if (response.status === 200 && response.data.code === 200) {
            return response.data.data
        }
        return null 
    }).catch(error => {
        console.error(error);
        return null
    });
}
</script>

<template>
    <div class="person-container">
        <el-row>
            <el-col :span="1">
                <div class="grid-content ep-bg-purple"></div>
            </el-col>
            <el-col :span="6">
                <!-- 个人-左信息区 -->
                <div class="grid-content ep-bg-purple">
                    <el-card class="box-card">
                        <div class="person-info">
                            <div>
                                <div>
                                    <el-row style="width:100%;">
                                        <el-col :span="10">
                                            <div class="grid-content ep-bg-purple"
                                                style="width: 100%; display: flex; align-items: center; justify-content: center;">
                                                <el-avatar shape="square" :size="120"
                                                    :src="UIVO.userAvatar" />
                                            </div>
                                        </el-col>
                                        <el-col :span="4">
                                            <div class="grid-content ep-bg-purple"
                                                style="display: flex; align-items: center; justify-content: center;">
                                            </div>
                                        </el-col>
                                        <el-col :span="10">
                                            <div class="grid-content ep-bg-purple"
                                                style="display: flex; height: 100%; flex-direction: column;  justify-content: center;">
                                                <p style="margin-bottom: auto;">{{ UIVO.username }}</p>
                                                <el-button type="primary" style="margin-top: auto;">编辑资料</el-button>
                                            </div>
                                        </el-col>
                                    </el-row>
                                </div>
                                <!-- 关注、粉丝-->
                                <div style="margin-top: 5%;">
                                    <el-row>
                                        <el-col :span="10">
                                            <div class="grid-content ep-bg-purple"
                                                style="display: flex; align-items: center; justify-content: center;">
                                                <el-space direction="vertical">
                                                    <el-text>关注了</el-text>
                                                    <el-text>{{ UIVO.totalFollowings }}</el-text>
                                                </el-space>
                                            </div>
                                        </el-col>
                                        <el-col :span="4">
                                            <div class="grid-content ep-bg-purple"
                                                style="display: flex; align-items: center; justify-content: center;">
                                                <el-divider direction="vertical" style="height: 40px;" />
                                            </div>
                                        </el-col>
                                        <el-col :span="10">
                                            <div class="grid-content ep-bg-purple"
                                                style="display: flex; align-items: center; justify-content: center;">
                                                <el-space direction="vertical">
                                                    <el-text>关注者</el-text>
                                                    <el-text>{{ UIVO.totalFollowers }}</el-text>
                                                </el-space>
                                            </div>
                                        </el-col>
                                    </el-row>
                                </div>
                                <!-- 关注、私信 -->
                                <div v-if="userId != user?.id">
                                    <el-row>
                                        <el-col :span="10">
                                            <div class="grid-content ep-bg-purple"
                                                style="display: flex; align-items: center; justify-content: center;">
                                                <el-button type="info" style="width:100%" :icon="Message">私信</el-button>
                                            </div>
                                        </el-col>
                                        <el-col :span="4"></el-col>
                                        <el-col :span="10">
                                            <div class="grid-content ep-bg-purple-light">
                                                <el-button type="primary" style="width:100%" :icon="Plus">关注</el-button>
                                            </div>
                                        </el-col>
                                    </el-row>
                                </div>
                                <!-- 成就贡献 -->
                                <el-divider></el-divider>
                                <div class="person-achivement">
                                    <div class="person-achivement-title">
                                        <span style="font-size: 20px;">成就贡献</span>
                                    </div>
                                    <div class="person-achivement-content">
                                        <div class="person-achivement-item"><el-text><el-icon>
                                                    <View />
                                                </el-icon> 浏览人数 {{UIVO.totalViews}}</el-text></div>
                                        <div class="person-achivement-item"><el-text><el-icon>
                                                    <Pointer />
                                                </el-icon> 点赞人数 {{UIVO.totalLikes}}</el-text></div>
                                        <div class="person-achivement-item"><el-text><el-icon>
                                                    <Star />
                                                </el-icon> 收藏人数 {{UIVO.totalFavorites}}</el-text></div>
                                    </div>
                                </div>
                                <!-- 统计 -->
                                <el-divider></el-divider>
                                <div class="person-statistic">
                                    <el-card class="box-card">
                                        hello
                                    </el-card>
                                </div>
                            </div>
                        </div>
                    </el-card>
                </div>
            </el-col>
            <el-col :span="1">
                <div class="grid-content ep-bg-purple"></div>
            </el-col>
            <el-col :span="15">
                <!-- 个人-右信息区 -->
                <div class="grid-content ep-bg-purple">
                    <el-card class="box-card">
                        <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
                            <el-tab-pane label="发布" name="first">
                                <div class="article-item" v-for="(article, index) in UIVO.articles" :key="index">
                                    <el-row :gutter="20">
                                        <el-col :span="18">
                                            <div class="grid-content ep-bg-purple">
                                                {{article.articleTitle}}
                                            </div>
                                        </el-col>
                                        <el-col :span="6">
                                            <div class="grid-content ep-bg-purple">
                                                <el-text><el-icon>
                                                        <View />
                                                    </el-icon>{{article.totalViews}}</el-text>
                                                <el-text><el-icon>
                                                        <Pointer />
                                                    </el-icon>{{article.totalLikes}}</el-text>
                                                <el-text><el-icon>
                                                        <Timer />
                                                    </el-icon>{{ article.articleCreatedAt }}</el-text>
                                            </div>
                                        </el-col>
                                    </el-row>
                                </div>
                            </el-tab-pane>
                            <el-tab-pane label="收藏" name="second">Config</el-tab-pane>
                        </el-tabs>
                    </el-card>
                </div>
            </el-col>
            <el-col :span="1">
                <div class="grid-content ep-bg-purple"></div>
            </el-col>
        </el-row>
    </div>
</template>

<style scoped>
.article-item {
    height: 50px;
    padding: 0 10px;
    justify-content: center;
}

.article-item:nth-child(2n+1) {
    background-color: rgb(247, 248, 250);
    justify-items: cneter;
}

.article-item div {
    height: 100%;
    display: flex;
    align-items: center;
}

/* 个人贡献 */
.person-achivement-item {
    height: 40px;
    display: flex;
    align-items: center;
}
</style>