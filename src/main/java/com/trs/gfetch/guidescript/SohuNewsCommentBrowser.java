package com.trs.gfetch.guidescript;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.login.SohuLoginBrowser;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@Slf4j
public class SohuNewsCommentBrowser extends GuideAbstract {

    public static void main(String args[]){
        Task task = new Task();
        task.setAddress("http://www.sohu.com/a/290910910_119038?code=55e53ae603a03f65c59ec67485af8e29&_f=index_chan08cpc_0");
        task.setAccount("15626027805");
        task.setPassword("mm949811");
        task.setCorpus("领导果然有方");

        new SohuNewsCommentBrowser().start(task);
    }
    @Override
    public void start(Task task) {
        //初始化一些参数
        init(task);
        run();
    }

    @Override
    public void toComment() throws Exception {
        try {
            //点击评论数
            driver.findElement(By.className("comment-more-clone")).click();
            Thread.sleep(1000 * 2);
        } catch (Exception e) {
            log.info("未找到点击评论的地方");
        }

        driver.findElement(By.xpath("//*[@id='mpbox']/div[2]/div/div[1]/textarea")).click();
        driver.findElement(By.xpath("//*[@id='mpbox']/div[2]/div/div[1]/textarea")).sendKeys(task.getCorpus());
        Thread.sleep(500);
        driver.findElement(By.xpath("//*[@id='mpbox']/div[2]/div/div[3]")).click();
        //
        WebElement commentDiv = null;
        try {
            commentDiv = driver.findElement(By.xpath("//*[@id=\"mpbox\"]/div[4]/div/div[4]/div[1]/div[2]"));
        } catch (Exception e) {
            commentDiv = driver.findElement(By.xpath("//*[@id=\"mpbox\"]/div[4]/div/div[2]/div[1]/div[2]"));
            log.info("进入第二种方式");
        }
        try {
            isSucc(driver,commentDiv,"sohu");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean login() {
        return SohuLoginBrowser.login(driver,task);
    }

}
