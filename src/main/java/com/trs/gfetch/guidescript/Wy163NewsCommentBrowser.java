package com.trs.gfetch.guidescript;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.login.Wy163LoginBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.List;

public class Wy163NewsCommentBrowser extends GuideAbstract {

    public static void main(String args[]){
        Task task = new Task();
        task.setAccount("augy00@163.com");
        task.setPassword("qg2048");
        task.setAddress("https://news.163.com/19/0104/05/E4LDKEN10001875O.html");
        task.setCorpus("这小美国...");
        new Wy163NewsCommentBrowser().start(task);
    }

    @Override
    public void toComment() throws Exception {
        if(task.getCorpus().length()<2){
            task.setResult("语料必须大于2个字");
            task.setCode(302);
            return;
        }
        String commentAddress = Wy163LoginBrowser.getCommentAddress(task);
        if(commentAddress==null){
            task.setCode(402);
            task.setResult("未找到评论链接!");
            return;
        }
        comment();
    }
    public void comment(){
        try {
            driver.get(task.getAddress());
            Thread.sleep(2000);

            try {
                driver.findElement(By.xpath("//*[@id='tie-main']/div[4]/div/textarea")).sendKeys(task.getCorpus());
            } catch (Exception e) {
                //第二种寻找评论方式
                driver.findElement(By.xpath("//*[@id='tie-main']/div[4]/div/textarea")).sendKeys(task.getCorpus());
            }
            Thread.sleep(1000);
            //点击提交
            try {
                WebElement l1 = driver.findElement(By.xpath("//*[@id='tie-main']/div[4]/div/div/div[2]/span"));
                JavascriptExecutor executor = (JavascriptExecutor) driver;
                executor.executeScript("arguments[0].click();",l1);
            } catch (Exception e2) {
                try {
                    WebElement last = driver.findElement(By.xpath("//*[@id=\"tie-main\"]/div[4]/div/div/div[2]/span"));
                    JavascriptExecutor executor = (JavascriptExecutor) driver;
                    executor.executeScript("arguments[0].click();",last);
                } catch (Exception e) {
                    try {
                        driver.findElement(By.className("submit")).click();
                    } catch (Exception e1) {

                    }
                }
            }

            Thread.sleep(2000);
            try {
                WebElement realNameAlertMsg = driver.findElement(By.id("realNameAlertMsg"));
                if(realNameAlertMsg.isDisplayed()){
                    task.setCode(105);
                    task.setResult("请绑定手机号");
                    return;
                }
            } catch (Exception e1) {}

            WebElement myResult = null;
            try {
                System.out.println("第1种查找刚发的评论,根据列表查找");
                WebElement commentList = driver.findElement(By.xpath("//*[@id=\"tie-main\"]/div[3]/div[2]/div"));
                List<WebElement> list = commentList.findElements(By.tagName("div"));
                myResult = list.get(0);
            } catch (Exception e1) {
                System.out.println("第二种class查找刚发的评论");
                try {
                    List<WebElement> list = driver.findElements(By.className("trunk clearfix"));
                    myResult = list.get(0);
                } catch (Exception e) {
                }
            }
            isSucc(driver,myResult,"wy163");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean login() {
        return Wy163LoginBrowser.toLogin(driver,task);
    }

    @Override
    public void start(Task task) {
        //初始化一些参数
        init(task);
        run();
    }
}
