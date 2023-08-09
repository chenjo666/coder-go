package com.cj.codergobackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.codergobackend.vo.ArticleDetailVO;
import com.cj.codergobackend.vo.ArticleOverviewVO;
import com.cj.codergobackend.vo.ArticlePersonalVO;
import com.cj.codergobackend.dto.Response;
import com.cj.codergobackend.pojo.Article;

import java.util.List;

public interface ArticleService extends IService<Article> {

    /********************************** 四个 CRUD 方法 **********************************/
    ArticleDetailVO getArticleDetail(Long articleId, Integer currentPage, Integer pageSize);
    Response createArticle(String articleTitle, String articleContent, String articleType, List<String> articleTags);
    Response updateArticle(Long articleId, String newContent);
    Response deleteArticle(Long articleId);


    /********************************** 两个查询文章方法 **********************************/
    // 得到某人发布的文章列表
    List<ArticlePersonalVO> getPublishedArticles(Long userId);
    Response getArticlePublications(Long userId);
    // 得到某人发布的文章访问量
    Long getArticleTotalViews(Long userId);
    // 得到某人收藏的文章列表
    Response getArticleFavorites(Long userId);


    /********************************** 两个搜索文章方法 **********************************/
    /**
     * Mysql 搜索文章，进行模糊查询
     */
    // 参数：文章类型，排序规则，关键字，页码，每页限制
    Response searchArticlesByMySQL(String type, String order, String key, Integer page, Integer limit);
    /**
     * Elasticsearch 搜索文章，进行结果关键词的高亮显示
     * @param type
     * @param order
     * @param key
     * @param page
     * @param limit
     * @return
     */
    Response searchArticlesByElasticsearch(String type, String order, String key, Integer page, Integer limit);


    /********************************** 三个转换对象方法 **********************************/
    ArticleOverviewVO getArticleOverviewVO(Article article);
    ArticleDetailVO getArticleDetailVO(Article article);
    ArticlePersonalVO getArticlePersonVO(Article article);


    /********************************** 四个点赞、收藏相关方法 **********************************/
    /**
     * 文章的点赞业务
     */
    Response likeArticle(Long articleId);
    /**
     * 文章的取消点赞业务
     */
    Response dislikeArticle(Long articleId);
    /**
     * 文章的收藏业务
     */
    Response favoriteArticle(Long articleId);
    /**
     * 文章的取消收藏业务
     */
    Response unFavoriteArticle(Long articleId);

    /*************************************** 关键词提示 ****************************/
    Response getSearchSuggestions(String key);

    Long getArticleTotalLikes(Long userId);
    Long getArticleTotalFavorites(Long userId);


    // 评论数加一
    boolean incTotalReply(Long articleId);


    // 查找文章
    Article getByIdFromEs(Long articleId);
}
