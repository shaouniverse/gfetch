package com.trs.gfetch.guidescript;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.login.Judge404;
import com.trs.gfetch.utils.DriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SinaNewsCommentDigg extends GuideAbstract {

    public static void main(String args[]){
        Task task = new Task();
        //channel=gn&newsid=comos-hrfqziz9914444&mid=5C4696ED-B6306283-593D569F-862-8CE
        task.setDiggContent("玩命啊这是");
        task.setAddress("https://news.sina.com.cn/s/2019-05-06/doc-ihvhiqax6805935.shtml");

        new SinaNewsCommentDigg().start(task);
    }

    @Override
    public void start(Task task) {
        //初始化一些参数
        init(task);
        run();
    }

    @Override
    public void toComment() throws Exception {
        if(!Judge404.judgeIsExsitSina(driver,task)){
            System.out.println("-------------->访问的页面不存在");
            return;
        }
        driver.findElement(By.xpath("//a[contains(@href,\"comment5.news.sina\")]")).click();
        DriverUtil.switchToNewWindow(driver);
        Thread.sleep(1500);
        //滚动到底部获得第一页
        DriverUtil.scrollToButtom(driver);
        Thread.sleep(1500);
        //滚动到底部获得第二页
        DriverUtil.scrollToButtom(driver);
        Thread.sleep(1500);

        boolean flag = findElementDigg();

        if(flag){
            task.setCode(200);
            task.setResult("点赞成功");
        }else{
            task.setCode(401);
            task.setResult("点赞失败");
        }

    }
    public boolean findElementDigg(){
        WebElement big = driver.findElement(By.className("sina-comment-list-has-latest"));
        List<WebElement> list = big.findElements(By.className("cont"));
        for(WebElement element: list){
            if(element.getText().contains(task.getDiggContent())){
                DriverUtil.moveMouseToElement(driver,element);
                element.findElement(By.className("vote")).click();
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 点赞不需要登录,直接返回true
     * @return
     */
    @Override
    public boolean login() {
        return true;
    }

}
