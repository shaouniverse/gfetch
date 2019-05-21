package com.trs.gfetch.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

class dt_info{
	String str_svr;
	long time;
}

/**
 * http://www.91yzm.com/help/flow.php
 * 91验证码识别
 */
public class Yzm91 {

	private String str_url;
	private static String ENCODING = "gb2312";
	private static String EXTRA  = "--";
	private String BOUNDARY;
	private static String CRLF = "\r\n";
	private static String QUOTE = "\"";
	private String FILE_NAME = "; filename=";
	private String CONTENT_DISPOSITION = "Content-Disposition: form-data; name=";
	private static String str_author="";
	private static String str_query_svr="http://plugin.config.91yzm.com:8080/apisvrs.php?ver=20";
	private static Vector<String> svr_addr=new Vector<String>();
	private static Map<String,dt_info> id_addr=new HashMap<String,dt_info>();//保存对应的题号的发题svr地址
	private static long preQueryTime=0;


	private HttpURLConnection conn =null;

	public Yzm91(String remoteUrl){
		str_url=remoteUrl;
	}
	public static void SetAuthor(String author){
		str_author=author;
	}
	public void GetSvrAddr(){
		long curTime=System.currentTimeMillis();

		String data="";
		//5分钟更新次线路
		if(curTime>preQueryTime+5*60*1000){
			preQueryTime=curTime;
			try{
				data=SendGet(str_query_svr,"");
			} catch(Exception e){

			}
		}
		if(data!=null && !data.isEmpty()){
			//解析或得到的服务器地址
			Vector<String> vAddr=new Vector<String>();
			int pre_pos=0;
			while(true){
				int pos = data.indexOf(";");
				if(pos!=-1){
					vAddr.add(data.substring(pre_pos, pos));
					data=data.substring(pos+1);
				}
				else {
					if(!data.isEmpty()) vAddr.add(data);
					break;
				}
			}

			synchronized(svr_addr){
				svr_addr=vAddr;
			}
		}
		//return "";
	}
	public String SendFile(String ac,String file_name,int dati_type,int timeout,int pri,String extra_str){
		GetSvrAddr();
		Vector<String> vAddr=new Vector<String>();
		//获取发题线路
		//默认的2个发题地址
		vAddr.add("http://dt1.91yzm.com:8080");
		vAddr.add("http://dt2.91yzm.com:8080");
		synchronized(svr_addr){
			if(!svr_addr.isEmpty()) vAddr=svr_addr;
		}
		//逐个发题，遇到成功的即可返回
		for(int i=0;i<vAddr.size();i++){
			try {
				BOUNDARY= "----------------"+System.currentTimeMillis();
				URL url;
				url = new URL(vAddr.get(i)+"/uploadpic.php");
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);//设置超时时间
				conn.setReadTimeout(30000);
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setRequestProperty("User-Agent", "91yzm_java_http");
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="+BOUNDARY);
				InputStream is = new FileInputStream(file_name);
				StringBuffer postData = new StringBuffer(EXTRA+BOUNDARY+CRLF);
				postData.append(CONTENT_DISPOSITION+QUOTE+"pic"+QUOTE+FILE_NAME+QUOTE+file_name+QUOTE+CRLF);
				postData.append("Content-Type: image/jpeg"+CRLF+CRLF);

				OutputStream os = conn.getOutputStream();
				byte headerData [] =postData.toString().getBytes();
				os.write(headerData);

				byte[] fileData = getBytes(is);
				os.write(fileData);
				os.write((EXTRA+BOUNDARY+CRLF).getBytes());
				//发送其他参数
				String param = genernateRequestParam("acc_str",ac)+genernateRequestParam("dati_type",Integer.toString(dati_type))+genernateRequestParam("timeout",Integer.toString(timeout))+genernateRequestParam("pri",Integer.toString(pri))+genernateRequestParam("extra_str",extra_str);
				if(!str_author.isEmpty()) param+=genernateRequestParam("zz",str_author);
				os.write(param.getBytes());

				String endS = EXTRA+BOUNDARY+EXTRA+CRLF;
				os.write(endS.getBytes());
				os.flush();
				os.close();
				String id="";
				InputStreamReader ir=new InputStreamReader(conn.getInputStream());
				BufferedReader br = new BufferedReader(ir);
				id=br.readLine();
				conn.disconnect();
				if(id!=null&&!id.isEmpty()&&id.charAt(0)!='#'){
					dt_info di=new dt_info();
					di.str_svr=vAddr.get(i);
					di.time=System.currentTimeMillis();
					synchronized(id_addr){
						id_addr.put(id, di);
					}
				}
				return id;

			} catch (FileNotFoundException e) {
				return "#读取文件失败";
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}

		return "#发题失败";
	}
	public String SendGet(String urlname,String param) throws Exception{
		try{
			String urlNameString = urlname;
			if(param!=null && !param.isEmpty())urlNameString=urlNameString+ "?" + param;
			URL realUrl = new URL(urlNameString);
			conn = (HttpURLConnection) realUrl.openConnection();
			conn.setConnectTimeout(5000);//设置超时时间
			conn.setReadTimeout(10000);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("User-Agent", "91yzm_java_http");
			InputStreamReader ir=new InputStreamReader(conn.getInputStream());
			BufferedReader br = new BufferedReader(ir);
			String answer="";
			answer=br.readLine();
			conn.disconnect();

			return answer;
			//return br.readLine();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
	public String GetAnswer(String id) {
		//删除5分钟前的题目信息，免得占用内存
		long curTime=System.currentTimeMillis();
		synchronized(id_addr){
			Iterator<String> iter = id_addr.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				if(curTime-id_addr.get(key).time>5*60*1000) iter.remove();
			}
		}

		if(id.length()>0&&id.charAt(0)=='#') return id;
		String str_svr="";
		synchronized(id_addr){
			dt_info dii=id_addr.get(id);
			if(dii!=null)str_svr=dii.str_svr;

		}
		String answer="";
		if(str_svr!=null&&!str_svr.isEmpty()){
			try{
				answer=SendGet(str_svr+"/query.php","sid="+id);
				if(answer!=null&&!answer.isEmpty()) id_addr.remove(id);//获取到答案后就从map中删除信息以免内存占用太大
				return answer;
			} catch(Exception e){
			}
		}
		//如果保存的线路无法获取答案，则从其他线路获取答案
		id_addr.remove(id);
		Vector<String> vAddr=new Vector<String>();
		vAddr.add("http://dt1.91yzm.com:8080");
		vAddr.add("http://dt2.91yzm.com:8080");
		synchronized(svr_addr){
			if(!svr_addr.isEmpty()) vAddr=svr_addr;
		}
		for(int i=0;i<vAddr.size();i++){
			if(str_svr==vAddr.get(i)) continue;
			try{
				answer=SendGet(vAddr.get(i)+"/query.php","sid="+id);
				return answer;
			} catch(Exception e){
			}
		}
		return answer;
	}
	//把文件转换成字节数组
	private byte[] getBytes(InputStream in) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int n;
		while ((n = in.read(b)) != -1) {
			out.write(b, 0, n);
		}
		in.close();
		return out.toByteArray();
	}
	//模拟发送表单参数。
	private String genernateRequestParam(String name,String value){
		StringBuffer param = new StringBuffer(EXTRA+BOUNDARY+CRLF);
		param.append(CONTENT_DISPOSITION+QUOTE+name+QUOTE+CRLF);
		param.append(CRLF);
		param.append(value+CRLF);
		return param.toString();
	}

    /**
     * @param img  验证码路径
	 * @param code	识别验证码编码
	 *              1007-->4-6位字母数字
	 *              8015-->滑动验证码做标题
	 * @return
     */
	public static String getYzm(String img,int code){
		System.out.println("-------------->验证码处理中");
		Yzm91.SetAuthor("chwx2019");//设置作者帐号
		Yzm91 jh = new Yzm91("");

		String id=jh.SendFile("4EDa3Iv41JeN6AQH",img,code,30,1,"");
		String answer="";
		while(answer==null||answer.length()==0){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			jh =new Yzm91("");
			answer=jh.GetAnswer(id);
		}
		System.out.println("验证码结果-------->"+answer);
		return answer;
	}
	//4-6位字母数字
	public static String getYzmYS(String img){
		return getYzm(img,1007);
	}
	/**
	 * 获取滑动验证码
	 * @param attr
	 * @return
	 */
	public static String getCodeResultDrap(String attr,String picName) {

		try{
			URL url = new URL(attr);

			HttpURLConnection openConnection = (HttpURLConnection)url.openConnection();

			openConnection.connect();

			InputStream is = openConnection.getInputStream();

			byte[] b = new byte[1024];
			int len;
			String pic = "c:\\vcode\\"+picName+".png";
			OutputStream os = new FileOutputStream(pic);

			while((len = is.read(b)) != -1){

				os.write(b, 0, len);
			}

			os.close();
			is.close();

			String code = Yzm91.getYzm(pic,8015);
//		String code = RuoKuai.createByPostNew("6137", "c:\\vcode\\slide.png");
			System.out.println(code);
			return code;

		}catch(Exception e){

			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args){
		getYzm("C:\\vcode\\1.jpg",1007);
	}
}

