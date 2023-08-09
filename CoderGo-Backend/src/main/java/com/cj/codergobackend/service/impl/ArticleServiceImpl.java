package com.cj.codergobackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.codergobackend.constants.ArticleCommentSort;
import com.cj.codergobackend.constants.ArticleSort;
import com.cj.codergobackend.constants.ArticleType;
import com.cj.codergobackend.pojo.Article;
import com.cj.codergobackend.service.*;
import com.cj.codergobackend.util.RedisUtil;
import com.cj.codergobackend.util.TextUtil;
import com.cj.codergobackend.vo.ArticleCommentVO;
import com.cj.codergobackend.vo.ArticleDetailVO;
import com.cj.codergobackend.vo.ArticleOverviewVO;
import com.cj.codergobackend.vo.ArticlePersonalVO;
import com.cj.codergobackend.dao.ArticleMapper;
import com.cj.codergobackend.dto.Response;
import com.cj.codergobackend.pojo.User;
import com.cj.codergobackend.util.DataUtil;
import com.cj.codergobackend.util.UserUtil;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ScriptType;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.data.elasticsearch.core.script.Script;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    private static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);
    @Resource
    private UserService userService;
    @Resource
    private UserUtil userUtil;
    @Resource
    private LikeService likeService;
    @Resource
    private ArticleFavoriteService favoriteService;
    @Resource
    private ArticleCommentService articleCommentService;
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public ArticleDetailVO getArticleDetail(Long ArticleId, Integer currentPage, Integer pageSize) {
        Article article = elasticsearchTemplate.searchOne(NativeQuery.builder()
                .withQuery(q -> q.term(m -> m.field("id").value(ArticleId)))
                .build(), Article.class).getContent();
        ArticleDetailVO articleDetailVO = getArticleDetailVO(article);

        List<ArticleCommentVO> articleCommentVOS = articleCommentService.getCommentVOs(ArticleId, ArticleCommentSort.DEFAULT, currentPage, pageSize);
        if (articleCommentVOS == null) {
            return null;
        }
        articleDetailVO.setTotalReplies(Math.toIntExact(article.getTotalReply()));
        articleDetailVO.setTotalComments(articleCommentVOS.size()); // 外层评论数量
        articleDetailVO.setParentCommentListVO(articleCommentVOS);  // 子评论

        return articleDetailVO;
    }
    @Override
    public Response createArticle(String ArticleTitle, String ArticleContent, String ArticleType, List<String> ArticleTags) {
        if (userUtil.getUser() == null) {
            return Response.unauthorized();
        }
        Article article = new Article();
        article.setUserId(userUtil.getUser().getId());
        article.setTitle(ArticleTitle);
        article.setContent(TextUtil.filter(ArticleContent));
        article.setType(ArticleType);
        article.setCreatedAt(new Date());
        article.setIsGem(0);
        article.setIsTop(0);
        article.setTotalReply(0L);
        article.setScore(0.0);
        article.setTags(unionTags(ArticleTags));
        try {
            // mysql 存储文章
            save(article);
            // elasticsearch 存储文章
            elasticsearchTemplate.save(article);
        } catch (Exception e) {
            return Response.internalServerError();
        }
        return Response.ok(article);
    }
    // 更新文章
    @Override
    public Response updateArticle(Long ArticleId, String newContent) {
        boolean res = update(new UpdateWrapper<Article>().set("content", newContent).eq("id", ArticleId));
        return res ? Response.ok() : Response.notContent();
    }
    // 删除文章
    @Override
    public Response deleteArticle(Long ArticleId) {
        // mysql 删除
        boolean result = removeById(ArticleId);
        // es 删除
        elasticsearchTemplate.delete(ArticleId.toString(), Article.class);
        return result ? Response.ok() : Response.notContent();
    }
    @Override
    public List<ArticlePersonalVO> getPublishedArticles(Long userId) {
        // 1. 得到发布的文章
        List<Article> articles = list(new QueryWrapper<Article>().eq("user_id", userId));
        if (articles == null) {
            return null;
        }
        // 2. 转换为 VO
        List<ArticlePersonalVO> articlePersonalVOList = articles
                .stream()
                .map(this::getArticlePersonVO)
                .collect(Collectors.toList());
        return articlePersonalVOList;
    }

    @Override
    public Response getArticlePublications(Long userId) {
        return Response.ok(getPublishedArticles(userId));
    }

    @Override
    public Long getArticleTotalViews(Long userId) {
        List<ArticlePersonalVO> articlePublications = this.getPublishedArticles(userId);
        Long total = 0L;
        for (ArticlePersonalVO articlePublication : articlePublications) {
            total += articlePublication.getTotalViews();
        }
        return total;
    }

    @Override
    public Response getArticleFavorites(Long userId) {
        // 1. 得到收藏的文章
        List<Article> articles = list(new QueryWrapper<Article>()
                .inSql("id", "SELECT article_id FROM article_favorite WHERE user_id = " + userId));
        if (articles == null) {
            return Response.notContent();
        }
        // 2. 转换为 VO
        List<ArticlePersonalVO> articlePersonalVOList = articles
                .stream()
                .map(this::getArticlePersonVO)
                .collect(Collectors.toList());
        return Response.ok(articlePersonalVOList);
    }

    /*********************************** 两个搜索文章业务 ***********************************/
    @Override
    public Response searchArticlesByMySQL(String type, String order, String key, Integer page, Integer limit) {
        if (StringUtils.isBlank(type) || StringUtils.isBlank(order) || page == null || limit == null) {
            return Response.badRequest();
        }
        Map<String, Object> data = new HashMap<>();
        QueryWrapper<Article> queryWrapper = getQueryWrapper(type, order, key);
        data.put("totalArticles", Math.toIntExact(count(queryWrapper)));

        queryWrapper.last(String.format("LIMIT %d,%d", (page - 1) * limit, limit));
        List<Article> articles = list(queryWrapper);
        List<ArticleOverviewVO> articleOverviewVOS = new ArrayList<>();
        for (Article article : articles) {
            articleOverviewVOS.add(getArticleOverviewVO(article));
        }
        data.put("articles", articleOverviewVOS);

        return Response.ok(data);
    }
    @Override
    public Response searchArticlesByElasticsearch(String type, String order, String key, Integer page, Integer limit) {
        if (StringUtils.isBlank(type) || StringUtils.isBlank(order) || page == null || limit == null) {
            return Response.badRequest();
        }
        // 如果达到查询缓存条件，则查询缓存
//        if (type.equals(ArticleType.ALL) && StringUtils.isBlank(key)) {
//            return Response.ok(homeCache.get(getCacheKey(order, page, limit)));
//        }
        // 否则加载数据库
        Map<String, Object> data = searchArticlesHelper(type, order, key, page, limit);
        return Response.ok(data);
    }

    private Map<String, Object> searchArticlesHelper(String type, String order, String key, Integer page, Integer limit) {
        Map<String, Object> data = new HashMap<>();
        List<ArticleOverviewVO> articleOverviewVOS = new ArrayList<>();
        // 搜索条件
        NativeQueryBuilder builder = getQueryBuilder(type, order, key);
        // 搜索命中总数
        Query totalQuery = builder.build();
        SearchHits<Article> totalHits = elasticsearchTemplate.search(totalQuery, Article.class);
        data.put("totalArticles", Math.toIntExact(totalHits.getTotalHits()));
        // 搜索命中的文章
        // 1. 构建分页
        NativeQuery query = builder.withPageable(PageRequest.of(page - 1, limit)).build();
        // 2. 查询结果
        SearchHits<Article> searchHits = elasticsearchTemplate.search(query, Article.class);
        for (SearchHit<Article> searchHit : searchHits) {
            Article article = searchHit.getContent();
            // 3. 构建高亮
            List<String> highlightTitles = searchHit.getHighlightFields().get("title");
            if (highlightTitles != null)
                article.setTitle(highlightTitles.get(0));
            List<String> highlightContents = searchHit.getHighlightFields().get("content");
            if (highlightContents != null)
                article.setContent(highlightContents.get(0));
            // 4. 载入容器
            articleOverviewVOS.add(getArticleOverviewVO(article));
        }
        // 5. 载入结果
        data.put("articles", articleOverviewVOS);
        return data;
    }
    /*********************************** 两个查询构造条件 ***********************************/
    // 构建查询条件 elasticsearch
    public NativeQueryBuilder getQueryBuilder(String type, String order, String key) {
        NativeQueryBuilder builder = NativeQuery.builder();

        // 文章类型；【全部、或者其它】
        if (!ArticleType.ALL.equals(type) && !ArticleType.OTHER.equals(type)) {
            builder.withFilter(q -> q.term(t -> t.field("type").value(type)));
        }

        // 文章搜索关键字
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
            builder.withHighlightQuery(new HighlightQuery(highlight, Article.class));
        }
        ArticleSort.queryOrder(builder, order);

        return builder;
    }

    // 构建查询条件 mysql
    private QueryWrapper<Article> getQueryWrapper(String type, String order, String key) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        // 文章类型；【全部、或者其它】
        if (!ArticleType.ALL.equals(type) && !ArticleType.OTHER.equals(type)) {
            queryWrapper.eq("type", type);
        }
        // 文章搜索关键子
        if (key != null && !StringUtils.isBlank(key)) {
            queryWrapper
                    .and(wrapper -> wrapper
                            .like("content", key)
                            .or()
                            .like("title", key)
                    );
        }
        // 文章排序规则
        ArticleSort.queryOrder(queryWrapper, order);

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
    public Response likeArticle(Long ArticleId) {
        if ( userUtil.getUser() == null) {
            return Response.unauthorized();
        }
        likeService.createArticleLike(ArticleId, userUtil.getUser().getId());
        return Response.created();
    }
    @Override
    public Response dislikeArticle(Long ArticleId) {
        if (userUtil.getUser() == null) {
            return Response.unauthorized();
        }
        likeService.deleteArticleLike(ArticleId, userUtil.getUser().getId());
        return Response.ok();
    }
    @Override
    public Response favoriteArticle(Long ArticleId) {
        if (userUtil.getUser() == null) {
            return Response.unauthorized();
        }
        favoriteService.createArticleFavorite(ArticleId, userUtil.getUser().getId());
        return Response.ok();
    }
    @Override
    public Response unFavoriteArticle(Long ArticleId) {
        if (userUtil.getUser() == null) {
            return Response.unauthorized();
        }
        favoriteService.deleteArticleFavorite(ArticleId, userUtil.getUser().getId());
        return Response.ok();
    }

    @Override
    public Response getSearchSuggestions(String key) {
        return null;
    }

    @Override
    public Long getArticleTotalLikes(Long userId) {
        List<Article> articles = list(new QueryWrapper<Article>().eq("user_id", userId));
        Long totalLikes = 0L;
        for (Article article : articles) {
            Long likes = likeService.getArticleLikeTotal(article.getId());
            totalLikes += likes;
        }
        return totalLikes;
    }

    @Override
    public Long getArticleTotalFavorites(Long userId) {
        List<Article> articles = list(new QueryWrapper<Article>().eq("user_id", userId));
        Long totalFavorites = 0L;
        for (Article article : articles) {
            Long likes = favoriteService.getArticleFavoriteTotal(article.getId());
            totalFavorites += likes;
        }
        return totalFavorites;
    }

    // 为文章添加一个评论数
    @Override
    public boolean incTotalReply(Long articleId) {
        // mysql
        update(new UpdateWrapper<Article>().setSql("total_reply = total_reply + 1").eq("id", articleId));
        // es
        Article article = getByIdFromEs(articleId);
        article.setTotalReply(article.getTotalReply() + 1);
        elasticsearchTemplate.update(article);
        return true;
    }

    @Override
    public Article getByIdFromEs(Long articleId) {
        // es
        Query query = NativeQuery.builder()
                .withQuery(q -> q.term(m -> m.field("id").value(articleId)))
                .build();
        SearchHit<Article> search = elasticsearchTemplate.searchOne(query, Article.class);
        assert search != null;
        return search.getContent();
    }

    /****************************************三个视图对象*****************************************/
    // 转换 ArticleOverviewVO 对象
    @Override
    public ArticleOverviewVO getArticleOverviewVO(Article article) {
        ArticleOverviewVO articleOverviewVO = new ArticleOverviewVO();
        // 文章作者
        User author = userService.getById(article.getUserId());
        articleOverviewVO.setUserAvatar(author.getAvatar());
        articleOverviewVO.setUserId(article.getUserId());
        // 文章
        articleOverviewVO.setArticleId(article.getId());
        articleOverviewVO.setArticleTitle(article.getTitle());
        articleOverviewVO.setArticleContent(article.getContent());
        articleOverviewVO.setArticleCreatedAt(DataUtil.formatDateTime(article.getCreatedAt()));
        articleOverviewVO.setGem(article.getIsGem() == 1);
        articleOverviewVO.setTop(article.getIsTop() == 1);
        articleOverviewVO.setTotalReplies(article.getTotalReply());
        // 点赞数量
        Long count = likeService.getArticleLikeTotal(article.getId());
        articleOverviewVO.setTotalLikes(count);
        // 观看人数
        String key = RedisUtil.getArticleViewKey(article.getId());
        articleOverviewVO.setTotalViews(redisTemplate.opsForHyperLogLog().size(key));
        return articleOverviewVO;
    }
    // 转换 ArticleDetailVO 对象
    @Override
    public ArticleDetailVO getArticleDetailVO(Article article) {
        ArticleDetailVO articleDetailVO = new ArticleDetailVO();
        // 文章服务
        articleDetailVO.setArticleId(article.getId());
        articleDetailVO.setArticleContent(article.getContent());
        articleDetailVO.setArticleTitle(article.getTitle());
        articleDetailVO.setArticleType(article.getType());
        articleDetailVO.setArticleCreatedAt(DataUtil.formatDateTime(article.getCreatedAt()));
        articleDetailVO.setArticleTags(splitTags(article.getTags()));
        // 访问量
        String key = RedisUtil.getArticleViewKey(article.getId());
        articleDetailVO.setTotalViews(Math.toIntExact(redisTemplate.opsForHyperLogLog().size(key)));
        // 用户服务
        User author = userService.getById(article.getUserId());
        articleDetailVO.setAuthorId(author.getId());
        articleDetailVO.setAuthorName(author.getUsername());
        articleDetailVO.setAuthorAvatar(author.getAvatar());
        // 点赞业务
        Long count = likeService.getArticleLikeTotal(article.getId());
        articleDetailVO.setTotalLikes(Math.toIntExact(count));
        articleDetailVO.setTotalFavorites(Math.toIntExact(favoriteService.getArticleFavoriteTotal(article.getId())));
        if (userUtil.getUser() == null) {
            articleDetailVO.setLike(false);
            articleDetailVO.setFavorite(false);
            articleDetailVO.setFollowAuthor(false);
            return articleDetailVO;
        }
        boolean isLike  = likeService.isLikeArticleByUser(article.getId(), userUtil.getUser().getId());
        articleDetailVO.setLike(isLike);
        // 收藏业务
        articleDetailVO.setFavorite(favoriteService.isFavoriteArticleByUser(article.getId(), userUtil.getUser().getId()));

        return articleDetailVO;
    }
    // 转换 ArticlePersonVO 对象
    @Override
    public ArticlePersonalVO getArticlePersonVO(Article article) {
        ArticlePersonalVO articlePersonalVO = new ArticlePersonalVO();
        articlePersonalVO.setArticleId(article.getId());
        articlePersonalVO.setArticleTitle(article.getTitle());
        articlePersonalVO.setArticleCreatedAt(DataUtil.formatDateTime(article.getCreatedAt()));
        // 访问量
        String key = RedisUtil.getArticleViewKey(article.getId());
        articlePersonalVO.setTotalViews(redisTemplate.opsForHyperLogLog().size(key));
        // 点赞数
        Long likes = likeService.getArticleLikeTotal(article.getId());
        articlePersonalVO.setTotalLikes(likes);
        return articlePersonalVO;
    }

    /****************************************首页数据缓存*****************************************/
    private static final int maximumSize = 10;
    private static final int expireSeconds = 300;

    private LoadingCache<String, Map<String, Object>> homeCache;


    @PostConstruct
    public void initCache() {
        homeCache = Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(s -> {
                    String[] parts = s.split(":");
                    String order = parts[0];
                    int page = Integer.parseInt(parts[1]);
                    int limit = Integer.parseInt(parts[2]);
                    logger.info("load cache from es!");
                    return searchArticlesHelper(ArticleType.ALL, order, null, page, limit);
                });
    }
    private String getCacheKey(String order, Integer page, Integer limit) {
        return order + ":" + page + ":" + limit;
    }
}
