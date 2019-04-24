package com.trs.gfetch.guidescript.login;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SohuLoginBrowser {

    public static boolean login(WebDriver driver, Task task) {
        try {
            //https登录不能成功
            String address = task.getAddress().replace("https:", "http:");
            driver.get(address);
            //页面不存在直接返回
            if(!judgeIsExsit(driver,task)) return false;

            driver.manage().window().maximize();
            Thread.sleep(1000);
            driver.findElement(By.className("login-sohu")).click();
            driver.findElement(By.xpath("/html/body/div[6]/div[1]/ul/li[1]")).click();
            driver.findElement(By.xpath("/html/body/div[6]/div[3]/ul/li[1]/input")).sendKeys(task.getAccount());
            driver.findElement(By.xpath("/html/body/div[6]/div[3]/ul/li[2]/input")).sendKeys(task.getPassword());
            driver.findElement(By.xpath("/html/body/div[6]/div[3]/div[2]/input")).click();
            Thread.sleep(2000);
            //上面输入并提及在账号密码,下面错误信息
            WebElement error = driver.findElement(By.className("err-info"));
            if(!error.isDisplayed()) return true;
            //错误信息处理
            String errorInfo = error.getText();
            if(StringUtils.isNotBlank(errorInfo)){
                if(errorInfo.contains("密码")){
                    task.setCode(101);
                    task.setResult("用户名或密码错误");
                    return false;
                }else if(errorInfo.contains("验证码")){
                    return vericode(driver,task);
                }else{
                    task.setCode(501);
                    task.setResult("错误:"+errorInfo);
                    return false;
                }
            }
        } catch (Exception e) {
            task.setCode(103);
            task.setResult("登录报错");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 需要验证码,判断三次验证码
     * @return
     */
    public static boolean vericode(WebDriver driver,Task task){
        int flag = 0;
        try {
            for(int i=0;i<3;i++){
                WebElement codeEle = driver.findElement(By.xpath("/html/body/div[5]/div[3]/ul/li[3]/img"));
                String code = GuideAbstract.getVerificationCode(driver,codeEle,"sohu","3040");
                driver.findElement(By.xpath("/html/body/div[5]/div[3]/ul/li[3]/input")).sendKeys(code);
                driver.findElement(By.xpath("/html/body/div[5]/div[3]/div[2]/input")).click();
                Thread.sleep(1000);
                String errorText = driver.findElement(By.className("err-info")).getText();
                if(StringUtils.isNotBlank(errorText)){
                    if(errorText.contains("验证码")){
                        continue;
                    }else{
                        flag = 1;
                        break;
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(flag==0){
            task.setCode(106);
            task.setResult("验证码验证失败");
            return false;
        }
        WebElement loginWin = driver.findElement(By.xpath("/html/body/div[4]"));
        if(loginWin.isDisplayed()){
            task.setCode(102);
            task.setResult("账号需认证");
            return false;
        }
        return true;
    }

    /**
     * 判断页面是否404
     * @param driver
     * @param task
     * @return
     */
    public static boolean judgeIsExsit(WebDriver driver,Task task){
        if(driver.getTitle().contains("404")){
            task.setCode(404);
            task.setResult("访问的页面不存在");
            return false;
        }
        return true;
    }
}
