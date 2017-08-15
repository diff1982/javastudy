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
	private String userName;// �û���
	private String passWord;// ����
	private String proxyAddr=null;// �����˿ںŸ�ʽ:"http://proxy3.bj.petrochina:8080"
	private	Date checkPoint=null;// ʱ��
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
	
	//��Grant-Type:password�ķ�ʽ��ȡaccessToken
	public List<String> getAccessToken(String proxyaddr) {
	
		DefaultHttpClient client = null;
		HttpPost post = new HttpPost(authenticationServerUrl);
		
		//��֯��֤����
		List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
		parametersBody.add(new BasicNameValuePair("grant_type",grantType));
		parametersBody.add(new BasicNameValuePair("username",userName));
		parametersBody.add(new BasicNameValuePair("password",passWord));
		
		//��鲢��������ô��������
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

	//��ȡ�ܱ�����Դ
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
	
	//httpget������ setHeader�ľ���������֯����
	public static String getAuthorizationHeaderForAccessToken(String accessToken) {
			return "Bearer" + " " + accessToken;
		}
	
	//�ж�����ֵ��������
	public static Map handleResponse(HttpResponse response) {
		//��ʼ����httpresponse��Ӧ��ʽΪjson ֵΪ"application/json"
		String contentType = "application/json";
		//�ж�httpresponse���Ƿ���ContentType�ֶΣ�������������ӳ��jsp��MIME����Ӧ�����д���
		//��response����ֵContentType�ֶβ�Ϊ��ʱ����ֵ����contentType�������и�ʽ�ж�
		if (response.getEntity().getContentType() != null) {
			contentType = response.getEntity().getContentType().getValue();
		}
		//��response����ֵ����ȷ��Ϊjson��ʽ��ʹ��handleJsonResponse��������response����ֵ
		if (contentType.contains(OAuthConfig.JSON_CONTENT)) {
			return handleJsonResponse(response);
		} else {
			// Unsupported Content type
			//�����ж���ʧ��ʱ���׳�����ʱ�쳣������ʾ��Ϣ��response�޷�����������ֻ֧��json xml ��urlencoded��ʽ
			throw new RuntimeException(
					"Cannot handle "
							+ contentType
							+ " content type. Supported content types include JSON, XML and URLEncoded");
		}
	}
		
	//����JSON���� 
	public static Map handleJsonResponse(HttpResponse response) {
		Map<String, String> oauthLoginResponse = null;
		String contentType = response.getEntity().getContentType().getValue();
		try {
			//��responseʵ������ַ���ת����ʹ��json������������map��map������ʽΪ<"key","value">
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

