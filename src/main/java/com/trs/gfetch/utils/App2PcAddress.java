package com.trs.gfetch.utils;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class App2PcAddress {

    public static void main(String args[]){
        String ifengaddress = "https://ishare.ifeng.com/c/s/7mcc3uucufY?aman=445c121j240q102Y068";
        String sina = "https://news.sina.cn/gn/2019-05-08/detail-ihvhiews0648098.d.html?sinawapsharesource=newsapp&wm=3200_0024&sinawapsharesource=newsappliteios";
        String a163 = "https://c.m.163.com/news/a/EENQA19G00038FO9.html?spss=newsapp";
        String qq = "https://view.inews.qq.com/a/20190513A0NE8T00?chlid=news_news_top";
        try {
            System.out.println("new add:--->");
//            System.out.println(app163(a163));
//            System.out.println(appIfeng(ifengaddress));
//            System.out.println(appSina(sina));
            System.out.println(appQQ(qq));
        } catch (Exception e) {
            System.out.println("----------->解析地址失败");
        }
    }
    /**
     * QQ APP地址转pc地址
     * APP: https://view.inews.qq.com/a/20190508A0897800?uid=&chlid=news_news_bj
     * https://new.qq.com/omn/20190508/20190508A08978.html
     */
    public static String appQQ(String address) throws Exception{
        if(!address.contains("view.inews.qq")) return address;
        if(address.contains("?")){
            address = address.substring(0,address.indexOf("?"));
        }
        address = address.substring(address.lastIndexOf("/")+1,address.length()-2);
        StringBuilder sb = new StringBuilder();
        sb.append("https://new.qq.com/omn/");
        sb.append(address.substring(0,8)+"/");
        sb.append(address);
        sb.append(".html");
        System.out.println("qq pcUrl------------>"+sb.toString());
        return sb.toString();
    }

    /**
     * 凤凰APP地址转pc地址
     * APP: https://ishare.ifeng.com/c/s/7mVDyt49XrE?aman=445D121A240O1020068
     * 源码里面包含 pcUrl 数据
     */
    public static String appIfeng(String address) throws Exception{
        if(!address.contains("ishare.ifeng")) return address;
        String sourceCode = getHtmlContent(address);
        int ind = sourceCode.indexOf("pcUrl")+8;
        sourceCode = sourceCode.substring(ind);
        int f = sourceCode.indexOf("\"");
        String pcUrl = sourceCode.substring(0,f);
        System.out.println("ifeng pcUrl------------>"+pcUrl);
        return pcUrl;
    }

    /**
     * 新浪APP地址转pc地址
     * APP: https://news.sina.cn/gn/2019-05-08/detail-ihvhiews0648098.d.html?sinawapsharesource=newsapp&wm=3200_0024&sinawapsharesource=newsappliteios
     * 源码里面包含 sudaLogConfig 数据
     */
    public static String appSina(String address) throws Exception{
        if(!address.contains("newsapp")) return address;
        String sourceCode = getHtmlContent(address);
        String mark = "sudaLogConfig";
        sourceCode = sourceCode.substring(sourceCode.indexOf(mark)+mark.length()+3);
        sourceCode = sourceCode.substring(0,sourceCode.indexOf("};")+1);
        JSONObject jsonObject = JSONObject.parseObject(sourceCode);
        String url = jsonObject.get("url").toString();
        System.out.println("------>"+url);
        return url;
    }

    /**
     * 网易APP地址转pc地址
     * APP: https://c.m.163.com/news/a/EEMGFI6M0001899O.html?spss=newsapp
     * pc: https://news.163.com/19/0508/22/EEMGFI6M0001899O.html
     */
    public static String app163(String address) throws Exception{
        if(!address.contains("newsapp")) return address;
        try {
            String sourceCode = getHtmlContent(address);
            //获得board
            String httpH = get163Http(address);
            String board = get163Board(sourceCode);
            String timeUrl = get163Times(sourceCode);
            String docid = get163Docid(sourceCode);
            StringBuilder sb = new StringBuilder();
            sb.append(httpH);
            sb.append(board);
            sb.append(".163.com/");
            sb.append(timeUrl);
            sb.append(docid);
            sb.append(".html");
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("163解析地址失败----------------------->");
        }
        return address;
    }
    //获得网易board
    public static String get163Board(String sourceCode) throws Exception{
        String boardid = "data-boardid=";
        String boardTmp = sourceCode.substring(sourceCode.indexOf(boardid)+boardid.length()+1);
        boardTmp = boardTmp.substring(0,boardTmp.indexOf("\""));
        String boards[] = boardTmp.split("_");
        String boardLast = boards[0].substring(boards[0].length()-1,boards[0].length());
        try {
            Integer.parseInt(boardLast);
            return boards[0].substring(0,boards[0].length()-1);
        } catch (NumberFormatException e) {
            System.out.println("------>board最后一位不是数字");
        }
        System.out.println("解析163 board-------->"+boards[0]);
        return boards[0];

    }
    //获得163时间信息
    public static String get163Times(String sourceCode) throws Exception{
        StringBuffer sb = new StringBuffer();
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        if(sourceCode.contains(year+"")) sb.append((year-2000)+"/");
        else sb.append((year-2001)+"/");
        {
            String boardid = "g-subtitle";
            String boardTmp = sourceCode.substring(sourceCode.indexOf(boardid));
            String date = boardTmp.substring(boardTmp.indexOf("<span>")+6,boardTmp.indexOf("</span>"));
            String month = date.substring(0,2);
            sb.append(month);
            String day = date.substring(3,5);
            sb.append(day+"/");
            String min = date.substring(6,8);
            sb.append(min+"/");
        }
        System.out.println("解析163 时间地址---------------->"+sb.toString());
        return sb.toString();
    }
    //获得163 文章id
    public static String get163Docid(String sourceCode) throws Exception{
        String boardid = "data-docid=";
        String boardTmp = sourceCode.substring(sourceCode.indexOf(boardid)+boardid.length()+1);
        boardTmp = boardTmp.substring(0,boardTmp.indexOf("\""));
        System.out.println("解析163文章id---------------->"+boardTmp);
        return boardTmp;
    }
    //获得163 http请求头
    public static String get163Http(String address){
        String add[] = address.split(":");
        return add[0]+"://";
    }


    /**
     * 获取url网页的源代码
     * @param urlStr 链接
     * @return 网页源代码
     */
    public static String getHtmlContent(String urlStr) {

        URL url=null;
        StringBuffer contentBuffer = new StringBuffer(); //内容源代码
        int responseCode = -1;  //网页返回信息吗
        HttpURLConnection con = null;

        try {
            url = new URL(urlStr);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");// IE代理进行下载
            con.setConnectTimeout(60000);  //连接超时设置为60s即一分钟
            con.setReadTimeout(60000);//网页停留时间
            con.setDoOutput(true);
            // 获得网页返回信息码
            responseCode = con.getResponseCode();
            System.out.println("responseCode---------->"+responseCode);
            //重定向后
            if(responseCode==301 || responseCode==302){
                String location = con.getHeaderField("Location");
                System.out.println("responseAddress---------->"+location);
                return getHtmlContent(location);
            }
            if (responseCode == -1) {
                System.out.println(url.toString() + " : connection is failure...");
                con.disconnect();
                return null;
            }
            if (responseCode >= 400) // 请求失败
            {
                System.out.println("请求失败:get response code: " + responseCode);
                con.disconnect();
                return null;
            }
            //获取网页源代码
            InputStream in = con.getInputStream();
            //InputStream in = url.openStream();
            InputStreamReader is = new InputStreamReader(in, "utf-8");
            BufferedReader br = new BufferedReader(is);

            String str = null;
            while ((str = br.readLine()) != null){
                contentBuffer.append(str);
//                System.out.println(str);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            contentBuffer = null;
            System.out.println("error: " + url.toString());
        }finally {
            con.disconnect();
        }
        return contentBuffer.toString();
    }

}
