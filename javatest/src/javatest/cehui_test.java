package javatest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class cehui_test{
	static void NotAuthTestPost(boolean flag) throws IOException {
		//flag为代理设置，true时执行代理连接，flase执行直接连接。
		flag = false; 
		Map<String, String> params = new HashMap<String, String>();  
		params.put("username", "13400231981"); //ID
		params.put("password", "123456");  //瀵嗙爜
		params.put("grant_type", "password");//鎺堟潈绫诲瀷
		String xml =post_method( params,flag); 
		System.out.print(xml);
		}
	private static String post_method(Map<String, String> params,boolean flag )
	{
		DefaultHttpClient httpclient;
		//flag =true 执行代理
		if(flag) {
			HttpHost proxy = new HttpHost("proxy3.bj.petrochina", 8080); 
			httpclient = new DefaultHttpClient();  
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}else {
			httpclient = new DefaultHttpClient();  
		}
		
	String body = null; 
	HttpPost post = postForm(params);  
	body = invoke(httpclient, post);  
	httpclient.getConnectionManager().shutdown();  
	return body;  
	}
	private static HttpPost postForm(Map<String, String> params){  
		HttpPost httpost = new
				HttpPost("http://123.127.139.225:9099/tgis_server/api/user/login");  //鑷繁鏈湴鐨�(鎺堟潈)URL鍦板潃
		List<NameValuePair> nvps = new ArrayList <NameValuePair>();  
		Set<String> keySet = params.keySet();  
		for(String key : keySet) {  
		nvps.add(new BasicNameValuePair(key, params.get(key)));  
		}  
		try {  
		httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));  
		} catch (UnsupportedEncodingException e) {  
		e.printStackTrace();  
		}  
		return httpost;  
		}
	private static String invoke(DefaultHttpClient httpclient,HttpUriRequest httpost) {  
		HttpResponse response = sendRequest(httpclient, httpost);  
		String body = paseResponse(response);  
		return body;  
		}  
	private static HttpResponse sendRequest(DefaultHttpClient httpclient,    HttpUriRequest httpost) {  
		HttpResponse response = null;  
		try {  
		response = httpclient.execute(httpost);  
		} catch (ClientProtocolException e) {  
		e.printStackTrace();  
		} catch (IOException e) {  
		e.printStackTrace();  
		}  
		return response;  
		}  
	private static String paseResponse(HttpResponse response) {  
		HttpEntity entity = response.getEntity();  
		String charset = EntityUtils.getContentCharSet(entity);  
		String body = null;  
		try {
		body = EntityUtils.toString(entity);
		} catch (ParseException e) {
		// TODO 鑷姩鐢熸垚鐨� catch 鍧�
		e.printStackTrace();
		} catch (IOException e) {
		// TODO 鑷姩鐢熸垚鐨� catch 鍧�
		e.printStackTrace();
		}  
		return body;  
		}  

	//鑾峰彇杩斿洖鍊奸獙璇�
	public static String get(String url) {  
	DefaultHttpClient httpclient = new DefaultHttpClient();  
	String body = null;  
	HttpGet get = new HttpGet(url);  
	//Authorization:http楠岃瘉鐨勫ご锛岀浜屼釜鍙傛暟锛歵oken绫诲瀷+绌烘牸+杩炴帴浠ょ墝(鎴戜滑鐢ㄨ繖涓�"access_token")
	//get.setHeader("Authorization", "Bearer" +"oq1dan5f6eAcc10mBBlMQZEUxCS_-ViD82DLXbHcejGECRL4W7Pag9oQ0BAqmyYj5Z3YddomTL7fElIDm2vG-p9ZZoGwI26JLXJXRnyTbaQCo78uL0IXSzYykkmNt0TV731WHScmFrxBXKq2zCKyGhkGq8Y1CTq6HZRUe8hqMbnLRXGu4kHR9ALMzkTt5UflxSlYG4SoRq10xRXqBQS9Xhqpwufmn559iMQkIPUIVA28xV2BKkP0_Gr6SAqanZLfY_Ne3dSsg4H8KoXNvEr8dvnOQ7Xwth6980C6-_j7fsI4tDY_iKa-WromMD2gVExP7SNmDHNxvazkavrs5kxxaJxwg2st-hJ6lwrNyknIrSQNyl9Cz6ga9UpJcNvWy4PquwQamVSWzMa75EQBrQBjqjzMSLQJ_MsW09sJI6f5hnM");
	body = invoke(httpclient, get);
	httpclient.getConnectionManager().shutdown();  
	return body;  
	}
}