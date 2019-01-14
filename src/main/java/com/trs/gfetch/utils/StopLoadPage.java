package com.trs.gfetch.utils;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * 浏览器形式加载超时后,点击esc键,停止加载
 */
public class StopLoadPage {

	private int sec = 30;//默认加载25s后按下esc键,可以创建的时候指定时间
	public int isEnterESC = 1;

	public StopLoadPage(){
		start();
	}
	public StopLoadPage(int sec){
		this.sec = sec;
		start();
	}

	/**
	 * 测试
	 * @param ar
	 */
	public static void main(String ar[]){
		new StopLoadPage();
		int number = 0;
		while (true){
			System.out.println("----"+number);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 开始
	 */
	private void start(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(sec * 1000);
					if(isEnterESC==1){
						Robot r=new Robot();
						r.keyPress(KeyEvent.VK_ESCAPE);//按下Esc键
						r.keyRelease(KeyEvent.VK_ESCAPE);//释放Esc键
						System.out.println("---------按下Esc键------------");
						System.out.println("----------释放Esc键------------");
					}else{
						System.out.println("----------加载完毕------------");
						System.out.println("----------加载完毕------------");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}
