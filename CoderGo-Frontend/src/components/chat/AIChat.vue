<script setup lang="ts">
import { Plus, Edit, ChatDotRound, DocumentCopy, RefreshRight, Delete, Setting, Present, Scissor, Timer, Position, More } from '@element-plus/icons-vue'
import axios from 'axios';
import { ref, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ElScrollbar } from 'element-plus'
import IconOperation from "../icons/IconOperation.vue"
import { marked } from 'marked';
/******************************************* 工具区 ***********************************************/
// 滑动框工具
const chatListValue = ref(0)
const chatListScroll = ({ scrollTop }) => {
    chatListValue.value = scrollTop
}
const chatItemValue = ref(0)
const chatItemScroll = ({ scrollTop }) => {
    chatItemValue.value = scrollTop
}
// 对话滑动条
const conversationScrollbarRef = ref<InstanceType<typeof ElScrollbar>>()
const conversationInnerRef = ref<HTMLDivElement>()
// 消息滑动条
const messageInnerRef = ref<HTMLDivElement>()
const messageScrollbarRef = ref<InstanceType<typeof ElScrollbar>>()
// 对话自动滑到顶部
const autoScrolToTop = () => {
    nextTick(() => {
        conversationScrollbarRef.value!.setScrollTop(0)
    })
}
// 消息自动滑到底部
const autoScrolToBottom = () => {
    nextTick(() => {
        if (messageInnerRef.value!.clientHeight > messageScrollbarRef.value!.$el.clientHeight) {
            messageScrollbarRef.value!.setScrollTop(messageInnerRef.value!.clientHeight)
        }
    })
}
// 反馈工具
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
// 时间工具
const formatDate = (date: Date): string => {
    const year = date.getFullYear().toString();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}
// 流式工具
const flowShowContent = (messageIndex, content) => {
    const text = content;
    let idx = 0;

    const intervalId = setInterval(() => {
        if (idx < text.length) {
            chatCompletionVO.value.messageListVO[messageIndex].content += text.charAt(idx);
            idx++;
        } else {
            clearInterval(intervalId); // 清除interval
            btnSendDisabled.value = false // 恢复点击
        }
        autoScrolToBottom()
    }, 50)
}
// 随机化时间工具
const addRandomSeconds = (date: Date, min: number, max: number): Date => {
    const secondsToAdd = Math.floor(Math.random() * max) + min;
    const newDate = new Date(date.getTime() + secondsToAdd * 1000);
    return newDate;
}
// 文本转换
const markedContent = (content) => {
    console.log(content)
    return marked.parse(content);
}
// 复制消息工具
const copyMessage = (index) => {
    /* 获取文本框的值 */
    const text = chatCompletionVO.value.messageVOList[index].content;
    try {
        /* 使用Clipboard API将文本复制到剪贴板 */
        navigator.clipboard.writeText(text);
        console.log('文本已复制');
    } catch (error) {
        console.error('复制失败: ', error);
    }
    /* 取消文字选中状态 */
    window.getSelection()?.removeAllRanges();
}
/********************************************** 数据区 ****************************************************/
// 视图对象
const chatCompletionVO = ref({
    conversationListVO: [
        {
            conversationId: '1',
            conversationName: 'hello',
        },
        {
            conversationId: '1',
            conversationName: 'hello',
        },
        {
            conversationId: '1',
            conversationName: 'hello',
        },
    ],
    messageListVO: [
        {
            messageId: '124214124',
            messageTargetId: '121',
            role: 'user',
            content: 'hello',
            createdAt: '2020-01-02 1:01:02'
        },
        {
            messageId: '124124',
            messageTargetId: '121',
            role: 'user',
            content: 'hello',
            createdAt: '2020-01-02 1:02:02'
        },
        {
            messageId: '12312312',
            messageTargetId: '121',
            role: 'user',
            content: '恰噶',
            createdAt: '2020-01-02 1:03:02'
        },

    ]
})
// ConversationVO
interface ConversationVO {
    conversationId: string,
    conversationName: string,
}
// MessageVO
interface MessageVO {
    messageId: string,
    messageTargetId: string,
    role: string,
    content: string,
    createdAt: string
}
// 界面数据
const activeConversationIndex = ref(0)
const editMode = ref(false)
const editConversationName = ref('');
const messageContent = ref('')
const btnSendDisabled = ref(false)
const loadingVisible = ref(false)
const user = JSON.parse(localStorage.getItem("user") ?? '');
/******************************************* 界面事件区 *************************************************/
// (0) 查找全部对话以及最新对话的全部消息
onMounted(() => {
    findConversationListRequest()
    .then(response => {
        chatCompletionVO.value = response
        autoScrolToBottom()
    }).catch(error => {
        console.log(error)
    })  
})
// (1) 点击新建对话事件：新建一个对话
// 数据响应：得到后端响应的对话对象，并存入数据区中
// 界面响应：（1）滑动到最顶部（2）自动切换到第一页的响应
const clickNewConservationEvent = () => {
    // 这是一个异步过程，得等结果之后再进行操作
    addConversationRequest()
        .then(res => {
            if (res != null) {
                // 1. 添加数据
                chatCompletionVO.value.conversationListVO.unshift(res)
                // 2.// 滑动到最顶部
                autoScrolToTop()
                // 切换到第一页
                clickConversationEvent(0)
            }
        })
}
// (2) 点击对话事件：触发查询对话中的全部数据请求
// 数据响应：得到当前对话的全部消息
// 界面响应：（1）当前对话活跃态（2）消息框反馈当前对话全部信息（3）滑动到最新消息
const clickConversationEvent = (index) => {
    activeConversationIndex.value = index
    console.log('click id: ', chatCompletionVO.value.conversationListVO[index].conversationId)
    findMessageListRequest(chatCompletionVO.value.conversationListVO[index].conversationId)
        .then(res => {
            if (res != null) {
                chatCompletionVO.value.messageListVO = res
                autoScrolToBottom()
            }
        }).catch(err => {
            console.log(err)
        })
}
// (3) 编辑对话事件：
// 界面响应：（1）编辑框显示 （2）编辑文字为原对话文字
const clickEditConversationEvent = (index) => {
    editMode.value = true;
    editConversationName.value = chatCompletionVO.value.conversationListVO[index].conversationName
    nextTick(() => {
        (document.querySelector('.el-input__inner') as HTMLInputElement).focus();
    });
};
// (4) 编辑对话结果事件：触发更新对话名称请求 
// 数据响应：更新成功与否的状态码
// 界面响应：（1）编辑模式取消 （2）请求更新对话名称 （3）界面展示响应结果
const editConversationEvent = (index) => {
    editMode.value = false;

    updateConversationNameRequest(chatCompletionVO.value.conversationListVO[index].conversationId, editConversationName.value)
        .then(res => {
            if (res === true) {
                chatCompletionVO.value.conversationListVO[index].conversationName = editConversationName.value
            }
        }).catch(error => {
            console.log(error)
        })
}
// (5) 删除对话事件：删除某一个对话
// 数据响应：删除成功与否的状态码
// 界面响应：（1）去除当前对话（3）指向当前对话
const deleteConversationEvent = (index) => {
    deleteConversationRequest(chatCompletionVO.value.conversationListVO[index].conversationId)
        .then(res => {
            // 删除成功，移除对话
            if (res) {
                chatCompletionVO.value.conversationListVO.splice(index, 1);

                // 删除之后，默认移动到上一个对话，如果上一个对话不存在，移到当前对话，如果没有对话，清空消息列表
                if (index === 0) {
                    if (chatCompletionVO.value.conversationListVO.length === 0) {
                        chatCompletionVO.value.messageListVO = []
                    } else {
                        clickConversationEvent(index)
                    }
                } else {
                    clickConversationEvent(index - 1)
                }
            }
        }).catch(err => {
            console.log(err)
        })

}
// (6) 发送消息事件：1. 发送请求 2. 展示结果 3. 清空输入框，4. 滑动栏自动跟随滑动
// 优化界面响应：界面先展示用户发送的消息，并展示 AI 的状态，并最终显示结果
const clickSendMessageEvent = (index) => {
    const conversationId = chatCompletionVO.value.conversationListVO[index].conversationId;
    const length = chatCompletionVO.value.messageListVO.length;
    const messageTargetId = length == 0 ? '0' : chatCompletionVO.value.messageListVO[length - 1].messageId;

    // 1. 创建一个自身对象
    const userMessageVO: MessageVO = {
        messageId: '',
        messageTargetId: messageTargetId,
        role: 'user',
        content:  messageContent.value,
        createdAt: formatDate(new Date())
    }
    // 自身对话
    chatCompletionVO.value.messageListVO.push(userMessageVO);
    // 2. ai 对象
    const aiMessageVO: MessageVO = {
        messageId: '',
        role: 'assistant',
        content: '',
        createdAt: formatDate(addRandomSeconds(new Date(), 1, 3))
    }
    chatCompletionVO.value.messageListVO.push(aiMessageVO);

    // 3 禁用点击
    btnSendDisabled.value = true

    // 4 加载界面
    const messageLength = chatCompletionVO.value.messageListVO.length;
    loadingVisible.value = true;

    autoScrolToBottom()
    // 1. 发送请求
    addMessageRequest(conversationId, messageTargetId, messageContent.value)
        .then(res => {
            // 3. 展示结果
            if (res !== null) {
                // 收到结果后，更新 messageID
                const userMessageId = res[0].messageId
                chatCompletionVO.value.messageListVO[messageLength - 2].messageId = userMessageId

                // 更新 ai 对象的结果
                const aiMessageId = res[1].messageId
                chatCompletionVO.value.messageListVO[messageLength - 1].messageId = aiMessageId
                loadingVisible.value = false;
                flowShowContent(messageLength - 1, markedContent(res[1].content))
            }
        })
        .catch(error => {
            console.error(error);
        });
    // 3. 清空输入框
    messageContent.value = ''
}
// （7）删除消息事件：1. 发送请求 2. 界面展示响应
const deleteMessageEvent = (index) => {
    delMessageRequest(chatCompletionVO.value.messageListVO[index].messageId)
    .then(response => {
        if (response) {
            chatCompletionVO.value.messageListVO.splice(index, 1);
        }
    }).catch(error => {
        console.log(error)
    })
}
// （8）更新消息内容事件：1. 发送请求 2. 界面展示响应
const updateMessageContentEvent = (index) => {
    // 1. 清空记录
    chatCompletionVO.value.messageListVO[index].content = ''
    // 2. 禁用点击
    btnSendDisabled.value = true
    // 3. 加载
    loadingVisible.value = true;

    setMessageContentRequest(chatCompletionVO.value.messageListVO[index].messageId)
    .then(res => {
        // 3. 展示结果
        if (res !== null) {
                // 收到结果后，更新 messageID
                const assistantMessageId = res.messageId
                chatCompletionVO.value.messageListVO[index].messageId = assistantMessageId

                // 更新 ai 对象的结果
                loadingVisible.value = false;
                flowShowContent(index, markedContent(res.content))
            }
    }).catch(error => {
        console.log(error)
    })
}

// 截图事件：
const saveMessageEvent = () => { }
// 切换历史模型事件
const changeHistoryEvent = () => { }
// 礼物事件
const toggleShiftEvent = () => { }

/** 界面优化事项 **/
// （1）如果前端能够处理数据并展示出用户所需要的数据，那么前端先处理数据，后请求后端，后端进行二次处理后返回给前端，前端填补其它重要属性，这样做提高用户体验
// （2）如果前端所需要展示的数据必须根据后端处理而来，那么只能先请求后端数据再展示了。
// 对于第一种，虽然提高了用户体验，但是如果用户操作很快，还未等到后端发送关键数据就进行其它需要重要属性的操作，就会出现问题
// 对于第二种，数据安全性更高（目前采用的）

/**************************************** 请求数据区 *****************************************/
// （1）查找全部对话请求：请求后端查询当前用户的全部对话，请求成功后，后端返回一个【chatConversationVO】
const findConversationListRequest = () => {
    return axios.get("/chat/v1/conversations")
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
            // 查找全部对话成功
            return ans.data
        })
        .catch(error => {
            console.error(error);
            return null
        });
}
// （2）新增对话请求：请求后端新增对话，请求成功后，前端新增一个对话即可
const addConversationRequest = () => {
    return axios.post("/chat/v1/conversations")
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
            // 新增成功，新增这个对话
            return ans.data
        })
        .catch(error => {
            errorMsg('网络出现错误！')
            return null
        });
}
// （3）删除对话请求：请求后端删除对话，当后端删除成功后，前端移除这个对话即可
const deleteConversationRequest = (conversationId) => {
    return axios.delete(`/chat/v1/conversations/${conversationId}`)
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
// （4）更新对话名请求：请求后端更新对话名，当后端更新成功后，前端更新这个对话即可，不采用重新请求的方式，
// 请求参数：【对话 id、新对话名称】
// 响应结果：【true or false】
const updateConversationNameRequest = (conversationId, newConversationName) => {
    return axios.put(`/chat/v1/conversations/${conversationId}`, { params: { 'newConversationName': newConversationName } })
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
            // 更新成功，更新这个对话名
            return true
        })
        .catch(error => {
            console.error(error);
            return false
        });
}

// （5）查询对话消息请求：请求后端查找当前对话全部消息，请求参数【对话 id】，响应结果【MessageListVO】
const findMessageListRequest = (conversationId) => {
    return axios.get(`/chat/v1/conversations/${conversationId}/messages`)
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
            // 新增成功，新增这个对话
            return ans.data
        })
        .catch(error => {
            console.error(error);
        });
}
// （6）新增消息请求：请求参数【对话 id、消息内容】，响应结果两个【MessageVO】
const addMessageRequest = (conversationId, messageTargetId, question) => {
    return axios.post(`/chat/v1/conversations/${conversationId}/messages`, { "messageTargetId" : messageTargetId, 'question': question })
        .then(response => {
            if (response.status !== 200) {
                errorMsg('请求失败！')
                return null
            }
            const res = response.data
            if (res.code !== 200) {
                errorMsg(res.msg)
                return null
            }
            return res.data
        }).catch(error => {
            errorMsg('网络故障')
            return null
        })
}
//  (7) 删除消息请求：请求参数【消息id】，响应结果【布尔值】
const delMessageRequest = (messageId) => {
    return axios.delete(`/chat/v1/conversations/messages/${messageId}`)
        .then(response => {
            if (response.status !== 200) {
                errorMsg('请求失败！')
                return false
            }
            const res = response.data
            if (res.code !== 200) {
                errorMsg(res.msg)
                return false
            }
            return true
        }).catch(error => {
            errorMsg(error)
            return false
        })
}
//  (8) 更新消息内容请求：请求参数【消息id、问题】，响应结果【MessageVO】
//  如果把提问消息删除了，重新请求，怎么获取问题是一个问题
//  此时应该冗余一个字段表示提问的问题，也可以冗余一个字段指向提问的消息（后端不好处理自身消息）
const setMessageContentRequest = (messageId) => {
    return axios.put(`/chat/v1/conversations/messages/${messageId}`)
        .then(response => {
            if (response.status !== 200) {
                errorMsg('请求失败！')
                return null
            }
            const res = response.data
            if (res.code !== 200) {
                errorMsg(res.msg)
                return null
            }
            return res.data
        }).catch(error => {
            errorMsg(error)
            return false
        })
}
</script>

<template>
    <el-card>
        <div class="ai-chat-container">
            <el-row>
                <el-col :span="4">
                    <div class="grid-content ep-bg-purple grid-view">
                        <div class="chat-new-container">
                            <div class="chat-new">
                                <div class="chat-new-info">
                                    <el-button type="primary" :icon="Plus"
                                        @click="clickNewConservationEvent">新建对话</el-button>
                                </div>
                            </div>
                        </div>
                        <div class="chat-items-container">
                            <el-scrollbar ref="conversationScrollbarRef" height="65vh" @scroll="chatListScroll">
                                <div class="chat-item-container" ref="conversationInnerRef"
                                    v-for="(conversation, index) in chatCompletionVO.conversationListVO" :key="index">
                                    <div class="chat-item"
                                        :class="{ 'chat-active-dialog': activeConversationIndex === index }"
                                        @click="clickConversationEvent(index)">
                                        <div class="chat-item-info">
                                            <el-row :gutter="20">
                                                <el-col :span="3">
                                                    <div class="grid-content ep-bg-purple">
                                                        <el-icon class="chat-item-icon">
                                                            <ChatDotRound></ChatDotRound>
                                                        </el-icon>
                                                    </div>
                                                </el-col>
                                                <el-col :span="15">
                                                    <div class="grid-content ep-bg-purple">
                                                        <el-input v-if="editMode && activeConversationIndex === index"
                                                            v-model="editConversationName"
                                                            @blur="editConversationEvent(index)" ref="inputRef"></el-input>
                                                        <el-text v-else truncated
                                                            :class="{ 'chat-active-text': activeConversationIndex === index }">
                                                            {{ conversation.conversationName }}</el-text>
                                                    </div>
                                                </el-col>
                                                <el-col :span="3">
                                                    <div class="grid-content ep-bg-purple"
                                                        v-if="activeConversationIndex == index">
                                                        <el-icon @click="clickEditConversationEvent(index)">
                                                            <Edit></Edit>
                                                        </el-icon>
                                                    </div>
                                                </el-col>
                                                <el-col :span="3">
                                                    <div class="grid-content ep-bg-purple"
                                                        v-if="activeConversationIndex == index">
                                                        <el-popconfirm title="删除当前对话？"
                                                            @confirm="deleteConversationEvent(index)">
                                                            <template #reference>
                                                                <el-icon>
                                                                    <Delete></Delete>
                                                                </el-icon>
                                                            </template>
                                                        </el-popconfirm>
                                                    </div>
                                                </el-col>
                                            </el-row>
                                        </div>
                                    </div>
                                </div>
                            </el-scrollbar>
                        </div>
                        <div class="chat-setting-container">
                            <el-icon>
                                <Setting></Setting>
                            </el-icon>
                        </div>
                    </div>
                </el-col>
                <el-col :span="20">
                    <div class="grid-content ep-bg-purple grid-view">
                        <div class="chat-detail-container">
                            <el-scrollbar ref="messageScrollbarRef" height="75vh" @scroll="chatItemScroll">
                                <div class="chat-container" ref="messageInnerRef">
                                    <div v-for="(message, index) in chatCompletionVO.messageListVO" :key="index">
                                        <!-- 对方 -->
                                        <div class="chat-word-other" v-if="message.role == 'assistant'">
                                            <img class="chat-avatar"
                                                src="https://img.zcool.cn/community/01f0865d45230ba8012187f485a17b.jpg@1280w_1l_2o_100sh.jpg">
                                            <div class="chat-info">
                                                <p class="chat-time">{{ message.createdAt }}</p>
                                                <div class="chat-content">
                                                    <el-button loading text class="btn-loading" bg
                                                        v-if="loadingVisible && index === chatCompletionVO.messageListVO.length - 1"></el-button>
                                                    <div v-html="markedContent(message.content)"></div>
                                                </div>
                                                <el-popover trigger="hover" placement="right">
                                                    <div style="text-align: left; margin: 0">
                                                        <div><el-button size="small" text :icon="RefreshRight"
                                                            @click="updateMessageContentEvent(index)">重新生成</el-button></div>
                                                        <div><el-button size="small" text :icon="DocumentCopy"
                                                            @click="copyMessage(index)">复制文本</el-button></div>
                                                        <div><el-button size="small" text :icon="Delete" 
                                                            @click="deleteMessageEvent(index)">删除消息</el-button>
                                                        </div>
                                                    </div>
                                                    <template #reference>
                                                        <el-icon class="chat-content-menu">
                                                            <IconOperation />
                                                        </el-icon>
                                                    </template>
                                                </el-popover>
                                            </div>
                                        </div>
                                        <!-- 我的 -->
                                        <div class="chat-word-self" v-else>
                                            <div class="chat-info">
                                                <p class="chat-time">{{ message.createdAt }}</p>
                                                <div class="chat-content" v-html="markedContent( message.content)"></div>
                                                <div class="chat-menu-parent">
                                                    <div class="chat-menu-child">
                                                        <el-popover trigger="hover" placement="left">
                                                            <div style="text-align: left; margin: 0">
                                                                <div><el-button size="small" text :icon="DocumentCopy"
                                                                    @click="copyMessage(index)">复制文本</el-button>
                                                                </div>
                                                                <div><el-button size="small" text :icon="Delete"
                                                                    @click="deleteMessageEvent(index)">删除消息</el-button></div>
                                                            </div>
                                                            <template #reference>
                                                                <el-icon class="chat-content-menu">
                                                                    <IconOperation />
                                                                </el-icon>
                                                            </template>
                                                        </el-popover>
                                                    </div>
                                                </div>
                                            </div>

                                            <img class="chat-avatar"
                                                :src="user?.avatar">
                                        </div>
                                    </div>
                                </div>
                            </el-scrollbar>
                        </div>
                        <div class="chat-menu-container">
                            <div class="chat-menu-info">
                                <el-row :gutter="20">
                                    <el-col :span="1">
                                        <div class="grid-content ep-bg-purple menu-container">
                                            <div>
                                                <el-button :icon="Present" size="large" circle />
                                            </div>
                                        </div>
                                    </el-col>
                                    <el-col :span="20" class="menu-container">
                                        <div class="grid-content ep-bg-purple">
                                            <el-input v-model="messageContent" :rows="2" type="textarea"
                                                placeholder="请输入..." @keydown.enter="" />
                                        </div>
                                    </el-col>
                                    <el-col :span="1">
                                        <div class="grid-content ep-bg-purple menu-container">
                                            <div>
                                                <el-button :icon="Scissor" size="large" circle />
                                            </div>
                                        </div>
                                    </el-col>
                                    <el-col :span="1">
                                        <div class="grid-content ep-bg-purple menu-container">
                                            <div>
                                                <el-button :icon="Timer" size="large" circle />
                                            </div>
                                        </div>
                                    </el-col>
                                    <el-col :span="1">
                                        <div class="grid-content ep-bg-purple menu-container">
                                            <div>
                                                <el-button type="primary" :icon="Position" :disabled="btnSendDisabled"
                                                    @click="clickSendMessageEvent(activeConversationIndex)" />
                                            </div>
                                        </div>
                                    </el-col>
                                </el-row>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </div>
    </el-card>
</template>

<style scoped>
.grid-view {
    border: solid 1px rgb(239, 239, 245);
}

/** 新建对话视图 */
.chat-new-container {
    height: 10vh;
}

.chat-new-container,
.chat-item-container {
    display: flex;
    justify-content: center;
    align-items: center;
}

.chat-item-container {
    height: 50px;
}

.chat-new-container,
.chat-items-container {
    border: solid 1px rgb(239, 239, 245);
}

.chat-new,
.chat-item {
    border: solid 1px rgb(239, 239, 245);
    border-radius: 4px;
}

.chat-new {
    width: 80%;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 50%;
}

.chat-new-info {
    height: 100%;
    width: 100%;
}

.chat-new-info .el-button {
    height: 100%;
}

.chat-item:hover {
    cursor: pointer;
    background-color: rgb(245, 245, 245);
}

.chat-new .el-button {
    width: 100%;
}

.chat-item {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 90%;
    width: 80%;
}

.chat-item-info {
    width: 90%;
}


/** 活跃对话样式 */
.chat-active-dialog,
.chat-active-text {
    color: blue;
}

.chat-active-dialog {
    border: 1px solid blue;
}

/** 设置样式 */
.chat-setting-container {
    height: 10vh;

}

.chat-menu-parent {
    position: relative;
    height: 100%;
    float: right;
}

.chat-menu-child {
    position: absolute;
    bottom: 10px;
    right: -10px;
}

/** 对话详情 */
.chat-container {
    width: 100%;
    padding: 30px;
}

.chat-word-other {
    display: flex;
    justify-content: flex-start;
    margin-bottom: 20px;
}

.chat-word-other .chat-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
}

.chat-word-other .chat-info {
    margin-left: 10px;
    position: relative;
}

.chat-word-other .chat-info .chat-time {
    font-size: 12px;
    color: rgba(51, 51, 51, 0.8);
    margin: 0;
    height: 20px;
    line-height: 20px;
    margin-top: -5px;
}

.chat-word-other .chat-info .chat-content {
    display: inline-block;
    padding: 10px 16px;
    font-size: 14px;
    background: #f5f7fa;
    position: relative;
    margin-top: 8px;
}

.chat-word-other .chat-info .chat-content::before {
    position: absolute;
    left: -8px;
    content: '';
    border-right: 10px solid #f5f7fa;
    border-top: 8px solid transparent;
    border-bottom: 8px solid transparent;
}

.chat-content {
    border-radius: 4px;
}

.chat-word-other .chat-info .chat-content-menu {
    position: absolute;
    bottom: 0px;
    /* 调整图标距离底部的距离，可根据需要自行调整 */
}

.chat-content-menu:hover {
    cursor: pointer;
    background-color: rgb(245, 245, 245);
}

.chat-word-self {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 20px;
}

.chat-word-self .chat-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
}

.chat-word-self .chat-info {
    margin-left: 10px;
    text-align: right;
    position: relative;
}

.chat-word-self .chat-info .chat-content-menu {
    margin-right: 10px;
    bottom: 0px;
}

.chat-word-self .chat-info .chat-time {
    font-size: 12px;
    color: rgba(51, 51, 51, 0.8);
    margin: 0;
    height: 20px;
    line-height: 20px;
    margin-top: -5px;
    margin-right: 10px;
}

.chat-word-self .chat-info .chat-content {
    padding: 10px;
    font-size: 14px;
    float: right;
    margin-right: 10px;
    position: relative;
    margin-top: 8px;
    background: #d9ecff;
    text-align: left;
}

.chat-word-self .chat-info .chat-content::after {
    position: absolute;
    right: -8px;
    top: 8px;
    content: '';
    border-left: 10px solid #d9ecff;
    border-top: 8px solid transparent;
    border-bottom: 8px solid transparent;
}

/** 菜单 */
.chat-menu-container {
    height: 10vh;
    display: flex;
    justify-content: center;
    align-items: center;
    border: solid 1px rgb(239, 239, 245);
}

.chat-menu-info {
    width: 95%;
}

.menu-container {
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}

</style>