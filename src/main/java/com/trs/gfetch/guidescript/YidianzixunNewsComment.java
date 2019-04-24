package com.trs.gfetch.guidescript;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.login.YidianzixunLogin;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class YidianzixunNewsComment extends GuideAbstract {

    public static void main(String args[]){
        Task task = new Task();
        task.setAddress("http://www.yidianzixun.com/article/0LPgJyf8");
        task.setCorpus("学习一下");
        task.setAccount("18513039252");
        task.setPassword("lilei419688");

        new YidianzixunNewsComment().start(task);
    }
    @Override
    public void toComment() throws Exception {
        driver.get(task.getAddress());
        Thread.sleep(500);
        driver.findElement(By.className("comment-input")).sendKeys(task.getCorpus());
        driver.findElement(By.className("add-comment-btn")).click();
        Thread.sleep(1000);
        WebElement cl = driver.findElement(By.className("comments-list"));
        List<WebElement> c = cl.findElements(By.tagName("li"));
        //判断是否发送成功
        isSucc(driver,c.get(0),"yidianzixun");
    }

    @Override
    public boolean login() {
        return YidianzixunLogin.login(driver,task);
    }

    @Override
    public void start(Task task) {
        //初始化一些参数
        init(task);
        run();
    }
}
