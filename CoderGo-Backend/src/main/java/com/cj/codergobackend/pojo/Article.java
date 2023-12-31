package com.cj.codergobackend.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@TableName("article")
@Document(indexName = "article")
public class Article {
    @TableId("id")
    @Id
    private Long id;
    @TableField("user_id")
    @Field(type= FieldType.Long)
    private Long userId;
    @TableField("title")
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;
    @TableField("content")
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;
    @TableField("created_at")
    @Field(type = FieldType.Date)
    private Date createdAt;
    @TableField("type")
    @Field(type = FieldType.Keyword)
    private String type;
    @TableField("is_top")
    @Field(type= FieldType.Integer)
    private Integer isTop;
    @TableField("is_gem")
    @Field(type= FieldType.Integer)
    private Integer isGem;
    @TableField("total_reply")
    @Field(type = FieldType.Long)
    private Long totalReply;
    @TableField("score")
    @Field(type= FieldType.Double)
    private Double score;
    @TableField("tags")
    @Field(type= FieldType.Text)
    private String tags;
}
