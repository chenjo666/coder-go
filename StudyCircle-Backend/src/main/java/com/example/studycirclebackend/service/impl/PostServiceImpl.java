package com.example.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studycirclebackend.dao.PostMapper;
import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.enums.*;
import com.example.studycirclebackend.pojo.Comment;
import com.example.studycirclebackend.pojo.Post;
import com.example.studycirclebackend.pojo.User;
import com.example.studycirclebackend.service.*;
import com.example.studycirclebackend.util.DataUtil;
import com.example.studycirclebackend.util.TextUtil;
import com.example.studycirclebackend.util.UserUtil;
import com.example.studycirclebackend.vo.CommentVO;
import com.example.studycirclebackend.vo.PostPersonalVO;
import com.example.studycirclebackend.vo.PostOverviewVO;
import com.example.studycirclebackend.vo.PostDetailVO;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);
    @Resource
    private UserService userService;
    @Resource
    private UserUtil userUtil;
    @Resource
    private LikeService likeService;
    @Resource
    private FollowService followService;
    @Resource
    private FavoriteService favoriteService;
    @Resource
    private CommentService commentService;


    @Override
    public Response getPostDetail(Long postId, Integer currentPage, Integer pageSize) {
        if (postId == null || currentPage == null || pageSize == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        Post post = getById(postId);
        if (post == null) {
            return Response.builder().code(-1).msg("参数错误！").build();
        }
        User author = userService.getById(post.getUserId());
        PostDetailVO postDetailVO = new PostDetailVO();
        postDetailVO.setAuthorId(author.getId());
        postDetailVO.setAuthorName(author.getUsername());
        postDetailVO.setAuthorAvatar(author.getAvatar());
        postDetailVO.setPostId(post.getId());
        postDetailVO.setPostContent(post.getContent());
        postDetailVO.setPostTitle(post.getTitle());
        postDetailVO.setPostType(post.getType());
        postDetailVO.setPostTime(DataUtil.formatDateTime(post.getPublishTime()));
        postDetailVO.setPostTags(resolveTags(post.getTags()));
        postDetailVO.setPostVisits(108472);

        // 点赞业务
        Long count = likeService.getPostLikeTotal(postId);
        boolean isLike = likeService.isLikePostByUser(postId, userUtil.getUser().getId());
        postDetailVO.setPostLikes(Math.toIntExact(count));
        postDetailVO.setLike(isLike);

        // 收藏业务
        postDetailVO.setPostFavorites(Math.toIntExact(favoriteService.getPostFavoriteTotal(postId)));
        postDetailVO.setFavorite(favoriteService.isFavoritePostByUser(postId, userUtil.getUser().getId()));

        // 关注业务
        boolean isFollowed = followService.isFollowedByUser(userUtil.getUser().getId(), post.getUserId());
        postDetailVO.setFollowAuthor(isFollowed);

        List<Comment> commentParentList = commentService.list(new QueryWrapper<Comment>()
                .eq("object_type", CommentObjectType.POST.getValue())
                .eq("object_id", post.getId())
                .orderByDesc("score")
                .last(String.format("LIMIT %d,%d", (currentPage - 1) * pageSize, pageSize)));

        List<CommentVO> parentCommentListVO = new ArrayList<>();

        for (Comment comment : commentParentList) {
            CommentVO commentVO = new CommentVO();

            User user = userService.getById(comment.getUserId());
            // 作者
            commentVO.setUserId(user.getId());
            commentVO.setUserAvatar(user.getAvatar());
            commentVO.setUserName(user.getUsername());
            // 评论
            commentVO.setCommentId(comment.getId());
            commentVO.setCommentTime(DataUtil.formatDateTime(comment.getCommentTime()));
            commentVO.setCommentContent(comment.getContent());
            commentVO.setCommentScore(comment.getScore());
            // 点赞
            count = likeService.getCommentLikeTotal(comment.getId());
            isLike = likeService.isLikeCommentByUser(comment.getId(), userUtil.getUser().getId());
            commentVO.setCommentLikes(Math.toIntExact(count));
            commentVO.setLike(isLike);
            // 子评论
            List<CommentVO> childCommentListVO = new ArrayList<>();
            dfs(comment, childCommentListVO);
            // 子评论按照分数排序
            childCommentListVO.sort(Comparator.comparing(CommentVO::getCommentScore));
            commentVO.setChildCommentListVO(childCommentListVO);
            commentVO.setCommentReplies(childCommentListVO.size());
            parentCommentListVO.add(commentVO);
        }
        parentCommentListVO.sort(Comparator.comparing(CommentVO::getCommentScore)); // 外层评论排序

        // 外层评论数量等于所有评论的总数
        List<Comment> allParentComments = commentService.getCommentAllByPost(postId);
        postDetailVO.setPostReplies(allParentComments.size());// 外层评论数量
        // 内层评论数量等于所有评论的子评论的总数
        int commentReplies = 0;
        for (Comment comment : allParentComments) {
            commentReplies += commentService.getChildCommentsByComment(comment.getId()).size();
        }
        postDetailVO.setCommentReplies(commentReplies);// 内层评论数量
        // 子评论
        postDetailVO.setParentCommentListVO(parentCommentListVO);

        return Response.builder().code(200).data(postDetailVO).build();
    }

    @Override
    public Response createPost(String postTitle, String postContent, String postType, List<String> postTags) {
        if (StringUtils.isBlank(postTitle) || StringUtils.isBlank(postContent) || StringUtils.isBlank(postType) || postTags == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        User user = userUtil.getUser();
        Post post = new Post();
        post.setUserId(user.getId());
        post.setTitle(postTitle);
        post.setContent(TextUtil.filter(postContent));
        post.setType(postType);
        post.setPublishTime(new Date());
        post.setIsGem(0);
        post.setIsTop(0);
        StringBuilder tags = new StringBuilder();
        if (postTags.size() != 0) {
            for (String tag : postTags) {
                tags.append(tag + ",");
            }
        }
        post.setTags(tags.toString());
        save(post);
        return Response.builder().code(200).build();
    }

    // 更新帖子
    @Override
    public Response updatePost(Long postId, String newContent) {
        if (postId == null || StringUtils.isBlank(newContent)) {
            return Response.builder()
                    .code(400)
                    .build();
        }
        boolean result = update(new UpdateWrapper<Post>().set("content", newContent).eq("id", postId));

        return Response.builder()
                .code(result ? ResponseCode.SUCCESS.getValue() : ResponseCode.FAILURE.getValue())
                .build();
    }

    // 删除帖子
    @Override
    public Response deletePost(Long postId) {
        if (postId == null) {
            return Response.builder()
                    .code(400)
                    .build();
        }
        boolean result = removeById(postId);
        return Response.builder()
                .code(result ? ResponseCode.SUCCESS.getValue() : ResponseCode.FAILURE.getValue())
                .build();
    }

    @Override
    public PostDetailVO getPostDetailVO(Post post) {
        PostDetailVO postDetailVO = new PostDetailVO();

        User author = userService.getById(post.getUserId());

        postDetailVO.setAuthorId(author.getId());
        postDetailVO.setAuthorName(author.getUsername());
        postDetailVO.setAuthorAvatar(author.getAvatar());

        postDetailVO.setPostId(post.getId());
        postDetailVO.setPostContent(post.getContent());
        postDetailVO.setPostTitle(post.getTitle());
        postDetailVO.setPostType(post.getType());
        postDetailVO.setPostTime(DataUtil.formatDateTime(post.getPublishTime()));
        postDetailVO.setPostTags(resolveTags(post.getTags()));
        postDetailVO.setPostVisits(108472);

        return postDetailVO;
    }

    @Override
    public PostPersonalVO convertToPersonPostVO(Post post) {
        PostPersonalVO favoritePostVO = new PostPersonalVO();
        favoritePostVO.setPostId(post.getId());
        favoritePostVO.setPostTime(DataUtil.formatDateTime(post.getPublishTime()));
        favoritePostVO.setPostViews(new Random().nextInt(1000));
        Long likes = likeService.getPostLikeTotal(post.getId());
        favoritePostVO.setPostLikes(Math.toIntExact(likes));
        return null;
    }

    @Override
    public Response getPublishPosts(Long userId) {
        if (userId == null) {
            return Response.builder().badRequest().build();
        }
        // 1. 得到发布的帖子
        List<Post> posts = list(new QueryWrapper<Post>().eq("user_id", userId));
        // 2. 转换为 VO
        List<PostPersonalVO> postPersonalVOList = posts
                .stream()
                .map(this::convertToPersonPostVO)
                .collect(Collectors.toList());
        return Response.builder().code(200).data(postPersonalVOList).build();
    }

    @Override
    public Response getFavoritePosts(Long userId) {
        if (userId == null) {
            return Response.builder().badRequest().build();
        }
        // 1. 得到收藏的帖子
        List<Post> posts = list(new QueryWrapper<Post>()
                .inSql("id", "SELECT post_id FROM favorite WHERE user_id = " + userId));
        // 2. 转换为 VO
        List<PostPersonalVO> postPersonalVOList = posts
                .stream()
                .map(this::convertToPersonPostVO)
                .collect(Collectors.toList());
        return Response.builder().ok().data(postPersonalVOList).build();
    }

    /**
     * 得到帖子总览视图对象
     * @param type
     * @param order
     * @param key
     * @param page
     * @param limit
     * @return
     */
    @Override
    public Response getPosts(String type, String order, String key, Integer page, Integer limit) {
        if (StringUtils.isBlank(type) || StringUtils.isBlank(order) || page == null || limit == null) {
            return Response.builder().badRequest().build();
        }
        QueryWrapper<Post> queryWrapper = getOrderCondition(type, order, key);
        queryWrapper.last(String.format("LIMIT %d,%d", (page - 1) * limit, limit));
        List<Post> posts = list(queryWrapper);
        Map<String, Object> data = new HashMap<>();
        List<PostOverviewVO> postOverviewVOs = new ArrayList<>();
        for (Post post : posts) {
            postOverviewVOs.add(getPostOverviewVO(post));
        }
        // 填补其它业务数据
        for (PostOverviewVO postOverviewVO : postOverviewVOs) {
            // 用户服务
            User user = userService.getById(postOverviewVO.getUserId());
            postOverviewVO.setUserAvatar(user.getAvatar());
            // 关注服务
            Long postLikes = likeService.getPostLikeTotal(postOverviewVO.getPostId());
            postOverviewVO.setPostLikes(postLikes);
            // 统计服务
            postOverviewVO.setPostViews(100L);
            // 评论服务
            Long postReplies = commentService.getPostRepliesByPostId(postOverviewVO.getPostId());
            postOverviewVO.setPostReplies(postReplies);
        }
        data.put("posts", postOverviewVOs);
        data.put("postTotal", getPostTotal(type, order, key));
        return Response.builder().ok().data(data).build();
    }

    /**
     * 得到帖子的总数
     * @param type
     * @param order
     * @param key
     * @return
     */
    @Override
    public Integer getPostTotal(String type, String order, String key) {
        QueryWrapper<Post> queryWrapper = getOrderCondition(type, order, key);
        return Math.toIntExact(count(queryWrapper));
    }

    /**
     * 转换值对象
     * */
    @Override
    public PostOverviewVO getPostOverviewVO(Post post) {
        PostOverviewVO postOverviewVO = new PostOverviewVO();
        postOverviewVO.setUserId(post.getUserId());
        postOverviewVO.setPostId(post.getId());
        postOverviewVO.setPostTitle(post.getTitle());
        postOverviewVO.setPostContent(post.getContent());
        postOverviewVO.setPostTime(DataUtil.formatDateTime(post.getPublishTime()));
        postOverviewVO.setGem(post.getIsGem() == 1);
        postOverviewVO.setTop(post.getIsTop() == 1);
        return postOverviewVO;
    }



    /**
     * 构建查询条件（辅助【得到帖子总览对象】）
     */
    private QueryWrapper<Post> getOrderCondition(String type, String order, String key) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        // 帖子类型；【全部、或者其它】
        if (!PostType.ALL.getValue().equals(type) && !PostType.OTHER.getValue().equals(type)) {
            queryWrapper.eq("type", type);
        }
        // 帖子搜索关键子
        if (key != null && !StringUtils.isBlank(key)) {
            queryWrapper
                    .and(wrapper -> wrapper
                            .like("content", key)
                            .or()
                            .like("title", key)
                    );
        }
        // 帖子排序规则
        if (PostOrderMode.NORMAL.getValue().equals(order)) {
            queryWrapper
                    .orderByDesc("is_top")
                    .orderByDesc("is_gem")
                    .orderByDesc("score");
        } else if (PostOrderMode.NEWEST.getValue().equals(order)) {
            queryWrapper
                    .orderByDesc("publish_time");
        } else if (PostOrderMode.HOTEST.getValue().equals(order)) {
            queryWrapper
                    .orderByDesc("score");
        }
        return queryWrapper;
    }


    private void dfs(Comment commentParent, List<CommentVO> childCommentListVO) {
        List<Comment> childComments = commentService.list(new QueryWrapper<Comment>()
                .eq("object_type", CommentObjectType.COMMENT.getValue())
                .eq("object_id", commentParent.getId()));
        if (childComments == null) {
            return;
        }
        for (Comment comment : childComments) {
            CommentVO commentVO = new CommentVO();

            User user = userService.getById(comment.getUserId());
            commentVO.setUserId(user.getId());
            commentVO.setUserAvatar(user.getAvatar());
            commentVO.setUserName(user.getUsername());

            commentVO.setCommentId(comment.getId());
            commentVO.setCommentTime(DataUtil.formatDateTime(comment.getCommentTime()));
            commentVO.setCommentContent(comment.getContent());


            Long count = likeService.getCommentLikeTotal(comment.getId());
            boolean isLike = likeService.isLikeCommentByUser( comment.getId(), userUtil.getUser().getId());
            commentVO.setCommentLikes(Math.toIntExact(count));
            commentVO.setLike(isLike);

            commentVO.setCommentScore(comment.getScore());
            dfs(comment, childCommentListVO);
            childCommentListVO.add(commentVO);
        }
    }




    private PostDetailVO convertToPostVO(Post post) {
        Random random = new Random();
        PostDetailVO postDetailVO = new PostDetailVO();
        // 用户服务
        User author = userService.getById(post.getUserId());
        postDetailVO.setAuthorAvatar(author.getAvatar());
        // 帖子服务
        postDetailVO.setPostId(post.getId());
        postDetailVO.setPostTitle(post.getTitle());
        postDetailVO.setPostContent(post.getContent());
        postDetailVO.setPostTime(DataUtil.formatDateTime(post.getPublishTime()));
        postDetailVO.setPostVisits(random.nextInt(100000));
        postDetailVO.setTop(post.getIsTop() == 1);
        postDetailVO.setGem(post.getIsGem() == 1);
        // 点赞数量
        Long count = likeService.getPostLikeTotal(post.getId());
        postDetailVO.setPostLikes(Math.toIntExact(count));
        // 评论数量
        List<Comment> allParentComments = commentService.getCommentAllByPost(post.getId());
        postDetailVO.setPostReplies(allParentComments.size());// 外层评论数量
        // 内层评论数量等于所有评论的子评论的总数
        long commentReplies = commentService.getPostRepliesByPostId(post.getId());
        postDetailVO.setCommentReplies((int) (commentReplies - allParentComments.size()));

        return postDetailVO;
    }

    private List<String> resolveTags(String tags) {
        if (tags == null) {
            return null;
        }
        List<String> ans = new ArrayList<>();
        String[] parts = tags.split(",");
        for (String part : parts) {
            if (!StringUtils.isBlank(part)) {
                ans.add(part);
            }
        }
        return ans;
    }


    @Override
    public Response likePost(Long postId) {
        if (postId == null || userUtil.getUser().getId() == null) {
            return Response.builder().badRequest().build();
        }
        likeService.createPostLike(postId, userUtil.getUser().getId());
        return Response.builder().ok().build();
    }

    @Override
    public Response dislikePost(Long postId) {
        if (postId == null || userUtil.getUser().getId() == null) {
            return Response.builder().badRequest().build();
        }
        likeService.deletePostLike(postId, userUtil.getUser().getId());
        return Response.builder().ok().build();
    }

    @Override
    public Response favoritePost(Long postId) {
        if (postId == null || userUtil.getUser().getId() == null) {
            return Response.builder().badRequest().build();
        }
        favoriteService.createPostFavorite(postId, userUtil.getUser().getId());
        return Response.builder().ok().build();
    }

    @Override
    public Response unFavoritePost(Long postId) {
        if (postId == null || userUtil.getUser().getId() == null) {
            return Response.builder().badRequest().build();
        }
        favoriteService.deletePostFavorite(postId, userUtil.getUser().getId());
        return Response.builder().ok().build();
    }
    // 综合排序（官方=>置顶=>普通，按分数）
    // SELECT * FROM post
    // WHERE type = 【postType】
    // ORDER BY is_top desc, is_gem desc, score desc
    // LIMIT 5, 5【(currentPage - 1) * pageSize, pageSize】

    // 最新排序（纯时间）
    // SELECT * FROM post
    // WHERE type = 【postType】
    // ORDER BY publish_time DESC

    // 最热排序（纯分数）
    // SELECT * FROM post
    // WHERE type = 【postType】
    // ORDER BY score DESC
}
