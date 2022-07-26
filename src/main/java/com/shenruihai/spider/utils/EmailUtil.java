package com.shenruihai.spider.utils;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.shenruihai.spider.log.SpiderLogger;
import com.sun.mail.util.MailSSLSocketFactory;

public class EmailUtil {

    public static boolean send(String title, String content, String mailServer, String sender, String password, String recipients) throws GeneralSecurityException {

        Properties properties = System.getProperties();// 获取系统属性
        properties.setProperty("mail.smtp.host", mailServer);// 设置邮件服务器 "smtp.163.com",这是发件人的邮箱服务器
        properties.put("mail.smtp.auth", "true");
        String senderNick = null;
        //部分邮箱服务器需要开启SSL验证才可以发送邮件，需要的话，开启注释即可　
        MailSSLSocketFactory sf = new MailSSLSocketFactory();// SSL验证 begin
        sf.setTrustAllHosts(true);
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.socketFactory", sf);// SSL验证 end

        Session session = Session.getDefaultInstance(properties,
                new Authenticator() {
                    public PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sender, password); // 发件人邮件用户名、密码
                    }
                });
        try {
            MimeMessage message = new MimeMessage(session);// 创建默认的 MimeMessage对象
            senderNick = javax.mail.internet.MimeUtility.encodeText("springDancer");//设置发件人昵称
            message.setFrom(new InternetAddress(senderNick+"<"+sender+">"));

            //message.addRecipient(Message.RecipientType.TO,new InternetAddress());//创建单个收件人
            String[] tos = recipients.split(",");//创建多个收件人
            if (tos != null && tos.length != 0) {
                InternetAddress[] toAddress = new InternetAddress[tos.length];
                for (int i = 0; i < tos.length; i++) {
                    toAddress[i] = new InternetAddress(tos[i]);
                }
                message.setRecipients(Message.RecipientType.TO, toAddress);
            }
            message.setSubject(title);//设置邮件主题
            message.setText(content);//发送纯文本内容
//            message.setContent(content, "text/html;charset=utf-8");//发送html邮件内容

            Transport.send(message);//发送Email
            SpiderLogger.infoLog.info(" send email success");
            return true;
        }catch (MessagingException mex) {
            mex.printStackTrace();
            SpiderLogger.errorLog.error(" send email failed: "+mex);
        } catch (UnsupportedEncodingException e) {
            SpiderLogger.errorLog.error(" send email failed, setting sender nickName error: "+e);
            e.printStackTrace();
        }
        return false;
    }

}
