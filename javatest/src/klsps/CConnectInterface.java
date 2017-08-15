package klsps;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.simple.parser.JSONParser;

import javatest.JsonFormatAnalysis;
import javatest.OAuthConfig;
import net.sf.json.JSONArray;

public class CConnectInterface {
	private String userName;// 用户名
	private String passWord;// 密码
	private String proxyAddr=null;// 代理含端口号格式:"http://proxy3.bj.petrochina:8080"
	private	Date checkPoint=null;// 时间
	private static String authenticationServerUrl;
	private static String sourceServerUrl;
	private static String grantType="password";
	private static String accessToken=null;
	private static int code=-1;
	static List<String> list = new ArrayList();
	
	
	public void setGrantType(String granttype) {
		this.grantType=granttype;
	}
	public void setSourceServerUrl(String sourceserverurl){	
		this.sourceServerUrl=sourceserverurl;
	}
	
	public void setAuthenticationServerUrl(String AuthenticationServerUrl) {
		this.authenticationServerUrl=AuthenticationServerUrl;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public void setProxyAddr(String proxyAddr) {
		this.proxyAddr = proxyAddr;
	}
	public String getProxyAddr() {
		return proxyAddr;
	}
	public void setCheckPoint(Date checkPoint) {
		this.checkPoint = checkPoint;
	}
	public int getServerStatus() {
		if(list.isEmpty()||list==null) {
			getAccessToken(proxyAddr);
		}
		int status = Integer.parseInt(list.get(1));
		return status;
	}
	public String getToken() {
		if(list==null||list.isEmpty()) {
			getAccessToken(proxyAddr);
		}
		String accessToken = list.get(0);
		return accessToken;
	}
	
	//以Grant-Type:password的方式获取accessToken
	public List<String> getAccessToken(String proxyaddr) {
	
		DefaultHttpClient client = null;
		HttpPost post = new HttpPost(authenticationServerUrl);
		
		//组织验证数据
		List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
		parametersBody.add(new BasicNameValuePair("grant_type",grantType));
		parametersBody.add(new BasicNameValuePair("username",userName));
		parametersBody.add(new BasicNameValuePair("password",passWord));
		
		//检查并按结果设置代理服务器
		if(proxyaddr==null) {			
			client = new DefaultHttpClient(); 		
		}else {
			HttpHost proxy = new HttpHost(proxyAddr); 
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		HttpResponse response = null;
		String accessToken = null;
		try {
			post.setEntity(new UrlEncodedFormEntity(parametersBody, HTTP.UTF_8));
			response = client.execute(post);
			code = response.getStatusLine().getStatusCode();
			if (code >=400) {
				System.out.println("Authorization server expects Basic authentication");
				
				}
			Map<String, String> map = handleJsonResponse(response);
			accessToken = map.get("access_token");
	}catch(Exception e) {
	}
		System.out.println(accessToken);			
		list.add(accessToken);
		list.add(String.valueOf(code));
		return list;
		}

	//获取受保护资源
	public void getProtectedResource(String proxyaddr) {
		DefaultHttpClient client =null;
		HttpGet get = new HttpGet(sourceServerUrl);
		if(accessToken==null) {
			List<String> templist = getAccessToken(proxyaddr);
			accessToken=templist.get(0);
		}
		get.addHeader("Authorization",getAuthorizationHeaderForAccessToken(accessToken));
		if(proxyaddr==null) {
			client = new DefaultHttpClient();  
		}else {
			HttpHost proxy = new HttpHost(proxyaddr);
			client  = new DefaultHttpClient();  
			client .getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		HttpResponse response = null;
		try {
			response = client.execute(get);
			code = response.getStatusLine().getStatusCode();
			if (code >= 400) {
				System.out.println("Access token is invalid or expired. Regenerating access token....");			
				if(accessToken==null) {
				List<String> templist = getAccessToken(proxyaddr);
				accessToken=templist.get(0);
				}else {
					System.out.println("Token error!");
				}
			}
			handleResponse(response);			
		}catch(Exception e) {
			
		}finally {
			get.releaseConnection();
		}	
		}
	
	//httpget对象中 setHeader的具体内容组织方法
	public static String getAuthorizationHeaderForAccessToken(String accessToken) {
			return "Bearer" + " " + accessToken;
		}
	
	//判定返回值数据类型
	public static Map handleResponse(HttpResponse response) {
		//初始设置httpresponse响应格式为json 值为"application/json"
		String contentType = "application/json";
		//判定httpresponse中是否有ContentType字段，并根据其内容映射jsp中MIME的响应，进行处理。
		//当response返回值ContentType字段不为空时，将值赋给contentType进而进行格式判断
		if (response.getEntity().getContentType() != null) {
			contentType = response.getEntity().getContentType().getValue();
		}
		//当response返回值对象确认为json格式，使用handleJsonResponse方法处理response返回值
		if (contentType.contains(OAuthConfig.JSON_CONTENT)) {
			return handleJsonResponse(response);
		} else {
			// Unsupported Content type
			//所有判定均失败时，抛出运行时异常，并提示信息，response无法解析，程序只支持json xml 和urlencoded方式
			throw new RuntimeException(
					"Cannot handle "
							+ contentType
							+ " content type. Supported content types include JSON, XML and URLEncoded");
		}
	}
		
	//处理JSON数据 
	public static Map handleJsonResponse(HttpResponse response) {
		Map<String, String> oauthLoginResponse = null;
		String contentType = response.getEntity().getContentType().getValue();
		try {
			//对response实体进行字符串转换后使用json解析，并存入map，map解析格式为<"key","value">
			oauthLoginResponse = (Map<String, String>) new JSONParser()
					.parse(EntityUtils.toString(response.getEntity()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException();
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException();
		} catch (RuntimeException e) {
			System.out.println("Could not parse JSON response");
			throw e;
		}
		System.out.println();
		System.out.println("********** Response Received **********");
		for (Map.Entry<String, String> entry : oauthLoginResponse.entrySet()) {
			System.out.println(String.format("  %s = %s", entry.getKey(),
					entry.getValue()));
			String temp=entry.getKey();
			if(temp.equals("Object")) {
			JSONArray jsonArray=JSONArray.fromObject(entry.getValue());
				JsonFormatAnalysis.PointList(jsonArray);
			}		 
		}
		return oauthLoginResponse;
	}
}

