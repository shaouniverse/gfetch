package com.trs.gfetch.common;

import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.*;
import com.trs.gfetch.guidescript.center.CctvNewsComment;
import com.trs.gfetch.guidescript.center.PeopleNewsComment;
import com.trs.gfetch.guidescript.center.ThepaperNewsComment;
import com.trs.gfetch.guidescript.center.XinhuanetNewsComment;
import com.trs.gfetch.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 引导脚本调度
 */
@Slf4j
public class GuideDispatch {
    //准备运行的脚本
    static{
        //新浪---新浪新闻点赞不需要账号
        GuideAbstract.handlerMap.put("weibo.sina.repost",new SinaWeiboPostBrowser());
        GuideAbstract.handlerMap.put("news.sina.comment",new SinaNewsCommentBrowser());
        GuideAbstract.handlerMap.put("news.sina.digg",new SinaNewsCommentDigg());
        //腾讯
        GuideAbstract.handlerMap.put("news.qq.comment",new QQNewsCommentBrowser());
        GuideAbstract.handlerMap.put("news.qq.digg",new QQNewsCommentDigg());
        //搜狐---digg right
        GuideAbstract.handlerMap.put("news.sohu.comment",new SohuNewsCommentBrowser());
        GuideAbstract.handlerMap.put("news.sohu.digg",new SohuNewsCommentDigg());
        //凤凰---digg right
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
        //澎湃网
        GuideAbstract.handlerMap.put("news.thepaper.comment",new ThepaperNewsComment());
        //今日头条
        GuideAbstract.handlerMap.put("news.toutiao.comment",new ToutiaoNewsComment());
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
    public static void guideDisp(Task task){
        taskDecode(task);
        System.out.println("script----------->"+task.getType());
        System.out.println("address----------->"+task.getAddress());
        GuideAbstract.handlerMap.forEach((k,v)->{
            if(k.equals(task.getType())){
                v.start(task);
            }
        });
    }
    /**
     * 容易出错的格式化下
     * @param task
     */
    public static void taskDecode(Task task){
        task.setResult(StringUtil.decode(task.getResult()));
        task.setAccount(StringUtil.decode(task.getAccount()));
        task.setAddress(StringUtil.decode(task.getAddress()));
        task.setCorpus(StringUtil.decode(task.getCorpus()));
        task.setTitle(StringUtil.decode(task.getTitle()));
        task.setDiggId(StringUtil.decode(task.getDiggId()));
        task.setDiggContent(StringUtil.decode(task.getDiggContent()));
    }

}
