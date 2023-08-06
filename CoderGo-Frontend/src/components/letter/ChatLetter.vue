<script setup lang="ts">
import { onMounted, ref, nextTick, onBeforeUnmount } from 'vue'
import { Search, MuteNotification, Share, Position, Picture, Delete } from '@element-plus/icons-vue'
import IconEmoji from "../icons/IconEmoji.vue"
import { ElMessage, ElScrollbar } from 'element-plus'
import axios from 'axios';
import EmojiPicker from './EmojiPicker.vue'
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
const showPopover = (index, event) => {
    event.preventDefault();
    activePopoverIndex.value = index;
}
const hidePopover = () => {
    activePopoverIndex.value = -1;
}

/******************************************* 数据区 *************************************************/
const UIVO = ref({
    letterOverviewVOList: [
        {
            userId: 1,
            userName: "John",
            userAvatar: "https://img.zcool.cn/community/01f0865d45230ba8012187f485a17b.jpg@1280w_1l_2o_100sh.jpg",
            star: true,
            fan: false,
            block: false,
            newContent: "Hello",
            newDate: "2021-10-01",
            totalUnread: 2
        },
        {
            userId: 2,
            userName: "Alice",
            userAvatar: "https://img.zcool.cn/community/01f0865d45230ba8012187f485a17b.jpg@1280w_1l_2o_100sh.jpg",
            star: false,
            fan: true,
            block: true,
            newContent: "Hi",
            newDate: "2021-10-02",
            totalUnread: 3
        },
        {
            userId: 3,
            userName: "Bob",
            userAvatar: "https://img.zcool.cn/community/01f0865d45230ba8012187f485a17b.jpg@1280w_1l_2o_100sh.jpg",
            star: true,
            fan: true,
            block: false,
            newContent: "Hi",
            newDate: "2021-10-02",
            totalUnread: 10
        }
    ],
    letterDetailVOList: [
        {
            self: true,
            content: "Hello",
            createdAt: "2021-10-01 10:00:00"
        },
        {
            self: false,
            content: "Hi",
            createdAt: "2021-10-02 09:00:00"
        }
    ]
});
const searchInput = ref('')
const letterContent = ref('')
const activeLetterIndex = ref(0)
const activePopoverIndex = ref(-1)
const user = JSON.parse(localStorage.getItem("user") ?? '');
let socket: WebSocket | null = null;
let heartbeatTimer: any, retryTimer: any;
const MAX_WAIT_DURATION = 60000;    // 最大等待心跳时间
const MAX_RETRY_DURATION = 10000;   // 重试间隔时间
class LetterRequest {
  private content: string;
  private time: string;

  constructor(content: string, time: string) {
    this.content = content;
    this.time = time;
  }
}
class LetterMessage {
  // 消息发送者
  private from: string;
  // 消息接收者
  private to: string;
  // 消息类型
  private type: number;
  // 消息数据域
  private data: object;

  constructor(from: string, to: string, type: number, data: object) {
    this.from = from;
    this.to = to;
    this.type = type;
    this.data = data;
  }
}
/******************************************* 事件区 *************************************************/

// （1）切换私信列表
const clickChangeLetterEvent = (index) => {
    activeLetterIndex.value = index
    getLetterDetailRequest(UIVO.value.letterOverviewVOList[activeLetterIndex.value].userId)
        .then(res => {
            if (res != null) {
                UIVO.value.letterDetailVOList = res
                UIVO.value.letterOverviewVOList[activeLetterIndex.value].totalUnread = 0
                autoScrolToBottom()
            }
        }).catch(err => {
            console.log(err)
        })

}
// （2）点击发送私信
// 界面新增一个 LetterDetailVO
const clickSendLetterEvent = () => {
    // 构建请求参数
    const self = true
    const content = letterContent.value
    const createdAt = formatDate(new Date())
    // 请求后行发送
    sendLetterRequest(user?.id, UIVO.value.letterOverviewVOList[activeLetterIndex.value].userId, content, createdAt)
    // 发送成功
    UIVO.value.letterDetailVOList.push({ 'self': self, 'content': content, 'createdAt': createdAt })
    autoScrolToBottom()
    // 左边的菜单栏更新内容及时间
    UIVO.value.letterOverviewVOList[activeLetterIndex.value].newContent = content
    UIVO.value.letterOverviewVOList[activeLetterIndex.value].newDate = createdAt
    // 左边
    // 清空输入框
    letterContent.value = ''
}
// （3）回车发送私信
const enterSendLetterEvent = (event) => {
    if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault()
        clickSendLetterEvent()
    }
}
/**（4）删除私信事件
 *  如果删除的私信不是自身，私信列表减少一个即可
 *      否则，私信列表删除当前私信会话，并优先返回上一个私信
 *          如果上一个私信不存在，则返回下一个私信记录
 *          如果下一个私信记录不存在，则清空聊天框
 *      否则返回到上一个私信记录
 * @param index 
 */
const clickDeleteLetterEvent = (index) => {
    deleteLetterRequest(UIVO.value.letterOverviewVOList[index].userId)
        .then(res => {
            if (res) {
                // 删除当前私信
                UIVO.value.letterOverviewVOList.splice(index, 1)
                // 取消弹窗
                hidePopover()
                // 移动索引

                //  删除的私信记录不是当前私信记录，则返回
                if (index !== activeLetterIndex.value) {
                    return;
                }

                //  否则如果自身为最后一个索引，清空聊天框
                if (UIVO.value.letterOverviewVOList.length === 1) {
                    activeLetterIndex.value = -1
                    UIVO.value.letterDetailVOList = []
                    return;
                }

                //  如果当前索引不为 0，则移动到上一个索引
                if (index !== 0) {
                    //  移动到上一个索引
                    activeLetterIndex.value--;
                } else {
                    activeLetterIndex.value++;
                }

                clickChangeLetterEvent(activeLetterIndex.value)
            }
        }).catch(err => {
            console.log(err)
        })
}
// （5）屏蔽私信事件
const clickBlockedLetterEvent = (index) => {
    const blockedUserId = UIVO.value.letterOverviewVOList[index].userId
    if (UIVO.value.letterOverviewVOList[index].block) {
        unblockLetterRequest(blockedUserId)
            .then(res => {
                if (res) {
                    // 改变是否屏蔽
                    UIVO.value.letterOverviewVOList[index].block = !UIVO.value.letterOverviewVOList[index].block
                }
            }).catch(err => {
                errorMsg(err)
            })
    } else {
        blockLetterRequest(blockedUserId)
            .then(res => {
                if (res) {
                    // 改变是否屏蔽
                    UIVO.value.letterOverviewVOList[index].block = !UIVO.value.letterOverviewVOList[index].block
                }
            }).catch(err => {
                errorMsg(err)
            })
    }
    // 关闭弹出框
    hidePopover()
}
//  (6) 表情包点击事件: 添加标签包至输入框
const emojiClickEvent = (result) => {
    letterContent.value += result
}
/******************************************* 请求区 *************************************************/
// 加载组件，需完成两件任务: 1. 查询私信列表  2. 建立 websocket 连接
onMounted(() => {
    getLetterAllRequest()
        .then(res => {
            if (res != null) {
                UIVO.value = res
                autoScrolToBottom()
            }
        }).catch(err => {
            console.log(err)
        })
    createSocketConnect()
    
})
// 卸载组件，需完成一件任务: 1. 断开 websocket 连接
onBeforeUnmount(() => {
    socket?.close();
});
// - 创建 WebSocket 连接
const createSocketConnect = () => {
    socket = new WebSocket(`ws://localhost:9000/websocket/${user?.id}`);
    socket.onerror = function(error) {
        console.error('WebSocket connection error:', error);
    };
    socket.onmessage = (event) => {
        const data = JSON.parse(event.data)
        handleMessage(data)
    };
    socket.onopen = () => {
        console.log(`${user?.id}`)
        // 清空当前重连
        if (retryTimer) {
            clearInterval(retryTimer);  
        }
        // 等待接收心跳
        monitorHeartbeat();
    }
}

// 定时监控心跳任务：60s 没接收到服务端的心跳，则进行重新连接
const monitorHeartbeat = () => {
  // 开始新的定时任务
  heartbeatTimer = setTimeout(() => {
    clearTimeout(heartbeatTimer);
    reconnect();
  }, MAX_WAIT_DURATION);
}
// 定时断线重连任务：每 10s 尝试进行一次连接，连接成功则取消重连任务
const reconnect = () => {
    retryTimer = setInterval(() => {
        createSocketConnect()
    },  MAX_RETRY_DURATION);
}
// 发送私信请求
const sendLetterRequest = (userFromId, userToId, content, date) => {
    socket?.send(JSON.stringify(new LetterMessage(userFromId, userToId, 2, new LetterRequest(content, date))))
}
// 处理请求消息，并据此来判定是【接收心跳任务 | 接收私信任务】
const handleMessage = (message) => {
    console.log(message)
    switch (message.type) {
        case 0:
            handleHeartbeat();
            break;
        case 3:
            handleResponse(message.data);
            break;
    }
}
// 接收心跳，进行响应
const handleHeartbeat = () => {
    // 清除之前的监控心跳定时器
    clearTimeout(heartbeatTimer);
    socket?.send(JSON.stringify(new LetterMessage(user?.id, "0", 1, new Object())))
    // 开始新的监控心跳任务
    monitorHeartbeat();
}
// 接收私信，进行处理
const handleResponse = (data) => {
    const userId = data.userId;
    // 对方恰好是私信对方
    if (UIVO.value.letterOverviewVOList[activeLetterIndex.value].userId === userId) {
        // 数据推送入界面
        UIVO.value.letterDetailVOList.push({ self: false, content: data.newContent, createdAt: data.newDate })
        // 修改新内容，新时间
        UIVO.value.letterOverviewVOList[activeLetterIndex.value].newContent = data.newContent
        UIVO.value.letterOverviewVOList[activeLetterIndex.value].newDate = data.newDate
        autoScrolToBottom()
    } else {
        // 查看对方存在自己的私信列表
        let isExist = false, index = -1         // 默认不存在
        for (let i = 0; i < UIVO.value.letterOverviewVOList.length; i++) {
            if (UIVO.value.letterOverviewVOList[i].userId === userId) {
                isExist = true;
                index = i;
                break;
            }
        }
        // 如果存在
        if (isExist) {
            // 更新私信概览中对应用户的最新消息内容和时间
            UIVO.value.letterOverviewVOList[index].newContent = data.newContent;
            UIVO.value.letterOverviewVOList[index].newDate = data.newDate;
            UIVO.value.letterOverviewVOList[index].totalUnread++;
        } else {
            // 如果不存在，在私信概览中添加新的用户项
            const newLetterItem = data
            UIVO.value.letterOverviewVOList.unshift(newLetterItem);
        }
    }
}

// 1. 查询私信全部信息请求
const getLetterAllRequest = () => {
    return axios.get(`/social/v1/letters`)
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
// 2. 查询私信详情请求
const getLetterDetailRequest = (userId) => {
    return axios.get(`/social/v1/letters/${userId}`)
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
// 3. 删除私信记录请求
const deleteLetterRequest = (userId) => {
    return axios.delete(`/social/v1/letters/${userId}`)
        .then(response => {
            if (response.status === 200 && (response.data.code >= 200 || response.data.code <= 299)) {
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
// 4. 添加屏蔽私信请求
const blockLetterRequest = (blockedUserId) => {
    return axios.post(`/social/v1/letters/blocks/${blockedUserId}`)
        .then(response => {
            if (response.status === 200 && (response.data.code >= 200 || response.data.code <= 299)) {
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
// 5. 取消屏蔽私信请求
const unblockLetterRequest = (blockedUserId) => {
    return axios.delete(`/social/v1/letters/blocks/${blockedUserId}`)
        .then(response => {
            if (response.status === 200 && (response.data.code >= 200 || response.data.code <= 299)) {
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
</script>

<template>
    <el-card>
        <div class="chat-message-container">
            <el-row>
                <!-- 界面左部 -->
                <el-col :span="8" class="chat-message-left">
                    <div class="grid-content ep-bg-purple">
                        <!-- 聊天搜索框 -->
                        <div class="chat-search">
                            <div class="chat-search-item">
                                <div class="chat-search-info">
                                    <el-input v-model="searchInput" placeholder="搜索">
                                        <template #append>
                                            <el-button :icon="Search" />
                                        </template>
                                    </el-input>
                                </div>
                            </div>
                        </div>
                        <!-- 聊天记录 -->
                        <div class="chat-list">
                            <el-scrollbar ref="conversationScrollbarRef" height="74vh" always @scroll="chatListScroll">
                                <div class="chat-item" v-for="(letterOverview, index) in UIVO.letterOverviewVOList"
                                    :key="index" @click="clickChangeLetterEvent(index)" ref="conversationInnerRef">
                                    <el-popover trigger="contextmenu" placement="right"
                                        :visible="activePopoverIndex === index" @hide="hidePopover">
                                        <div style="text-align: left; margin: 0">
                                            <div><el-button size="small" text :icon="MuteNotification"
                                                    @click="clickBlockedLetterEvent(index)">
                                                    {{ letterOverview.block ? '取消屏蔽': '屏蔽私信' }}</el-button>
                                            </div>
                                            <div><el-button size="small" text :icon="Delete"
                                                    @click="clickDeleteLetterEvent(index)">删除记录</el-button></div>
                                        </div>
                                        <template #reference>
                                            <el-row class="chat-item-info" @contextmenu="showPopover(index, $event)"
                                                :class="{ 'chat-item-clicked': activeLetterIndex == index }">
                                                <el-col :span="3">
                                                    <div class="grid-content ep-bg-purple badge-container">
                                                        <el-badge :value="letterOverview.totalUnread" :max="99"
                                                            :is-dot="letterOverview.block"
                                                            :hidden="letterOverview.totalUnread == 0">
                                                            <el-avatar :size="40" :src="letterOverview.userAvatar" />
                                                        </el-badge>
                                                    </div>
                                                </el-col>
                                                <el-col :span="14">
                                                    <div class="grid-content ep-bg-purple">
                                                        <div class="chat-item-title">
                                                            <el-text>{{ letterOverview.userName }}</el-text>
                                                            <el-tag v-if="letterOverview.star">关注</el-tag>
                                                            <el-tag v-if="letterOverview.fan" type="success">粉丝</el-tag>
                                                        </div>
                                                        <div class="chat-item-content">
                                                            <el-text truncated>{{ letterOverview.newContent }}</el-text>
                                                        </div>
                                                    </div>
                                                </el-col>
                                                <el-col :span="7">
                                                    <div class="grid-content ep-bg-purple">
                                                        <div class="chat-item-time">{{ letterOverview.newDate }}</div>
                                                        <div class="chat-item-status" v-if="letterOverview.block"><el-icon>
                                                                <MuteNotification />
                                                            </el-icon></div>
                                                    </div>
                                                </el-col>
                                            </el-row>
                                        </template>
                                    </el-popover>
                                </div>
                            </el-scrollbar>
                        </div>
                    </div>
                </el-col>
                <!-- 界面右部 -->
                <el-col :span="16">
                    <div class="grid-content ep-bg-purple-light">
                        <!-- 对方名字 -->
                        <div class="chat-message-other">
                            <div><el-text class="chat-message-other-name" size="large">{{
                                UIVO.letterOverviewVOList[activeLetterIndex].userName }}</el-text></div>
                        </div>
                        <!-- 显示框 -->
                        <div class="chat-message-content">
                            <el-scrollbar ref="messageScrollbarRef" height="50vh" always @scroll="chatItemScroll">
                                <div class="chat-content" ref="messageInnerRef">
                                    <div v-for="(letterDetail, index) in UIVO.letterDetailVOList" :key="index">
                                        <!-- 对方 -->
                                        <div class="word" v-if="!letterDetail.self">
                                            <img class="chat-avatar"
                                                :src="UIVO.letterOverviewVOList[activeLetterIndex].userAvatar">
                                            <div class="info">
                                                <p class="time">{{ letterDetail.createdAt }}</p>
                                                <div class="info-content">{{ letterDetail.content }}</div>
                                            </div>
                                        </div>
                                        <!-- 我的 -->
                                        <div class="word-my" v-else>
                                            <div class="info">
                                                <p class="time">{{ letterDetail.createdAt }}</p>
                                                <div class="info-content">{{ letterDetail.content }}</div>
                                            </div>
                                            <img class="chat-avatar"
                                                :src="user?.avatar">
                                        </div>
                                    </div>
                                </div>
                            </el-scrollbar>
                        </div>
                        <!-- 输入框 -->
                        <div class="chat-message-control">
                            <!-- 输入框菜单栏 -->
                            <div class="chat-message-menu">
                                <div>
                                    <!-- 表情包 -->
                                    <el-popover placement="top" :width="400" trigger="click">
                                        <template #reference>
                                            <el-button circle>
                                                <IconEmoji />
                                            </el-button>
                                        </template>
                                        <EmojiPicker  @emojiClicked="emojiClickEvent"></EmojiPicker>
                                    </el-popover>
                                    <!-- 图片 -->
                                    <el-button :icon="Picture" circle />
                                    <!-- 文件 -->
                                    <el-button :icon="Share" circle />
                                </div>
                            </div>
                            <div class="chat-message-input">
                                <el-input v-model="letterContent" :rows="5" type="textarea" placeholder="请输入..."
                                    @keydown.enter="enterSendLetterEvent" />
                            </div>
                            <div class="chat-message-output">
                                <div class="chat-message-button">
                                    <el-button type="primary" :icon="Position" @click="clickSendLetterEvent">发送</el-button>
                                </div>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </div>
    </el-card>
</template>

<style scoped>
.chat-message-container {
    height: 80vh;
}

.chat-message-left {
    border-right: 1px solid rgb(223, 223, 237);
}

.grid-content {
    height: 100%;
}

.chat-search {
    padding: 10px 0;
    height: 6vh;
}

.chat-search-item {
    display: flex;
    justify-content: center;
}

/** 私信记录 */
.chat-item {
    display: flex;
    justify-content: center;
    margin: 10px 0;
}


.chat-item-info,
.chat-search-info {
    width: 90%;
}

.chat-item-info:hover {
    cursor: pointer;
}

.chat-item-clicked {
    background-color: #E6E8EB;
}

.badge-container {
    display: flex;
    align-items: center;
}


/** 右视图 */
.chat-message-other {
    display: flex;
    justify-content: center;
    height: 6vh;
    padding: 10px 0;
    box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.1);
}

.chat-message-content {
    height: 50vh;
}

.chat-message-menu {
    height: 6vh;
    padding-left: 20px;
    box-shadow: 0px -4px 4px rgba(0, 0, 0, 0.1);
    display: flex;
    align-items: center;
}

.chat-message-output {
    height: 6vh;
    display: flex;
    align-items: center;
}

.chat-message-button {
    margin-left: auto;
}

/**
    聊天框显示
*/


.chat-content {
    width: 100%;
    padding: 20px;
}

.word {
    display: flex;
    justify-content: flex-start;
    margin-bottom: 20px;
}

.word .chat-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
}

.word .info {
    margin-left: 10px;
}

.word .info .time {
    font-size: 12px;
    color: rgba(51, 51, 51, 0.8);
    margin: 0;
    height: 20px;
    line-height: 20px;
    margin-top: -5px;
}

.word .info .info-content {
    display: inline-block;
    padding: 10px 16px;
    font-size: 14px;
    background: #A3C3F6;
    position: relative;
    margin-top: 8px;
}

.word .info .info-content::before {
    position: absolute;
    left: -8px;
    content: '';
    border-right: 10px solid #A3C3F6;
    border-top: 8px solid transparent;
    border-bottom: 8px solid transparent;
}

.word-my {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 20px;
}

.word-my img {
    width: 40px;
    height: 40px;
    border-radius: 50%;
}

.word-my .info {
    width: 90%;
    margin-left: 10px;
    text-align: right;
}

.word-my .info .time {
    font-size: 12px;
    color: rgba(51, 51, 51, 0.8);
    margin: 0;
    height: 20px;
    line-height: 20px;
    margin-top: -5px;
    margin-right: 10px;
}

.word-my .info .info-content {
    max-width: 70%;
    padding: 10px;
    font-size: 14px;
    float: right;
    margin-right: 10px;
    position: relative;
    margin-top: 8px;
    background: #dedfe0;
    text-align: left;
}

.word-my .info .info-content::after {
    position: absolute;
    right: -8px;
    top: 8px;
    content: '';
    border-left: 10px solid #dedfe0;
    border-top: 8px solid transparent;
    border-bottom: 8px solid transparent;
}
</style>