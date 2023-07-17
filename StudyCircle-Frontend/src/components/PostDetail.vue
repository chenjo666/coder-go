<script lang="ts" setup>
import { View, Plus, Minus, Pointer, ChatRound, Link, Star, User } from '@element-plus/icons-vue'
import { reactive } from 'vue'
import { ref } from 'vue'
import { computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import CommentEditor from './CommentEditor.vue'
import axios from 'axios';
import { ElMessage } from 'element-plus'
import { marked } from 'marked';

//***************************************************** 工具区 ****************************************************/
const route = useRoute()
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
// 文本转换
const markedContent = (content) => {
    const after = marked.parse(content);
    return after;
}
//***************************************************** 数据区 ****************************************************/
// 测试 VO
const postDetailVO = ref({
    // 用户服务
    authorId: '1',
    authorName: '段张罗',
    authorAvatar: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png',
    // 帖子服务
    postId: '1',
    postTitle: '我是大傻瓜',
    postTime: '2020-01-01 00:00:01',
    postType: '分享',
    postContent: '我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜',
    postTags: ['Redis', '数据库', '缓存'],
    postVisits: 190992,
    // 收藏服务
    postFavorites: 9201,
    favorite: false,
    // 评论服务
    postReplies: 8,
    commentReplies: 10,
    // 点赞服务
    postLikes: 19000,
    like: true,
    // 关注服务
    followAuthor: true,
    // 评论服务
    parentCommentListVO: [
        {
            "userId": "1676909673324552210",
            userName: '张衡风',
            userAvatar: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png',
            commentId: '123',
            commentTime: '2020-01-01 08:01:02',
            commentContent: '我赞同你的观点',
            commentReplies: 12,
            commentLikes: 123,
            childCommentListVO: [
                {
                    userName: '序列化',
                    userAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
                    commentId: '2131',
                    commentTime: '2020-01-01 08:01:03',
                    commentLikes: 9,
                    commentContent: '您真是森头',
                    like: true
                },
                {
                    userName: '序列化2号',
                    userAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
                    commentId: '123',
                    commentTime: '2020-01-01 08:01:03',
                    commentLikes: 0,
                    commentContent: '您真是森头',
                    like: false
                },
            ],
            like: true
        },
        {
            userName: '短息龙',
            userAvatar: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png',
            commentId: "1",
            commentTime: '2020-01-01 08:01:02',
            commentContent: '我也赞同你的观点',
            commentLikes: 128,
            commentReplies: 89,
            childCommentListVO: [
                {
                    userName: '您真是深思熟虑',
                    userAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
                    commentId: "1",
                    commentTime: '2020-01-01 08:01:03',
                    commentContent: '您真是森头',
                    commentLikes: 9,
                    like: true
                },
                {
                    userName: '您真是深思熟虑',
                    userAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
                    commentId: "1",
                    commentTime: '2020-01-01 08:01:03',
                    commentContent: '您真是森头',
                    commentLikes: 19,
                    like: false
                },
            ],
            like: true
        },
        {
            userName: '罗好评',
            userAvatar: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png',
            commentId: "2",
            commentTime: '2020-01-01 08:01:02',
            commentContent: '我附以',
            commentLikes: 0,
            commentReplies: 12,
            childCommentListVO: [

            ],
            like: true
        },
    ]
})
const currentPage = ref(1)
const pageSize = ref(10)
const postId = ref('0')
const activeIndex = ref('1')
const objectType = ['post', 'comment']
const orderModeTable = ['', 'normal', 'newest']
const repliesVisible = reactive([])
const editorVisible = reactive({})
function toggleCommentEditor(index, childIndex) {
    const key = `reply_${index}_${childIndex}`;
    // 切换当前编辑框的显示状态
    editorVisible[key] = !editorVisible[key];
    // 遍历 editorVisible 对象
    for (const k in editorVisible) {
        if (k !== key) {
            editorVisible[k] = false; // 将其他编辑框设置为隐藏状态
        }
    }
}
//
function toggleRepliesVisible(index: number) {
    const key = `reply_${index}`;
    if (repliesVisible[key]) {
        // 如果属性已存在，则通过删除属性来切换显示状态
        delete repliesVisible[key];
    } else {
        // 否则，通过设置属性来切换显示状态
        repliesVisible[key] = true;
    }
}
const displayText = computed(() => {
    return (index: number) => {
        const key = `reply_${index}`;
        const cnt = postDetailVO.value.parentCommentListVO[index].commentReplies;
        return repliesVisible[key] ? '隐藏回复' : '查看' + cnt + '条回复';
    };
});

//***************************************************** 事件区 ****************************************************/
// 评论、回复、分页按钮都会刷新评论区，重新请求数据
// （0）刷新界面事件
onMounted(() => {
    postId.value = Array.isArray(route.params.postId) ? route.params.postId[0] : route.params.postId
    getPostDetailRequest(postId.value)
        .then(res => {
            if (res != null) {
                postDetailVO.value = res
            }
        }).catch(err => {
            console.log(err)
        })
})
// （1）关注事件
const clickFollowEvent = (userId) => {
    let isFollow = postDetailVO.value.followAuthor
    postDetailVO.value.followAuthor = !postDetailVO.value.followAuthor
    if (isFollow) {
        unfollowRequest(userId)
            .then(res => {
                console.log(res)
            }).catch(err => {
                console.log(err)
            })
    } else {
        followRequest(userId)
            .then(res => {
                console.log(res)
            }).catch(err => {
                console.log(err)
            })
    }
}
// （2）收藏事件
const clickFavoriteEvent = (postId) => {
    let isFavorite = postDetailVO.value.favorite
    postDetailVO.value.postFavorites = (postDetailVO.value.favorite ? postDetailVO.value.postFavorites - 1 : postDetailVO.value.postFavorites + 1)
    postDetailVO.value.favorite = !postDetailVO.value.favorite
    if (isFavorite) {
        unfavoriteRequest(postId)
            .then(res => {
                console.log("favorite it")
            }).catch(err => {
                console.log(err)
            })
    } else {
        favoriteRequest(postId)
            .then(res => {
                console.log("unfavorite it")
            }).catch(err => {
                console.log(err)
            })
    }
}
// （3）点赞事件（帖子、评论）
const clickLikeEvent = (objectId, objectType, parentIndex, childIndex) => {
    let isLike = false
    if (objectType == 'post') {
        postDetailVO.value.postLikes = (postDetailVO.value.like ? postDetailVO.value.postLikes - 1 : postDetailVO.value.postLikes + 1)
        if (postDetailVO.value.like) {
            isLike = true
        }
        postDetailVO.value.like = !postDetailVO.value.like
    } else {
        // 父级评论
        if (childIndex === -1) {
            postDetailVO.value.parentCommentListVO[parentIndex].commentLikes = (postDetailVO.value.parentCommentListVO[parentIndex].like ?
                postDetailVO.value.parentCommentListVO[parentIndex].commentLikes - 1 : postDetailVO.value.parentCommentListVO[parentIndex].commentLikes + 1)
            if (postDetailVO.value.parentCommentListVO[parentIndex].like) {
                isLike = true
            }
            postDetailVO.value.parentCommentListVO[parentIndex].like = !postDetailVO.value.parentCommentListVO[parentIndex].like
        } else {
            postDetailVO.value.parentCommentListVO[parentIndex].childCommentListVO[childIndex].commentLikes = (postDetailVO.value.parentCommentListVO[parentIndex].childCommentListVO[childIndex].like ?
                postDetailVO.value.parentCommentListVO[parentIndex].childCommentListVO[childIndex].commentLikes - 1 : postDetailVO.value.parentCommentListVO[parentIndex].childCommentListVO[childIndex].commentLikes + 1)
            if (postDetailVO.value.parentCommentListVO[parentIndex].childCommentListVO[childIndex].like) {
                isLike = true
            }
            postDetailVO.value.parentCommentListVO[parentIndex].childCommentListVO[childIndex].like = !postDetailVO.value.parentCommentListVO[parentIndex].childCommentListVO[childIndex].like
        }
    }
    if (isLike && objectType == 'post') {
        dislikePostRequest(objectId)
            .then(res => {
                console.log('like it');
            }).catch(err => {
                console.log(err)
            })
    } else if (isLike && objectType == 'comment') {
        dislikeCommentRequest(objectId)
            .then(res => {
                console.log('like it');
            }).catch(err => {
                console.log(err)
            })
    } else if (!isLike && objectType == 'post') {
        likePostRequest(objectId)
            .then(res => {
                console.log('like it');
            }).catch(err => {
                console.log(err)
            })
    } else if (!isLike && objectType == 'comment') {
        likeCommentRequest(objectId)
            .then(res => {
                console.log('like it');
            }).catch(err => {
                console.log(err)
            })
    }
}
// （4）评论结果事件
//  业务逻辑：发布自己的评论，如果成功则以最新排序规则返回评论区数据
//  评论需要调到最新界面、评论的评论不需要、评论的评论的评论不需要
const receiveCommentEvent = (idx1, idx2) => {
    console.log("idxs: ", idx1, idx2)
    if (idx1 === -1 && idx2 === -1) {
        getCommentListRequest(postDetailVO.value.postId, "newest", 1, pageSize.value)
            .then(res => {
                if (res != null) {
                    activeIndex.value = '2'
                    postDetailVO.value.parentCommentListVO = res
                }
            }).catch(err => {
                console.log(err)
            })
    } else {
        let commentId = postDetailVO.value.parentCommentListVO[idx1].commentId
        toggleCommentEditor(idx1, idx2)
        getCommentListByCommentRequest(commentId)
            .then(res => {
                if (res != null) {
                    postDetailVO.value.parentCommentListVO[idx1].commentReplies++
                    postDetailVO.value.parentCommentListVO[idx1].childCommentListVO = res
                }
            }).catch(err => {
                console.log(err)
            })
    }
}
// （5）点击评论按钮事件
//  业务规则：仅改变评论区对应页码的评论
const clickCommmentPageEvent = () => {
    const orderMode = orderModeTable[activeIndex.value]
    getCommentListRequest(postDetailVO.value.postId, orderMode, currentPage.value, pageSize.value)
        .then(res => {
            if (res != null) {
                postDetailVO.value.parentCommentListVO = res
            }
        }).catch(err => {
            console.log(err)
        })
}
// （6）点击评论排序事件
//  仅更新评论区，页码回到第一页
const clickOrderEvent = (ordermode: string) => {
    currentPage.value = 1
    getCommentListRequest(postDetailVO.value.postId, ordermode, currentPage.value, pageSize.value)
        .then(res => {
            if (res !== null) {
                postDetailVO.value.parentCommentListVO = res
            }
        }).catch(err => {
            console.log(err)
        })
}

/******************************************************* 请求区 *******************************************************/
// （0）刷新界面请求：传入帖子 id，返回帖子全部信息（默认第一页）
const getPostDetailRequest = (postId: string) => {
    return axios.get(`/posts/${postId}`, { params: { "currentPage": currentPage.value, "pageSize": pageSize.value } })
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
            return null
        })
}
// （1）添加关注请求，参数为对方 id
const followRequest = (userId: string) => {
    return axios.post(`/users/v1/following/${userId}`)
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
// （2）取消关注请求，参数为对方 id
const unfollowRequest = (userId: string) => {
    return axios.delete(`/users/v1/following/${userId}`)
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
// （3）添加收藏请求，参数为帖子 id
const favoriteRequest = (postId: string) => {
    return axios.post(`/posts/v1/${postId}/favorites`)
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
//  (4) 取消收藏请求，参数为帖子 id
const unfavoriteRequest = (postId: string) => {
    return axios.delete(`/posts/v1/${postId}/favorites`)
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
// （7）查询帖子的评论请求：传入【帖子id、页码、每页大小】，返回此页的评论对象数组 CommentVO[]
const getCommentListRequest = (postId: string, orderMode: string, currentPage: number, pageSize: number) => {
    return axios.get(`/comments/posts/${postId}`, { params: { 'orderMode': orderMode, 'currentPage': currentPage, 'pageSize': pageSize } })
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
//  (8) 查询评论的评论请求：传入【评论id】，返回【CommentVO[]】
const getCommentListByCommentRequest = (commentId: string) => {
    return axios.get(`/comments/comments/${commentId}`)
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
// （9）更新评论请求：传入【评论id、评论新内容】
const setCommentContentRequest = (commentId: string, newContent: string) => {

}
// （10）删除评论请求：传入【评论id】，返回状态码确认是否成功
const delCommentRequest = (commentId: string) => {
    return axios.delete(`/comments/${commentId}`)
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



// (v1.1) 点赞帖子请求：
const likePostRequest = (postId) => {
    return axios.post(`/posts/v1/${postId}/likes`)
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
// (v1.2) 取消点赞帖子请求：
const dislikePostRequest = (postId) => {
    return axios.delete(`/posts/v1/${postId}/likes`)
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
// (v1.3) 点赞评论请求：
const likeCommentRequest = (commentId) => {
    return axios.post(`/comments/v1/${commentId}/likes`)
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
// (v1.4) 取消点赞评论请求：
const dislikeCommentRequest = (commentId) => {
    return axios.delete(`/comments/v1/${commentId}/likes`)
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
</script>

<template>
    <el-card class="box-card">
        <div class="post-detail-container">
            <div class="common-layout">
                <el-container>
                    <el-main>
                        <div>
                            <!-- 帖子内容 -->
                            <div class="post-component">
                                <div class="post-info">
                                    <div class="post-header">
                                        <el-avatar :size="30" :src="postDetailVO.authorAvatar" class="post-author" />
                                        <el-text class="post-author-name">
                                            <h1 style="color:black;font-weight: 600;">{{ postDetailVO.postTitle }}</h1>
                                        </el-text>
                                        <el-button :type="postDetailVO.followAuthor ? 'info' : 'primary'"
                                            :icon="postDetailVO.followAuthor ? Minus : Plus"
                                            @click="clickFollowEvent(postDetailVO.authorId)">
                                            {{ postDetailVO.followAuthor ? '取消关注' : '一键关注' }}
                                        </el-button>
                                    </div>
                                    <div class="post-header-bottom">
                                        <div class="post-header-bottom-item">
                                            <el-text class="post-author-name"><el-icon>
                                                    <User />
                                                </el-icon>{{ postDetailVO.authorName }}</el-text>
                                        </div>
                                        <div class="post-header-bottom-item">
                                            <el-text class="post-views"><el-icon>
                                                    <View />
                                                </el-icon>{{ postDetailVO.postVisits }}</el-text>
                                        </div>

                                        <div class="post-header-bottom-item"><el-text class="post-create-time">发布于{{
                                            postDetailVO.postTime }}</el-text></div>
                                        <div class="post-header-bottom-item"><el-text class="post-tag"
                                                style="margin-right: 10px;">文章标签</el-text>
                                            <span v-if="postDetailVO.postTags != null">
                                                <el-tag type="info" v-for="tag in postDetailVO.postTags"
                                                    style="margin-right: 10px;">
                                                    {{ tag }}
                                                </el-tag>
                                            </span>
                                        </div>

                                    </div>
                                </div>


                                <div class="post-content" v-html="markedContent(postDetailVO.postContent)">
                                </div>
                            </div>
                            <!-- 操作 -->
                            <div class="post-operations-container">

                                <div class="post-operations-item"><el-text class="post-likes" @click="
                                    clickLikeEvent(postDetailVO.postId, objectType[0], -1, -1)"
                                        :style="{ color: (postDetailVO.like ? 'blue' : 'gray'), fontSize: '20px' }"><el-icon
                                            size="20">
                                            <Pointer />
                                        </el-icon>{{ postDetailVO.postLikes }}</el-text></div>
                                <div class="post-operations-item"><el-text class="post-collects"
                                        @click="clickFavoriteEvent(postDetailVO.postId)"
                                        :style="{ color: (postDetailVO.favorite ? 'blue' : 'gray'), fontSize: '20px' }"><el-icon
                                            size="20">
                                            <Star />
                                        </el-icon>{{ postDetailVO.postFavorites }}</el-text></div>
                                <div class="post-operations-item"><el-text class="post-replies"
                                        :style="{ fontSize: '20px' }"><el-icon size="20">
                                            <ChatRound />
                                        </el-icon>{{ postDetailVO.postReplies + postDetailVO.commentReplies }}</el-text></div>
                            </div>


                            <!-- 输入框 -->
                            <!-- <el-divider></el-divider> -->
                            <CommentEditor :objectId="postDetailVO.postId" :objectType="'post'" :parentIndex="-1" :childIndex="-1"
                                @commentResult="receiveCommentEvent" />

                            <!-- 评论区 -->
                            <div class="comment-component">
                                <el-divider>评论区</el-divider>
                                <div class="comment-menu">
                                    <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal"
                                        :ellipsis="false">
                                        <div class="flex-grow"></div>
                                        <el-menu-item index="1" @click="clickOrderEvent('normal')">综合</el-menu-item>
                                        <el-menu-item index="2" @click="clickOrderEvent('newest')">最新</el-menu-item>
                                    </el-menu>
                                </div>
                                <!-- 每条评论 -->
                                <div class="comment-info" v-for="(comment, index) in postDetailVO.parentCommentListVO"
                                    :key="index">
                                    <div class="comment-header">
                                        <el-avatar :size="30" :src="comment.userAvatar
                                            " class="comment-author" />
                                        <el-text class="comment-author-name">{{ comment.userName }}</el-text>
                                        <el-text class="comment-create-time">{{ comment.commentTime }}</el-text>
                                    </div>
                                    <div class="comment-content" v-html="markedContent(comment.commentContent)">
                                        
                                    </div>
                                    <div class="comment-operation">
                                        <el-text class="comment-likes"
                                            @click="clickLikeEvent(comment.commentId, objectType[1], index, -1)"
                                            :style="{ color: (comment.like ? 'blue' : 'gray') }"><el-icon>
                                                <Pointer />
                                            </el-icon>{{ comment.commentLikes == 0 ? '赞' : comment.commentLikes }}
                                        </el-text>
                                        <!-- 回复列表-按钮 -->
                                        <el-text class="comment-views clickable" @click="toggleRepliesVisible(index)"
                                            v-if="comment.commentReplies !== 0">
                                            <el-icon>
                                                <ChatRound />
                                            </el-icon> {{ displayText(index) }}
                                        </el-text>
                                        <el-text class="comment-reply" @click="toggleCommentEditor(index, -1)"><el-icon>
                                                <Link />
                                            </el-icon>{{ editorVisible[`reply_${index}_-1`] ? '收起' : '回复' }}</el-text>

                                    </div>
                                    <!-- CommentEditor 组件 -->
                                    <div :style="{ display: editorVisible[`reply_${index}_-1`] ? 'block' : 'none' }">
                                        <CommentEditor :objectId="comment.commentId" :objectType="'comment'"
                                            :parentIndex="index" :childIndex="-1" @commentResult="receiveCommentEvent" />
                                    </div>
                                    <!-- 回复列表 -->
                                    <el-card class="comment-replies"
                                        :style="{ display: repliesVisible[`reply_${index}`] ? 'block' : 'none' }">
                                        <div v-for="(childComment, childIndex) in comment.childCommentListVO"
                                            :key="childIndex" class="comment-info">
                                            <div class="comment-header">
                                                <el-avatar :size="30" :src="childComment.userAvatar" />
                                                <el-text class="comment-author-name">{{ childComment.userName }}</el-text>
                                                <el-text class="comment-create-time">{{ childComment.commentTime
                                                }}</el-text>
                                            </div>
                                            <div class="comment-content" v-html="markedContent(childComment.commentContent)">
                                            </div>
                                            <div class="comment-operation">
                                                <el-text class="comment-likes"
                                                    @click="clickLikeEvent(childComment.commentId, objectType[1], index, childIndex)"
                                                    :style="{ color: (childComment.like ? 'blue' : 'gray') }">
                                                    <el-icon>
                                                        <Pointer />
                                                    </el-icon>{{ childComment.commentLikes == 0 ? '赞' :
                                                        childComment.commentLikes }}</el-text>
                                                <el-text class="comment-reply"
                                                    @click="toggleCommentEditor(index, childIndex)"><el-icon>
                                                        <Link />
                                                    </el-icon>
                                                    {{ editorVisible[`reply_${index}_${childIndex}`] ? '收起' : '回复' }}
                                                </el-text>
                                            </div>
                                            <!-- CommentEditor 组件 -->
                                            <div
                                                :style="{ display: editorVisible[`reply_${index}_${childIndex}`] ? 'block' : 'none' }">
                                                <CommentEditor :objectId="childComment.commentId" :objectType="'comment'"
                                                    :parentIndex="index" :childIndex="childIndex"
                                                    @commentResult="receiveCommentEvent" />
                                            </div>
                                        </div>
                                    </el-card>
                                    <el-divider border-style="dotted"
                                        v-if="index !== postDetailVO.parentCommentListVO.length - 1" />
                                </div>
                            </div>
                            <!-- 分页-->
                            <div class="comment-pagination">
                                <div>
                                    <el-pagination @click="clickCommmentPageEvent()" v-model:current-page="currentPage"
                                        background layout="prev, pager, next" :total="postDetailVO.postReplies"
                                        :default-page-size="pageSize" />
                                </div>
                            </div>
                        </div>
                    </el-main>
                    <el-footer>

                    </el-footer>
                </el-container>
            </div>
        </div>
    </el-card>
</template>

<style scoped>
.post-detail-container {
    color: black;
}

.post-content {
    margin-top: 10px;
}

.post-component {
    background-color: white;
    border-radius: 4px;
    margin-bottom: 10px;
}

.post-operations-container {
    display: flex;
    align-items: center;
    margin: 20px 0;
    height: 50px;
    border: 2px solid rgb(245, 245, 245);
    border-radius: 4px;
    /* background-color: #ecf5ff; */
}

.post-operations-item {
    margin-right: 20px;
}

.post-header-bottom {
    display: flex;
    align-items: center;
}

.post-header-bottom-item {
    margin-right: 20px;
}

.post-header,
.comment-header {
    display: flex;
    align-items: center;
}

.post-author,
.comment-author,
.post-postTitle {
    margin-right: 10px;
}

.comment-menu {
    /** 评论排序方式 */
    margin-bottom: 20px;
}

.comment-operation {
    display: flex;
    /* 将父元素设置为弹性容器 */
    justify-content: left;
    /* 水平间距均匀分布 */
}

.comment-operation .comment-likes .comment-views .comment-reply {
    margin-right: 10px;
    /* 设置文本元素之间的右侧间距 */
}

.comment-likes:hover,
.comment-views:hover,
.comment-reply:hover {
    cursor: pointer;
    color: black;
}

.post-likes:hover,
.post-collects:hover,
.post-replies:hover {
    cursor: pointer;
    color: black;
}

.flex-grow {
    flex-grow: 1;
}

.replies {
    display: none;
    transition: max-height 0.3s ease-out;
    /* 添加过渡效果 */
}

.comment-replies {
    display: none;
}

.replies.show {
    display: block;
    max-height: 500px;
    /* 设置最大高度以控制过渡效果的展开高度 */
}

.comment-create-time {
    margin-left: 10px;
}

.comment-pagination {
    display: flex;
    justify-content: center;
}
</style>