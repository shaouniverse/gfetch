package com.trs.gfetch.guidescript;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.login.SohuLoginBrowser;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.Point;
import org.openqa.selenium.interactions.Actions;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

@Slf4j
public class SohuNewsCommentDigg extends GuideAbstract {

    public static void main(String args[]){
        Task task = new Task();
        task.setAddress("http://www.sohu.com/a/290910910_119038?code=55e53ae603a03f65c59ec67485af8e29&_f=index_chan08cpc_0");
        task.setAccount("15626027805");
        task.setPassword("mm949811");
        task.setDiggContent("持习主席的讲话，居安思危");

        new SohuNewsCommentDigg().start(task);
    }

    @Override
    public void toComment() throws Exception {
        driver.navigate().refresh();
        Thread.sleep(1000 * 1);
        for(int i=0;i<3;i++){
            try {
                //获得前3页评论
                driver.findElement(By.className("comment-more-clone")).click();
                Thread.sleep(1000 * 2);
            } catch (Exception e) {
                log.info("未找到点击评论的地方");
                break;
            }
        }
        //找到div,循环子div,判断
        int flag = 0;
        List<WebElement> maindivlist = driver.findElements(By.className("c-comment-main"));
        for(int i=0;i<maindivlist.size() && flag==0;i++){
            List<WebElement> list = maindivlist.get(i).findElements(By.tagName("div"));
            for(WebElement element : list){
                if(element.getText().contains(task.getDiggContent())){
                    Thread.sleep(1000);
                    //找到元素位置,并滚动鼠标到该位置
                    Point point = element.getLocation();
                    int y = point.getY();
                    JavascriptExecutor web= (JavascriptExecutor)driver;
                    String js = String.format("window.scroll(0, %s)", y);
                    web.executeScript(js);
                    Thread.sleep(1000);
                    //点击
                    WebElement digg = element.findElement(By.className("c-thumb"));
                    digg.click();
                    flag = 1;
                    break;
                }
            }
        }

        if(flag==1){
            task.setCode(200);
        }else{
            task.setCode(502);
            task.setResult("未找到所在评论");
        }
    }

    @Override
    public boolean login() {
        return SohuLoginBrowser.login(driver,task);
    }

    @Override
    public void start(Task task) {
        //初始化一些参数
        init(task);
        run();
    }
}
