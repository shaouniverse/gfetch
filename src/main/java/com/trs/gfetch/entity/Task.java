package com.trs.gfetch.entity;

import java.io.Serializable;

public class Task implements Serializable {

	private Integer id;//执行任务id
	private Integer idSub;//子id

	private String account;//执行账号
	private String password;//执行账号密码
//	private String nick;//执行账号昵称
	private String address;//执行地址
	private String corpus;//执行语料
	private String title;//执行标题

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
	 * 102 账号被锁
	 * 105 账号需认证
	 * 103 登录报错
	 * 104 登录失败
	 * 201 评论报错失败
	 * 301 网络繁忙
	 * 401 发帖失败
	 */
	private Integer code;//状态码--200成功
	private String result;//结果内容
	private String screenshotData;//返回图片二进制文件

	public String getScreenshotData() {
		return screenshotData;
	}

	public void setScreenshotData(String screenshotData) {
		this.screenshotData = screenshotData;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Integer getOtherOpera() {
		return otherOpera;
	}

	public void setOtherOpera(Integer otherOpera) {
		this.otherOpera = otherOpera;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdSub() {
		return idSub;
	}

	public void setIdSub(Integer idSub) {
		this.idSub = idSub;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCorpus() {
		return corpus;
	}

	public void setCorpus(String corpus) {
		this.corpus = corpus;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDiggId() {
		return diggId;
	}

	public void setDiggId(String diggId) {
		this.diggId = diggId;
	}

	public String getDiggContent() {
		return diggContent;
	}

	public void setDiggContent(String diggContent) {
		this.diggContent = diggContent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRemark1() {
		return remark1;
	}

	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}

	public String getRemark2() {
		return remark2;
	}

	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}

	public String getRemark3() {
		return remark3;
	}

	public void setRemark3(String remark3) {
		this.remark3 = remark3;
	}

	public String getRemark4() {
		return remark4;
	}

	public void setRemark4(String remark4) {
		this.remark4 = remark4;
	}

	public String getRemark5() {
		return remark5;
	}

	public void setRemark5(String remark5) {
		this.remark5 = remark5;
	}

}
