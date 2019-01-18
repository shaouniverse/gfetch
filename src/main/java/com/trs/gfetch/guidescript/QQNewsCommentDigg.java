package com.trs.gfetch.guidescript;

import com.alibaba.fastjson.JSONObject;
import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.login.QQLoginBrowser;
import com.trs.gfetch.utils.DriverUtil;
import com.trs.gfetch.utils.MQSender;
import com.trs.gfetch.utils.StopLoadPage;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Slf4j
public class QQNewsCommentDigg extends GuideAbstract {

    String targetId = null;

    public static void main(String[] args) {

        Task task = new Task();
        task.setAddress("http://bj.jjj.qq.com/a/20181114/002675.htm");
        task.setDiggContent("政府是治理环境的牵头人和主要责任人");
        task.setDiggId("3443033808");
        task.setNick("502023904");
        task.setPassword("lilei516688");

        task.setNick("2598532239");
        task.setPassword("4211432a");

        new QQNewsCommentDigg().start(task);

    }

    @Override
    public void start(Task task) {
        //初始化一些参数
        init(task);
        //把新闻链接转化成评论链接,评论链接不变
        targetId = QQLoginBrowser.formatAddress(task);
        run();
    }

    @Override
    public void run() {
        WebDriver driver = DriverUtil.getDriver();
        try {
            //登录
            boolean suc = QQLoginBrowser.toLogin(driver, task);
            if(!suc){
                isSuccess(task);
            }else{
                //打开转发地址
                StopLoadPage stopLoadPage = new StopLoadPage();
                driver.get(task.getAddress());
                stopLoadPage.isEnterESC=0;
                toDigg(driver);
            }
        } catch (Exception e) {
            task.setCode(201);
            e.printStackTrace();
        } finally {
            DriverUtil.quit(driver);
            MQSender.toMQ(task);
            log.info("任务结束");
        }
    }

    /**
     * 去点赞
     * @param driver
     * @throws Exception
     */
    public void toDigg(WebDriver driver) throws Exception{
        driver.switchTo().frame(driver.findElement(By.id("commentIframe")));
        for(int i=0;i<2;i++){
            try {
                //下一页
                driver.findElement(By.className("J_shortMore")).click();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }

        WebElement divCmt = driver.findElement(By.id("J_ShortComment"));
        List<WebElement> list = divCmt.findElements(By.tagName("div"));
        for(WebElement element: list){
            if(element.getText().contains(task.getDiggContent())){
                element.findElement(By.className("comment-operate-up")).click();
                task.setCode(200);
                task.setResult("点赞成功");
                break;
            }
        }
    }

    /**
     * jsoup方式获得targetId
     * @return
     */
    public String getTargetId(){
        String cmt_id = "";
        try {
            Document doc2 =Jsoup.connect(task.getAddress()).get();
            String result = doc2.toString();
            if(result.contains("cmt_id")){
                result = result.substring(result.indexOf("cmt_id"));
                cmt_id = result.substring(result.indexOf("cmt_id")+6,result.indexOf(";")).replace("=", "").trim();
            }else if(result.contains("comment_id")){
                result = result.substring(result.indexOf("comment_id")+12);
                result = result.substring(0,result.indexOf(","));
                cmt_id = result.replaceAll("\"","");
            }else{
                System.err.println("找不到targetid");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  cmt_id;
    }

}
