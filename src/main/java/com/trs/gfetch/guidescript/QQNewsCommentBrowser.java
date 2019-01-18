package com.trs.gfetch.guidescript;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.login.QQLoginBrowser;
import com.trs.gfetch.guidescript.login.SinaLoginBrowser;
import com.trs.gfetch.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 腾讯新闻评论
 * @2019-01-17 下午3:04:02
 * @version v1.0
 */
@Slf4j
public class QQNewsCommentBrowser extends GuideAbstract {

	public static void main(String[] args) {

		Task task = new Task();
		task.setAddress("http://bj.jjj.qq.com/a/20181114/002675.htm");

		task.setNick("2598532239");
		task.setPassword("4211432a");
		task.setCorpus("果然厉害!!!");

		task.setNick("502023904");
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
	public void run() {
		WebDriver driver = DriverUtil.getDriver();
		try {
			//登录
			boolean suc = QQLoginBrowser.toLogin(driver, task);
			if(!suc){
				isSuccess(task);
			}else{
				//打开转发地址
				StopLoadPage stopLoadPage = new StopLoadPage();
				driver.get(task.getAddress());
				stopLoadPage.isEnterESC=0;

				//去评论并判断是否成功
				toComment(driver);
			}
		} catch (Exception e) {
			task.setCode(201);
			e.printStackTrace();
		} finally {
			DriverUtil.quit(driver);
			MQSender.toMQ(task);
			log.info("任务结束");
		}
	}

	/**
	 * 新浪新闻评论
	 */
	public void toComment(WebDriver driver) {
		try{

//			driver = driver.switchTo().parentFrame();
			driver = driver.switchTo().frame(driver.findElement(By.id("commentIframe")));

			WebElement textArea = driver.findElement(By.id("J_Textarea"));
			textArea.clear();
			textArea.sendKeys(task.getCorpus());
			Thread.sleep(1000);
			driver.findElement(By.id("J_PostBtn")).click();
			
			Thread.sleep(3000);
			WebElement content = driver.findElement(By.xpath("//*[@id='J_ShortComment']/div[1]"));
			//根据语料 判断是否成功
			String tt = task.getCorpus().length() > 1 ? task.getCorpus().substring(1) : task.getCorpus();
			String cor = tt.length() > 6 ? tt.substring(0, 6) : tt;
			System.out.println("text==" + content.getText());
			System.out.println("corpus==" + cor);
			System.out.println("contains==" + content.getText().contains(cor));
			if (content.getText().contains(cor)) {
				System.out.println("截图发帖成功!");
				String picUri = getPic(driver, content);
				task.setScreenshotData(Pic2Binary.getImageBinary(picUri));
				task.setCode(200);
				task.setResult("发帖成功!");
			} else {
				System.out.println("截图发帖失败!");
				task.setCode(401);
				task.setResult("截图发帖失败!");
			}
		}catch(Exception e){
			log.info("评论异常");
			task.setCode(201);
			e.printStackTrace();
		}
	}

	/**
	 * 获取评论图片
	 * @param driver
	 * @throws IOException 
	 */
	private static String getPic(WebDriver driver, WebElement comment) {
		String picName = null;
		try {
			File screenshotAs = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

			BufferedImage bufferedImage = ImageIO.read(screenshotAs);

			Point point = comment.getLocation();

			int width = comment.getSize().getWidth();
			int height = comment.getSize().getHeight() + 100;
			if(width<=0) width=300;

			BufferedImage subimage = bufferedImage.getSubimage(point.getX(), point.getY()+50, width, height);

			ImageIO.write(subimage, "png", screenshotAs);

			picName = getPicName("qq");
			File file = new File(picName);
			FileUtils.copyFile(screenshotAs, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return picName;
	}



}