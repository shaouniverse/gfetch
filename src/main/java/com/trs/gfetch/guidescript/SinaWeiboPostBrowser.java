package com.trs.gfetch.guidescript;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.login.SinaLoginBrowser;
import com.trs.gfetch.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * sina微博转发或同时评论 截图
 * @author lilei
 * @2018-1-14 下午3:10:04
 * @version v1.0
 */
@Slf4j
public class SinaWeiboPostBrowser extends GuideAbstract {

	public static void main(String[] args) {
		Task task = new Task();
		task.setAddress("https://weibo.com/2230913455/Hcm0yuJGy");
		task.setCorpus("哈哈,还有这样的");
		task.setAccount("lilei1929@163.com");
		task.setPassword("lilei419688..");
//		task.setPassword("a123456a");
//		task.setNick("13426195063");
//		task.setPassword("haotianwen1985");
		new SinaWeiboPostBrowser().start(task);
	}

	@Override
	public void start(Task task) {
		//初始化一些参数
		init(task);
		//把新闻链接转化成评论链接,评论链接不变
		formatAddress();
		//运行
		run();
	}

	/**
	 * 登录打开地址
	 */
	@Override
	public void run(){
		WebDriver driver = DriverUtil.getDriver();
		try {
			//登录
			boolean suc = SinaLoginBrowser.toLogin(driver, task, 0);
			if(!suc){
				toSend(task);
			}else{
				//打开转发地址
				StopLoadPage stopLoadPage = new StopLoadPage();
				driver.get(task.getAddress());
				stopLoadPage.isEnterESC=0;
				//去评论并判断是否成功
				toComment(driver);
			}
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

	/**
	 * 去评论
	 * @param driver
	 * @throws Exception
	 */
	public void toComment(WebDriver driver) throws Exception{
		//输入语料
		if(task.getCorpus().length()>0){
			//转发语料最大限制为140
			List<WebElement> writeCorpus = driver.findElements(By.xpath("//textarea[contains(@title,'转发微博内容')]"));
			if(writeCorpus.size()>0) writeCorpus.get(0).clear();
			else{
				driver.navigate().refresh();
				writeCorpus = driver.findElements(By.xpath("//textarea[contains(@title,'转发微博内容')]"));
				writeCorpus.get(0).clear();
			}
			if(task.getCorpus().length()>140) task.setCorpus(task.getCorpus().substring(0,140));
			Thread.sleep(1000);
			writeCorpus.get(0).sendKeys(task.getCorpus());
		}
		Thread.sleep(1000);
		WebElement postSubmit = driver.findElement(By.linkText("转发"));
		postSubmit.click();//提交转发
		//获得截图并判断程序
		WebElement commentDiv = null;
		try {
			Thread.sleep(4000);
			List<WebElement> list = driver.findElements(By.xpath("//div[contains(@class,'list_li S_line1 clearfix')]"));
			commentDiv = list.get(0);
		} catch (Exception e1) {
			Thread.sleep(4000);
			System.out.println("xpath未找到");
			commentDiv = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div/div[2]/div[1]/div/div/div/div/div[5]/div/div[3]/div[2]/div/div[1]"));
		}
		Thread.sleep(200);
		//根据语料 判断是否成功
		isSucc(driver,commentDiv,task.getCorpus());

	}

	/**
	 * 找到转发的链接
	 * @return
	 */
	public String formatAddress(){
		String address = task.getAddress();
		if(address!=null && address.contains("?"))
			address = address.substring(0, address.indexOf("?"));
		address += "?type=repost";
		task.setAddress(address);
		return address;
	}

}
