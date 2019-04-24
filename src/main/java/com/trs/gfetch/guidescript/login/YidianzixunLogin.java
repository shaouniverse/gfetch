package com.trs.gfetch.guidescript.login;

import com.trs.gfetch.entity.Task;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class YidianzixunLogin {

    public static boolean login(WebDriver driver, Task task){
        try {
            driver.get("https://mp.yidianzixun.com/");

            driver.findElement(By.className("show-login")).click();
            Thread.sleep(500);
            driver.findElement(By.name("username")).clear();
            Thread.sleep(500);
            driver.findElement(By.name("username")).sendKeys(task.getAccount());
            Thread.sleep(500);
            driver.findElement(By.name("password")).clear();
            Thread.sleep(500);
            driver.findElement(By.name("password")).sendKeys(task.getPassword());
            Thread.sleep(500);
            driver.findElement(By.xpath("//button[@type='submit']")).click();
            Thread.sleep(1000);

            String currentUrl = driver.getCurrentUrl();
            if(currentUrl.equals("https://mp.yidianzixun.com/")){
                String msg = driver.findElement(By.className("error-message")).getText();
                if(msg.contains("密码")){
                    task.setCode(101);
                    task.setResult("用户名或密码错误");
                }else{
                    task.setCode(501);
                    task.setResult("未知错误");
                }
                return false;
            }else{
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
