package com.trs.gfetch.guidescript.center;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class XinhuanetNewsComment extends GuideAbstract {

    public static void main(String args[]){
        Task task = new Task();
        task.setAddress("http://www.xinhuanet.com/world/2019-02/12/c_1124101668.htm");
        task.setCorpus("一定要好好治理农村这种现象");
        task.setAccount("18513039252");
        task.setPassword("lilei419688");

        new XinhuanetNewsComment().start(task);
    }

    @Override
    public void toComment() throws Exception {
        driver.get(task.getAddress());
        WebElement textArea = driver.findElement(By.className("textplaceholder-tip"));
        textArea.click();
        Thread.sleep(500);
        WebElement textArea2 = driver.findElement(By.xpath("//*[@id=\"da-comment\"]/div[3]/div/div/form/div[1]/div[2]/div[2]/textarea"));
        textArea2.sendKeys(task.getCorpus());
        Thread.sleep(500);
        driver.findElement(By.className("submit")).click();

        task.setCode(200);
        task.setResult("发表成功");
    }

    @Override
    public boolean login() {
        try {
            driver.get("http://user.news.cn/sso/login");
            WebElement user = driver.findElement(By.id("loginname"));
            user.click();
            user.sendKeys(task.getAccount());
            Thread.sleep(500);
            WebElement password = driver.findElement(By.id("password"));
            password.click();
            password.sendKeys(task.getPassword());
            Thread.sleep(500);

            WebElement loginBtn = driver.findElement(By.id("btn-login"));
            loginBtn.click();

            Thread.sleep(500);
            if(driver.getCurrentUrl().contains("login")){
                task.setCode(104);
                task.setResult("登录失败");
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void start(Task task) {
        //初始化一些参数
        init(task);
        run();
    }
}
