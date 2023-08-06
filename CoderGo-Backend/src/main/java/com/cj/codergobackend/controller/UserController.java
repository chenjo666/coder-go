package com.cj.codergobackend.controller;

import com.cj.codergobackend.dto.Response;
import com.cj.codergobackend.pojo.User;
import com.cj.codergobackend.service.ArticleCommentService;
import com.cj.codergobackend.service.ArticleService;
import com.cj.codergobackend.service.FollowService;
import com.cj.codergobackend.service.UserService;
import com.cj.codergobackend.vo.ArticlePersonalVO;
import com.cj.codergobackend.vo.UserDetailVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private FollowService followService;
    @Resource
    private ArticleService articleService;
    @Resource
    private ArticleCommentService articleCommentService;

    // v1 - 用户登录
    @PostMapping("/v1/login")
    public Response login(String email, String password, HttpServletResponse response) {
        return userService.login(email, password, response);
    }
    // v1 - 用户登出
    @DeleteMapping("/v1/logout")
    public Response logout(@CookieValue("token") String token) {
        return userService.logout(token);
    }

    // v1 - 用户激活
    @PostMapping("/v1/activation")
    public Response activation(String email) {
        return userService.activate(email);
    }

    // v1 - 用户注册
    @PostMapping("/v1/register")
    public Response register(String email, String password, String code, HttpServletResponse response) {
        return userService.register(email, password, code, response);
    }

    // v1 - 查询用户详情
    @GetMapping("/v1/{userId}")
    public Response getUserDetails(@PathVariable("userId") Long userId) {
        // 查询用户信息
        User user = userService.getById(userId);
        // 查询用户关注的数量
        Set<Object> userFollowingsSet = followService.getUserFollowingsSet(userId);
        Set<Object> userFollowersSet = followService.getUserFollowersSet(userId);
        // 查询用户的发布帖子
        List<ArticlePersonalVO> articlePublications = articleService.getPublishedArticles(userId);
        // 计算访问量
        Long totalViews = articleService.getArticleTotalViews(userId);
        // 计算点赞数量（文章 + 评论）
        Long articleTotalLikes = articleService.getArticleTotalLikes(userId);
        Long commentTotalLikes = articleCommentService.getCommentTotalLikes(userId);
        // 计算收藏量（文章）
        Long totalFavorites = articleService.getArticleTotalFavorites(userId);

        UserDetailVO userDetailVO = UserDetailVO.builder()
                .userId(userId)
                .username(user.getUsername())
                .userAvatar(user.getAvatar())
                .totalFollowers((long) userFollowersSet.size())
                .totalFollowings((long) userFollowingsSet.size())
                .totalLikes(articleTotalLikes + commentTotalLikes)
                .totalViews(totalViews)
                .totalFavorites(totalFavorites)
                .articles(articlePublications)
                .build();

        return Response.ok(userDetailVO);
    }

}
