package com.cj.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.studycirclebackend.constants.CommentSort;
import com.cj.studycirclebackend.constants.PostSort;
import com.cj.studycirclebackend.enums.PostType;
import com.cj.studycirclebackend.service.*;
import com.cj.studycirclebackend.util.RedisUtil;
import com.cj.studycirclebackend.util.TextUtil;
import com.cj.studycirclebackend.vo.CommentVO;
import com.cj.studycirclebackend.vo.PostDetailVO;
import com.cj.studycirclebackend.vo.PostOverviewVO;
import com.cj.studycirclebackend.vo.PostPersonalVO;
import com.cj.studycirclebackend.dao.PostMapper;
import com.cj.studycirclebackend.dto.Response;
import com.cj.studycirclebackend.pojo.Post;
import com.cj.studycirclebackend.pojo.User;
import com.cj.studycirclebackend.util.DataUtil;
import com.cj.studycirclebackend.util.UserUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.data.redis.core.RedisTemplate;
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
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Response getPostDetail(Long postId, Integer currentPage, Integer pageSize) {
        if (postId == null || currentPage == null || pageSize == null) {
            return Response.badRequest();
        }
        Post post = getById(postId);
        if (post == null) {
            return Response.notContent();
        }
        PostDetailVO postDetailVO = getPostDetailVO(post);

        List<CommentVO> commentVOs = commentService.getCommentVOs(postId, CommentSort.DEFAULT, currentPage, pageSize);

        postDetailVO.setPostReplies(commentVOs.size());  // 外层评论数量
        postDetailVO.setCommentReplies(Math.toIntExact(commentService.getPostRepliesByPostId(postId) - commentVOs.size()));         // 内层评论数量
        postDetailVO.setParentCommentListVO(commentVOs);  // 子评论

        return Response.ok(postDetailVO);
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
        post.setTags(unionTags(postTags));
        // mysql 存储帖子
        save(post);
        // elasticsearch 存储帖子
        elasticsearchTemplate.save(post);
        return Response.ok();
    }
    // 更新帖子
    @Override
    public Response updatePost(Long postId, String newContent) {
        if (postId == null || StringUtils.isBlank(newContent)) {
            return Response.badRequest();
        }
        boolean result = update(new UpdateWrapper<Post>().set("content", newContent).eq("id", postId));
        if (!result)
            return Response.notContent();
        return Response.ok();
    }
    // 删除帖子
    @Override
    public Response deletePost(Long postId) {
        if (postId == null) {
            return Response.badRequest();
        }
        // mysql 删除
        boolean result = removeById(postId);
        // es 删除
        elasticsearchTemplate.delete(postId.toString(), Post.class);
        return Response.ok();
    }
    @Override
    public Response getPostPublications(Long userId) {
        if (userId == null) {
            return Response.badRequest();
        }
        // 1. 得到发布的帖子
        List<Post> posts = list(new QueryWrapper<Post>().eq("user_id", userId));
        // 2. 转换为 VO
        List<PostPersonalVO> postPersonalVOList = posts
                .stream()
                .map(this::getPostPersonVO)
                .collect(Collectors.toList());
        return Response.ok(postPersonalVOList);
    }
    @Override
    public Response getPostFavorites(Long userId) {
        if (userId == null) {
            return Response.badRequest();
        }
        // 1. 得到收藏的帖子
        List<Post> posts = list(new QueryWrapper<Post>()
                .inSql("id", "SELECT post_id FROM favorite WHERE user_id = " + userId));
        // 2. 转换为 VO
        List<PostPersonalVO> postPersonalVOList = posts
                .stream()
                .map(this::getPostPersonVO)
                .collect(Collectors.toList());
        return Response.ok(postPersonalVOList);
    }

    /*********************************** 两个搜索帖子业务 ***********************************/
    @Override
    public Response searchPostsByMySQL(String type, String order, String key, Integer page, Integer limit) {
        if (StringUtils.isBlank(type) || StringUtils.isBlank(order) || page == null || limit == null) {
            return Response.badRequest();
        }
        Map<String, Object> data = new HashMap<>();
        QueryWrapper<Post> queryWrapper = getQueryWrapper(type, order, key);
        data.put("postTotal", Math.toIntExact(count(queryWrapper)));

        queryWrapper.last(String.format("LIMIT %d,%d", (page - 1) * limit, limit));
        List<Post> posts = list(queryWrapper);
        List<PostOverviewVO> postOverviewVOs = new ArrayList<>();
        for (Post post : posts) {
            postOverviewVOs.add(getPostOverviewVO(post));
        }
        data.put("posts", postOverviewVOs);

        return Response.ok(data);
    }
    @Override
    public Response searchPostsByElasticsearch(String type, String order, String key, Integer page, Integer limit) {
        if (StringUtils.isBlank(type) || StringUtils.isBlank(order) || page == null || limit == null) {
            return Response.badRequest();
        }
        long time = System.currentTimeMillis();
        Map<String, Object> data = new HashMap<>();
        List<PostOverviewVO> postOverviewVOs = new ArrayList<>();
        // 搜索条件
        NativeQueryBuilder builder = getQueryBuilder(type, order, key);
        // 搜索命中总数
        Query totalQuery = builder.build();
        SearchHits<Post> totalHits = elasticsearchTemplate.search(totalQuery, Post.class);
        data.put("postTotal", Math.toIntExact(totalHits.getTotalHits()));
        // 搜索命中的帖子
        // 1. 构建分页
        NativeQuery query = builder.withPageable(PageRequest.of(page - 1, limit)).build();
        // 2. 查询结果
        SearchHits<Post> searchHits = elasticsearchTemplate.search(query, Post.class);
        for (SearchHit<Post> searchHit : searchHits) {
            Post post = searchHit.getContent();
            // 3. 构建高亮
            List<String> highlightTitles = searchHit.getHighlightFields().get("title");
            if (highlightTitles != null)
                post.setTitle(highlightTitles.get(0));
            List<String> highlightContents = searchHit.getHighlightFields().get("content");
            if (highlightContents != null)
                post.setContent(highlightContents.get(0));
            // 4. 载入容器
            postOverviewVOs.add(getPostOverviewVO(post));
        }
//        logger.info("es 搜索高亮 + 转换耗时: {}", System.currentTimeMillis() - time);
        // 5. 载入结果
        data.put("posts", postOverviewVOs);
        return Response.ok(data);
    }

    /*********************************** 两个查询构造条件 ***********************************/
    // 构建查询条件 elasticsearch
    public NativeQueryBuilder getQueryBuilder(String type, String order, String key) {
        NativeQueryBuilder builder = NativeQuery.builder();

        // 帖子类型；【全部、或者其它】
        if (!PostType.ALL.getValue().equals(type) && !PostType.OTHER.getValue().equals(type)) {
            builder.withFilter(q -> q.term(t -> t.field("type").value(type)));
        }

        // 帖子搜索关键字
        if (!StringUtils.isBlank(key)) {
            // (1) 构建查询内容
            builder.withQuery(q -> q
                    .multiMatch(m -> m
                            .fields("title", "content")
                            .query(key)));
            // (2) 构建高亮
            HighlightFieldParameters fieldParameters = HighlightFieldParameters.builder()
                    .withPreTags("<span style=\"color:red\">")
                    .withPostTags("</span>")
                    .build();
            List<HighlightField> list = new ArrayList<>();
            list.add(new HighlightField("title", fieldParameters));
            list.add(new HighlightField("content", fieldParameters));
            Highlight highlight = new Highlight(list);
            builder.withHighlightQuery(new HighlightQuery(highlight, Post.class));
        }
        PostSort.queryOrder(builder, order);

        return builder;
    }

    // 构建查询条件 mysql
    private QueryWrapper<Post> getQueryWrapper(String type, String order, String key) {
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
        PostSort.queryOrder(queryWrapper, order);

        return queryWrapper;
    }

    /*********************************** 两个标签转换工具 ***********************************/
    private List<String> splitTags(String tags) {
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
    private String unionTags(List<String> tags) {
        StringBuilder ans = new StringBuilder();
        if (tags.size() != 0) {
            for (String tag : tags) {
                ans.append(tag).append(",");
            }
        }
        return ans.toString();
    }

    /*********************************** 四个点赞、收藏相关业务 ***********************************/
    @Override
    public Response likePost(Long postId) {
        if (postId == null || userUtil.getUser().getId() == null) {
            return Response.badRequest();
        }
        likeService.createPostLike(postId, userUtil.getUser().getId());
        return Response.ok();
    }
    @Override
    public Response dislikePost(Long postId) {
        if (postId == null || userUtil.getUser().getId() == null) {
            return Response.badRequest();
        }
        likeService.deletePostLike(postId, userUtil.getUser().getId());
        return Response.ok();
    }
    @Override
    public Response favoritePost(Long postId) {
        if (postId == null || userUtil.getUser().getId() == null) {
            return Response.badRequest();
        }
        favoriteService.createPostFavorite(postId, userUtil.getUser().getId());
        return Response.ok();
    }
    @Override
    public Response unFavoritePost(Long postId) {
        if (postId == null || userUtil.getUser().getId() == null) {
            return Response.badRequest();
        }
        favoriteService.deletePostFavorite(postId, userUtil.getUser().getId());
        return Response.ok();
    }

    @Override
    public Response getSearchSuggestions(String key) {
        return null;
    }


    /****************************************三个视图对象*****************************************/
    // 转换 PostOverviewVO 对象
    @Override
    public PostOverviewVO getPostOverviewVO(Post post) {
        long time = System.currentTimeMillis();
        PostOverviewVO postOverviewVO = new PostOverviewVO();
        // 帖子作者
        User author = userService.getById(post.getUserId());
        postOverviewVO.setUserAvatar(author.getAvatar());
        postOverviewVO.setUserId(post.getUserId());
        // 帖子
        postOverviewVO.setPostId(post.getId());
        postOverviewVO.setPostTitle(post.getTitle());
        postOverviewVO.setPostContent(post.getContent());
        postOverviewVO.setPostTime(DataUtil.formatDateTime(post.getPublishTime()));
        postOverviewVO.setGem(post.getIsGem() == 1);
        postOverviewVO.setTop(post.getIsTop() == 1);
        postOverviewVO.setPostReplies(post.getReplyTotal());
        // 点赞数量
        Long count = likeService.getPostLikeTotal(post.getId());
        postOverviewVO.setPostLikes(count);
        // 观看人数
        String key = RedisUtil.getPostViewKey(post.getId());
        postOverviewVO.setPostViews(redisTemplate.opsForHyperLogLog().size(key));
        return postOverviewVO;
    }
    // 转换 PostDetailVO 对象
    @Override
    public PostDetailVO getPostDetailVO(Post post) {
        PostDetailVO postDetailVO = new PostDetailVO();
        // 帖子服务
        postDetailVO.setPostId(post.getId());
        postDetailVO.setPostContent(post.getContent());
        postDetailVO.setPostTitle(post.getTitle());
        postDetailVO.setPostType(post.getType());
        postDetailVO.setPostTime(DataUtil.formatDateTime(post.getPublishTime()));
        postDetailVO.setPostTags(splitTags(post.getTags()));
        // 访问量
        String key = RedisUtil.getPostViewKey(post.getId());
        postDetailVO.setPostVisits(Math.toIntExact(redisTemplate.opsForHyperLogLog().size(key)));
        // 用户服务
        User author = userService.getById(post.getUserId());
        postDetailVO.setAuthorId(author.getId());
        postDetailVO.setAuthorName(author.getUsername());
        postDetailVO.setAuthorAvatar(author.getAvatar());
        // 点赞业务
        Long count = likeService.getPostLikeTotal(post.getId());
        boolean isLike = likeService.isLikePostByUser(post.getId(), userUtil.getUser().getId());
        postDetailVO.setPostLikes(Math.toIntExact(count));
        postDetailVO.setLike(isLike);
        // 收藏业务
        postDetailVO.setPostFavorites(Math.toIntExact(favoriteService.getPostFavoriteTotal(post.getId())));
        postDetailVO.setFavorite(favoriteService.isFavoritePostByUser(post.getId(), userUtil.getUser().getId()));
        // 关注业务
        boolean isFollowed = followService.isFollowedByUser(userUtil.getUser().getId(), post.getUserId());
        postDetailVO.setFollowAuthor(isFollowed);

        return postDetailVO;
    }
    // 转换 PostPersonVO 对象
    @Override
    public PostPersonalVO getPostPersonVO(Post post) {
        PostPersonalVO postPersonalVO = new PostPersonalVO();
        postPersonalVO.setPostId(post.getId());
        postPersonalVO.setPostTitle(post.getTitle());
        postPersonalVO.setPostTime(DataUtil.formatDateTime(post.getPublishTime()));
        // 访问量
        String key = RedisUtil.getPostViewKey(post.getId());
        postPersonalVO.setPostViews(redisTemplate.opsForHyperLogLog().size(key));
        // 点赞数
        Long likes = likeService.getPostLikeTotal(post.getId());
        postPersonalVO.setPostLikes(likes);
        return postPersonalVO;
    }
}
