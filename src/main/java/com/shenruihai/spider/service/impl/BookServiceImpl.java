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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        DoubanBook readBook = doubanBookDao.findByDouban(subjectId);
        if(readBook != null){
            SpiderLogger.infoLog.info("数据已存在, subjectId= "+subjectId+" , ISBN: "+ readBook.isbnCode);
            return true;
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
            Map cookieMap = getCookieMap();

            return Jsoup.connect("https://book.douban.com/subject/"+subjectId+"/")
//                    .proxy("39.175.92.205", 30001)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                    .cookies(cookieMap)
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

    public long isExistInfo(long subjectId){
        DoubanBook book = doubanBookDao.findByDouban(subjectId);
        return book != null ? book.id : -1;
    }

    public Map getCookieMap(){
        String cookiePlain = "bid=bi1mQfnRUd8; ll=\"108288\"; __utmc=30149280; __utmc=81379588; gr_user_id=80a83b3f-a27a-4a98-8cd7-9ea2852832ca; _vwo_uuid_v2=DBB321D1F5CA061502578B8869D179975|5ffeff6c64f73f1e524b67493b92ca17; _ga=GA1.1.1452951026.1658801495; _ga_RXNMP372GL=GS1.1.1658801495.1.0.1658801497.58; Hm_lvt_16a14f3002af32bf3a75dfe352478639=1658801504; Hm_lpvt_16a14f3002af32bf3a75dfe352478639=1658801504; push_noty_num=0; push_doumail_num=0; __utmv=30149280.14535; ct=y; viewed=\"27613353_35288324_35317149_27167368_35498118_26969957_4820710_25818441_25798623_1000067\"; dbcl2=\"145353480:61lnoaQbgx8\"; ck=tqLM; __utmz=30149280.1660009024.26.10.utmcsr=search.douban.com|utmccn=(referral)|utmcmd=referral|utmcct=/book/subject_search; __utmz=81379588.1660009024.20.9.utmcsr=search.douban.com|utmccn=(referral)|utmcmd=referral|utmcct=/book/subject_search; gr_session_id_22c937bbd8ebd703f2d8e9445f7dfd03=1681702f-5ba7-4b51-ae31-9815104d243b; gr_cs1_1681702f-5ba7-4b51-ae31-9815104d243b=user_id:1; gr_session_id_22c937bbd8ebd703f2d8e9445f7dfd03_1681702f-5ba7-4b51-ae31-9815104d243b=true; ap_v=0,6.0; _pk_ref.100001.3ac3=[\"\",\"\",1660205745,\"https://search.douban.com/book/subject_search?search_text=%E4%B8%AD%E5%9B%BD%E5%91%BD%E7%90%86%E5%AD%A6&cat=1001\"]; _pk_id.100001.3ac3=d6bb4abb099b79b3.1658223009.22.1660205745.1660111813.; _pk_ses.100001.3ac3=*; __utma=30149280.1603367308.1656569548.1660111753.1660205745.28; __utmb=30149280.1.10.1660205745; __utma=81379588.157348678.1658223009.1660111753.1660205745.22; __utmb=81379588.1.10.1660205745";
        List<String> cookieStrList = Arrays.asList(cookiePlain.split(" ").clone());
        Map<String, String> resultMap = new HashMap<>();
        for(String cookieStr : cookieStrList){
            String[] cookieArr = cookieStr.split("=");
            resultMap.put(cookieArr[0], cookieArr[1]);
        }
        return resultMap;
    }

}
