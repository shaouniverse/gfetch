package com.trs.gfetch.guidescript;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.login.IfengLoginBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class IfengNewsCommentBrowser extends GuideAbstract {

    public static void main(String args[]){
        Task task = new Task();
        task.setAccount("13042079465");
        task.setPassword("qqqwww");
        task.setCorpus("改革开放");
        task.setAddress("https://news.ifeng.com/c/7jirSaZVrWq");

        new IfengNewsCommentBrowser().start(task);
    }

    @Override
    public void toComment() {
        IfengLoginBrowser.toCommentAddress(driver,task);
        WebElement contentEle = driver.findElement(By.xpath("//*[@id='js_cmtContainer']/div[5]/div[2]/div[1]/div"));
        isSucc(driver,contentEle,"ifeng");
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
