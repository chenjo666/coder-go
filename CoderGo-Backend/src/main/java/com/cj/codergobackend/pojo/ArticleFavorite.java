package com.cj.codergobackend.pojo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("article_favorite")
public class ArticleFavorite {
    @TableId("id")
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("article_id")
    private Long articleId;
}
