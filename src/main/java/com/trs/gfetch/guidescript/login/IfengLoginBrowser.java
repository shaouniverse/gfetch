package com.trs.gfetch.guidescript.login;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Iterator;
import java.util.Set;

public class IfengLoginBrowser {

    public static boolean login(WebDriver driver, Task task){

        try {
            driver.get("https://id.ifeng.com/user/login");
            Thread.sleep(1000);
            driver.findElement(By.id("userLogin_name")).sendKeys(task.getAccount());
            driver.findElement(By.id("userLogin_pwd")).sendKeys(task.getPassword());

            boolean succ = inputVcode(driver,task);
            if(!succ) return false;

            if(driver.getPageSource().contains("未绑定")){
                task.setCode(105);
                task.setResult("账号需认证");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    private static boolean inputVcode(WebDriver driver,Task task){
        for(int i=0;i<5;i++){
            try {
                //获得验证码
                WebElement imgCode = driver.findElement(By.id("code_img"));
                String code = GuideAbstract.getVerificationCode(driver,imgCode,"ifeng","3040");
                //输入验证码,并提交
                driver.findElement(By.id("userLogin_securityCode")).clear();
                driver.findElement(By.id("userLogin_securityCode")).sendKeys(code);
                driver.findElement(By.id("userLogin_btn")).click();
                Thread.sleep(1000);
                if(driver.getCurrentUrl().contains("user/login")){
                    String text = driver.findElement(By.xpath("/html/body/div[2]/div/div[1]/div/div[1]/div[6]")).getText();
                    if(StringUtils.isNotBlank(text)){
                        if(text.contains("验证码")){
                            task.setCode(106);
                            task.setResult("验证码验证失败");
                            continue;
                        }else if(text.contains("帐号")){
                            task.setCode(101);
                            task.setResult("用户名或密码错误");
                            return false;
                        }
                    }
                }else{
                    return true;
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    //切换到评论地址
    public static void toCommentAddress(WebDriver driver,Task task){
        try {
            driver.get(task.getAddress());
            Thread.sleep(1000);
            //非评论页的话,点击到评论页
            if(!task.getAddress().contains("gentie")){
                driver.findElement(By.className("text-2HpRlQLh")).click();
                switchToNewWindow(driver);
                Thread.sleep(2000);

                driver.findElement(By.xpath("//*[@id='js_cmtContainer']/div[6]/div[2]/form/div[1]/textarea"))
                        .sendKeys(task.getCorpus());
                driver.findElement(By.xpath("//*[@id='js_cmtContainer']/div[6]/div[2]/form/div[2]/a")).click();

                Thread.sleep(1500);
                if(driver.getPageSource().contains("allsite/loginmob")){
                    task.setCode(105);
                    task.setResult("需要手机核验");
                    return ;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //切换到新窗口
    public static void switchToNewWindow(WebDriver driver){
        String currentWindow = driver.getWindowHandle();
        //得到所有窗口的句柄
        Set<String> handles = driver.getWindowHandles();
        //排除当前窗口的句柄，则剩下是新窗口
        Iterator<String> it = handles.iterator();
        while(it.hasNext()){
            if(currentWindow == it.next()) continue;
            driver.switchTo().window(it.next());
        }

    }
}