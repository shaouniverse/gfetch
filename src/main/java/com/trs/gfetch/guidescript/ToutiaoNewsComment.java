package com.trs.gfetch.guidescript;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.login.ToutiaoLogin;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ToutiaoNewsComment extends GuideAbstract {

    public static void main(String args[]){
        Task task = new Task();
        task.setAddress("https://www.toutiao.com/a6659167857206624779/");
        task.setCorpus("围观一下");
        task.setAccount("18311242559");
        task.setPassword("chwx62115358");

        new ToutiaoNewsComment().start(task);
    }

    @Override
    public void toComment() throws Exception {
        driver.get(task.getAddress());
        if(!ToutiaoLogin.judgeIsExsit(driver,task)) return;

        driver.findElement(By.xpath("//*[@id=\"comment\"]/div[2]/div/div[2]/div[1]/textarea")).sendKeys(task.getCorpus());
        driver.findElement(By.className("c-submit")).click();
        Thread.sleep(1000);

        try {
            WebElement commentDiv = driver.findElement(By.xpath("//*[@id=\"comment\"]/ul/li[1]"));
            isSucc(driver,commentDiv,"toutiao");
        } catch (Exception e) {
            e.printStackTrace();
            task.setCode(200);
            task.setResult("判断成功出错,默认发帖成功!");
        }

    }

    @Override
    public boolean login() {
        return ToutiaoLogin.login(driver,task);
    }

    @Override
    public void start(Task task) {
        //初始化一些参数
        init(task);
        run();
    }
}
