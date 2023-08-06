<script lang="ts" setup>
import { View, Plus, Minus, Pointer, ChatRound, Link, Star, User } from '@element-plus/icons-vue'
import { reactive } from 'vue'
import { ref } from 'vue'
import { computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import CommentEditor from './ArticleCommentEditor.vue'
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
    const modified = after.replace(/<img src="([^"]+)"[^>]*>/g, '<img src="$1" style="max-width: 100%; height: auto;">');
    return modified;
}
//***************************************************** 数据区 ****************************************************/
// 测试 VO
const articleDetailVO = ref({
    // 用户服务
    authorId: '1',
    authorName: '段张罗',
    authorAvatar: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png',
    // 文章服务
    articleId: '1',
    articleTitle: '我是大傻瓜',
    articleCreatedAt: '2020-01-01 00:00:01',
    articleType: '分享',
    articleContent: '我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜我是大傻瓜',
    articleTags: ['Redis', '数据库', '缓存'],
    totalViews: 190992,
    // 收藏服务
    totalFavorites: 9201,
    favorite: false,
    // 评论服务
    totalReplies: 8,
    totalComments: 10,
    // 点赞服务
    totalLikes: 19000,
    like: true,
    // 关注服务
    followAuthor: true,
    // 评论服务
    parentCommentListVO: [
        {
            userId: "1676909673324552210",
            userName: '张衡风',
            userAvatar: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png',
            commentId: '123',
            commentCreatedAt: '2020-01-01 08:01:02',
            commentContent: '我赞同你的观点',
            totalReplies: 12,
            totalLikes: 123,
            like: true,
            childCommentListVO: [
                {
                    userName: '序列化',
                    userAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
                    commentId: '2131',
                    commentCreatedAt: '2020-01-01 08:01:03',
                    commentContent: '您真是森头',
                    totalLikes: 9,
                    like: true
                },
                {
                    userName: '序列化2号',
                    userAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
                    commentId: '123',
                    commentCreatedAt: '2020-01-01 08:01:03',
                    totalLikes: 0,
                    commentContent: '您真是森头',
                    like: false
                },
            ]
        },
        {
            userName: '短息龙',
            userAvatar: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png',
            commentId: "1",
            commentCreatedAt: '2020-01-01 08:01:02',
            commentContent: '我也赞同你的观点',
            totalLikes: 128,
            totalReplies: 89,
            like: true,
            childCommentListVO: [
                {
                    userName: '您真是深思熟虑',
                    userAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
                    commentId: "1",
                    commentCreatedAt: '2020-01-01 08:01:03',
                    commentContent: '您真是森头',
                    totalLikes: 9,
                    like: true
                }
            ]
        },
        {
            userName: '罗好评',
            userAvatar: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png',
            commentId: "2",
            commentCreatedAt: '2020-01-01 08:01:02',
            commentContent: '我附以',
            totalLikes: 0,
            totalReplies: 12,
            childCommentListVO: [],
            like: true
        },
    ]
})
const COMMENT_SORT = ['default', 'new']
const COMMENT_OBJ = ['article', 'comment']
const PAGE_SIZE = 10

const activeIndex = ref('0')
const currentPage = ref(1)

const articleId = ref('0')
const repliesVisible = reactive([])
const editorVisible = reactive({})
const user = JSON.parse(localStorage.getItem("user") ?? '');
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
        const cnt = articleDetailVO.value.parentCommentListVO[index].totalReplies;
        return repliesVisible[key] ? '隐藏回复' : '查看' + cnt + '条回复';
    };
});

//***************************************************** 事件区 ****************************************************/
// 评论、回复、分页按钮都会刷新评论区，重新请求数据
// （0）刷新界面事件
onMounted(() => {
    articleId.value = Array.isArray(route.params.articleId) ? route.params.articleId[0] : route.params.articleId
    getArticleDetailRequest(articleId.value)
        .then(res => {
            articleDetailVO.value = res?.data
        }).catch(err => {
            console.log(err)
        })
})
// （1）关注事件
const clickFollowEvent = (userId) => {
    let isFollow = articleDetailVO.value.followAuthor
    articleDetailVO.value.followAuthor = !articleDetailVO.value.followAuthor
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
const clickFavoriteEvent = (articleId) => {
    let isFavorite = articleDetailVO.value.favorite
    articleDetailVO.value.totalFavorites = (articleDetailVO.value.favorite ? articleDetailVO.value.totalFavorites - 1 : articleDetailVO.value.totalFavorites + 1)
    articleDetailVO.value.favorite = !articleDetailVO.value.favorite
    if (isFavorite) {
        unfavoriteRequest(articleId)
            .then(res => {
                console.log("favorite it")
            }).catch(err => {
                console.log(err)
            })
    } else {
        favoriteRequest(articleId)
            .then(res => {
                console.log("unfavorite it")
            }).catch(err => {
                console.log(err)
            })
    }
}
// （3）点赞事件（文章、评论）
const clickLikeEvent = (objectId, objectType, parentIndex, childIndex) => {
    let isLike = false
    if (objectType == 'article') {
        articleDetailVO.value.totalLikes = (articleDetailVO.value.like ? articleDetailVO.value.totalLikes - 1 : articleDetailVO.value.totalLikes + 1)
        if (articleDetailVO.value.like) {
            isLike = true
        }
        articleDetailVO.value.like = !articleDetailVO.value.like
    } else {
        // 父级评论
        if (childIndex === -1) {
            articleDetailVO.value.parentCommentListVO[parentIndex].totalLikes = (articleDetailVO.value.parentCommentListVO[parentIndex].like ?
                articleDetailVO.value.parentCommentListVO[parentIndex].totalLikes - 1 : articleDetailVO.value.parentCommentListVO[parentIndex].totalLikes + 1)
            if (articleDetailVO.value.parentCommentListVO[parentIndex].like) {
                isLike = true
            }
            articleDetailVO.value.parentCommentListVO[parentIndex].like = !articleDetailVO.value.parentCommentListVO[parentIndex].like
        } else {
            articleDetailVO.value.parentCommentListVO[parentIndex].childCommentListVO[childIndex].totalLikes = (articleDetailVO.value.parentCommentListVO[parentIndex].childCommentListVO[childIndex].like ?
                articleDetailVO.value.parentCommentListVO[parentIndex].childCommentListVO[childIndex].totalLikes - 1 : articleDetailVO.value.parentCommentListVO[parentIndex].childCommentListVO[childIndex].totalLikes + 1)
            if (articleDetailVO.value.parentCommentListVO[parentIndex].childCommentListVO[childIndex].like) {
                isLike = true
            }
            articleDetailVO.value.parentCommentListVO[parentIndex].childCommentListVO[childIndex].like = !articleDetailVO.value.parentCommentListVO[parentIndex].childCommentListVO[childIndex].like
        }
    }
    if (isLike && objectType == 'article') {
        dislikeArticleRequest(objectId)
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
    } else if (!isLike && objectType == 'article') {
        likeArticleRequest(objectId)
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
        clickOrderEvent(COMMENT_SORT[1])
        activeIndex.value = '1'
    } else {
        let commentId = articleDetailVO.value.parentCommentListVO[idx1].commentId
        toggleCommentEditor(idx1, idx2)
        getCommentListByCommentRequest(commentId)
            .then(res => {
                if (res != null) {
                    articleDetailVO.value.parentCommentListVO[idx1].totalReplies++
                    articleDetailVO.value.parentCommentListVO[idx1].childCommentListVO = res
                }
            }).catch(err => {
                console.log(err)
            })
    }
}
// （5）点击评论按钮事件
//  业务规则：仅改变评论区对应页码的评论
const clickCommmentPageEvent = () => {
    const orderMode = COMMENT_SORT[activeIndex.value]
    getCommentListRequest(articleDetailVO.value.articleId, orderMode, currentPage.value, PAGE_SIZE)
        .then(res => {
            if (res != null) {
                articleDetailVO.value.parentCommentListVO = res
            }

        }).catch(err => {
            console.log(err)
        })
}
// （6）点击评论排序事件
//  仅更新评论区，页码回到第一页
const clickOrderEvent = (ordermode: string) => {
    currentPage.value = 1
    getCommentListRequest(articleDetailVO.value.articleId, ordermode, currentPage.value, PAGE_SIZE)
        .then(res => {
            if (res !== null) {
                articleDetailVO.value.parentCommentListVO = res
            }
        }).catch(err => {
            console.log(err)
        })
}

/******************************************************* 请求区 *******************************************************/
// （0）刷新界面请求：传入文章 id，返回文章全部信息（默认第一页）
const getArticleDetailRequest = (articleId: string) => {
    return axios.get(`/article/v1/articles/${articleId}`, { params: { "commentPage": currentPage.value, "commentLimit": PAGE_SIZE } })
        .then(response => {
            if (response.status === 200 && response.data.code === 200) {
                return response.data
            }
            console.log(response.status)
            errorMsg(response.status !== 200 ? '网络出错' : '服务器出错')
            return null 
        }).catch(error => {
            errorMsg(error)
            return null
        })
}
// （1）添加关注请求，参数为对方 id
const followRequest = (userId: string) => {
    return axios.post(`/social/v1/following/${userId}`)
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
    return axios.delete(`/social/v1/following/${userId}`)
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
// （3）添加收藏请求，参数为文章 id
const favoriteRequest = (articleId: string) => {
    return axios.post(`/article/v1/articles/${articleId}/favorites`)
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
//  (4) 取消收藏请求，参数为文章 id
const unfavoriteRequest = (articleId: string) => {
    return axios.delete(`/article/v1/articles/${articleId}/favorites`)
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
// （7）查询文章的评论请求：传入【文章id、页码、每页大小】，返回此页的评论对象数组 CommentVO[]
const getCommentListRequest = (articleId: string, sort: string, page: number, limit: number) => {
    return axios.get(`/article/v1/articles/${articleId}/comments`, { params: { 'commentSort': sort, 'commentPage': page, 'commentLimit': limit } })
        .then(response => {
            if (response.status === 200 && (response.data.code >= 200 || response.data.code <= 299)) {
                return response.data.data;
            }
            errorMsg(response.status === 200 ? '网络错误' : response.data.msg);
            return null;
        })
        .catch(error => {
            console.error(error);
            return null
        });
}
//  (8) 查询评论的评论请求：传入【评论id】，返回【CommentVO[]】
const getCommentListByCommentRequest = (commentId: string) => {
    return axios.get(`/articles/v1/articles/comments/${commentId}/comments`)
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
const delCommentRequest = (articleId: string, commentId: string) => {
    return axios.delete(`/articles/v1/articles/${articleId}/comments/${commentId}`)
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
// (v1.1) 点赞文章请求：
const likeArticleRequest = (articleId) => {
    return axios.post(`/article/v1/articles/${articleId}/likes`)
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
// (v1.2) 取消点赞文章请求：
const dislikeArticleRequest = (articleId) => {
    return axios.delete(`/article/v1/articles/${articleId}/likes`)
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
    return axios.post(`/article/v1/articles/comments/${commentId}/likes`)
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
    return axios.delete(`/article/v1/articles/comments/${commentId}/likes`)
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
        <div class="article-detail-container">
            <div class="common-layout">
                <el-container>
                    <el-main>
                        <div>
                            <!-- 文章内容 -->
                            <div class="article-component">
                                <div class="article-info">
                                    <div class="article-header">
                                        <el-avatar :size="30" :src="articleDetailVO.authorAvatar" class="article-author" />
                                        <el-text class="article-author-name">
                                            <h1 style="color:black;font-weight: 600;">{{ articleDetailVO.articleTitle }}</h1>
                                        </el-text>
                                        <el-button
                                            v-if="articleDetailVO.authorId !== user?.id" 
                                            :type="articleDetailVO.followAuthor ? 'info' : 'primary'"
                                            :icon="articleDetailVO.followAuthor ? Minus : Plus"
                                            @click="clickFollowEvent(articleDetailVO.authorId)">
                                            {{ articleDetailVO.followAuthor ? '取消关注' : '一键关注' }}
                                        </el-button>
                                    </div>
                                    <div class="article-header-bottom">
                                        <div class="article-header-bottom-item">
                                            <el-text class="article-author-name"><el-icon>
                                                    <User />
                                                </el-icon>{{ articleDetailVO.authorName }}</el-text>
                                        </div>
                                        <div class="article-header-bottom-item">
                                            <el-text class="article-views"><el-icon>
                                                    <View />
                                                </el-icon>{{ articleDetailVO.totalViews }}</el-text>
                                        </div>

                                        <div class="article-header-bottom-item"><el-text class="article-create-time">发布于{{
                                            articleDetailVO.articleCreatedAt }}</el-text></div>
                                        <div class="article-header-bottom-item"><el-text class="article-tag"
                                                style="margin-right: 10px;">文章标签</el-text>
                                            <span v-if="articleDetailVO.articleTags != null">
                                                <el-tag type="info" v-for="tag in articleDetailVO.articleTags"
                                                    style="margin-right: 10px;">
                                                    {{ tag }}
                                                </el-tag>
                                            </span>
                                        </div>

                                    </div>
                                </div>
                                <div class="article-content" v-html="markedContent(articleDetailVO.articleContent)">
                                </div>
                            </div>
                            <!-- 操作 -->
                            <div class="article-operations-container">

                                <div class="article-operations-item"><el-text class="article-likes" @click="
                                    clickLikeEvent(articleDetailVO.articleId, COMMENT_OBJ[0], -1, -1)"
                                        :style="{ color: (articleDetailVO.like ? 'blue' : 'gray'), fontSize: '20px' }"><el-icon
                                            size="20">
                                            <Pointer />
                                        </el-icon>{{ articleDetailVO.totalLikes }}</el-text></div>
                                <div class="article-operations-item"><el-text class="article-collects"
                                        @click="clickFavoriteEvent(articleDetailVO.articleId)"
                                        :style="{ color: (articleDetailVO.favorite ? 'blue' : 'gray'), fontSize: '20px' }"><el-icon
                                            size="20">
                                            <Star />
                                        </el-icon>{{ articleDetailVO.totalFavorites }}</el-text></div>
                                <div class="article-operations-item"><el-text class="article-replies"
                                        :style="{ fontSize: '20px' }"><el-icon size="20">
                                            <ChatRound />
                                        </el-icon>{{ articleDetailVO.totalReplies }}</el-text></div>
                            </div>


                            <!-- 输入框 -->
                            <!-- <el-divider></el-divider> -->
                            <CommentEditor :articleId="articleDetailVO.articleId"  
                                :objectId="articleDetailVO.articleId" 
                                :objectType="'article'" 
                                :parentIndex="-1" :childIndex="-1"
                                @commentResult="receiveCommentEvent" />

                            <!-- 评论区 -->
                            <div class="comment-component">
                                <el-divider>评论区</el-divider>
                                <div class="comment-menu">
                                    <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal"
                                        :ellipsis="false">
                                        <div class="flex-grow"></div>
                                        <el-menu-item index="0" @click="clickOrderEvent(COMMENT_SORT[0])">综合</el-menu-item>
                                        <el-menu-item index="1" @click="clickOrderEvent(COMMENT_SORT[1])">最新</el-menu-item>
                                    </el-menu>
                                </div>
                                <!-- 每条评论 -->
                                <div class="comment-info" v-for="(comment, index) in articleDetailVO.parentCommentListVO"
                                    :key="index">
                                    <div class="comment-header">
                                        <el-avatar :size="30" :src="comment.userAvatar
                                            " class="comment-author" />
                                        <el-text class="comment-author-name">{{ comment.userName }}</el-text>
                                        <el-text class="comment-create-time">{{ comment.commentCreatedAt }}</el-text>
                                    </div>
                                    <div class="comment-content" v-html="markedContent(comment.commentContent)">
                                        
                                    </div>
                                    <div class="comment-operation">
                                        <el-text class="comment-likes"
                                            @click="clickLikeEvent(comment.commentId, COMMENT_OBJ[1], index, -1)"
                                            :style="{ color: (comment.like ? 'blue' : 'gray') }"><el-icon>
                                                <Pointer />
                                            </el-icon>{{ comment.totalLikes == 0 ? '赞' : comment.totalLikes }}
                                        </el-text>
                                        <!-- 回复列表-按钮 -->
                                        <el-text class="comment-views clickable" @click="toggleRepliesVisible(index)"
                                            v-if="comment.totalReplies !== 0">
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
                                        <CommentEditor :articleId="articleDetailVO.articleId"  
                                            :objectId="comment.commentId" :objectType="'comment'"
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
                                                <el-text class="comment-create-time">{{ childComment.commentCreatedAt
                        
                                                }}</el-text>
                                            </div>
                                            <div class="comment-content" v-html="markedContent(childComment.commentContent)">
                                            </div>
                                            <div class="comment-operation">
                                                <el-text class="comment-likes"
                                                    @click="clickLikeEvent(childComment.commentId, COMMENT_OBJ[1], index, childIndex)"
                                                    :style="{ color: (childComment.like ? 'blue' : 'gray') }">
                                                    <el-icon>
                                                        <Pointer />
                                                    </el-icon>{{ childComment.totalLikes == 0 ? '赞' :
                                                        childComment.totalLikes }}</el-text>
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
                                                <CommentEditor :articleId="articleDetailVO.articleId" 
                                                    :objectId="childComment.commentId" 
                                                    :objectType="'comment'"
                                                    :parentIndex="index" 
                                                    :childIndex="childIndex"
                                                    @commentResult="receiveCommentEvent" />
                                            </div>
                                        </div>
                                    </el-card>
                                    <el-divider border-style="dotted"
                                        v-if="index !== articleDetailVO.parentCommentListVO.length - 1" />
                                </div>
                            </div>
                            <!-- 分页-->
                            <div class="comment-pagination">
                                <div>
                                    <el-pagination @click="clickCommmentPageEvent()" v-model:current-page="currentPage"
                                        background layout="prev, pager, next" :total="articleDetailVO.totalComments"
                                        :default-page-size="PAGE_SIZE" />
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
.article-detail-container {
    color: black;
}

.article-content {
    margin-top: 10px;
}

.article-component {
    background-color: white;
    border-radius: 4px;
    margin-bottom: 10px;
}

.article-operations-container {
    display: flex;
    align-items: center;
    margin: 20px 0;
    height: 50px;
    border: 2px solid rgb(245, 245, 245);
    border-radius: 4px;
    /* background-color: #ecf5ff; */
}

.article-operations-item {
    margin-right: 20px;
}

.article-header-bottom {
    display: flex;
    align-items: center;
}

.article-header-bottom-item {
    margin-right: 20px;
}

.article-header,
.comment-header {
    display: flex;
    align-items: center;
}

.article-author,
.comment-author,
.article-articleTitle {
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

.article-likes:hover,
.article-collects:hover,
.article-replies:hover {
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

img {width: 200px;}
</style>