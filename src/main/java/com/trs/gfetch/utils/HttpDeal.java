package com.trs.gfetch.utils;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;

/** 
 * 在java中处理http请求. 
 * @author nagsh 
 * 
 */  
public class HttpDeal {  
    /** 
     * 处理get请求. 
     * @param url  请求路径 
     * @return  json 
     */  
    public static String get(String url){  
        //实例化httpclient  
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //实例化get方法  
        HttpGet httpget = new HttpGet(url);
        //请求结果  
        CloseableHttpResponse response = null;
        String content ="";  
        try {
        	RequestConfig requestConfig = RequestConfig.custom()
            	.setConnectTimeout(15000).setConnectionRequestTimeout(20000)  
            	.setSocketTimeout(15000).build(); 

        	httpget.setConfig(requestConfig);  
            //执行get方法  
            response = httpclient.execute(httpget);  
            if(response.getStatusLine().getStatusCode()==200){  
                content = EntityUtils.toString(response.getEntity(),"utf-8");
            }  
        } catch (ClientProtocolException e) {
            e.printStackTrace();  
        } catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  finally {
            try {
            	httpclient.close();
            } catch (IOException e) {
                System.out.println("http接口调用异常：url is::" + url);
            }

        }
        return content;  
    }  
    /** 
     * 处理post请求. 
     * @param url  请求路径 
     * @param params  参数 
     * @return  json 
     *
     * @throws Exception 
     */  
    public static String post(String url,Map<String, String> params) throws Exception{  

    	System.out.println("postUrl::"+url);
        //实例化httpClient  
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //实例化post方法  
        HttpPost httpPost = new HttpPost(url);
        //处理参数  
        List<NameValuePair> nvps = new ArrayList <NameValuePair>();
        Set<String> keySet = params.keySet();    
        for(String key : keySet) {    
            nvps.add(new BasicNameValuePair(key, params.get(key)));
        }    
        //结果  
        CloseableHttpResponse response = null;
        String content="";  
        //提交的参数  
        UrlEncodedFormEntity uefEntity  = new UrlEncodedFormEntity(nvps, "UTF-8");
        //将参数给post方法  
        httpPost.setEntity(uefEntity); 
        RequestConfig requestConfig = RequestConfig.custom()
        .setConnectTimeout(15000).setConnectionRequestTimeout(20000)  
        .setSocketTimeout(15000).build(); 
        httpPost.setConfig(requestConfig);  
        //执行post方法  
        response = httpclient.execute(httpPost);  
        if(response.getStatusLine().getStatusCode()==200){  
        	content = EntityUtils.toString(response.getEntity(),"utf-8");
        	System.out.println("postReturn::"+content);  
        } 

        return content;  
    }  
    public static void main(String[] args) {  
        HttpDeal.get("http://118.190.172.70:8088/proMQ/msg/getMsg.do?vr=6&region=10");  
        Map<String,String> map = new HashMap<String,String>();  
        map.put("id","1");  

        try {
			HttpDeal.post("http://localhost:8080/springMVC/menu/getChildren.do",map);
		} catch (Exception e1) {
			e1.printStackTrace();
		}  

        try {
			HttpDeal.post("http://localhost:8080/springMVC/menu/getChildren.do",map);
		} catch (Exception e) {
			e.printStackTrace();
		}  

    }  
  
}  
