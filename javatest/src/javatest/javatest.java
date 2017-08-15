package javatest;

import java.util.*;

import javax.swing.text.html.HTMLDocument.Iterator;

//import org.json.JSONObject;

import javatest.OAuthConfig;
import javatest.OAuthUtils;
import javatest.TokenInfo;

public class javatest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//json鏁版嵁缁勭粐鐢熸垚-*******娴嬭瘯閫氳繃***************
//		fileprocess fp = new fileprocess();
//		String str = "d:\\2.s";
//		str = new String(str.getBytes("gbk"),"utf-8");
//		List<List<String>> sf =fp.sfilein(str); 
//		preparejson pj = new preparejson();
//		JSONObject tempmap = pj.prepareJonObject(sf);
//		
		//----------------------------------------------
		//瀹㈡埛绔笌鏈嶅姟绔痵ervlet浜や簰
		//鍙戦�乯son娴嬭瘯
		//sendJson sj = new sendJson();
		//String s1 = sj.sendJson("http://localhost:8080/jspstudy/jsonget","name=Tom&pass=123456");
		//System.out.println("ok");
		//httpPostWithJSON hpj = new httpPostWithJSON();
		//String result = hpj.postJson("http://localhost:8080/jspstudy/jsonget");
		//System.out.println("result");
        //HttpClientTest test = new HttpClientTest();
        // 娴嬭瘯GET璇锋眰
        //test.get();
        // 娴嬭瘯POST璇锋眰
        //test.post();
		
		//测绘中心服务器Oauth基础测试 通过
		//cehui_test ct = new cehui_test();
		//flag为代理开关，true时执行代理连接，false执行直接连接
		//boolean flag = false;
		//ct.NotAuthTestPost(flag);
		//String xmlget =ct.get("http://123.127.139.225:9099/tgis_server/api/user/login");////鑷繁鏈湴鐨�(璋冪敤鎺ュ彛鏂规硶)URL鍦板潃
		//System.out.print(xmlget);
		
		//OAuth资源请求测试
		//设定配置文件路径
		String path = "F:\\OAuth20\\OAuth2.0\\src\\com\\ibm\\oauth\\Oauth2Client.config";
		//读取配置文件
		Properties config = OAuthUtils.getClientConfigProps(path);
		
		//设定资源请求url
		String resourceServerUrl ="http://123.127.139.225:9099/tgis_server/api/userpoint/list_view_time?uptime=2017-08-11T09:56:52";
		String username = config.getProperty(OAuthConfig.USERNAME);
		String password = config.getProperty(OAuthConfig.PASSWORD);
		String grantType = config.getProperty(OAuthConfig.GRANT_TYPE);
		//设定验证请求url
		String authenticationServerUrl = config
		        .getProperty(OAuthConfig.AUTHENTICATION_SERVER_URL);
		//flag控制是否使用代理服务器 true为使用，flase为不使用	
		boolean flag = false;
		
		//输入数据有效性验证
		if (!OAuthUtils.isValid(username)
				   || !OAuthUtils.isValid(password)
				   || !OAuthUtils.isValid(authenticationServerUrl)
				   || !OAuthUtils.isValid(grantType)) {
				 System.out
				       .println("Please provide valid values for username, password,authentication server url and grant type");
				 System.exit(0);				 
				  }
		if(!OAuthUtils.isValid(resourceServerUrl)) {
			// Resource server url is not valid.
			//Only retrieve the access token
			System.out.println("Retrieving Access Token");
			TokenInfo oauthDetails = OAuthUtils.createOAuthDetails(config);
			String accessToken = OAuthUtils.getAccessToken(oauthDetails,flag);
			System.out
			.println("Successfully retrieved Access token for Password Grant:" + accessToken);
			}
			else {
			// Response from the resource server must be in Json or
			//Urlencoded or xml
			System.out.println("Resource endpoint url:" + resourceServerUrl);
			System.out.println("Attempting to retrieve protected resource");
			OAuthUtils.getProtectedResource(config,flag);
			     }
	}

}
