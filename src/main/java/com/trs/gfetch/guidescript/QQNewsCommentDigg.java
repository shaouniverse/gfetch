package com.trs.gfetch.guidescript;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.login.Judge404;
import com.trs.gfetch.guidescript.login.QQLoginBrowser;
import com.trs.gfetch.utils.StopLoadPage;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

@Slf4j
public class QQNewsCommentDigg extends GuideAbstract {

    public static void main(String[] args) {

        Task task = new Task();
        task.setAddress("http://coral.qq.com/3670454015");
        task.setDiggContent("出行高峰期小编也可以感受一下昌平线");
        task.setDiggId("3443033808"); //无用
        {
            task.setAccount("502023904");
            task.setPassword("lilei516688");
        }

        task.setAccount("2598532239");
        task.setPassword("4211432a");

        new QQNewsCommentDigg().start(task);

    }

    @Override
    public void start(Task task) {
        //初始化一些参数
        init(task);
        //把新闻链接转化成评论链接,评论链接不变
        QQLoginBrowser.formatAddress(task);
        run();
    }

    @Override
    public boolean login() {
        return QQLoginBrowser.toLogin(driver, task);
    }
    /**
     * 去点赞
     * @throws Exception
     */
    @Override
    public void toComment() throws Exception{
        //打开转发地址
//        StopLoadPage stopLoadPage = new StopLoadPage();
        driver.get(task.getAddress());
        if(!Judge404.judgeIsExsit(driver,task)){
            System.out.println("-------------->访问的页面不存在");
            return;
        }
//        stopLoadPage.isEnterESC=0;

        driver.switchTo().frame(driver.findElement(By.id("commentIframe")));
        for(int i=0;i<2;i++){
            try {
                //下一页
                driver.findElement(By.className("J_shortMore")).click();
                Thread.sleep(1000);
            } catch (Exception e) {
                log.info("无下一页...");
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


}
