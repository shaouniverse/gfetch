package com.trs.gfetch.fetchscript;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;

/**
 * 根据链接获得链接信息
 */
public class GetInfoByLink {

    /**
     * 针对网易app,可以获取标题后在 百度搜索
     * 浙江女厅官诸葛慧艳被查 曾任衢州市委宣传部部长 网易新闻
     * 一般情况下第一条就是
     * 还可以找到规则---news/年/月日/时 可以去源文件查
     * http://news.163.com/19/0304/19/E9ETEHU40001899N.html#f=post1603_tab_news
     *                      年/月日/时/
     *
     * 针对新浪: pc直接可评
     *
     * 腾讯: 有规则可寻 https://view.inews.qq.com/a/20190305A0ZIAM00
     * 链接可变为: https://new.qq.com/omn/20190305/20190305A0ZIAM00
     */
    @Test
    public void getTitleByLinkTest(){
        //浙江女厅官诸葛慧艳被查 曾任衢州市委宣传部部长
//        getTitleByLink("https://c.m.163.com/news/a/E9ETEHU40001899N.html?spss=newsapp");
        //7旬大爷在外漂泊14年，老家拆迁后想要回家，儿子：想都别想！
//        getTitleByLink("https://c.m.163.com/news/a/E9EU30UI0523HM56.html?spss=newsapp");
        //号外|原云南红塔集团董事长褚时健去世
        getTitleByLink("https://c.m.163.com/news/a/E9GU410F00258152.html?spss=newsapp");
    }

    /**
     * 根据链接获得链接标题
     * @param url
     * @return
     */
    public static String getTitleByLink(String url){
        try {
            Document doc =Jsoup.connect(url).get();
            System.out.println("----------->"+doc.title());
            return doc.title();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
