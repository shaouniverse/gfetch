package com.trs.gfetch.guidescript;

import com.alibaba.fastjson.JSONObject;
import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.login.SinaLoginBrowser;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SinaNewsCommentDiggScript extends GuideAbstract {

    public static void main(String args[]){
        Task task = new Task();
        //channel=gn&newsid=comos-hrfqziz9914444&mid=5C4696ED-B6306283-593D569F-862-8CE
        task.setDiggId("5C46D414-6FC9E06C-1669949CE-862-835");
        task.setDiggContent("开年大吉");
        task.setAddress("https://news.sina.com.cn/c/2019-01-22/doc-ihrfqziz9914444.shtml");

        new SinaNewsCommentDiggScript().start(task);
    }

    @Override
    public void start(Task task) {
        //初始化一些参数
        init(task);
        run();
    }

    @Override
    public void toComment() throws Exception {
        String address = SinaNewsCommentBrowser.formatAddress(task);
        if(null == address){
            driver.get(task.getAddress());
            Thread.sleep(2000);
            if(!SinaLoginBrowser.judgeIsExsit(driver,task)){
                System.out.println("-------------->访问的页面不存在");
                return;
            }
            address = SinaNewsCommentBrowser.getCommentAddress(task,driver);
        }
        if(null == address){
            task.setResult("查找评论链接失败");
            task.setCode(402);
            return ;
        }
        task.setAddress(address);

        Map<String,String> map = getParams();
        //评论id,要去找
        String commentId = task.getDiggId();
        URL u3 = new URL("http://comment5.news.sina.com.cn/cmnt/vote");
        HttpURLConnection c3 = (HttpURLConnection) u3.openConnection();
        String params = "channel=" + map.get("chanel")
                + "&newsid=" + map.get("key")
                + "&parent=" + commentId
                + "&format=js"
                + "&vote=1"
                + "&callback=function+%28o%29%7B%7D"
                + "&domain=sina.com.cn";
        c3.addRequestProperty("Host", "comment5.news.sina.com.cn");
        c3.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:30.0) Gecko/20100101 Firefox/30.0");
        c3.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        c3.addRequestProperty("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        c3.addRequestProperty("Referer", task.getAddress());
        c3.addRequestProperty("Connection", "keep-alive");
        c3.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        c3.addRequestProperty("Content-Length", String.valueOf(params.length()));
        c3.setDoInput(true);
        c3.setDoOutput(true);
        PrintWriter out3 = new PrintWriter(c3.getOutputStream());
        out3.print(params);
        out3.flush();

        InputStream i3 = c3.getInputStream();
        Scanner s3 = new Scanner(i3);
        StringBuffer sb = new StringBuffer();
        while(s3.hasNext()){
            String tt = s3.nextLine();
            sb.append(tt);
        }

        System.out.println("res=="+sb.toString());
        String res = sb.toString().replace("var data=","");
        JSONObject jb = JSONObject.parseObject(res);
        JSONObject result = jb.getJSONObject("result");
        JSONObject status = result.getJSONObject("status");
        int code = status.getInteger("code");
        if(code==0){
            task.setCode(200);
            task.setResult("点赞成功");
        }else{
            task.setCode(401);
            task.setResult("点赞失败");
        }

    }
    //所需参数
    public Map<String,String> getParams(){
        Map<String,String> map = new HashMap<>();
        String key = null;
        String chanel = null;
        map.put("key",key);
        map.put("chanel",chanel);
        try {

            //http://comment5.news.sina.com.cn/comment/skin/default.html?channel=ty&newsid=6-12-7458297
            String url = task.getAddress();
            if(url.indexOf("http://comment5") > -1){
                key = url.substring(url.indexOf("newsid=")).replace("newsid=", "");
                if(key.indexOf("&") > -1){
                    key = key.substring(0, key.indexOf("&"));
                }
                chanel = url.substring(url.indexOf("channel=")).replace("channel=", "");
                if(chanel.indexOf("&") > -1){
                    chanel = chanel.substring(0, chanel.indexOf("&"));
                }
            }
            map.put("key",key);
            map.put("chanel",chanel);
            if(key!=null && chanel!=null) return map;

            URL u2 = new URL(task.getAddress());
            HttpURLConnection c2 = (HttpURLConnection) u2.openConnection();
            c2.connect();

            InputStream ins = c2.getInputStream();
            Scanner scanner = new Scanner(ins,"gb2312");

            while (scanner.hasNext()){
                String scsc = scanner.nextLine();
                if(scsc.indexOf("moodcounter") > -1 && scsc.indexOf("key=") > -1){
                    key = scsc.substring(scsc.indexOf("key=")+5).trim();
                    key = key.substring(0, key.indexOf("\""));
                }else if(scsc.indexOf("meta") > -1 && scsc.indexOf("name=\"comment\"") > -1){
                    key = scsc.substring(scsc.indexOf("content=")+7).trim();
                    key = key.substring(key.indexOf(":") + 1);
                    key = key.substring(0, key.indexOf("\""));
                }
                if(scsc.indexOf("channel:'") > -1){
                    chanel = scsc.substring(scsc.indexOf("channel:'")+9).trim();
                    chanel = chanel.substring(0, chanel.indexOf("'"));
                }
                if(key!=null && chanel!=null) break;
            }
            map.put("key",key);
            map.put("chanel",chanel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 点赞不需要登录,直接返回true
     * @return
     */
    @Override
    public boolean login() {
        return true;
    }

}
