package com.trs.gfetch.guidescript;

import com.alibaba.fastjson.JSONObject;
import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.login.SinaLoginBrowser;
import com.trs.gfetch.utils.DriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SinaNewsCommentDigg extends GuideAbstract {

    public static void main(String args[]){
        Task task = new Task();
        //channel=gn&newsid=comos-hrfqziz9914444&mid=5C4696ED-B6306283-593D569F-862-8CE
        task.setDiggContent("让妈祖劝劝爱人");
        task.setAddress("https://news.sina.com.cn/o/2019-04-25/doc-ihvhiqax5043013.shtml");

        new SinaNewsCommentDigg().start(task);
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
        //不需要账号点两次
        for(int i=0;i<2;i++){
            driver.get(task.getAddress());
            //滚动到底部获得第一页
            DriverUtil.scrollToButtom(driver);
            Thread.sleep(1500);
            //滚动到底部获得第二页
            DriverUtil.scrollToButtom(driver);
            Thread.sleep(1500);

            boolean flag = findElementDigg();

            if(flag){
                task.setCode(200);
                task.setResult("点赞成功");
            }else{
                task.setCode(401);
                task.setResult("点赞失败");
            }
        }

    }
    public boolean findElementDigg(){
        WebElement big = driver.findElement(By.className("sina-comment-list-has-latest"));
        List<WebElement> list = big.findElements(By.className("cont"));
        for(WebElement element: list){
            if(element.getText().contains(task.getDiggContent())){
                DriverUtil.moveMouseToElement(driver,element);
                element.findElement(By.className("vote")).click();
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
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
