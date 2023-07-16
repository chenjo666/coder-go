<script setup lang="ts">
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { ref, reactive } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'

// 登录逻辑
const ruleFormRef = ref<FormInstance>()
const registerForm = reactive({
    email: "", password: "", password2: "", code: ""
})
const emit = defineEmits(['registerResult'])
// 校验邮箱
const validateEmail = (rule: any, value: any, callback: any) => {
    const regEmail = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/
    if (!regEmail.test(value)) {
        callback(new Error("请输入合法的邮箱格式！"))
    }
    callback()
}
// 校验激活码
const validateCode = (rule: any, value: any, callback: any) => {
    if (value === '') {
        callback(new Error('请输入激活码！'))
    } else if (value.length != 32) {
        callback(new Error("请输入正确的激活码！"))
    }
    callback()
}
// 校验密码
const validatePass = (rule: any, value: any, callback: any) => {
    if (value === '') {
        callback(new Error('请输入密码！'))
    } else if (value.length <= 6) {
        callback(new Error("密码长度必须大于 6 位！"))
    }
    callback()
}
// 二次校验密码
const validatePass2 = (rule: any, value: any, callback: any) => {
    if (value === '') {
        callback(new Error('请输入确认密码！'))
    } else if (value != registerForm.password) {
        callback(new Error("两次输入的密码不一致！"))
    }
    callback()
}
const rules = reactive<FormRules<typeof registerForm>>({
    email: [{ validator: validateEmail, trigger: 'blur' }],
    password: [{ validator: validatePass, trigger: 'blur' }],
    password2: [{ validator: validatePass2, trigger: 'blur' }],
    code: [{ validator: validateCode, trigger: 'blur' }]
})
const clearRegisterForm = () => {
    registerForm.email = registerForm.password = registerForm.password2 = registerForm.code = ''
}
const errorMsg = (msg) => {
    ElMessage({
        showClose: true,
        message: msg,
        type: 'error',
    })
}
const successMsg = (msg) => {
    ElMessage({
        showClose: true,
        message: msg,
        type: 'success',
    })
}
// 激活
const activate = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.validateField('email', (valid) => {
        if (!valid) {
            errorMsg('请输入正确的邮箱后进行激活账号！')
            return
        }
        axios.post('/users/activation', registerForm, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        }).then((res) => {
            if (res.status == 200) {
                // 发送激活码成功
                if (res.data.code === 200) {
                    successMsg(res.data.msg)
                } else {
                    errorMsg(res.data.msg)
                }
            } else {
                errorMsg('服务器发生未知错误，请求失败')
            }
        }).catch((err) => {
            console.log(err)
        })
    })
}
// 注册
const register = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.validate((valid) => {
        if (!valid) {
            errorMsg('请输入正确的邮箱、密码、激活码后进行注册！')
            return
        }
        axios.post('/users/register', registerForm, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        }).then((res) => {
            console.log('/user/register res:', res)
            if (res.status !== 200) {
                errorMsg('服务器发生未知错误！')
                return
            }
            const ans = res.data
            if (ans.code !== 200) {
                errorMsg(ans.msg)
                return
            }
            // 存储 user 对象在本地
            successMsg(ans.msg)
            const user = ans.data
            localStorage.setItem('user', JSON.stringify(user))
            emit('registerResult', user);
            clearRegisterForm()
        })
    })
}
</script>


<template>
    <el-dialog>
        <el-form ref="ruleFormRef" :model="registerForm" :rules="rules" status-icon label-width="120px">
            <el-form-item label="邮箱" prop="email">
                <el-input v-model="registerForm.email" type="text" autocomplete="off" />
            </el-form-item>
            <el-form-item label="激活码" prop="code">
                <el-row :span="24">
                    <el-col :span="13">
                        <el-input v-model="registerForm.code" type="text" auto-complete="off"></el-input>
                    </el-col>
                    <el-col :span="11">
                        <el-button auto-complete="off" @click="activate(ruleFormRef)">获取激活码</el-button>
                    </el-col>
                </el-row>
            </el-form-item>
            <el-form-item label="密码" prop="password">
                <el-input v-model="registerForm.password" type="password" autocomplete="off" />
            </el-form-item>
            <el-form-item label="确认密码" prop="password2">
                <el-input v-model="registerForm.password2" type="password" autocomplete="off" />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer" style="display: flex; justify-content: center;">
                <el-button type="primary" style="width: 100%;" @click="register(ruleFormRef)">
                    注册
                </el-button>
            </span>
        </template>
    </el-dialog>
</template>