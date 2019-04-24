package com.trs.gfetch.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Task implements Serializable {

	private Integer id;//子任务id
	private Integer gid;//引导任务idid
	private String serviceType;//所在服务器

	private Integer regionid;//发帖账号区域
	private Integer accountid;//发帖账号id
	private String account;//执行账号
	private String password;//执行账号密码
	private Integer espeed;//频率
	//	private String nick;//执行账号昵称
	private String address;//执行地址
	private String corpus;//执行语料
	private String title;//执行标题
	private Integer failTimes; //失败次数


	private String diggId;//点赞id
	private String diggContent;//点赞内容
	private String type;//执行类型--调用哪个脚本
	private Integer otherOpera;//附加数据--比如同时转发...

	private String remark1;//备用1
	private String remark2;//备用2
	private String remark3;//备用3
	private String remark4;//备用4
	private String remark5;//备用5
	/**
	 * 下面是执行结果
	 * code: 200 发帖成功
	 * 101 用户名或密码错误
	 * 102 账号被锁/账号需认证/账号不存在/需要手机核验
	 * 103 登录报错
	 * 104 登录失败
	 * 105
	 * 106 验证码验证失败
	 * 201 评论报错失败
	 * 301 网络繁忙
	 * 302 字数不符合评论标准
	 * 401 发帖失败
	 * 402 查找评论链接失败
	 * 404 页面不存在
	 * 501 错误:xxx
	 * 502 未找到所在评论
	 */
	private Integer code;//状态码--200成功
	private String result;//结果内容
	private String execTime;//发帖时间
	private String screenshotData;//返回图片二进制文件


}
