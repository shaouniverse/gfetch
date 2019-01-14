package com.trs.gfetch.common;

import com.trs.gfetch.entity.Task;
import com.trs.gfetch.utils.MQSender;

import java.io.File;

public abstract class GuideAbstract {
	public boolean isSend = false;//成功与否,确保只还回一次
	public final static String picName = "c:\\jietu\\";//截图地址
	static {
		File file =new File(picName);
		if(!file.exists() && !file.isDirectory()){
			file.mkdirs();
		}
	}
	public abstract void start(Task task);
	public abstract void run(Task task);

	/**
	 * 判断是否成功
	 */
	public void isSuccess(Task task,String msg){
		if(isSend){
			isSend = false;
			MQSender.toMQ(task);
		}
	}

	/**
	 * 获得截图的地址
	 * @param name
	 * @return
	 */
	public String getPicName(String name){
		String picUri = picName+name+".png";
		return picUri;
	}
}
