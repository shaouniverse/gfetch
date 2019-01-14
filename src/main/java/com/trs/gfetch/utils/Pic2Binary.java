package com.trs.gfetch.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class Pic2Binary {
	static BASE64Encoder encoder = new sun.misc.BASE64Encoder();
	static BASE64Decoder decoder = new sun.misc.BASE64Decoder();

	public static void main(String[] args) {

//        System.out.print(getImageBinary("d:\\vcode\\ifeng.png"));
		base64StringToImage(getImageBinary("d:\\vcode\\ifeng.png"));

//        System.out.print(getImageBinary("c:\\ifeng.png"));
		base64StringToImage(getImageBinary("c:\\vcode\\ifeng.png"));
//        System.out.print(getImageBinary("d:\\vcode\\ifeng.png"));
//        base64StringToImage(getImageBinary("c:\\vcode\\ifeng.png"));

	}
	/**
	 * 将图片转换成二进制
	 *
	 * @return
	 */
	public static String getImageBinary(String uri) {
		if(uri==null) return null;
		File f = new File(uri);
		BufferedImage bi;
		try {
			bi = ImageIO.read(f);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bi, "png", baos);  //经测试转换的图片是格式这里就什么格式，否则会失真
			byte[] bytes = baos.toByteArray();
			String kk = encoder.encodeBuffer(bytes).trim();
			return kk;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 将二进制转换为图片
	 *
	 * @param base64String
	 */

	public static String base64StringToImage(String base64String) {

		String imgPath = "d://"+System.currentTimeMillis() + ".png";
		try {


			byte[] bytes1 = decoder.decodeBuffer(base64String);

			ByteArrayInputStream bais = new ByteArrayInputStream(bytes1);
			BufferedImage bi1 = ImageIO.read(bais);

			File w2 = new File(imgPath);// 可以是jpg,png,gif格式
			ImageIO.write(bi1, "png", w2);// 不管输出什么格式图片，此处不需改动

			return imgPath;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
}
