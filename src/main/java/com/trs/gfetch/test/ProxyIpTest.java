package com.trs.gfetch.test;

import com.trs.gfetch.entity.Task;
import com.trs.gfetch.utils.DriverUtil;
import com.trs.gfetch.utils.MQSender;
import com.trs.gfetch.utils.StringUtil;
import org.openqa.selenium.WebDriver;

public class ProxyIpTest {

    public static void main(String args[]){
        //http://ip.zdaye.com/dayProxy.html
        //上面免费代理地址
//        WebDriver driver = DriverUtil.getDriverProxy("119.187.120.118",8060);
//        driver.get("http://www.ip138.com/");
//        Task task = new Task();
//        task.setResult("我是中文");
//        MQSender.toMQ(task);
        System.out.println(StringUtil.decode("%7B%22proxyIP%22%3A%22%22%2C%22proxyPort%22%3A0%2C%22result%22%3A%22%25E6%2588%2591%25E6%2598%25AF%25E4%25B8%25AD%25E6%2596%2587%22%7D"));
    }

}
