package com.trs.gfetch.guidescript.center;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.utils.DriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CctvNewsComment extends GuideAbstract {

    public static void main(String args[]){
        Task task = new Task();
        task.setAddress("http://news.cctv.com/2019/02/13/ARTI8VRJdAih6pMqG009Ig7y190213.shtml");
        task.setAccount("18513039252");
        task.setPassword("lilei419688");
        task.setCorpus("围观一下");

        new CctvNewsComment().start(task);
    }

    @Override
    public void toComment() throws Exception {
        //字数小于300
        if(task.getCorpus().length()>300) task.setCorpus(task.getCorpus().substring(0,280)+"...");
        WebElement comment = driver.findElement(By.id("comment_content"));
        comment.click();comment.sendKeys(task.getCorpus());
        Thread.sleep(500);

        WebElement submit = driver.findElement(By.linkText("发表评论"));
        submit.click();

        task.setCode(200);
        task.setResult("发帖成功");
    }

    @Override
    public boolean login() {
        try {
            //点击登录
            driver.get(task.getAddress());

            driver.switchTo().frame("comment_iframe");

            WebElement loginBtn = driver.findElement(By.xpath("//*[@id='comment_nologin']/span[1]/span[2]/a"));
            DriverUtil.moveMouseToElement(driver,loginBtn);
            Thread.sleep(500);
            loginBtn.click();
            Thread.sleep(500);
            //登录页
            WebElement loginDiv = driver.findElement(By.className("logpop"));
            if(loginDiv.isDisplayed()){
                WebElement username = driver.findElement(By.id("comment_username"));
                username.click();
                username.sendKeys(task.getAccount());
                WebElement password = driver.findElement(By.id("comment_password"));
                password.sendKeys(task.getPassword());
                driver.findElement(By.xpath("/html/body/div/div/div[2]/div/div[2]/div[1]/div[1]/div/table/tbody/tr[7]/td/a")).click();
                Thread.sleep(1000);
            }
            return loginSuc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /** 10秒判断登录框是否还显示,显示说明登录不成功 */
    public boolean loginSuc(){
        for(int i=0;i<10;i++){
            try {
                WebElement loginDiv = driver.findElement(By.className("logpop"));
                if(loginDiv.isDisplayed()){
                    Thread.sleep(1000);
                }else{
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        task.setCode(101);
        task.setResult("用户名或密码错误");
        return false;
    }
    @Override
    public void start(Task task) {
        //初始化一些参数
        init(task);
        run();
    }
}
