package com.shenruihai.spider.dao;

import com.shenruihai.spider.dao.model.DoubanMovie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoubanMovieDao extends JpaRepository<DoubanMovie, Long> {

    DoubanMovie findByImdbCode(String imdbCode);

    /**
     * 获取最新一条数据
     * @return
     */
    DoubanMovie findTopByOrderByIdDesc();

}
