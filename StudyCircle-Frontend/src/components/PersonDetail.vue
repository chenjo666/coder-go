<script lang="ts" setup>
import { ref } from 'vue'
import { Timer, Pointer, View, Plus, Star, Edit, Message } from '@element-plus/icons-vue'
import type { TabsPaneContext } from 'element-plus'

const activeName = ref('first')

const handleClick = (tab: TabsPaneContext, event: Event) => {
    console.log(tab, event)
}
const count = ref(10)
const load = () => {
    count.value += 2
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
                                                    src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
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
                                                <p style="margin-bottom: auto;">个人信息</p>
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
                                                    <el-text>6</el-text>
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
                                                    <el-text>6</el-text>
                                                </el-space>
                                            </div>
                                        </el-col>
                                    </el-row>
                                </div>
                                <!-- 关注、私信 -->
                                <div v-if="true == true">
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
                                                </el-icon> 浏览人数 298</el-text></div>
                                        <div class="person-achivement-item"><el-text><el-icon>
                                                    <Pointer />
                                                </el-icon> 点赞人数 64</el-text></div>
                                        <div class="person-achivement-item"><el-text><el-icon>
                                                    <Star />
                                                </el-icon> 收藏人数 15</el-text></div>
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
                                <div class="post-item" v-for="n in 5">
                                    <el-row :gutter="20">
                                        <el-col :span="18">
                                            <div class="grid-content ep-bg-purple">
                                                content
                                            </div>
                                        </el-col>
                                        <el-col :span="6">
                                            <div class="grid-content ep-bg-purple">
                                                <el-text><el-icon>
                                                        <View />
                                                    </el-icon>199</el-text>
                                                <el-text><el-icon>
                                                        <Pointer />
                                                    </el-icon>100</el-text>
                                                <el-text><el-icon>
                                                        <Timer />
                                                    </el-icon>2022-11-01</el-text>
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
.post-item {
    height: 50px;
    padding: 0 10px;
    justify-content: center;
}

.post-item:nth-child(2n+1) {
    background-color: rgb(247, 248, 250);
    justify-items: cneter;
}

.post-item div {
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