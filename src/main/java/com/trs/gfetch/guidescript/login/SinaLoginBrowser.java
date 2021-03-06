package com.trs.gfetch.guidescript.login;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.utils.RuoKuai;
import com.trs.gfetch.utils.Yzm91;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SinaLoginBrowser {
	public static final String codeName = "login.png";

	public static boolean toLogin(WebDriver driver, Task task, int number){
		try {
			driver.get("https://login.sina.com.cn/signup/signin.php?entry=ent&entry=ent");

			WebElement username = driver.findElement(By.id("username"));
			username.clear();
			username.sendKeys(task.getAccount());

			WebElement password = driver.findElement(By.id("password"));
			password.clear();
			password.sendKeys(task.getPassword());

			try {
				WebElement loginButton2 = driver.findElement(By.xpath("//*[@id='remLoginName']"));
				loginButton2.click();
			} catch (Exception e) {}

			WebElement loginButton = driver.findElement(By.xpath("//*[@id='vForm']/div[2]/div/ul/li[7]/div[1]/input"));
			loginButton.click();

			Thread.sleep(4000);
			try {
				WebElement imgCode = driver.findElement(By.id("check_img"));
				if(imgCode.isDisplayed()){
					try{
						String code = getPicCode(driver, imgCode);
						System.out.println("验证码:"+code);
						WebElement codePut = driver.findElement(By.id("door"));
						codePut.clear();
						codePut.sendKeys(code);

						WebElement loginButton2 = driver.findElement(By.xpath("//*[@id='vForm']/div[2]/div/ul/li[7]/div[1]/input"));
						loginButton2.click();
						Thread.sleep(2000);
					}catch(Exception e){
						System.out.println("可能无验证码");
						System.out.println("可能无验证码");
					}
				}else{
					System.out.println("无验证码");
					System.out.println("无验证码");
				}
			}catch (Exception e){}

			String currentUrl = driver.getCurrentUrl();
			Thread.sleep(200);
			if(StringUtils.isNotBlank(currentUrl) && currentUrl.contains("login.sina.com")){
				try {
					WebElement error = driver.findElement(By.xpath("//*[@id='login_err']/span/i[2]"));
					System.out.println("error.getText()=="+error.getText());
					if(error.getText().contains("密码")){
						task.setCode(101);
						task.setResult("用户名或密码错误");
						return false;
					}
				} catch (Exception e) {}
				//验证码错误循环输入
				if(number<4){
					number++;
					return toLogin(driver,task,number);
				}
			}else if(StringUtils.isNotBlank(currentUrl) && currentUrl.contains("security.weibo.com")){
				task.setCode(102);
				task.setResult("账号被锁");
				return false;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			task.setCode(103);
			task.setResult("登录报错");
			return false;
		}
		return true;
	}
	/**
	 * 获取验证码
	 * @param driver
	 * @throws IOException
	 */
	private static String getPicCode(WebDriver driver, WebElement comment) throws IOException {

		File screenshotAs = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

		BufferedImage bufferedImage = ImageIO.read(screenshotAs);

		Point point = comment.getLocation();

		int width = comment.getSize().getWidth();
		int height = comment.getSize().getHeight();
		if(width<=0) width=300;
		if(height<=0) height=100;
		BufferedImage subimage = bufferedImage.getSubimage(point.getX(), point.getY(), width, height);

		ImageIO.write(subimage, "png", screenshotAs);

		File file = new File(GuideAbstract.picCode+codeName);
		FileUtils.copyFile(screenshotAs, file);

		String code = RuoKuai.createByPostNew("3000", GuideAbstract.picCode+codeName);

		return code;
	}

}
