package com.trs.gfetch.utils;

import com.alibaba.fastjson.JSON;
import com.trs.gfetch.entity.Task;
import lombok.extern.slf4j.Slf4j;

/**
 * 运行结果返回
 */
@Slf4j
public class MQSender {
	public static void toMQ(Task task){
		if(task.getScreenshotData()!=null && task.getScreenshotData().length()>100){
			task.setScreenshotData(task.getScreenshotData().replaceAll("\r\n", ""));
		}
		String json = JSON.toJSONString(task);
		log.info("code=="+task.getCode());
		log.info("result=="+task.getResult());
	}
}
