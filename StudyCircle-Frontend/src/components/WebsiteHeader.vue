<script setup lang="ts">
import { ref } from 'vue'
import { User, SwitchButton, Bell, CirclePlus, Message } from '@element-plus/icons-vue'
import LoginForm from './LoginForm.vue'
import RegisterForm from './RegisterForm.vue'
import axios from 'axios'
import PostEditor from './PostEditor.vue';
import router from '../router'

const user = JSON.parse(localStorage.getItem("user") ?? '');
const navChoice = ref('home')
const loginFormVisible = ref(false)
const registerFormVisible = ref(false)
const postEditorVisible = ref(false)
// 退出登录事件
const logoutEvent = () => {
    localStorage.removeItem('user')
    user.value = null
    axios.delete('/users/logout')
        .then((res) => {
            console.log(res)
        })
        .catch((err) => {
            console.log(err)
        })
}
// 个人中心
const presonEvent = () => {

}
// login
const loginEvent = (result) => {
    user.value = result
    loginFormVisible.value = false
}
// register
const registerEvent = (result) => {
    user.value = result
    registerFormVisible.value = false;
}
// post
const postEvent = (result) => {
    postEditorVisible.value = false;
}
// notice
const clickNoticeEvent = () => {
    router.push("/notice");
}
</script>


<template>
    <el-row>
        <el-col :span="1">
            <div class="grid-content ep-bg-purple"></div>
        </el-col>
        <el-col :span="3">
            <div class="grid-content ep-bg-purple logo-container">
                <img src="../assets/logo.png" class="logo" />
            </div>
        </el-col>
        <el-col :span="5">
            <div class="grid-content ep-bg-purple">
                <el-row class="row-bg" justify="space-evenly">
                    <el-col :span="6">
                        <div class="grid-content ep-bg-purple menu-container">
                            <RouterLink :to="{ path: '/' }" class="menu-font" :class="{ 'navActive': navChoice == 'home' }"
                                @click="navChoice = 'home'">主页</RouterLink>
                        </div>
                    </el-col>
                    <el-col :span="6">
                        <div class="grid-content ep-bg-purple menu-container">
                            <RouterLink :to="{ path: '/discuss' }" class="menu-font"
                                :class="{ 'navActive': navChoice == 'study' }" @click="navChoice = 'study'">学习</RouterLink>
                        </div>
                    </el-col>
                    <el-col :span="6">
                        <div class="grid-content ep-bg-purple menu-container">
                            <RouterLink :to="{ path: '/discuss' }" class="menu-font"
                                :class="{ 'navActive': navChoice == 'discuss' }" @click="navChoice = 'discuss'">讨论
                            </RouterLink>
                        </div>
                    </el-col>
                    <el-col :span="6">
                        <div class="grid-content ep-bg-purple menu-container">
                            <RouterLink :to="{ path: '/chat' }" class="menu-font"
                                :class="{ 'navActive': navChoice == 'chat' }" @click="navChoice = 'chat'">智答</RouterLink>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </el-col>
        <el-col :span="11">
            <div class="grid-content ep-bg-purple"></div>
        </el-col>
        <el-col :span="3">
            <div class="grid-content ep-bg-purple">
                <!-- user not login -->
                <div v-if="user == null">
                    <el-row class="row-bg" justify="space-evenly">
                        <el-col :span="8">
                            <el-button text class="grid-content ep-bg-purple menu-container menu-font"
                                :class="{ 'navActive': navChoice == '登录' }"
                                @click="navChoice = '登录', loginFormVisible = true">登录</el-button>
                        </el-col>
                        <el-col :span="8">
                            <div class="grid-content ep-bg-purple menu-container">或</div>
                        </el-col>
                        <el-col :span="8">
                            <el-button text class="grid-content ep-bg-purple menu-container menu-font"
                                :class="{ 'navActive': navChoice == '注册' }"
                                @click="navChoice = '注册', registerFormVisible = true">注册</el-button>
                        </el-col>
                    </el-row>
                </div>
                <!-- user login -->
                <div class="login-container" v-if="user != null">
                    <!-- 通知 -->
                    <div class="popover-content">
                        <el-badge :value="0" class="item login-user-notice">
                            <el-button :icon="Bell" circle size="large" @click="clickNoticeEvent()">
                            </el-button>
                        </el-badge>
                    </div>
                    <!-- 头像栏 -->
                    <el-popover placement="bottom" :width="150" trigger="click">
                        <template #reference>
                            <div class="popover-content">
                                <el-avatar :src="user.avatar"
                                    class="user-avatar" :size="40" />
                            </div>
                        </template>
                        <div class="user-avatar-menu">
                            <el-button class="user-avatar-menu-choice" :icon="User" text>
                                <RouterLink :to="{ path: '/person/1' }" class="user-avatar-menu-link">个人中心</RouterLink>
                            </el-button>
                        </div>
                        <div class="user-avatar-menu">
                            <el-button class="user-avatar-menu-choice" :icon="Message" text>
                                <RouterLink :to="{ path: '/letter' }" class="user-avatar-menu-link">私信</RouterLink>
                            </el-button>
                        </div>
                        <div class="user-avatar-menu">
                            <el-button class="user-avatar-menu-choice" :icon="SwitchButton" text @click="logoutEvent">
                                <RouterLink :to="{ path: '/' }" class="user-avatar-menu-link">退出&emsp;&emsp;</RouterLink>
                            </el-button>
                        </div>
                    </el-popover>
                    <!-- 发布帖子 -->
                    <el-button type="primary" :icon="CirclePlus" @click="postEditorVisible = true">发布帖子</el-button>
                </div>
            </div>
        </el-col>
        <el-col :span="1">
            <div class="grid-content ep-bg-purple"></div>
        </el-col>
    </el-row>
    <LoginForm v-model="loginFormVisible" @loginResult="loginEvent"></LoginForm>
    <RegisterForm v-model="registerFormVisible" @registerResult="registerEvent"></RegisterForm>
    <PostEditor v-model="postEditorVisible" @postResult="postEvent"></PostEditor>
</template>

<style scoped>
.logo-container {
    position: relative;
}

.logo-container img {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    height: 55px;
    width: 100px;
}

.menu-container {
    display: flex;
    justify-content: center;
    align-items: center;
}

.menu-font {
    color: black;
}

.menu-font:hover {
    color: blue;
    cursor: pointer;
}

.el-row {
    height: 55px;
}

.el-row:last-child {
    margin-bottom: 0;
}

.el-col {
    border-radius: 4px;
}

.grid-content {
    border-radius: 4px;
    height: 100%;
}

.user-avatar:hover {
    cursor: pointer;
}

.el-icon {
    width: 30px;
}

.login-container {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: space-evenly;
}

.user-avatar-menu-link {
    color: black;
}

.user-avatar-menu-link:hover {
    color: black;
    background-color: rgb(245, 247, 250);
}

.login-user-notice:hover {
    cursor: pointer;
    color: blue;
}

.scrollbar-demo-item {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 50px;
    margin: 10px;
    border-radius: 4px;
    color: var(--el-color-primary);
}

.notice-msg {
    float: left;
}

.notice-time {
    float: right;
}

.navActive {
    color: blue;
    text-decoration: solid;
}
</style>