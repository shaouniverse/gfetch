package com.trs.gfetch.guidescript.center;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.utils.DriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * 人民网新闻评论
 */
public class PeopleNewsComment extends GuideAbstract {

    public static void main(String args[]){
        Task task = new Task();
        task.setAccount("18513039252");
        task.setPassword("lilei419688");
        task.setAddress("http://politics.people.com.cn/n1/2019/0201/c1001-30604199.html");
        task.setCorpus("习大大好样的");

        new PeopleNewsComment().start(task);
    }

    @Override
    public void toComment() throws Exception {
        WebElement subTextarea = driver.findElement(By.className("subTextarea"));
        subTextarea.click();
        subTextarea.sendKeys(task.getCorpus());
        Thread.sleep(1000);
        driver.findElement(By.linkText("发表")).click();
        task.setCode(200);
    }

    @Override
    public boolean login() {
        try {
            driver.get(task.getAddress());
            WebElement cdiv = driver.findElement(By.xpath("/html/body/div[5]/div[1]/div[2]/div[2]/a/img"));
            cdiv.click();
            Thread.sleep(1000 * 2);

            DriverUtil.switchToNewWindow(driver);

            WebElement loginBtn = driver.findElement(By.className("loginBtn"));
            DriverUtil.moveMouseToElement(driver,loginBtn);
            Thread.sleep(500);
            loginBtn.click();

            Thread.sleep(500);
            driver.findElement(By.id("userName")).sendKeys(task.getAccount());
            driver.findElement(By.id("passWord")).sendKeys(task.getPassword());
            Thread.sleep(500);
            driver.findElement(By.id("login_btn")).click();
        } catch (Exception e) {
            e.printStackTrace();
        }
        WebElement logindiv = driver.findElement(By.className("maskWrap"));
        if(logindiv.isDisplayed()){
            task.setCode(101);
            task.setResult("用户名或密码错误");
            return false;
        }else{
            return true;
        }

    }

    @Override
    public void start(Task task) {
        //初始化一些参数
        init(task);
        run();
    }
}
