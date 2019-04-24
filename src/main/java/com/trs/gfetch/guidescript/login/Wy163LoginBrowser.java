package com.trs.gfetch.guidescript.login;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.utils.RuoKuai;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class Wy163LoginBrowser {

    public static boolean toLogin(WebDriver driver, Task task){
        try {
            driver.get("https://3g.163.com/touch/login");
            Thread.sleep(2000);
            driver.switchTo().frame(1);
            //输入用户名 //输入密码
            driver.findElement(By.xpath("//*[@id='account-box']/div[2]/input")).sendKeys(task.getAccount());
            driver.findElement(By.xpath("//*[@id='login-form']/div[1]/div[3]/div[2]/input[2]")).sendKeys(task.getPassword());
            //提交
            driver.findElement(By.id("dologin")).click();
            Thread.sleep(2000);

            //存在验证码
            if(driver.getCurrentUrl().contains("login")){
                try {
                    //滑动验证码
                    boolean code = dragCode(driver);
                    if(!code){
                        task.setCode(106);
                        task.setResult("滑动验证码失败");
                        return false;
                    }
                } catch (Exception e2) {}

                try {//点击登录
                    driver.findElement(By.id("dologin")).click();
                } catch (Exception e2) {}

                Thread.sleep(3000);

                try {
                    driver.findElement(By.xpath("//div[@id='cnt-box-parent']/div[3]/div/div[2]/div[2]/a[1]")).click();
                    Thread.sleep(3000);
                } catch (Exception e) {}

                try {
                    String userMsg = driver.findElement(By.xpath("//div[@id='cnt-box2']/div/div[2]/div[2]")).getText();//继续登录
                    if(StringUtils.isNotBlank(userMsg) && userMsg.contains("手机")){
                        task.setCode(102);
                        task.setResult("建议你绑定手机");
                        return false;
                    }
                } catch (Exception e1) {
                }

                String currentUrl = driver.getCurrentUrl();
                System.out.println(currentUrl);

                if(currentUrl.contains("login")){
                    String error = driver.findElement(By.className("ferrorhead")).getText();
                    if(error.contains("密码错误")){
                        task.setCode(101);
                        task.setResult("用户名或密码错误");
                    }else{
                        task.setCode(104);
                        task.setResult("登录失败");
                    }
                    return false;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
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
                Thread.sleep(2000);
                {
                    //鼠标停留在滑动按钮上
                    WebElement onElement = driver.findElement(By.className("yidun_slider__icon"));
                    onElement.click();
                    Actions action = new Actions(driver);
                    action.moveToElement(onElement).build().perform();
                    Thread.sleep(2000);
                }

                String pageSource = driver.getPageSource();
                Document document = Jsoup.parse(pageSource);
                Elements elements = document.select("img");
                String param = "";
                //获得验证码图片
                img:for (Element element : elements) {
                    String attr = element.attr("src");
                    if(StringUtils.isNotBlank(attr) && attr.contains("necaptcha")){
                        //<img style="border-radius: 2px" class="yidun_bg-img" src="https://necaptcha.nosdn.127.net/fea0a111fd1a498c8f6c51a51e6d38bf.jpg" />
                        param = getParam(attr);
                        break img;
                    }
                }

                Actions actions = new Actions(driver);
                WebElement button = driver.findElement(By.className("yidun_slider__icon"));
                int x = GuideAbstract.getXY(Integer.parseInt(param.split(",")[0]));
                if(x>180) x = x-75;
                else if(x<60) x = x-30;
                else x = x-50;
                System.out.println("x=="+x);
                actions.dragAndDropBy(button, x, GuideAbstract.getXY(Integer.parseInt(param.split(",")[1]))).perform();//第一次

                Thread.sleep(1000);
                String errmsg = "";
                try {
                    errmsg = driver.findElement(By.xpath("//*[@id='nerror']/div[2]")).getText();
                } catch (Exception e2) {
                }
                log.error("errmsg=="+errmsg);
                if(StringUtils.isNotBlank(errmsg) && errmsg.contains("验证")){
                    System.out.println("滑动失败,继续 "+(m+1));
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
            URL url = new URL(attr);//图片地址
            HttpURLConnection openConnection = (HttpURLConnection)url.openConnection();
            openConnection.connect();
            InputStream is = openConnection.getInputStream();
            byte[] b = new byte[1024];
            int len;
            OutputStream os = new FileOutputStream(new File(GuideAbstract.getPicName("wy163")));
            while((len = is.read(b)) != -1){
                os.write(b, 0, len);
            }
            os.close();
            is.close();
            String code = RuoKuai.createByPostNew("6137", GuideAbstract.getPicName("wy163"));
            System.out.println(code);
            return code;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取评论地址
     * @param task
     * @return
     */
    public static String getCommentAddress(Task task) {
        try{
            String address = task.getAddress();
            if(address.substring(0,20).contains("comment")){
                return address;
            }
            {
                String docId = getDocId(address);
                if(!docId.equals("")){
                    task.setAddress("http://comment.tie.163.com/"+docId+".html");
                    return "http://comment.tie.163.com/"+docId+".html";
                }
            }

            String docId = address.substring(address.lastIndexOf("/")+1,address.indexOf(".html"));
            address = "http://sdk.comment.163.com/api/v1/products/a2869674571f77b5a0867c3d71db5856/threads/"+docId+"?ibc=jssdk&callback=tool10042492721813033474_"+System.currentTimeMillis()+"&_="+System.currentTimeMillis();
            URL url = new URL(address);
            HttpURLConnection openConnection = (HttpURLConnection)url.openConnection();
            openConnection.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(openConnection.getInputStream(),"utf-8"));
            StringBuffer sb = new StringBuffer();

            String line;
            while((line = br.readLine())!=null){
                sb.append(line);
            }

            JSONObject parseObject = JSON.parseObject(sb.toString().substring(sb.toString().indexOf("(")+1,sb.toString().indexOf(")")));
            String boardId = parseObject.getString("boardId");
            docId = parseObject.getString("docId");

            address = "http://comment.news.163.com/"+boardId+"/"+docId+".html";

            task.setAddress(address);
            return address;
        }catch(Exception e){
            log.info("获取评论地址错误");
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获得文章id,评论链接用
     * @param address
     * @return
     */
    public static String getDocId(String address){
        Document doc2 = null;
        try {
            doc2 = Jsoup.connect(address).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = doc2.toString();
        String docId = "";
        if(result.contains("docId")){
            result = result.substring(result.indexOf("docId")+8,result.indexOf("docId")+200);
            result = result.substring(0,result.indexOf(","));
            docId = result.trim().replaceAll("\"","");
        }
        return docId;
    }

}
