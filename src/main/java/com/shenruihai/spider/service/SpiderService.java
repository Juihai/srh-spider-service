package com.shenruihai.spider.service;

import com.shenruihai.spider.dao.model.DoubanBook;
import org.jsoup.nodes.Document;

/**
 * 爬虫处理
 * @author shenruihai
 * @date 2022/5/14
 */
public abstract class SpiderService {

    /**
     * 负责控制爬虫进程
     * @param subjectId
     * @return
     */
    public abstract boolean spiderDancer(long subjectId);

    /**
     * 获取原始html信息
     * @param subjectId
     * @return
     */
    protected abstract Document doGet(long subjectId);

    /**
     * 解析html转为持久层对象
     * @param document
     * @param subjectId
     * @return
     */
    protected abstract DoubanBook toParse(Document document, long subjectId);

    /**
     * 获取当已落库最大subjectId
     * @return
     */
    public abstract long findTopByOrderByIdDesc();

    /**
     * 判断豆瓣id是否存在
     * @param id
     * @return
     */
    public abstract boolean isExistInfo(long id);


}
