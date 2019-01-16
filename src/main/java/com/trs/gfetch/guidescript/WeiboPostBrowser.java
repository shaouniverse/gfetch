package com.trs.gfetch.guidescript;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.guidescript.sina.LoginBrowser;
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
public class WeiboPostBrowser extends GuideAbstract {

	public WeiboPostBrowser(Task task){
		this.task = task;
		start();
		formatAddress();
	}

	public static void main(String[] args) {
		Task task = new Task();
		task.setAddress("https://weibo.com/2803301701/HbUznf0gf?ref=home&rid=0_131072_8_1413025514326955505_0_0_0&type=comment#_rnd1547452436739");
		task.setCorpus("就应该这样");
		task.setNick("lilei1929@163.com");
		task.setPassword("lilei419688..");
//		task.setNick("15558480767");
//		task.setPassword("a123456a");
//		task.setNick("13426195063");
//		task.setPassword("haotianwen1985");
		new WeiboPostBrowser(task).run();
	}

	/**
	 * 登录打开地址
	 */
	@Override
	public void run(){
		WebDriver driver = DriverUtil.getDriver();
		try {
			//登录
			String suc = LoginBrowser.toLogin(driver, task, 0);
			if(!suc.equals("suc")){
				task.setCode(202);//登录失败
				task.setResult("登录失败");
				isSuccess(task, suc);
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
			MQSender.toMQ(task);
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
		boolean flag = juiJietu(driver,commentDiv,task.getCorpus());
		if(flag){
			log.info("截图发帖成功");
			String picName = getPic(task,driver,commentDiv);
			task.setScreenshotData(Pic2Binary.getImageBinary(picName));
			task.setCode(200);
			task.setResult("截图发帖成功");
		}else{
			log.info("截图发帖失败!");
			task.setCode(200);
			task.setResult("匹配文字失败");
		}
	}

	/**
	 * 根据语料判断是否发送成功
	 * @param driver
	 * @param commentDiv
	 * @param corpus
	 * @return
	 */
	public static boolean juiJietu(WebDriver driver,WebElement commentDiv,String corpus){
		WebElement nickE = driver.findElement(By.xpath("/html/body/div[1]/div/div[1]/div/div/div[3]/div[1]/ul/li[5]/a/em[2]"));
		String nick = nickE.getText().trim();
		if(commentDiv.getText().trim().contains(nick)){
			return true;
		}
		String tt = corpus.length()>1?corpus.substring(1):corpus;
		String cor = tt.length()>6?tt.substring(0, 6):tt;
		System.out.println("text=="+commentDiv.getText());
		System.out.println("corpus=="+cor);
		System.out.println("contains=="+commentDiv.getText().contains(cor));
		if(commentDiv.getText().trim().contains(cor)) return true;
		return false;
	}
	/**
	 * 获取评论图片
	 * @param driver
	 * @throws IOException
	 */
	public static String getPic(Task task,WebDriver driver,WebElement comment) {
		driver.manage().window().maximize();
		String picName = null;
		try {
			//让整个页面截图
			File screenshotAs = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

			BufferedImage bufferedImage = ImageIO.read(screenshotAs);

			//获取页面上元素的位置
			Point point = comment.getLocation();

			int width = comment.getSize().getWidth();
			int height = comment.getSize().getHeight();
			log.info("width=="+width+"  height=="+height);
			log.info("x=="+point.getX()+"  y=="+point.getY());
			//裁剪整个页面的截图，以获得元素的屏幕截图
			BufferedImage subimage = bufferedImage.getSubimage(point.getX(), point.getY(), width, height);
			ImageIO.write(subimage, "png", screenshotAs);

			picName = getPicName("weibo");
			//将元素截图复制到磁盘
			File file = new File(picName);
			FileUtils.copyFile(screenshotAs, file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return picName;
	}
	/**
	 * 找到转发的链接
	 * @return
	 */
	@Override
	public String formatAddress(){
		String address = task.getAddress();
		if(address!=null && address.contains("?"))
			address = address.substring(0, address.indexOf("?"));
		address += "?type=repost";
		task.setAddress(address);
		return address;
	}

}
