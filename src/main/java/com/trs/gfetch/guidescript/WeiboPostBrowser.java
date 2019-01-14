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
import java.util.concurrent.TimeUnit;

/**
 * sina微博转发或同时评论 截图
 * @author lilei
 * @2017-11-28 下午3:10:04
 * @version v1.0
 */
@Slf4j
public class WeiboPostBrowser extends GuideAbstract {

	public static void main(String[] args) {
		Task task = new Task();
		task.setAddress("https://weibo.com/76649/Hb0sP5n96");
		task.setCorpus("继续努力");
		task.setNick("lilei1929@163.com");
		task.setPassword("lilei419688..");
//		task.setNick("15558480767");
//		task.setPassword("a123456a");
//		task.setNick("13426195063");
//		task.setPassword("haotianwen1985");
		new WeiboPostBrowser().start(task);
	}
	@Override
	public void start(Task task) {
		//先清空存放截图的文件夹
		FileUtil.deleteDirectoryFiles();
		//保证只返回一次结果
		isSend = true;
		run(task);
	}
	@Override
	public void run(Task task){
		WebDriver driver = DriverUtil.getDriver();
		try {
			//登录
			String suc = LoginBrowser.toLogin(driver, task, 0);
			if(!suc.equals("suc")){
				task.setCode(202);//登录失败
				task.setResult("登录失败");
				isSuccess(task, suc);
				return ;
			}
			//打开转发地址
			String address = formatAddress(task);
			StopLoadPage stopLoadPage = new StopLoadPage();
			driver.get(address);
			stopLoadPage.isEnterESC=0;
			//去评论并判断是否成功
			toComment(driver,task);

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
	public void toComment(WebDriver driver,Task task) throws Exception{
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
	 * 找到转发的链接
	 * @param task
	 * @return
	 */
	public String formatAddress(Task task){
		String address = task.getAddress();
		if(address!=null && address.contains("?"))
			address = address.substring(0, address.indexOf("?"));
		address += "?type=repost";
		return address;
	}

	/**
	 * 根据语料判断是否发送成功
	 * @param driver
	 * @param commentDiv
	 * @param corpus
	 * @return
	 */
	public boolean juiJietu(WebDriver driver,WebElement commentDiv,String corpus){
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
	private String getPic(Task task,WebDriver driver,WebElement comment) {
		String picName = null;
		try {
			//让整个页面截图
			File screenshotAs = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

			BufferedImage bufferedImage = ImageIO.read(screenshotAs);

			//获取页面上元素的位置
			Point point = comment.getLocation();

			int width = comment.getSize().getWidth();
			int height = comment.getSize().getHeight();

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

}
