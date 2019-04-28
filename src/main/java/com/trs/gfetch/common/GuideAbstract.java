package com.trs.gfetch.common;

import com.trs.gfetch.entity.Task;
import com.trs.gfetch.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
public abstract class GuideAbstract implements GuideFetchInterf{
	public static final String picName = "c:\\jietu\\";//截图地址
	public static final String picCode = "c:\\vcode\\";//验证码图片地址
	//成功与否,确保只返回一次,目前单线程模式,多线程需改动
	public boolean isSend = false;
	//定义集合,存放脚本,调用接口调用
	public static Map<String,GuideFetchInterf>  handlerMap = new HashMap<>();
	//存放截图/验证码的文件夹不存在默认创建
	static {
		File file =new File(picName);
		if(!file.exists() && !file.isDirectory()){
			file.mkdirs();
		}

		File fileCode =new File(picCode);
		if(!fileCode.exists() && !fileCode.isDirectory()){
			fileCode.mkdirs();
		}
	}

	public Task task;
	public WebDriver driver = null;
	public abstract void toComment() throws Exception;//执行评论/点赞
	public abstract boolean login();//执行登录
	//真正运行
	public void run() {
		driver = DriverUtil.getProxyDriver(task);
//		driver = DriverUtil.getDriver();
		driver.manage().window().maximize();
		try {
			//登录
			boolean suc = login();
			if(!suc) toSend(task);
			else toComment();
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
	 * 发送消息到队列
	 */
	public void toSend(Task task){
		if(isSend){
			isSend = false;
			MQSender.toMQ(task);
		}
	}
	/**
	 *初始化一些信息
	 */
	public void init(Task task){
		//先清空存放截图的文件夹
		FileUtil.deleteDirectoryFiles();
		//保证只返回一次结果
		isSend = true;
		task.setCode(401);
		task.setResult("默认发帖失败");
		this.task = task;
	}
	/**
	 * 判断是否发贴成功
	 * @return
	 */
	public boolean isSucc(WebDriver driver,WebElement element,String picName){
		//根据语料 判断是否成功
		String tt = task.getCorpus().length() > 1 ? task.getCorpus().substring(1) : task.getCorpus();
		String cor = tt.length() > 6 ? tt.substring(0, 6) : tt;
		System.out.println("text==" + element.getText());
		System.out.println("corpus==" + cor);
		System.out.println("contains==" + element.getText().contains(cor));
		if (element.getText().contains(cor)) {
			System.out.println("截图发帖成功!");
			String picUri = getPic(driver, element,picName);
			task.setScreenshotData(Pic2Binary.getImageBinary(picUri));
			task.setCode(200);
			task.setResult("发帖成功!");
		} else {
			System.out.println("截图发帖失败!");
			task.setCode(401);
			task.setResult("截图发帖失败!");
		}
		return element.getText().contains(cor);
	}
	/**
	 * 获得图片验证码
	 * @param element
	 * @param name --验证码名称
	 * @param code --解析验证码编码
	 * @return
	 */
	public static String getVerificationCode(WebDriver driver,WebElement element,String name,String code){
		try {

			File screenshotAs = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			BufferedImage bufferedImage = ImageIO.read(screenshotAs);
			Point point = element.getLocation();
			int width = getXY(element.getSize().getWidth());
			int height = getXY(element.getSize().getHeight());
			int x = getXY(point.getX());
			int y = getXY(point.getY());
			log.info("x:"+point.getX()+",y:"+point.getY());
			log.info("x:"+point.x+",y:"+point.y);
			BufferedImage subimage = bufferedImage.getSubimage(x, y, width, height);
			ImageIO.write(subimage, "png", screenshotAs);

			File file = new File(picCode+name+".png");
			FileUtils.copyFile(screenshotAs, file);

			String result = RuoKuai.createByPostNew(code, picCode+name+".png");
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}


	/**
	 * 获得截图的地址--根据新闻页查找评论页
	 */
	public static String getPicName(String name){
		String picUri = picName+name+".png";
		System.out.println("picUri=="+picUri);
		return picUri;
	}
	/**
	 * 获取评论图片--isSucc方法用
	 * @param driver
	 */
	private String getPic(WebDriver driver, WebElement comment,String name) {
		String picName = null;
		try {
			File screenshotAs = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			BufferedImage bufferedImage = ImageIO.read(screenshotAs);
			Point point = comment.getLocation();
			int width = getXY(comment.getSize().getWidth());
			int height = getXY(comment.getSize().getHeight()) + 100;
			if(width<=0) width=300;
			int x = getXY(point.getX());
			int y = getXY(point.getY());
			BufferedImage subimage = bufferedImage.getSubimage(x, y, width, height);
			ImageIO.write(subimage, "png", screenshotAs);

			picName = getPicName(name);
			File file = new File(picName);
			FileUtils.copyFile(screenshotAs, file);
		} catch (Exception e) {
			System.err.println("--------->(y + height) is outside of Raster");
			log.info("截图失败!");
		}
		return picName;
	}

	/**
	 * 分辨率不一样导致截图不准解决
	 */
	public static int getXY(int xy){
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		java.awt.Dimension scrnsize = toolkit.getScreenSize();
		int width = scrnsize.width;
		System.out.println ("Screen size : " + scrnsize.width + " * " + scrnsize.height);
		if(width >= 1800){
			xy = (int)(xy*1.25);
		}
		return xy;
	}

	/**
	 * 滑动验证码时,微调xy
	 * @param x
	 * @return
	 */
	public static int resetXY(int x){
		if(x>180) x = x-75;
		else if(x<60) x = x-30;
		else x = x-50;
		return x;
	}
}
