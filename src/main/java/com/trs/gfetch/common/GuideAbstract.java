package com.trs.gfetch.common;

import com.trs.gfetch.entity.Task;
import com.trs.gfetch.utils.FileUtil;
import com.trs.gfetch.utils.MQSender;
import com.trs.gfetch.utils.Pic2Binary;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.filters.WebdavFixFilter;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
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
	public abstract void run();//执行

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
	 * 获得截图的地址--根据新闻页查找评论页
	 */
	public static String getPicName(String name){
		String picUri = picName+name+".png";
		return picUri;
	}

	/**
	 *初始化一些信息
	 */
	public void init(Task task){
		//先清空存放截图的文件夹
		FileUtil.deleteDirectoryFiles();
		//保证只返回一次结果
		isSend = true;
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
	 * 获取评论图片--isSucc方法用
	 * @param driver
	 */
	private static String getPic(WebDriver driver, WebElement comment,String name) {
		String picName = null;
		try {
			File screenshotAs = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			BufferedImage bufferedImage = ImageIO.read(screenshotAs);
			Point point = comment.getLocation();
			int width = comment.getSize().getWidth();
			int height = comment.getSize().getHeight() + 100;
			if(width<=0) width=300;

			BufferedImage subimage = bufferedImage.getSubimage(point.getX(), point.getY()+25, width, height);
			ImageIO.write(subimage, "png", screenshotAs);

			picName = getPicName(name);
			File file = new File(picName);
			FileUtils.copyFile(screenshotAs, file);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("截图失败!");
		}
		return picName;
	}

}
