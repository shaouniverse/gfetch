package com.trs.gfetch.common;

import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.WeiboPostBrowser;
import lombok.extern.slf4j.Slf4j;

/**
 * 引导脚本调度
 */
@Slf4j
public class GuideDispatch {
    //准备运行的脚本
    static{
        //新浪微博转发
        GuideAbstract.handlerMap.put("weibo.sina.repost",new WeiboPostBrowser());
        //腾讯新闻评论
        GuideAbstract.handlerMap.put("news.qq.comment",new WeiboPostBrowser());
    }

    public static void main(String[] args) {
        Task task = new Task();
        task.setAddress("https://weibo.com/2093492691/Hcum3yZO2?ref=home&rid=9_0_8_4727147237745368504_0_0_0");
        task.setCorpus("学习了");
        task.setNick("lilei1929@163.com");
        task.setPassword("lilei419688..");
        task.setType("weibo.sina.repost");

        new GuideDispatch().guideDisp(task);
    }

    /**
     * 调度使用脚本
     * @param task
     * 策略模式
     * jdk1.8使用Lambda遍历map
     */
    public void guideDisp(Task task){
        GuideAbstract.handlerMap.forEach((k,v)->{
            if(k.equals(task.getType())){
                v.start(task);
            }
        });
    }

}
