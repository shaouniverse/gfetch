package com.trs.gfetch.guidescript;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.login.QQLoginBrowser;
import com.trs.gfetch.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;

/**
 * 腾讯新闻评论
 * @2019-01-17 下午3:04:02
 * @version v1.0
 */
@Slf4j
public class QQNewsCommentBrowser extends GuideAbstract {

	public static void main(String[] args) {

		Task task = new Task();
		task.setAddress("http://bj.jjj.qq.com/a/20181114/002675_000.htm");

		task.setAccount("2598532239");
		task.setPassword("4211432a");
		task.setCorpus("果然厉害!!!");

		task.setAccount("502023904");
		task.setPassword("lilei516688");
		new QQNewsCommentBrowser().start(task);
		
	}
	@Override
	public void start(Task task) {
		//初始化一些参数
		init(task);
		//把新闻链接转化成评论链接,评论链接不变
		QQLoginBrowser.formatAddress(task);
		run();
	}

	@Override
	public boolean login() {
		return QQLoginBrowser.toLogin(driver, task);
	}

	/**
	 * 新浪新闻评论
	 */
	@Override
	public void toComment(){
		try{
			//打开转发地址
//			StopLoadPage stopLoadPage = new StopLoadPage();
			driver.get(task.getAddress());
			if(!QQLoginBrowser.judgeIsExsit(driver,task)){
				System.out.println("-------------->访问的页面不存在");
				return;
			}
//			stopLoadPage.isEnterESC=0;

			driver = driver.switchTo().frame(driver.findElement(By.id("commentIframe")));

			WebElement textArea = driver.findElement(By.id("J_Textarea"));
			textArea.clear();
			textArea.sendKeys(task.getCorpus());
			Thread.sleep(1000);
			driver.findElement(By.id("J_PostBtn")).click();
			
			Thread.sleep(3000);
			WebElement content = driver.findElement(By.xpath("//*[@id='J_ShortComment']/div[1]"));
			//判断是否发送成功
			isSucc(driver,content,"qq");
		}catch(Exception e){
			log.info("评论异常");
			task.setCode(201);
			e.printStackTrace();
		}
	}


}