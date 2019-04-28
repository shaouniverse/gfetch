package com.trs.gfetch.guidescript;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.login.Judge404;
import com.trs.gfetch.guidescript.login.Wy163LoginBrowser;
import com.trs.gfetch.utils.DriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class Wy163NewsCommentDigg extends GuideAbstract {

    public static void main(String args[]){
        Task task = new Task();
        task.setAccount("augy00@163.com");
        task.setPassword("qg2048");
        task.setAddress("https://sports.163.com/19/0425/10/EDJQLGCF0005877V.html");
        task.setDiggContent("并未出战过发展联盟的比赛");
        task.setProxyIP("111.26.9.26");
        task.setProxyPort(80);
        new Wy163NewsCommentDigg().start(task);
    }


    @Override
    public void toComment() throws Exception {
        String commentAddress = Wy163LoginBrowser.getCommentAddress(task);
        if(commentAddress==null){
            task.setCode(402);
            task.setResult("未找到评论链接!");
            return;
        }
        driver.get(commentAddress);
        if(!Judge404.judgeIsExsit(driver,task)){
            System.out.println("-------------->访问的页面不存在");
            return;
        }
        for (int i=0;i<3;i++){
            int suc = digg();
            if(suc==1){
                break;
            }else{
                //点击下一页
                WebElement next = driver.findElement(By.xpath("//*[@id='tie-main']/div[3]/div[3]/div/ul/li[4]/span"));
                DriverUtil.click(next,driver);
                Thread.sleep(1000 * 2);
            }
        }

    }
    public int digg(){
        System.out.println("WYSupportVps");
        try{
            //在热帖中或最新帖
            List<WebElement> listhotE = driver.findElements(By.className("list-bdy"));
            for(int i=0;i<listhotE.size();i++){
                WebElement hotE = listhotE.get(i);
                List<WebElement> listHot = hotE.findElements(By.tagName("div"));
                for(WebElement element: listHot){
                    if(element.getText().contains(task.getDiggContent())){
                        DriverUtil.moveMouseToElement(driver,element);
                        Thread.sleep(1000);
                        WebElement supportBtn = element.findElement(By.xpath("./div[2]/div[3]/div/ul/li[1]/span"));
                        supportBtn.click();
                        System.out.println("text=="+supportBtn.getText());
                        if(supportBtn.getText().contains("顶")){
                            task.setCode(200);
                            return 1;
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        task.setCode(502);
        task.setResult("未找到所在评论");
        return 0;
    }
    @Override
    public boolean login() {
        return Wy163LoginBrowser.toLogin(driver,task);
    }

    @Override
    public void start(Task task) {
        //初始化一些参数
        init(task);
        run();
    }
}
