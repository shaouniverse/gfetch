package com.trs.gfetch.guidescript;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.login.IfengLoginBrowser;
import com.trs.gfetch.guidescript.login.Judge404;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class IfengNewsCommentBrowser extends GuideAbstract {

    public static void main(String args[]){
        Task task = new Task();
        task.setAccount("13042079465");
        task.setPassword("qqqwww");
        task.setCorpus("越来越激烈了");
        task.setAddress("https://news.ifeng.com/c/7jirSaZVrWq");
        task.setAddress("http://news.ifeng.com/c/7m83i45CHWC");

        new IfengNewsCommentBrowser().start(task);
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
        //发帖
        try {
            driver.findElement(By.xpath("//*[@id='js_cmtContainer']/div[6]/div[2]/form/div[1]/textarea"))
                    .sendKeys(task.getCorpus());
            driver.findElement(By.xpath("//*[@id='js_cmtContainer']/div[6]/div[2]/form/div[2]/a")).click();

            Thread.sleep(1500);
            if(driver.getPageSource().contains("allsite/loginmob")){
                task.setCode(102);
                task.setResult("需要手机核验");
                return ;
            }
            try {
                WebElement contentEle = driver.findElement(By.xpath("//*[@id='js_cmtContainer']/div[5]/div[2]/div[1]/div"));
                isSucc(driver,contentEle,"ifeng");
            } catch (Exception e) {
                e.printStackTrace();
                task.setCode(200);
                task.setResult("验证发帖成功与否失败,默认成功");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
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
