package com.trs.gfetch.utils;

import com.trs.gfetch.config.PropertiesConfig;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.util.Iterator;
import java.util.Set;

public class DriverUtil {

	private DriverUtil(){}
	
	public static void main(String[] args) throws Exception{
		WebDriver driver = getDriver();
		driver.get("https://blog.csdn.net/yoyocat915/article/details/81772422");
		Thread.sleep(1000*10);
		driver.quit();

	}

	/**
	 * 获得driver
	 * @return
	 */
	public static WebDriver getDriver(){
		return produceDriver();
	}

	/**
	 * 配置参数
	 * 谷歌浏览器为默认安装路径
	 * 安装包: D:\software\gfetch新版本加载浏览器
	 */
	public static WebDriver produceDriver(){
		{
			//http://www.cnblogs.com/jinxiao-pu/p/8682126.html 版本对应
			//定义firefox driver的获取地址
			//firebox版本64位：65.0.1 对应： geckodriver_0-24.exe
			try {
                PropertiesConfig propertiesConfig = (PropertiesConfig) SpringUtil.getObject("propertiesConfig");
                System.setProperty("webdriver.gecko.driver",propertiesConfig.geckodriver);
                FirefoxOptions options = new FirefoxOptions();
                System.out.println("webdriver.gecko.driver-------->"+propertiesConfig.geckodriver);
                System.out.println("propertiesConfig.firefoxurl-------->"+propertiesConfig.firefoxurl);
                options.setBinary(propertiesConfig.firefoxurl);
                return new FirefoxDriver(options);
            }catch (Exception e){
                System.out.println("----------->本地调用");
                System.setProperty("webdriver.gecko.driver","target/classes/templates/geckodriver_0-24.exe");
                FirefoxOptions options = new FirefoxOptions();
				System.out.println("------>spring未启动,直接写死的URL地址");
				System.out.println("firefoxurl-------->D:\\Program Files\\Mozilla Firefox\\firefox.exe");
				options.setBinary("D:\\Program Files\\Mozilla Firefox\\firefox.exe");
                return new FirefoxDriver(options);
			}
		}
//		{
			//chromedriver下载地址: http://chromedriver.storage.googleapis.com/index.html
			//尽量控制Chrome自动更新,或者驱动要不断升级--仅仅滑动验证码失效改用上面的火狐浏览器
//			System.setProperty("webdriver.chrome.driver", "target/classes/templates/chromedriver.exe");
//			System.setProperty("webdriver.chrome.driver", "target/classes/templates/chromedriver_2-41.exe");
//			System.setProperty("webdriver.chrome.driver", "target/classes/templates/chromedriver2-45.exe");
//			ChromeOptions options = new ChromeOptions();
//			options.setBinary("D:\\ProgramFiles\\Google Chrome/chrome.exe");
//			ChromeDriver driver = new ChromeDriver(options);
//			ChromeDriver driver = new ChromeDriver();
//
//			return driver;
//		}

	}

	/**
	 * 退出driver
	 * @param driver
	 */
	public static void quit(WebDriver driver){
		try {
			System.out.println("关闭driver");
			if(driver != null){
				driver.quit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 点击元素
	 * @param element
	 * @param driver
	 */
	public static void click(WebElement element,WebDriver driver){
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();",element);
	}

	/**
	 * 滚动到当前元素
	 * @param driver
	 * @param element
	 */
	public static void moveMouseToElement(WebDriver driver,WebElement element){
		//找到元素位置,并滚动鼠标到该位置
		Point point = element.getLocation();
		int y = point.getY();
		JavascriptExecutor web= (JavascriptExecutor)driver;
		String js = String.format("window.scroll(0, %s)", y);
		web.executeScript(js);
	}

	/**
	 * 切换到新窗口
	 */
	public static void switchToNewWindow(WebDriver driver){
		String currentWindow = driver.getWindowHandle();
		//得到所有窗口的句柄
		Set<String> handles = driver.getWindowHandles();
		//排除当前窗口的句柄，则剩下是新窗口
		Iterator<String> it = handles.iterator();
		while(it.hasNext()){
			if(currentWindow == it.next()) continue;
			driver.switchTo().window(it.next());
		}

	}

	public static void scrollTop(WebDriver driver, int y){
		String js = "var q=document.documentElement.scrollTop="+y;
		JavascriptExecutor jj= (JavascriptExecutor)driver;
		jj.executeScript(js);
	}

}
