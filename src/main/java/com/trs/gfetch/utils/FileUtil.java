package com.trs.gfetch.utils;


import com.trs.gfetch.common.GuideAbstract;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;


public class FileUtil {
	public static int times = 1;
	public static void main(String[] args) {
		deleteDirectoryFiles();
	}
	 /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName
     *            要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteFile(fileName);
        }
        return false;
    }
	 /**
     * 删除单个文件
     *
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }
    /**
     * 目录下的文件
     *
     * @param dir
     *            要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectoryFiles() {
    	System.out.println("截图删除");
    	if(times<15){
    		times++;
    		return true;
    	}
    	times = 1;
    	String dir = GuideAbstract.picName;
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = FileUtil.deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        return true;
    }

    /**
     * String url = "http://192.168.1.158/estun_cs/banner_img/head_pic.jpg";  
     * String path="d:/test/pic.jpg";  
     * downloadPicture(url,path);  
     * @param urlList
     * @param path
     */
    public static boolean downloadPicture(String urlList,String path) {  
        URL url = null;  
        try {  
            url = new URL(urlList);  
            DataInputStream dataInputStream = new DataInputStream(url.openStream());  
  
            FileOutputStream fileOutputStream = new FileOutputStream(new File(path));  
            ByteArrayOutputStream output = new ByteArrayOutputStream();  
  
            byte[] buffer = new byte[1024];  
            int length;  
  
            while ((length = dataInputStream.read(buffer)) > 0) {  
                output.write(buffer, 0, length);  
            }  
            fileOutputStream.write(output.toByteArray());  
            dataInputStream.close();  
            fileOutputStream.close(); 
            return true;
        } catch (MalformedURLException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } 
        return false;
    }  
    public static void dirExists(String uri){
		File file =new File(uri);
		if(!file.exists() && !file.isDirectory()){
		    file.mkdirs();  
		}
	}

}
