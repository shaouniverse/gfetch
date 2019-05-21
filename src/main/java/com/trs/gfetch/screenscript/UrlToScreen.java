package com.trs.gfetch.screenscript;

import com.trs.gfetch.entity.Task;
import com.trs.gfetch.utils.DriverUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

@Slf4j
public class UrlToScreen {
    public static final String screenDir = "c:\\screen\\";//截图地址
    //截图地址 不存在默认创建
    static {
        File file =new File(screenDir);
        if(!file.exists() && !file.isDirectory()){
            file.mkdirs();
        }
    }

    public static void main(String args[]) throws Exception{
        Task task = new Task();
//        task.setAddress("https://segmentfault.com/q/1010000013658145");
        task.setAddress("https://www.sina.com.cn/");
        task.setCorpus("根子在美国对华遏制政策");
        task.setId(2);
        toScreen(task);
    }

    public static void toScreen(Task task){
        WebDriver driver = DriverUtil.getDriver();
        driver.manage().window().maximize();
        try {
            log.info("----------------->");
            driver.get(task.getAddress());
            log.info("----------------->获得pageSource");

            hasKey(task,driver);

        } catch (Exception e) {
            e.printStackTrace();
        }
//        DriverUtil.quit(driver);
    }
    /**
     * 获取评论图片--isSucc方法用
     * @param driver
     */
//    private static String getPic(WebDriver driver, WebElement comment,String name) {
//        String picName = null;
//        try {
//            File screenshotAs = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
//            BufferedImage bufferedImage = ImageIO.read(screenshotAs);
//            Point point = comment.getLocation();
//            int width = comment.getSize().getWidth();
//            int height = comment.getSize().getHeight() + 100;
//            if(width<=0) width=300;
//            int x = width;
//            int y = height;
//            BufferedImage subimage = bufferedImage.getSubimage(x, y, width, height);
//            ImageIO.write(subimage, "png", screenshotAs);
//
//            picName = "c:\\screen\\"+name+".png";
//            File file = new File(picName);
//            FileUtils.copyFile(screenshotAs, file);
//        } catch (Exception e) {
//            System.err.println("--------->(y + height) is outside of Raster");
//            log.info("截图失败!");
//        }
//        return picName;
//    }

    /**
     * 截图并保存
     * @param driver
     * @param task
     */
    public static void screen(WebDriver driver,Task task,String key){
        try {
            WebElement element = driver.findElement(By.partialLinkText(key));
            Point point = element.getLocation();
            //滚动到当前位置
            int y = point.getY()-100;
            DriverUtil.scrollTop(driver,y);

            Thread.sleep(1000);
            File screenshotAs = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            BufferedImage bufferedImage = ImageIO.read(screenshotAs);
            ImageIO.write(bufferedImage, "png", screenshotAs);
            String screenPath = getPicName(task);
            File file = new File(screenPath);
            FileUtils.copyFile(screenshotAs, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获得截图的地址
     */
    public static String getPicName(Task task){
        String picUri = screenDir+task.getId()+".png";
        System.out.println("picUri------>"+picUri);
        return picUri;
    }
    /**
     * 判断首页是否有关键字
     * @param task
     * @return
     */
    public static boolean hasKey(Task task,WebDriver driver){
        if(task.getCorpus()==null){
            log.info("----------->没有匹配的关键字");
            return false;
        }
        String pageSource = driver.getPageSource();
        String keys[] = task.getCorpus().split("#");
        for(String key: keys){
            if(pageSource.contains(key)){
                screen(driver,task,key);
                return true;
            }
        }
        log.info("-----------没有匹配上关键字");
        return false;
    }

}
