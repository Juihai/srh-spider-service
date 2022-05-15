package com.shenruihai.spider.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
public class HttpClient {


    /**
     * 设置请求头和参数 post提交
     *
     * @param urlStr 地址
     * @param headMap 请求头
     * @param paramMap 内容参数
     * @return
     */
    public static String connectPost(String urlStr, Map<String, String> headMap, Map<String, String> paramMap) {
        log.info("========设置请求头和参数并以 post提交=======");
        URL url;
        String sCurrentLine = "";
        String sTotalString = "";

        DataOutputStream out = null;

        try {
            url = new URL(urlStr);
            log.info("请求地址:" + urlStr);
            URLConnection URLconnection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) URLconnection;
            // httpConnection.setRequestProperty("Content-type", "application/json");
            httpConnection.setRequestProperty("Accept-Charset", "utf-8");
            httpConnection.setRequestProperty("contentType", "utf-8");

            if (headMap != null && !headMap.isEmpty()) {
                for (String key : headMap.keySet()) {
                    log.info("头部信息key:" + key + "===值: " + headMap.get(key));
                    httpConnection.setRequestProperty(key, headMap.get(key));
                }
            }

            httpConnection.setRequestMethod("POST");

            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);

            StringBuffer params = new StringBuffer();
            // 表单参数与get形式一样
            if (paramMap != null && !paramMap.isEmpty()) {
                for (String key : paramMap.keySet()) {
                    if (params.length() > 1) {
                        params.append("&");
                    }
                    params.append(key).append("=").append(paramMap.get(key).trim());

                }
                log.info("请求参数: " + params.toString());
            }
            //System.out.println("params = " + params.toString());
            out = new DataOutputStream(httpConnection.getOutputStream());
            // 发送请求参数
            if (params!=null) {
                out.writeBytes(params.toString());
            }
            // flush输出流的缓冲
            out.flush();
            // int responseCode = httpConnection.getResponseCode();
            // if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream urlStream = httpConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlStream));

            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                sTotalString += sCurrentLine;
            }
        } catch (Exception e) {
            log.info("请求错误: " + e.getMessage());
            log.error("系统错误:",e);
        } finally {

        }
        log.info("响应信息: " + sTotalString);
        return sTotalString;
    }


    public static void test() throws Exception{
        String url = "http://******/api/hdfs/proxy/create?username=******&password=*****&uri=/test/test-xx.txt";
        byte[] bytes = "1".getBytes();

        CloseableHttpClient client = HttpClients.createDefault();
        RequestBuilder requestBuilder = RequestBuilder.post();
        requestBuilder.setEntity(new ByteArrayEntity(bytes));
        requestBuilder.setUri(url);

        HttpResponse response = client.execute(requestBuilder.build());
        HttpEntity entity = response.getEntity();
        //获得响应流
        InputStream is = entity.getContent();

        //读取流中内容
        ByteArrayBuffer buffer = new ByteArrayBuffer(4096);
        byte[] tmp = new byte[4096];
        int count;
        try {
            while ((count = is.read(tmp)) != -1) {
                buffer.append(tmp, 0, count);
            }
        } catch (EOFException e) {
            log.error("系统错误:",e);
        }
        //System.out.println(new String(buffer.toByteArray()));
    }

    /**
     * Http Get方法
     *
     * @param url
     * @param param
     * @return
     */
    public static String doGet(String url, Map<String, String> param) {
        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);

            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            log.error("系统错误:",e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                log.error("系统错误:",e);
            }
        }
        return resultString;
    }

    /**
     * Http Get方法
     *
     * @param url
     * @param param
     * @return
     */
    public static String doGet(String url,Map<String, String> headMap,Map<String, String> param) {
        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }

            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);

            if (headMap != null && !headMap.isEmpty()) {
                for (String key : headMap.keySet()) {
                    log.info("头部信息key:" + key + "===值: " + headMap.get(key));
                    httpGet.addHeader(key, headMap.get(key));
                }
            }

            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            log.error("系统错误:",e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                log.error("系统错误:",e);
            }
        }
        return resultString;
    }

    public static String doGet(String url) {
        return doGet(url, null);
    }

    /**
     * httpclient post方法
     *
     * @param url
     * @param param
     * @return
     */
    public static String doPost(String url,Map<String, String> headers,Map<String, String> param) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            if(headers != null) {
                for (String key : headers.keySet()) {
                    httpPost.setHeader(key, headers.get(key));
                }
            }
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "utf-8");
                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            log.error("系统错误:",e);
        } finally {
            try {
                if (response!=null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error("系统错误:",e);
            }
        }
        return resultString;
    }

    public static String doPost(String url) {
        return doPost(url,null,null);
    }

    /**
     * 请求的参数类型为json
     *
     * @param url
     * @param json
     * @return {username:"",pass:""}
     */
    public static String doPostJson(String url, String json) {

        log.info("=====请求地址:"+url);
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            log.info("=====请求参数:"+json);
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            log.info("=====响应参数:"+response);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            log.error("系统错误:",e);
        } finally {
            try {
                if (response!=null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error("系统错误:",e);
            }
        }
        return resultString;
    }

    /**
     * 请求的参数类型为json
     *
     * @param url
     * @param json
     * @return {username:"",pass:""}
     */
    public static String doPostJson(String url, String json, Map<String, String> headers) {

        log.info("=====请求地址:"+url);
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            if(headers != null) {
                for (String key : headers.keySet()) {
                    httpPost.setHeader(key, headers.get(key));
                }
            }

            // 创建请求内容
            log.info("=====请求参数:"+json);
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            log.info("=====响应参数:"+response);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            log.error("系统错误:",e);
        } finally {
            try {
                if (response!=null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error("系统错误:",e);
            }
        }
        return resultString;
    }

    /**
     * 发送HTTP_POST请求
     *
     * @param reqURL
     *            请求地址
     * @param params
     * @return 远程主机响应正文`HTTP状态码,如<code>"SUCCESS`200"</code><br>
     *         若通信过程中发生异常则返回"Failed`HTTP状态码",如<code>"Failed`500"</code>
     */
    public static String sendPostRequestByJava(String reqURL, Map<String, String> params) {
        StringBuilder sendData = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sendData.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        if (sendData.length() > 0) {
            sendData.setLength(sendData.length() - 1); // 删除最后一个&符号
        }
        return sendPostRequestByJava(reqURL, sendData.toString());
    }

    /**
     * 发送HTTP_POST请求
     *
     * @param reqURL
     *            请求地址
     * @param sendData
     *            发送到远程主机的正文数据
     * @return 远程主机响应正文`HTTP状态码,如<code>"SUCCESS`200"</code><br>
     *         若通信过程中发生异常则返回"Failed`HTTP状态码",如<code>"Failed`500"</code>
     */
    public static String sendPostRequestByJava(String reqURL, String sendData) {
        HttpURLConnection httpURLConnection = null;
        OutputStream out = null; // 写
        InputStream in = null; // 读
        int httpStatusCode = 0; // 远程主机响应的HTTP状态码
        try {
            URL sendUrl = new URL(reqURL);
            httpURLConnection = (HttpURLConnection) sendUrl.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true); // 指示应用程序要将数据写入URL连接,其值默认为false
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(30000); // 30秒连接超时
            httpURLConnection.setReadTimeout(30000); // 30秒读取超时

            out = httpURLConnection.getOutputStream();
            out.write(sendData.toString().getBytes());

            // 清空缓冲区,发送数据
            out.flush();

            // 获取HTTP状态码
            httpStatusCode = httpURLConnection.getResponseCode();

            in = httpURLConnection.getInputStream();
            byte[] byteDatas = new byte[in.available()];
            in.read(byteDatas);
            return new String(byteDatas) + "`" + httpStatusCode;
        } catch (Exception e) {
            log.debug(e.getMessage());
            return "Failed`" + httpStatusCode;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    log.debug("关闭输出流时发生异常,堆栈信息如下", e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    log.debug("关闭输入流时发生异常,堆栈信息如下", e);
                }
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
                httpURLConnection = null;
            }
        }
    }

    /**
     * 发送HTTP_POST请求,json格式数据
     *
     * @param url
     * @param body
     * @return
     * @throws Exception
     */
    public static String sendPostByJson(String url, String body) throws Exception {
        CloseableHttpClient httpclient = HttpClients.custom().build();
        HttpPost post = null;
        String resData = null;
        CloseableHttpResponse result = null;
        try {
            post = new HttpPost(url);
            HttpEntity entity2 = new StringEntity(body, Consts.UTF_8);
            post.setConfig(RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(30000).build());
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Access-Token", "sund2f3bf3e7ecea902bcdb7027e9139a02");
            post.setEntity(entity2);
            result = httpclient.execute(post);
            if (HttpStatus.SC_OK == result.getStatusLine().getStatusCode()) {
                resData = EntityUtils.toString(result.getEntity());
            }
        } finally {
            if (result != null) {
                result.close();
            }
            if (post != null) {
                post.releaseConnection();
            }
            httpclient.close();
        }
        return resData;
    }

}

