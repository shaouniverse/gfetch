package com.trs.gfetch.guidescript;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.login.IfengLoginBrowser;
import com.trs.gfetch.guidescript.login.Judge404;
import com.trs.gfetch.utils.DriverUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

@Slf4j
public class IfengNewsCommentDigg extends GuideAbstract {

    public static void main(String args[]){
        Task task = new Task();
        task.setAccount("13042079465");
        task.setPassword("qqqwww");
        task.setDiggContent("在列强环伺的中东地区，以色列愈战愈勇、勇战愈强");
        task.setAddress("https://history.ifeng.com/c/7m52Sgtlzkm");

        new IfengNewsCommentDigg().start(task);
    }

    @Override
    public void toComment() {
        driver.get(task.getAddress());
        if(!Judge404.judgeIsExsit(driver,task)){
            System.out.println("-------------->访问的页面不存在");
            return;
        }
        //非评论页点击到评论页
        IfengLoginBrowser.toCommentAddress(driver,task);
        //找到评论区
        int flag = 0;
        for(int i=0;i<3 && flag==0;i++){
            try {
                Thread.sleep(1500);
                List<WebElement> list = driver.findElements(By.className("mod-articleCommentList"));
                for(WebElement element: list){
                    String text = element.getText();
                    if(text.contains(task.getDiggContent())){
                        DriverUtil.moveMouseToElement(driver,element);
                        Thread.sleep(1000);
                        element.findElement(By.className("w-rep-num")).click();
                        flag = 1;
                        break;
                    }
                }
                if(flag == 0) driver.findElement(By.className("js_nextPage")).click();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if(flag==1){
            task.setCode(200);
            task.setResult("成功");
        }else{
            task.setCode(502);
            task.setResult("未找到所在评论");
        }

    }

    @Override
    public boolean login() {
        return IfengLoginBrowser.login(driver,task);
    }

    @Override
    public void start(Task task) {
        //初始化一些参数
        init(task);
        run();
    }
}
