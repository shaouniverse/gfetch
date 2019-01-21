package com.trs.gfetch.guidescript;

import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.login.SinaLoginBrowser;
import com.trs.gfetch.utils.DriverUtil;
import com.trs.gfetch.utils.StopLoadPage;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * 新浪新闻评论
 */
@Slf4j
public class SinaNewsCommentBrowser extends GuideAbstract {

    public static void main(String args[]){
        Task task = new Task();
        task.setAddress("http://news.sina.com.cn/o/2018-05-25/doc-ihcaqueu0706189.shtml");
        task.setCorpus("听说河北也不行了");
        task.setAccount("lilei1929@163.com");
        task.setPassword("lilei419688..");
        new SinaNewsCommentBrowser().start(task);
    }

    @Override
    public void start(Task task) {
        //初始化一些参数
        init(task);
        run();
    }

    @Override
    public void run() {
        WebDriver driver = DriverUtil.getDriver();
        try {
            //登录
            boolean suc = SinaLoginBrowser.toLogin(driver, task, 0);
            if(!suc) toSend(task);
            else toComment(driver);
        } catch (Exception e) {
            e.printStackTrace();
            task.setCode(201);
            task.setResult("评论报错失败");
        } finally {
            DriverUtil.quit(driver);
            toSend(task);
            log.info("任务结束");
        }
    }

    public void toComment(WebDriver driver) throws Exception{
        formatAddress(driver);
        //打开转发地址
        StopLoadPage stopLoadPage = new StopLoadPage();
        driver.get(task.getAddress());
        stopLoadPage.isEnterESC=0;
        Thread.sleep(1500);
        //输入预料
        WebElement commentArea = driver.findElement(By.xpath("//*[@id='SI_Wrap']/div[1]/div[1]/div/div[1]/div[2]/div[1]/div/textarea"));
        commentArea.clear();
        commentArea.sendKeys(task.getCorpus());
        Thread.sleep(1500);
        //提交按钮
        WebElement publicComment=null;
        try {
            publicComment = driver.findElement(By.tagName("发布"));
        } catch (Exception e) {
            publicComment = driver.findElement(By.xpath("//*[@id='bottom_sina_comment']/div[1]/div[3]/div[2]/a[1]"));
        }
        publicComment.click();

        WebElement commentDiv = driver.findElement(By.xpath("//*[@id='SI_Wrap']/div[1]/div[1]/div/div[2]/div[2]/div[3]/div[1]"));
        isSucc(driver,commentDiv,"sina");

    }

    /**
     * 新闻页转化成评论页
     * @return
     */
    public String formatAddress(WebDriver driver){
        //是评论页,直接返回
        if(task.getAddress().contains("comment5.news.sina")) return task.getAddress();

        WebClient webClient = new WebClient();
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setTimeout(30000);

        HtmlPage htmlPage = null;
        String address = null;
        try {
            htmlPage = webClient.getPage(task.getAddress());
            String result = htmlPage.asXml();
            if(task.getAddress().contains("sports")){
                result = result.substring(result.indexOf("PAGEDATA")).replace("=", "");
                result = result.substring(0, result.indexOf("</script>"));
                String channel = result.substring(result.indexOf("channel")+8,result.indexOf("newsid")).replace("'", "").replace(",", "").trim();
                String newidPre = result.substring(result.indexOf("newsid"));
                String newsIds = newidPre.substring(newidPre.indexOf("conmos-")+7,newidPre.indexOf(",")).replace("'", "").replace(",", "").trim();

                address = "http://comment5.news.sina.com.cn/comment/skin/default.html?channel="+channel+"&newsid=comos-"+newsIds;
            }else if(result.indexOf("SINA_TEXT_PAGE_INFO")>-1){
                result = result.substring(result.indexOf("SINA_TEXT_PAGE_INFO")).replace("=", "");
                result = result.substring(0, result.indexOf("</script>"));
                String channel = result.substring(result.indexOf("channel")+8,result.indexOf("newsid")).replace("'", "").replace(",", "").trim();
                String newidPre = result.substring(result.indexOf("newsid"));

                String newsIds = newidPre.substring(newidPre.indexOf("newsid")+7,newidPre.indexOf(",")).replace("'", "").replace(",", "").trim();
                address = "http://comment5.news.sina.com.cn/comment/skin/default.html?channel="+channel+"&newsid="+newsIds;
            }else if(result.indexOf("ARTICLE_DATA")>-1){
                result = result.substring(result.indexOf("ARTICLE_DATA")+12).replace("=", "");
                result = result.substring(0, result.indexOf("</script>"));

                String cn = result.substring(result.indexOf("channel")+8);
                String channel = cn.substring(0,cn.indexOf(",")-1).replace("'", "").trim();
                String cs = result.substring(result.indexOf("newsid")+7);
                String newsIds = cs.substring(0,cs.indexOf(",")-1).replace("'", "").trim();
                address = "http://comment5.news.sina.com.cn/comment/skin/default.html?channel="+channel+"&newsid="+newsIds;
            }else{
                result = result.substring(result.indexOf("DFZ.CFG"));
                result = result.substring(result.indexOf("{"), result.indexOf("}")+1);
                JSONObject jsStr = JSONObject.parseObject(result);
                String channel = jsStr.getString("channel");
                String newsIds = task.getAddress().substring(0, task.getAddress().indexOf("htm"));
                newsIds = newsIds.substring(newsIds.lastIndexOf("/")+1,newsIds.lastIndexOf("."));
                address = "http://comment5.news.sina.com.cn/comment/skin/default.html?channel="+channel+"&newsid="+newsIds;
            }
        }  catch (Exception e1) {
            address = null;
            e1.printStackTrace();
        }
        if(null == address){
            address = getCommentAddress(driver);
        }
        return address;
    }

    /**
     * 获取评论地址
     * @param driver
     * @return
     */
    private String getCommentAddress(WebDriver driver){
        try {
            driver.get(task.getAddress());
            Thread.sleep(2000);
            String pageSource = driver.getPageSource();

            int cnindex = pageSource.indexOf("channel: '");
            String t1 = pageSource.substring(cnindex);
            int cnindex_1 = t1.indexOf(",");
            String t11 = t1.substring(10, cnindex_1);
            String channel = t11.replaceAll("'", "").trim();

            int cnindex2 = pageSource.indexOf("newsid: '");
            String t2 = pageSource.substring(cnindex2);
            int cnindex_2 = t2.indexOf(",");
            String t21 = t2.substring(9, cnindex_2);
            String newsid = t21.replaceAll("'", "").trim();
            if(task.getAddress().contains("sports")){
                newsid = newsid.replace("conmos", "comos");
            }
            return "http://comment5.news.sina.com.cn/comment/skin/default.html?channel="+channel+"&newsid="+newsid;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
