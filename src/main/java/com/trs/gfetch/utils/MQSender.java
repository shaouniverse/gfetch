package com.trs.gfetch.utils;

import com.alibaba.fastjson.JSON;
import com.trs.gfetch.config.PropertiesConfig;
import com.trs.gfetch.entity.Task;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 运行结果返回
 */
@Slf4j
public class MQSender {
	public static void toMQ(Task task){
		if(task.getScreenshotData()!=null && task.getScreenshotData().length()>100){
			task.setScreenshotData(task.getScreenshotData().replaceAll("\r\n", ""));
		}
		taskEncode(task);//encode结果集

		String json = JSON.toJSONString(task);
		log.info("code=="+task.getCode());
		log.info("result=="+StringUtil.decode(task.getResult()));
		Map<String, String> params = new HashMap<String,String>();
		params.put("mark", task.getServiceType());
		params.put("content", json);
		//获得返回的mq地址
		String url = "";
		try {
			PropertiesConfig propertiesConfig = (PropertiesConfig) SpringUtil.getObject("propertiesConfig");
			url = propertiesConfig.mqsenderurl;
		}catch (Exception e){
			url = "http://118.190.172.70:8088/proMQ/msg/addMsgResult.do";
		}
		System.out.println("url---->"+url);
		System.out.println("-------->发送到mq");
		try {
			System.out.println("返回json--->"+json);
			HttpDeal.post(url,params);
		} catch (Exception e) {
			try {
				System.out.println("---------->返回结果失败,再次尝试一次");
				Thread.sleep(2000);
				HttpDeal.post(url,params);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	/**
	 * 容易出错的格式化下
	 * @param task
	 */
	public static void taskEncode(Task task){
		task.setResult(StringUtil.encode(task.getResult()));
		task.setAccount(StringUtil.encode(task.getAccount()));
		task.setAddress(StringUtil.encode(task.getAddress()));
		task.setCorpus(StringUtil.encode(task.getCorpus()));
		task.setTitle(StringUtil.encode(task.getTitle()));
		task.setDiggId(StringUtil.encode(task.getDiggId()));
		task.setDiggContent(StringUtil.encode(task.getDiggContent()));
	}
}
