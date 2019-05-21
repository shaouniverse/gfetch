package com.trs.gfetch.guidescript;

import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.login.Judge404;
import com.trs.gfetch.guidescript.login.SinaLoginBrowser;
import com.trs.gfetch.utils.App2PcAddress;
import com.trs.gfetch.utils.DriverUtil;
import com.trs.gfetch.utils.StopLoadPage;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * 新浪新闻评论
 */
@Slf4j
public class SinaNewsCommentBrowser extends GuideAbstract {

    public static void main(String args[]){
        Task task = new Task();
        task.setAddress("http://news.sina.com.cn/o/2018-05-25/doc-ihcaqueu0706189.shtml");
        task.setAddress("http://news.sina.com.cn/o/2018-05-25/doc-ihcaqueu0706189_00.shtml");
        task.setCorpus("看看情况吧");
        task.setAccount("lilei1929@163.com");
        task.setPassword("lilei419688..");
        new SinaNewsCommentBrowser().start(task);
    }

    @Override
    public void start(Task task) {
        //初始化一些参数
        init(task);
        run();
    }

    @Override
    public void toComment(){
        try {
            if(!Judge404.judgeIsExsitSina(driver,task)){
                System.out.println("-------------->访问的页面不存在");
                return;
            }
            driver.findElement(By.xpath("//a[contains(@href,\"comment5.news.sina\")]")).click();
            DriverUtil.switchToNewWindow(driver);
            Thread.sleep(1500);

            //输入预料
            WebElement commentArea = driver.findElement(By.xpath("//*[@id='SI_Wrap']/div[1]/div[1]/div/div[1]/div[2]/div[1]/div/textarea"));
            commentArea.clear();
            commentArea.sendKeys(task.getCorpus());
            Thread.sleep(1500);
            //提交按钮
            WebElement publicComment=null;
            try {
                publicComment = driver.findElement(By.tagName("发布"));
            } catch (Exception e) {
                publicComment = driver.findElement(By.xpath("//*[@id='bottom_sina_comment']/div[1]/div[3]/div[2]/a[1]"));
            }
            publicComment.click();

            try {
                WebElement commentDiv = driver.findElement(By.xpath("//*[@id='SI_Wrap']/div[1]/div[1]/div/div[2]/div[2]/div[3]/div[1]"));
                isSucc(driver,commentDiv,"sina");
            } catch (Exception e) {
                task.setCode(200);
                task.setResult("发帖成功!");
                System.out.println("----->成功与否判断失败");
            }
        } catch (Exception e) {
            task.setCode(201);
            task.setResult("评论异常");
            e.printStackTrace();
        }
    }

    @Override
    public boolean login() {
        return SinaLoginBrowser.toLogin(driver, task, 0);
    }

}
