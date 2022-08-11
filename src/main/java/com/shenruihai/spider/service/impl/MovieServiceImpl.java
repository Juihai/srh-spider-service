package com.shenruihai.spider.service.impl;

import com.shenruihai.spider.dao.model.DoubanBook;
import com.shenruihai.spider.service.SpiderService;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

/**
 * 负责电影数据的爬去和解析
 * @author shenruihai
 * @date 2022/5/15
 */
@Service("movieService")
public class MovieServiceImpl extends SpiderService {

    /**
     * 流程控制
     * @param subjectId
     * @return
     */
    @Override
    public boolean spiderDancer(long subjectId) {
        return false;
    }

    /**
     * 获取原始html数据
     * @param subjectId
     * @return
     */
    @Override
    public Document doGet(long subjectId) {
        return null;
    }

    /**
     * 解析html转为movieInfo对象
     * @param document
     * @param subjectId
     * @return
     */
    @Override
    public DoubanBook toParse(Document document, long subjectId) {
        return null;
    }

    @Override
    public long findTopByOrderByIdDesc() {
        return 0l;
    }

    @Override
    public boolean isExistInfo(long id) {
        return false;
    }
}
