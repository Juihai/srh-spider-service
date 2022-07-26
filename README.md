# 豆瓣爬虫

#### 1. 指定端口号并后台启动spiderDancer
```shell
java -jar spider.jar --server.port=22080 &
```
#### 2. 触发请求，开始爬去
```
curl 'http://127.0.0.1:22080/cron/spiderRun?serviceName=bookService'
```

#### 3. ip被封测试链接
```json5
curl 'https://book.douban.com/subject/1010599/'
```
