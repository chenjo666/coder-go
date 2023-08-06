<script setup lang="ts">
import CheckCode from './LoginFormCode.vue'

import axios from 'axios'
import { ref, reactive  } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'

// 验证码生成逻辑
const identifyCodes = '1234567890abcdefjhijklinopqrsduvwxyz'//随机串内容
const identifyCode = ref('')
const emit = defineEmits(['loginResult'])

function refreshCode(n) {
    identifyCode.value = ''
    for (let i = 0; i < n; i++) {
        identifyCode.value += identifyCodes[randomNum(0, identifyCodes.length)]
    }
}
function randomNum(min, max) {
    return Math.floor(Math.random() * (max - min) + min)
}
refreshCode(4)
// 验证码生成逻辑

// 登录逻辑
const ruleFormRef = ref<FormInstance>()
const loginForm = reactive({
    email: "", password: "", code: ""
})
// 校验邮箱
const validateEmail = (rule: any, value: any, callback: any) => {
    const regEmail = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/
    if (!regEmail.test(value)) {
        callback(new Error("请输入合法的邮箱"))
    }
    callback()
}
// 校验密码
const validatePass = (rule: any, value: any, callback: any) => {
    if (value === '') {
        callback(new Error('请输入密码'))
    } else if (value.length <= 2) {
        callback(new Error("密码长度必须大于 2 位"))
    }
    callback()
}
// 校验验证码
const validateCode = (rule: any, value: any, callback: any) => {
    if (value === '') {
        callback(new Error('请输入验证码'))
    } else if (value !== identifyCode.value) {
        callback(new Error("验证码输入不正确"))
    }
    callback()
}
const rules = reactive<FormRules<typeof loginForm>>({
    email: [{ validator: validateEmail, trigger: 'blur' }],
    password: [{ validator: validatePass, trigger: 'blur' }],
    code: [{ validator: validateCode, trigger: 'blur' }]
})
const clearForm = () => {
    loginForm.email = ''
    loginForm.password = ''
    loginForm.code = ''
}
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
// 登录
const loginEvent = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.validate((valid) => {
        if (valid) {
            loginRequest()
        } else {
            errorMsg('请填写正确的数据后登录！')
            return false
        }
    })
}

const loginRequest = () => {
    axios.post('/user/v1/login', loginForm, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    }).then((res) => {
        if (res.status == 200 && res.data.code == 200) {
            successMsg(res.data.msg)
            const user = res.data.data
            localStorage.setItem('user', JSON.stringify(user))
            emit('loginResult', user);
            clearForm()
        } else {
            errorMsg('服务器发生未知错误！')
        }
    })
}
</script>


<template>
    <el-dialog draggable>
        <el-form ref="ruleFormRef" :model="loginForm" :rules="rules" status-icon label-width="120px">
            <el-form-item label="邮箱" prop="email">
                <el-input v-model="loginForm.email" type="email" autocomplete="off" />
            </el-form-item>
            <el-form-item label="密码" prop="password">
                <el-input v-model="loginForm.password" type="password" autocomplete="off" />
            </el-form-item>
            <el-form-item label="验证码" prop="code">
                <el-row :span="24">
                    <el-col :span="13">
                        <el-input v-model="loginForm.code" type="text" auto-complete="off"></el-input>
                    </el-col>
                    <el-col :span="11">
                        <div class="login-code" width="100%" @click="refreshCode(4)">
                            <!--验证码组件-->
                            <CheckCode :identifyCode="identifyCode"></CheckCode>
                        </div>
                    </el-col>
                </el-row>
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer" style="display: flex; justify-content: center;">
                <el-button type="primary" style="width: 100%;" @click="loginEvent(ruleFormRef)">
                    登录
                </el-button>
            </span>
        </template>
    </el-dialog>
</template>