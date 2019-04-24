package com.trs.gfetch.guidescript.login;

import com.trs.gfetch.entity.Task;
import com.trs.gfetch.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.*;

import java.io.IOException;
import java.util.Set;

@Slf4j
public class QQLoginBrowser {

    public static void main(String args[]){
        Task task = new Task();
        task.setAddress("http://coral.qq.com/3670454015");
        log.info(formatAddress(task));
    }

    /**
     * 登录
     * @param driver
     * @param task
     * @return
     */
    public static boolean toLogin(WebDriver driver, Task task){
        try {
            driver.get("https://graph.qq.com/oauth2.0/show?which=Login&display=pc&response_type=code&client_id=101487368" +
                    "&redirect_uri=https://pacaio.match.qq.com/qq/loginBack?surl=" + StringUtil.getURLEncoderString(task.getAddress()) +
                    "&state=5b481c68e379d");
            driver = driver.switchTo().frame(driver.findElement(By.name("ptlogin_iframe")));
            Thread.sleep(1000);
            driver.findElement(By.id("switcher_plogin")).click();

            WebElement username = driver.findElement(By.id("u"));
            username.clear();
            username.sendKeys(task.getAccount());

            WebElement password = driver.findElement(By.id("p"));
            password.clear();
            password.sendKeys(task.getPassword());

            Thread.sleep(1000);
            WebElement loginButton = driver.findElement(By.id("login_button"));
            Thread.sleep(500);
            loginButton.click();

            String error = "";
            try {
                error = driver.findElement(By.id("err_m")).getText();
            } catch (Exception e2) {}
            Thread.sleep(3000);
            String currentUrl = driver.getCurrentUrl();

            if(currentUrl.contains("cgi-bin/login?")){
                if(error.contains("密码不正确")){
                    task.setCode(101);
                    task.setResult("密码不正确");
                }else if(error.contains("网络繁忙")){
                    task.setCode(301);
                    task.setResult("网络繁忙");
                }else{
                    task.setCode(103);
                    task.setResult(error);
                }
                return false;
            }

            if(currentUrl != null && currentUrl.contains("graph.qq.com")){
                task.setCode(104);
                task.setResult("登录失败");
                return false;
            }
            if(currentUrl.contains("no_verifyimg")){
                task.setCode(102);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 应用js动态备份
     * @param driver
     * @param task
     * @return
     */
    public static boolean toLogin_bak(WebDriver driver, Task task){
        try {
            Thread.sleep(5000);
//            {
//                //动态设置iframe的高度
//                JavascriptExecutor jj= (JavascriptExecutor)driver;
//                jj.executeScript("document.getElementById('ptlogin_iframe').height='322px'");
//            }
            driver = driver.switchTo().frame(driver.findElement(By.name("ptlogin_iframe")));

            WebElement login = driver.findElement(By.xpath("//*[@id='switcher_plogin']"));
            login.click();
            //由于隐藏看不到,用js操作
//            JavascriptExecutor web= (JavascriptExecutor)driver;
//            web.executeScript("document.getElementById('u').value='"+task.getNick()+"'");
//            Thread.sleep(1000);
//            web.executeScript("document.getElementById('p').value='"+task.getPassword()+"'");
//            Thread.sleep(1000);
//            web.executeScript("document.getElementById('login_button').click()");
//            Thread.sleep(2000);
//            if(driver.getCurrentUrl().contains("graph.qq.com")){
//                driver.switchTo().parentFrame();
//                JavascriptExecutor jj= (JavascriptExecutor)driver;
//                jj.executeScript("document.getElementById('ptlogin_iframe').height='322px'");
//                Thread.sleep(3000);
//                driver = driver.switchTo().frame(driver.findElement(By.name("ptlogin_iframe")));
//                WebElement loginButton = driver.findElement(By.id("login_button"));
//                Thread.sleep(500);
//                loginButton.click();
//            }

			WebElement username = driver.findElement(By.id("u"));
			username.clear();
			username.sendKeys(task.getAccount());

			WebElement password = driver.findElement(By.id("p"));
			password.clear();
			password.sendKeys(task.getPassword());

			Thread.sleep(1000);
			WebElement loginButton = driver.findElement(By.id("login_button"));
			Thread.sleep(500);
			loginButton.click();

            Thread.sleep(3500);
            driver.navigate().refresh();
            String error = "";
            try {
                error = driver.findElement(By.id("err_m")).getText();
            } catch (Exception e2) {}
            Thread.sleep(3000);
            String currentUrl = driver.getCurrentUrl();

            if(currentUrl.contains("cgi-bin/login?")){
                if(error.contains("密码不正确")){
                    task.setCode(101);
                    task.setResult("密码不正确");
                }else if(error.contains("网络繁忙")){
                    task.setCode(301);
                    task.setResult("网络繁忙");
                }else{
                    task.setCode(103);
                    task.setResult(error);
                }
                return false;
            }

            if(currentUrl != null && currentUrl.contains("graph.qq.com")){
                task.setCode(104);
                task.setResult("登录失败");
                return false;
            }
            if(currentUrl.contains("no_verifyimg")){
                task.setCode(102);
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获得评论链接
     * @return
     */
    public static String formatAddress(Task task){
        if(task.getAddress().contains("coral.qq")){
            return task.getAddress().substring(task.getAddress().lastIndexOf("/")+1);
        }
        String targetId = "";
        try {
            Document doc2 = Jsoup.connect(task.getAddress()).get();
            String result = doc2.toString();
            if(task.getAddress().contains("xw.qq.com")){
                result = result.substring(result.indexOf("cid")+3);
                targetId = result.substring(0,result.indexOf(",")).replaceAll(":", "").replaceAll("\"", "").trim();
            }else{
                int i1 = result.indexOf("cmt_id");
                int i2 = result.indexOf("comment_id");
                if(i1 != -1){
                    result = result.substring(result.indexOf("cmt_id"));
                    targetId = result.substring(result.indexOf("cmt_id")+6,result.indexOf(";")).replace("=", "").replaceAll("\"", "").trim();
                }else{
                    result = result.substring(result.indexOf("comment_id"));
                    targetId = result.substring(result.indexOf("comment_id")+10,result.indexOf(",")).replace(":", "").replaceAll("\"", "").trim();
                }
            }
            String address = "http://coral.qq.com/"+targetId;
            task.setAddress(address);
            System.out.println(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetId;
    }

    /**
     * 登录后获得cookie
     * @return
     */
    public static String getCookies(WebDriver driver){
        Set<Cookie> cookies = driver.manage().getCookies();
        StringBuilder sb = new StringBuilder();
        for(Cookie cookie: cookies){
            sb.append(cookie.toString().substring(0,cookie.toString().indexOf(";"))+1);
        }
        return sb.toString();
    }

    /**
     *
     */
    public static boolean judgeIsExsit(WebDriver driver,Task task){
        if(driver.getCurrentUrl().contains("404")){
            task.setCode(404);
            task.setResult("访问的页面不存在");
            return false;
        }
        return true;
    }
}
