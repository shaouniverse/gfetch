package com.trs.gfetch.guidescript.login;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.utils.FileUtil;
import com.trs.gfetch.utils.RuoKuai;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class ToutiaoLogin {

    public static boolean login(WebDriver driver, Task task){
        try {
            driver.get("https://sso.toutiao.com/login/");
            driver.navigate().refresh();
            driver.findElement(By.className("login-type-icon")).click();
            driver.findElement(By.id("user-name")).clear();
            driver.findElement(By.id("user-name")).sendKeys(task.getAccount());
            driver.findElement(By.id("password")).clear();
            driver.findElement(By.id("password")).sendKeys(task.getPassword());
            Thread.sleep(500);
            driver.findElement(By.id("bytedance-SubmitStatic")).click();
            Thread.sleep(1000);
            boolean code = dragCode(driver);
            if(!code){
                task.setCode(106);
                task.setResult("滑动验证码失败");
                return false;
            }

            String currentUrl = driver.getCurrentUrl();
            if(currentUrl.contains("login")){
                String msg = driver.findElement(By.id("J-Message")).getText();
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
    /**
     * 滑动验证码
     * @param driver
     * @return
     */
    public static boolean dragCode(WebDriver driver){
        boolean flag = false;
        for(int m=0;m<6;m++){
            try {
                Thread.sleep(1000 * 2);
                WebElement vdiv = driver.findElement(By.id("validate-big"));
                //获得验证码图片
                String codeAddress = vdiv.getAttribute("src");
//                String param = getParam(codeAddress);
                String param = "100,200";

                Actions actions = new Actions(driver);
                WebElement button = driver.findElement(By.className("drag-button"));

                int x = GuideAbstract.resetXY(GuideAbstract.getXY(Integer.parseInt(param.split(",")[0])));
                System.out.println("x=="+x);
                Thread.sleep(500);
                actions.dragAndDropBy(button, 200, 0).perform();//第一次

                Thread.sleep(1000);
                if(driver.findElement(By.id("verify-container")).isDisplayed()){
                    System.out.println("滑动失败,继续 "+(m+1));
                    continue;
                }else{
                    flag = true;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
            }
        }
        return flag;
    }
    /**
     * 获取验证码--坐标
     * @param attr
     * @return
     */
    private static String getParam(String attr) {
        try{
            FileUtil.addFile2Local(attr,GuideAbstract.getPicName("toutiaoCode"));
            String code = RuoKuai.createByPostNew("6137", GuideAbstract.getPicName("toutiaoCode"));
            System.out.println(code);
            return code;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
