package com.trs.gfetch.common;

import com.trs.gfetch.entity.Task;
import com.trs.gfetch.utils.FileUtil;
import com.trs.gfetch.utils.MQSender;

import java.io.File;
import java.util.*;

public abstract class GuideAbstract implements GuideFetchInterf{
	//成功与否,确保只返回一次,目前单线程模式,多线程需改动
	public boolean isSend = false;
	public static final String picName = "c:\\jietu\\";//截图地址
	public static final String picCode = "c:\\vcode\\";//验证码图片地址
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
	//定义集合,存放脚本,调用接口调用
	public static Map<String,GuideFetchInterf>  handlerMap = new HashMap<>();

	public Task task;
	public abstract void run();//执行

	/**
	 * 发送消息到队列
	 */
	public void isSuccess(Task task){
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
}
