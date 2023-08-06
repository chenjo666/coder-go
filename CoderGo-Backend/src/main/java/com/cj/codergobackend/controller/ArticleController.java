package com.cj.codergobackend.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cj.codergobackend.constants.ArticleCommentObj;
import com.cj.codergobackend.dto.Response;
import com.cj.codergobackend.pojo.Article;
import com.cj.codergobackend.service.ArticleCommentService;
import com.cj.codergobackend.service.ArticleService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 文章
 */

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Resource
    private ArticleService articleService;
    @Resource
    private ArticleCommentService articleCommentService;

    // v1 - 查询文章详情
    @GetMapping("/v1/articles/{articleId}")
    public Response queryArticle(@PathVariable("articleId") Long articleId, @RequestParam Integer commentPage, @RequestParam Integer commentLimit) {
        return articleService.getArticleDetail(articleId, commentPage, commentLimit);
    }
    // v1 - 查询文章列表(mysql)
    @GetMapping("/v1/articles")
    public Response searchArticlesByMysql(@RequestParam String articleType, @RequestParam String articleSort,
                             @RequestParam String articleKey, @RequestParam Integer articlePage,
                             @RequestParam Integer articleLimit) {
        return articleService.searchArticlesByMySQL(articleType, articleSort, articleKey, articlePage, articleLimit);
    }
    // v2 - 查询文章列表(elasticsearch)
    @GetMapping("/v2/articles")
    public Response searchArticlesByElasticsearch(@RequestParam String articleType, @RequestParam String articleSort,
                                @RequestParam String articleKey, @RequestParam Integer articlePage,
                                @RequestParam Integer articleLimit) {
        return articleService.searchArticlesByElasticsearch(articleType, articleSort, articleKey, articlePage, articleLimit);
    }
    // v1 - 新建文章
    @PostMapping("/v1/articles")
    public Response createArticle(@RequestBody Map<String, Object> map) {
        String articleTitle = (String) map.get("articleTitle");
        String articleContent = (String) map.get("articleContent");
        String articleType = (String) map.get("articleType");
        List<String> articleTags = (List<String>) map.get("articleTags");
        return articleService.createArticle(articleTitle, articleContent, articleType, articleTags);
    }
    // v1 - 修改文章内容
    @PutMapping("/v1/articles/{articleId}")
    public Response updateArticle(@PathVariable("articleId") Long articleId, @RequestBody Map<String, String> map) {
        String articleContent = map.get("content");
        return articleService.updateArticle(articleId, articleContent);
    }
    // v1 - 删除文章
    @DeleteMapping("/v1/articles/{articleId}")
    public Response deleteArticle(@PathVariable("articleId") Long articleId) {
        return articleService.deleteArticle(articleId);
    }
    // v1 - 查询收藏的文章列表
    @GetMapping("/v1/articles/favorites/{userId}")
    public Response getArticleFavorites(@PathVariable("userId") Long userId) {
        return articleService.getArticleFavorites(userId);
    }
    // v1 - 查询发布的文章列表
    @GetMapping("/v1/articles/publications/{userId}")
    public Response getArticlePublications(@PathVariable("userId") Long userId) {
        return articleService.getArticlePublications(userId);
    }
    // v1 - 点赞文章业务
    @PostMapping("/v1/articles/{articleId}/likes")
    public Response createArticleLike(@PathVariable("articleId") Long articleId) {
        return articleService.likeArticle(articleId);
    }
    // v1 - 取消点赞文章业务
    @DeleteMapping("/v1/articles/{articleId}/likes")
    public Response deleteArticleLike(@PathVariable("articleId") Long articleId) {
        return articleService.dislikeArticle(articleId);
    }
    // v1 - 收藏文章业务
    @PostMapping("/v1/articles/{articleId}/favorites")
    public Response createArticleFavorite(@PathVariable("articleId") Long articleId) {
        return articleService.favoriteArticle(articleId);
    }
    // v1 - 取消收藏文章业务
    @DeleteMapping("/v1/articles/{articleId}/favorites")
    public Response deleteArticleFavorite(@PathVariable("articleId") Long articleId) {
        return articleService.unFavoriteArticle(articleId);
    }

    // v1 - 根据文章查询评论
    @GetMapping("/v1/articles/{articleId}/comments")
    public Response getCommentsByArticle(@PathVariable("articleId") Long articleId, @RequestParam String commentSort,
                                      @RequestParam Integer commentPage, @RequestParam Integer commentLimit) {
        return articleCommentService.getAllCommentsByArticle(articleId, commentSort, commentPage, commentLimit);
    }
    // v1 - 根据评论查询评论
    @GetMapping("/v1/articles/comments/{commentId}/comments")
    public Response getCommentsByComment(@PathVariable("commentId") Long commentId) {
        return articleCommentService.getAllCommentsByComment(commentId);
    }
    // v1 - 更新评论内容
    @PutMapping("/v1/articles/comments/{commentId}")
    public Response updateComment(@PathVariable("commentId") Long commentId, @RequestBody Map<String, String> args) {
        String content = args.get("content");
        return articleCommentService.updateComment(commentId, content);
    }
    // v1 - 删除评论
    @DeleteMapping("/v1/articles/{articleId}/comments/{commentId}")
    public Response deleteComment(@PathVariable("articleId") Long articleId, @PathVariable("commentId") Long commentId) {
        return null;
    }
    // v1 - 创建评论
    @PostMapping("/v1/articles/{articleId}/comments")
    public Response createComment(@PathVariable("articleId") Long articleId, @RequestBody Map<String, String> map) {
        if (articleService.getById(articleId) == null) {
            return Response.notFound();
        }
        Long objectId = Long.parseLong(map.get("objectId"));
        String objectType = map.get("objectType");
        String content = map.get("content");
        if (!ArticleCommentObj.ARTICLE.equals(objectType) && !ArticleCommentObj.COMMENT.equals(objectType)) {
            return Response.badRequest();
        }
        if (ArticleCommentObj.ARTICLE.equals(objectType) && articleService.getById(objectId) == null) {
            return Response.notContent();
        }
        if (ArticleCommentObj.COMMENT.equals(objectType) && articleCommentService.getById(objectId) == null) {
            return Response.notContent();
        }
        // 文章评论数加一
        articleService.incTotalReply(articleId);
        // 新增评论
        return articleCommentService.createComment(objectId, objectType, content);
    }
    // v1 - 点赞评论
    @PostMapping("/v1/articles/comments/{commentId}/likes")
    public Response createCommentLike(@PathVariable("commentId") Long commentId) {
        return articleCommentService.likeComment(commentId);
    }
    // v1 - 取消点赞评论
    @DeleteMapping("/v1/articles/comments/{commentId}/likes")
    public Response deleteCommentLike(@PathVariable("commentId") Long commentId) {
        return articleCommentService.dislikeComment(commentId);
    }

}
