package com.shenruihai.spider.dao.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 图书基本信息
 * @author shenruihai
 * @date 2022/5/13
 */
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class DoubanBook implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(nullable = false, unique = true, length = 20)
    public String isbnCode;

    @Column(nullable = false)
    public String name;//书名

    @Column
    public String subName;//子书名

    @Column(length = 128)
    public String originalName;//原作名

    @Column
    public String keywords;//关键词

    @Column(length = 128)
    public String author;//作者

    @Lob
    @Column(columnDefinition="TEXT")
    public String authorIntro;//作者简介

    @Column(length = 64)
    public String translator;//译者

    @Column
    public String photoUrl;//图片封面

    @Column
    public String doubanUrl;//豆瓣地址

    @Column
    public Long localPhotoId;//本地图片地址

    @Column(nullable = false, length = 64)
    public String publishing;//出版社

    @Column(length = 64)
    public String producer;//出品方

    @Column(length = 32)
    public String published;//出版时间

    @Lob
    @Column(columnDefinition="TEXT")
    public String description;//图书简介

    @Column(length = 32)
    public String designed;//装帧

    @Column(length = 16)
    public String price;//图书价格

    @Column(length = 20)
    public String doubanScore;//豆瓣评分

    @Column(length = 64)
    public String numScore;//评分人数

    @Column(length = 20)
    public Long douban;

    /** 部分扩展属性 **/
    @Column(length = 16)
    public String pages;//页数

    @Column(length = 32)
    public String froms;//来源

    @Column(length = 16)
    public String num;//排名

    @Column(length = 64)
    public String reviews;//评论数量

    @Column(length = 64)
    public String tags;//标签

    @Column(length = 128)
    public String series;//丛书

    @Column
    public String seriesDesc;//丛书信息

    @CreatedDate
    public Timestamp createTime;

    @LastModifiedDate
    public Timestamp updateTime;

}
