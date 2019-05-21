package com.trs.gfetch.guidescript.center;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * 澎湃新闻评论
 */
@Slf4j
public class ThepaperNewsComment extends GuideAbstract {

    public static void main(String args[]){
        Task task = new Task();
        task.setAccount("18513039252");
        task.setPassword("lilei419688");
        task.setCorpus("依法处理,围观下");
        task.setAddress("https://www.thepaper.cn/newsDetail_forward_2990089");

        new ThepaperNewsComment().start(task);
    }

    @Override
    public void toComment() throws Exception {
        Thread.sleep(1000);
        WebElement carea = driver.findElement(By.id("commText"));
        carea.clear();carea.sendKeys(task.getCorpus());

        driver.findElement(By.className("aqw_pub")).click();
        task.setCode(200);
        task.setResult("发表成功");
    }

    @Override
    public boolean login() {
        try {
            driver.get(task.getAddress());
            WebElement loginDiv = driver.findElement(By.xpath("//*[@id=\"loginManage\"]/div/a[3]"));
            loginDiv.click();
            Thread.sleep(500);
            WebElement user = driver.findElement(By.id("lg_wds_name"));
            user.click();user.sendKeys(task.getAccount());
            WebElement pwd = driver.findElement(By.id("lg_wds_pwd"));
            pwd.click();pwd.sendKeys(task.getPassword());

            return loginCode();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean loginCode(){
        for(int i=0;i<5;i++){
            WebElement picCode = driver.findElement(By.id("annexCode2"));
            String code = getVerificationCode(driver,picCode,"thepaper",1007);
            WebElement codeInput = driver.findElement(By.id("lg_wds_dynCode"));
            codeInput.clear();codeInput.sendKeys(code);

            driver.findElement(By.className("new_lg_bt")).click();

            try {
                Thread.sleep(1500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                WebElement errorMsg = driver.findElement(By.id("login_msg"));
                if(errorMsg.getText().contains("图片")){
                    task.setCode(106);
                    task.setResult("验证码错误");
                    continue;
                }else if(errorMsg.getText().contains("密码")){
                    task.setCode(101);
                    task.setResult("用户名或密码错误");
                    break;
                }
            } catch (Exception e) {
                log.info("登录成功");
            }
            return true;
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
