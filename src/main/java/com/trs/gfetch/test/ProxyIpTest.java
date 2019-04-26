package com.trs.gfetch.test;

import com.trs.gfetch.utils.DriverUtil;
import org.openqa.selenium.WebDriver;

public class ProxyIpTest {

    public static void main(String args[]){
        //http://ip.zdaye.com/dayProxy.html
        //上面免费代理地址
        WebDriver driver = DriverUtil.getDriverProxy("119.187.120.118",8060);
        driver.get("http://www.ip138.com/");
    }

}
