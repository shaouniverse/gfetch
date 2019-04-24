package com.trs.gfetch.utils;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 切换ip
 */
public class AsdlIp {
	public static void main(String[] args) throws Exception {  
//        connAdsl("宽带","hzhz**********","******");  
//        Thread.sleep(1000);  
        cutAdsl("chwx1103");  
        Thread.sleep(1000);  
        //再连，分配一个新的IP  
//        connAdsl("宽带","hzhz**********","******");  
    }  
    /** 
     * 执行CMD命令,并返回String字符串 
     */  
    public static String executeCmd(String strCmd) throws Exception {  
        Process p = Runtime.getRuntime().exec("cmd /c " + strCmd);  
        System.out.println("执行exec命令中...");
        /*为"错误输出流"单独开一个线程读取之,否则会造成标准输出流的阻塞*/  
        Thread t=new Thread(new InputStreamRunnable(p.getErrorStream(),"ErrorStream"));  
        t.start();

        StringBuilder sbCmd = new StringBuilder();  
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(),"gb2312"));  
        String line;  
        while ((line = br.readLine()) != null) {  
            sbCmd.append(line + "\n");  
        }  
        System.out.println("连接cmd::"+sbCmd.toString()); 
        return sbCmd.toString();  
    }  
  
    /** 
     * 连接ADSL 
     * rasdial aaa 051011301310 369258
     * 051011301310----369258
     */  
    public static boolean connAdsl(String adslTitle, String adslName, String adslPass) throws Exception {  
        System.out.println("正在建立连接...");  
        String adslCmd = "rasdial " + adslTitle + " " + adslName + " " + adslPass;  
        System.out.println("正在建立连接::"+adslCmd);
        String tempCmd = executeCmd(adslCmd);  
        // 判断是否连接成功  
        if (tempCmd.indexOf("已连接") > 0) {  
            System.out.println("已成功建立连接.");  
            return true;  
        } else {  
            System.err.println(tempCmd);  
            System.err.println("建立连接失败");  
            return false;  
        }  
    }  
  
    /** 
     * 断开ADSL 
     * rasdial aaa /disconnect
     */  
    public static boolean cutAdsl(String adslTitle) throws Exception {  
        String cutAdsl = "rasdial " + adslTitle + " /disconnect";  
        String result = executeCmd(cutAdsl); 
        System.out.println("断开连接返回:"+result);
        if (result.indexOf("没有连接")!=-1){  
            System.err.println(adslTitle + " conn is not exsits!");  
            return false;  
        } else {  
            System.out.println(adslTitle+" conn is cut");  
            return true;  
        }  
    }  
     
}

/**读取InputStream的线程*/
class InputStreamRunnable implements Runnable{  
    BufferedReader bReader=null;  
    String type=null;  
    public InputStreamRunnable(InputStream is, String _type){  
        try{  
            bReader=new BufferedReader(new InputStreamReader(new BufferedInputStream(is),"UTF-8"));  
            type=_type;  
        }  
        catch(Exception ex){  
        }  
    }  
    public void run(){  
        String line;  
        int lineNum=0;  
  
        try{  
            while((line=bReader.readLine())!=null){  
                lineNum++;  
                System.out.println(lineNum+", line=="+line);
                //Thread.sleep(200);  
            }  
            bReader.close();  
        }catch(Exception ex){  
        }  
    }  
}  
