package com.shenruihai.spider.service.impl;

import com.shenruihai.spider.dao.DoubanBookDao;
import com.shenruihai.spider.dao.model.DoubanBook;
import com.shenruihai.spider.log.SpiderLogger;
import com.shenruihai.spider.service.MsgNotifyService;
import com.shenruihai.spider.service.SpiderService;
import com.shenruihai.spider.utils.IsbnUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * 负责图书数据的爬取和解析
 * @author shenruihai
 * @date 2022/5/15
 */
@Service("bookService")
public class BookServiceImpl extends SpiderService {

    private final long minSubjectId = 1000000l;

    @Autowired
    DoubanBookDao doubanBookDao;
    @Autowired
    MsgNotifyService msgNotifyService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean spiderDancer(long subjectId){
        SpiderLogger.infoLog.info("Spider开始执行, subjectId: "+subjectId);
        if(subjectId <=minSubjectId){
            SpiderLogger.errorLog.error("异常信息：subjectId less than default, subjectId = "+ subjectId);
            return false;
        }

        Document document = this.doGet(subjectId);
        if(document == null){
            SpiderLogger.infoLog.info("未查询到对应信息, subjectId="+subjectId);
            return false;
        }
        DoubanBook book = this.toParse(document, subjectId);
        if(book == null){
            SpiderLogger.infoLog.info("你想访问的条目豆瓣不收录, subjectId="+subjectId);
            return false;
        }
        DoubanBook readBook = doubanBookDao.findByIsbnCode(book.isbnCode);
        if(readBook != null){
            return true;
        }
        try{
            doubanBookDao.save(book);
        }catch (Exception e){
            msgNotifyService.notifyDev("doubanBook save error,"+e.getMessage());
            System.out.println(e.getMessage());
        }
        SpiderLogger.infoLog.info("数据保存成功, subjectId= "+subjectId+" , ISBN: "+ book.isbnCode);

        return true;
    }

    /**
     * 获取原始html信息
     * @param subjectId
     * @return
     * @throws IOException
     */
    @Override
    public Document doGet(long subjectId) {

        try {
            return Jsoup.connect("https://book.douban.com/subject/"+subjectId+"/")
//                    .proxy("39.175.92.205", 30001)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.64 Safari/537.36")
                    .cookie("ll","108288")
                    .cookie("bid", "KM9WS0ul-wk")
                    .cookie("douban-fav-remind","1")
                    .cookie("gr_user_id", "80a83b3f-a27a-4a98-8cd7-9ea2852832ca")
                    .cookie("ck","HHws")
                    .cookie("viewed","1000067_35921760_24748615_1578545_1322455_35914877_26410730_3633461")
                    .cookie("gr_session_id_22c937bbd8ebd703f2d8e9445f7dfd03", "6632e156-7bfc-44ed-bf2b-121d614d1a75")
                    .cookie("bid","bi1mQfnRUd8")
                    .cookie("_pk_id.100001.3ac3","d6bb4abb099b79b3.1658223009.10.1658829103.1658825390.")
                    .cookie("_pk_id.100001.8cb4","de63b32ea26ff382.1656569547.4.1658825498.1658479967.")
                    .cookie("_pk_ses.100001.3ac3","*")
                    .cookie("_pk_ref.100001.3ac3","%5B%22%22%2C%22%22%2C1658829414%2C%22https%3A%2F%2Faccounts.douban.com%2F%22%5D")
                    .timeout(20000)
                    .post();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析html转为bookInfo对象
     * @param document
     * @param subjectId
     * @return
     */
    @Override
    public DoubanBook toParse(Document document, long subjectId){
        String isbn = document.getElementsByTag("meta").select("meta[property=book:isbn]").attr("content");
        if(StringUtils.isBlank(isbn) || !IsbnUtil.checkIsbn(isbn)){
            SpiderLogger.errorLog.error("非法的ISBN编码，ISBN: "+isbn);
            return null;
        }
        DoubanBook book = new DoubanBook();
        book.isbnCode = isbn;
        book.name = document.getElementsByTag("meta").select("meta[property=og:title]").attr("content");
        book.subName = document.select("div[id=info]").select("span:contains(副标题)").size()>0 ?
                document.select("div[id=info]").select("span:contains(副标题)").get(0).nextSibling().toString() : "";
        book.author = document.getElementsByTag("meta").select("meta[property=book:author]").size()>0 ?
                document.getElementsByTag("meta").select("meta[property=book:author]").attr("content") : "";

        book.translator = document.select("div[id=info]").select("span:contains(译者)").size()>0 ?
                document.select("div[id=info]").select("span:contains(译者)").get(0).nextElementSibling().text() : "";
        book.keywords = document.getElementsByTag("meta").select("meta[name=keywords]").attr("content");
        if(document.select("div[class=intro]").size()==3){
            book.description = document.select("div[class=intro]").get(1).select("p").text();
            book.authorIntro = document.select("div[class=intro]").get(2).select("p").text();
        }else if(document.select("div[class=intro]").size()==2){
            book.description = document.select("div[class=intro]").get(0).select("p").text();
            book.authorIntro = document.select("div[class=intro]").get(1).select("p").text();
        }

        book.froms = document.getElementsByTag("meta").select("meta[property=og:site_name]").attr("content");
        book.doubanUrl = document.getElementsByTag("meta").select("meta[property=og:url]").attr("content");
        book.photoUrl = document.getElementsByTag("meta").select("meta[property=og:image]").attr("content");
        book.doubanScore = document.getElementsByTag("strong").text();
        book.numScore = document.select("span[property=v:votes]").text();
        book.douban = subjectId;

        book.publishing = document.select("div[id=info]").select("span:contains(出版社)").next().select("a[href]").text();
        //出品方
        book.producer = document.select("div[id=info]").select("span:contains(出品方)").size()>0 ?
                document.select("div[id=info]").select("span:contains(出品方)").next().select("a[href]").text() : null;
        book.published = document.select("div[id=info]").select("span:contains(出版年)").size()>0 ?
                document.select("div[id=info]").select("span:contains(出版年)").get(0).nextSibling().toString() : null;
        //原作名
        book.originalName = document.select("div[id=info]").select("span:contains(原作名)").size()>0 ?
                document.select("div[id=info]").select("span:contains(原作名)").get(0).nextSibling().toString() : null;


        book.pages = document.select("div[id=info]").select("span:contains(页数)").size()>0 ?
                document.select("div[id=info]").select("span:contains(页数)").get(0).nextSibling().toString() : null;
        book.price = document.select("div[id=info]").select("span:contains(定价)").size()>0 ?
                document.select("div[id=info]").select("span:contains(定价)").get(0).nextSibling().toString().replace("元","") : null;
        book.designed = document.select("div[id=info]").select("span:contains(装帧)").size()>0 ?
                document.select("div[id=info]").select("span:contains(装帧)").get(0).nextSibling().toString() : null;
        //丛书
        book.series = document.select("div[id=info]").select("span:contains(丛书)").next().select("a[href]").text();
        book.seriesDesc = document.select("h2:contains(丛书信息)").next().select("a[href]").text();
        book.tags = document.select("span[class=rank-label-link]").text();
        book.num = document.select("span[class=rank-label-no]").text().replace("No.","");
        //评论数量
        String reviews = StringUtils.substringBetween(document.select("a[href=reviews]").text(), "全部","条") ;
        book.reviews = reviews != null ? reviews.trim() : "";

        return book;
    }

    @Override
    public long findTopByOrderByIdDesc() {
        DoubanBook book = doubanBookDao.findTopByOrderByIdDesc();
        return book != null ? book.douban : minSubjectId;
    }
}
