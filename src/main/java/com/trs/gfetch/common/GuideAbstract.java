package com.trs.gfetch.common;

import com.trs.gfetch.entity.Task;
import com.trs.gfetch.utils.FileUtil;
import com.trs.gfetch.utils.MQSender;

import java.io.File;

public abstract class GuideAbstract {
	//成功与否,确保只返回一次,目前单线程模式,多线程需改动
	public boolean isSend = false;
	public static final String picName = "c:\\jietu\\";//截图地址
	public static final String picCode = "c:\\vcode\\";//验证码图片地址
	//存放截图/验证码的文件夹不存在默认创建
	static {
		{
			File file =new File(picName);
			if(!file.exists() && !file.isDirectory()){
				file.mkdirs();
			}
		}
		File fileCode =new File(picCode);
		if(!fileCode.exists() && !fileCode.isDirectory()){
			fileCode.mkdirs();
		}
	}
	public Task task;
	public abstract void run();//执行
	public abstract String formatAddress();//新闻页或者评论页转化

	/**
	 * 首先进入的方法
	 */
	public void start() {
		//先清空存放截图的文件夹
		FileUtil.deleteDirectoryFiles();
		//保证只返回一次结果
		isSend = true;
	}

	/**
	 * 发送消息到队列
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
	public static String getPicName(String name){
		String picUri = picName+name+".png";
		return picUri;
	}
}
