package com.shenruihai.spider.dao;

import com.shenruihai.spider.dao.model.DoubanBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoubanBookDao extends JpaRepository<DoubanBook, Long> {

    DoubanBook findByIsbnCode(String isbn);

    /**
     * 获取最新一条数据
     * @return
     */
    DoubanBook findTopByOrderByIdDesc();

    /**
     *
     * @param subjectId
     * @return
     */
    DoubanBook findByDouban(long subjectId);

}
