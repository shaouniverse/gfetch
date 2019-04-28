package com.trs.gfetch.guidescript.login;

import com.trs.gfetch.entity.Task;
import org.openqa.selenium.WebDriver;

/**
 * 判断各页面是否存在
 */
public class Judge404 {

    /**
     *腾讯/头条/搜狐/iFeng/网易163--->判断页面是否存在
     */
    public static boolean judgeIsExsit(WebDriver driver,Task task){
        if(driver.getCurrentUrl().contains("404") || driver.getTitle().contains("载入出错")){
            task.setCode(404);
            task.setResult("访问的页面不存在/代理错误");
            return false;
        }
        return true;
    }
    /**
     * 新浪--->判断页面是否存在
     */
    public static boolean judgeIsExsitSina(WebDriver driver,Task task){
        if(driver.getTitle().contains("页面没有") || driver.getTitle().contains("载入出错")){
            task.setCode(404);
            task.setResult("访问的页面不存在/代理错误");
            return false;
        }
        return true;
    }
    /**
     *一点资讯--->判断页面是否存在
     */
    public static boolean judgeIsExsitYidianzhixun(WebDriver driver,Task task){
        if(driver.getCurrentUrl().equals("【一点资讯】 www.yidianzixun.com") || driver.getTitle().contains("载入出错")){
            task.setCode(404);
            task.setResult("访问的页面不存在/代理错误");
            return false;
        }
        return true;
    }

}
