package com.trs.gfetch.common;

import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.*;
import com.trs.gfetch.guidescript.center.CctvNewsComment;
import com.trs.gfetch.guidescript.center.PeopleNewsComment;
import com.trs.gfetch.guidescript.center.XinhuanetNewsComment;
import lombok.extern.slf4j.Slf4j;

/**
 * 引导脚本调度
 */
@Slf4j
public class GuideDispatch {
    //准备运行的脚本
    static{
        //新浪
        GuideAbstract.handlerMap.put("weibo.sina.repost",new SinaWeiboPostBrowser());
        GuideAbstract.handlerMap.put("news.sina.comment",new SinaNewsCommentBrowser());
        GuideAbstract.handlerMap.put("news.sina.digg",new SinaNewsCommentDigg());
        //腾讯
        GuideAbstract.handlerMap.put("news.qq.comment",new QQNewsCommentBrowser());
        GuideAbstract.handlerMap.put("news.qq.digg",new QQNewsCommentDigg());
        //搜狐
        GuideAbstract.handlerMap.put("news.sohu.comment",new SohuNewsCommentBrowser());
        GuideAbstract.handlerMap.put("news.sohu.digg",new SohuNewsCommentDigg());
        //凤凰
        GuideAbstract.handlerMap.put("news.ifeng.comment",new IfengNewsCommentBrowser());
        GuideAbstract.handlerMap.put("news.ifeng.digg",new IfengNewsCommentDigg());
        //网易
        GuideAbstract.handlerMap.put("news.163.comment",new Wy163NewsCommentBrowser());
        GuideAbstract.handlerMap.put("news.163.digg",new Wy163NewsCommentDigg());
        //人民网
        GuideAbstract.handlerMap.put("news.people.comment",new PeopleNewsComment());
        //新华网
        GuideAbstract.handlerMap.put("news.xinhua.comment",new XinhuanetNewsComment());
        //央视网
        GuideAbstract.handlerMap.put("news.cctv.comment",new CctvNewsComment());
    }

    public static void main(String[] args) throws Exception{
        Task task = new Task();
        {
            task.setAddress("https://weibo.com/2093492691/Hcum3yZO2?ref=home&rid=9_0_8_4727147237745368504_0_0_0");
            task.setCorpus("学习了");
            task.setAccount("lilei1929@163.com");
            task.setPassword("lilei419688..");
            task.setType("weibo.sina.repost");

            new GuideDispatch().guideDisp(task);
        }

        {
            Thread.sleep(1000*5);
            task.setAddress("http://news.sina.com.cn/o/2018-05-25/doc-ihcaqueu0706189.shtml");
            task.setCorpus("什么个情况");
            task.setAccount("lilei1929@163.com");
            task.setPassword("lilei419688..");
            task.setType("news.sina.comment");

            new GuideDispatch().guideDisp(task);
        }
    }

    /**
     * 调度使用脚本
     * @param task
     * 策略模式
     * jdk1.8使用Lambda遍历map
     */
    public void guideDisp(Task task){
        log.info("script:"+task.getType());
        GuideAbstract.handlerMap.forEach((k,v)->{
            if(k.equals(task.getType())){
                v.start(task);
            }
        });
    }

}
