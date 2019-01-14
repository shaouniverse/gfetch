package com.trs.gfetch.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;

import java.util.List;

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
//		Configur config = GetProprities.myConfig;
//		String firefoxUrl = config.getProperty("firefoxurl");
//		System.out.println("firefoxUrl=="+firefoxUrl);
		{
			//定义firefox driver的获取地址
			//设置系统变量,并设置 geckodriver 的路径为系统属性值
//			System.setProperty("webdriver.firefox.bin","D:\\software\\firefox46\\firefox.exe");
//			System.setProperty("webdriver.gecko.driver","D:\\software\\firefox46\\firefox.exe");
//			return new FirefoxDriver();
		}
//		{
			System.setProperty("webdriver.chrome.driver", "target/classes/templates/chromedriver.exe");
			return new ChromeDriver();
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
	 * 获得元素返回列表,有时候第二次才能获得到?,让人很无语...
	 * @param driver
	 * @param clsName
	 * @return
	 */
	public static List<WebElement> getElementsByClassName(WebDriver driver, String clsName){
		try {
			return driver.findElements(By.className(clsName));
		} catch (Exception e) {
			return driver.findElements(By.className(clsName));
		}
	}
	/**
	 * 获得元素返回列表,有时候第二次才能获得到?,让人很无语...
	 * @param driver
	 * @param xpath
	 * @return
	 */
	public static List<WebElement> getElements(WebDriver driver, String xpath){
		try {
			return driver.findElements(By.xpath(xpath));
		} catch (Exception e) {
			return driver.findElements(By.xpath(xpath));
		}
	}
	/**
	 * 获得元素
	 * @param driver
	 * @param xpath
	 * @return
	 */
	public static WebElement getElement(WebDriver driver, String xpath){
		try {
			return driver.findElement(By.xpath(xpath));
		} catch (Exception e) {
			return driver.findElement(By.xpath(xpath));
		}
	}


}
