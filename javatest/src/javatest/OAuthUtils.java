package javatest;

import javatest.TokenInfo;
import net.sf.json.JSONArray;
import javatest.OAuthConfig;
import javatest.JsonFormatAnalysis;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class OAuthUtils {
	
	//�����������ļ�OAuthConfig�л�ȡ���ֲ������õķ���
	public static TokenInfo createOAuthDetails(Properties config) {
		TokenInfo oauthDetails = new TokenInfo();
		oauthDetails.setAccessToken((String) config
				.get(OAuthConfig.ACCESS_TOKEN));
		oauthDetails.setRefreshToken((String) config
				.get(OAuthConfig.REFRESH_TOKEN));
		oauthDetails.setGrantType((String) config
				.get(OAuthConfig.GRANT_TYPE));
		oauthDetails.setClientId((String) config.get(OAuthConfig.CLIENT_ID));
		oauthDetails.setClientSecret((String) config
				.get(OAuthConfig.CLIENT_SECRET));
		oauthDetails.setScope((String) config.get(OAuthConfig.SCOPE));
		oauthDetails.setAuthenticationServerUrl((String) config
				.get(OAuthConfig.AUTHENTICATION_SERVER_URL));
		oauthDetails.setUsername((String) config.get(OAuthConfig.USERNAME));
		oauthDetails.setPassword((String) config.get(OAuthConfig.PASSWORD));

		return oauthDetails;
	}
	
	//�����������ļ�תΪ�����ж�ȡ����ֱ�ӷ���OAuthConfig����ķ���
	public static Properties getClientConfigProps(String path) {
		//InputStream is = OAuthUtils.class.getClassLoader().getResourceAsStream(path);
		Properties config = new Properties();
		try {
			config.load(new FileInputStream(path));
		} catch (IOException e) {
			System.out.println("Could not load properties from " + path);
			e.printStackTrace();
			return null;
		}
		return config;
	}
	
	//��ȡ��Token��������Դ����
	public static void getProtectedResource(Properties config,boolean flag) {
		//flag�����Ƿ�ʹ�ô�������� trueΪʹ�ã�flaseΪ��ʹ��		
		//��ȡ��Դ��������֤URL
		DefaultHttpClient client ;
		String resourceURL = config
				.getProperty(OAuthConfig.RESOURCE_SERVER_URL);
		
		//����OAuthConfig�����ȡ���г�����Ӧֵ��
		TokenInfo oauthDetails = createOAuthDetails(config);
		
		//ʹ��Httpget(url)����ʵ����httpget����
		//׼������Դ��������֤URLΪ��������get����
		HttpGet get = new HttpGet(resourceURL);
		
		//����׼��httpget����������Ҫ����������httpͷ��Ӧ�ṹ���Լ���Ӽ��ܷ�ʽ���ͻ�ȡ��Token
		//�˲���Ϊoauth2��֤�û���¼״̬���Ĳ���
		get.addHeader(OAuthConfig.AUTHORIZATION,
				getAuthorizationHeaderForAccessToken(oauthDetails
						.getAccessToken()));
		
		//����java httpclient����Ĭ�ϲ���Ϊ��
		//�ж�flag�Ƿ�ʹ�ô�������
		if(flag) {
			HttpHost proxy = new HttpHost("proxy3.bj.petrochina", 8080); 
			client  = new DefaultHttpClient();  
			client .getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}else {
			client  = new DefaultHttpClient();  
		}
		//��ʼ���������Ӧ��Ϣ����
		HttpResponse response = null;
		//��ʼ���������Ӧ��
		int code = -1;
		try {
			//ִ��get���󣬲�������˷���ֵ����httpresponse����
			response = client.execute(get);
			//�������Ӧ��ȡ����Ӧ���ж�������ֻ����400�Լ������ж�
			//oauthԴ�ĵ���׼400�����ж��������������������
			//400-500(������5000)Tokenֵ�Ƿ�����Ҫ�ͻ����������룬���ͻ��˽���refresh_token����
			//500+����500��Tokenֵ���ڣ���ִ��servlet.destory()��������oauth servletʵ�֣�
			//��Ҫ�ͻ�����������token�������ִ��refresh_token����ˢ����ȷ��tokenͬ������refresh_token
			//ҲΪ�ͻ������»�úϷ�token��Ҫ�ж�֮һ��
			code = response.getStatusLine().getStatusCode();
			if (code >= 400) {
				
				// Access token is invalid or expired.Regenerate the access
				// token
				//���Token
				System.out
						.println("Access token is invalid or expired. Regenerating access token....");
				//ִ��getAccessToken ��ȡtoken
				String accessToken = getAccessToken(oauthDetails,flag);
				
				
				//�ж�token�Ƿ���Ч
				if (isValid(accessToken)) {
					// update the access token
					// System.out.println("New access token: " + accessToken);
					oauthDetails.setAccessToken(accessToken);
					get.removeHeaders(OAuthConfig.AUTHORIZATION);
					get.addHeader(OAuthConfig.AUTHORIZATION,
							getAuthorizationHeaderForAccessToken(oauthDetails
									.getAccessToken()));
					//�ͷź�ʹ�ú��е�ǰtoken��Ϣ��header�������ӷ����
					//ȷ��token��Ч��
					get.releaseConnection();
					response = client.execute(get);
					code = response.getStatusLine().getStatusCode();
					if (code >= 400) {
						throw new RuntimeException(
								"Could not access protected resource. Server returned http code: "
										+ code);

					}

				} else {
					throw new RuntimeException(
							"Could not regenerate access token");
				}

			}

			handleResponse(response);

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			get.releaseConnection();
		}

	}
	//httpget������ setHeader�ľ���������֯����
	public static String getAuthorizationHeaderForAccessToken(String accessToken) {
		return OAuthConfig.BEARER + " " + accessToken;
	}
	
	
	//��ȡToken
	public static String getAccessToken(TokenInfo oauthDetails,boolean flag) {
		//flag�����Ƿ�ʹ�ô�������� trueΪʹ�ã�flaseΪ��ʹ��	
		//�����֤��������֤url
		DefaultHttpClient client;
		HttpPost post = new HttpPost(oauthDetails.getAuthenticationServerUrl());
		
		String clientId = oauthDetails.getClientId();
		String clientSecret = oauthDetails.getClientSecret();
		String scope = oauthDetails.getScope();
		
		
		//���û���password��ʽ��grant-type��֤
		List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
		parametersBody.add(new BasicNameValuePair(OAuthConfig.GRANT_TYPE,
				oauthDetails.getGrantType()));
		parametersBody.add(new BasicNameValuePair(OAuthConfig.USERNAME,
				oauthDetails.getUsername()));
		parametersBody.add(new BasicNameValuePair(OAuthConfig.PASSWORD,
				oauthDetails.getPassword()));

		//�ж�clientId��clientSecret,Scopeͬʱ��Ч�ԣ�
		//�Ӷ�ȷ���û���֤�ֶ����Ƿ���client_id
		if (isValid(clientId)) {
			parametersBody.add(new BasicNameValuePair(OAuthConfig.CLIENT_ID,
					clientId));
		}
		if (isValid(clientSecret)) {
			parametersBody.add(new BasicNameValuePair(
					OAuthConfig.CLIENT_SECRET, clientSecret));
		}
		if (isValid(scope)) {
			parametersBody.add(new BasicNameValuePair(OAuthConfig.SCOPE,
					scope));
		}

		//��ʼ��httpclient,httpresponse�����Լ�׼��accessToken�洢����
		if(flag) {
			HttpHost proxy = new HttpHost("proxy3.bj.petrochina", 8080); 
			client = new DefaultHttpClient();  
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}else {
			client = new DefaultHttpClient();  
		}
		HttpResponse response = null;
		String accessToken = null;
		try {
			//��׼�����봦��
			post.setEntity(new UrlEncodedFormEntity(parametersBody, HTTP.UTF_8));
			
			
			//ִ��post����
			response = client.execute(post);
			//��ȡ�������Ӧ������
			int code = response.getStatusLine().getStatusCode();
			
			//�������Ӧ�������ж������ϲ��־����getProtectedResourceһ�ڹ������жϲ���
			
			//����ķ�����������֤��ʽ�ж����緵��400+���ж����ж�Ϊ����˲��û�����֤
			
			if (code >= 400) {
				System.out
						.println("Authorization server expects Basic authentication");
				// Add Basic Authorization header
				//�ж��������ʾ������username+password ������֤
				//�ɴ˿ɼ�getAccessToken��Token��ȡ����Ĭ�ϲ���grant-type3���ڻ�����֤��Ϣ��
				//һ���Խ�username��ʽ�Լ�client_id����Ϣ���룬Ȼ��ֱ�����ж���
				
				//�ɷ������Ӧ���ж�������Token���ɷ�ʽ��׼��>=400
				//һ��ʼ����param���������grant-type�ֶ�ֻ�ǻ����ų�1��4��5ģʽ��
				//��ȷ��2��3ģʽ�������²����ж�����2��3
				
				//��httppost�������header���ã�������Ϊʹ��username+password������֤��header�ṹ
				//����ʾ���³��Ե�¼��Ϣ���ٴε�¼.
				post.addHeader(
						OAuthConfig.AUTHORIZATION,
						getBasicAuthorizationHeader(oauthDetails.getUsername(),
								oauthDetails.getPassword()));
				System.out.println("Retry with login credentials");
				post.releaseConnection();
				response = client.execute(post);
				//�ٴλ�÷����������벢�����ж���>=400���������header�ṹ��
				//����client_id,client_secret��ʽ�ٴ���֤
				code = response.getStatusLine().getStatusCode();
				if (code >= 400) {
					System.out.println("Retry with client credentials");
					post.removeHeaders(OAuthConfig.AUTHORIZATION);
					post.addHeader(
							OAuthConfig.AUTHORIZATION,
							getBasicAuthorizationHeader(
									oauthDetails.getClientId(),
									oauthDetails.getClientSecret()));
					post.releaseConnection();
					response = client.execute(post);
					code = response.getStatusLine().getStatusCode();
					//����2���ж�ʧ�ܺ��׳�����ʱ�쳣����ʾ�޷�Ϊ��ǰ�û���ȡtoken
					if (code >= 400) {
						throw new RuntimeException(
								"Could not retrieve access token for user: "
										+ oauthDetails.getUsername());
					}
				}

			}
			
			//��ʽ����Ӧ���� ���ݸ�ʽ����handleResponse����
			Map<String, String> map = handleResponse(response);
			//�Ӹ�ʽ������map��ȡ��token,������.
			accessToken = map.get(OAuthConfig.ACCESS_TOKEN);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return accessToken;
	}


public static String getBasicAuthorizationHeader(String username,
		String password) {
	return OAuthConfig.BASIC + " "
			+ encodeCredentials(username, password);
}

//��ʽ�����ݣ�Ĭ�ϲ���json��ʽ
public static Map handleResponse(HttpResponse response) {
	//��ʼ����httpresponse��Ӧ��ʽΪjson ֵΪ"application/json"
	String contentType = OAuthConfig.JSON_CONTENT;
	//�ж�httpresponse���Ƿ���ContentType�ֶΣ�������������ӳ��jsp��MIME����Ӧ�����д���
	//��response����ֵContentType�ֶβ�Ϊ��ʱ����ֵ����contentType�������и�ʽ�ж�
	if (response.getEntity().getContentType() != null) {
		contentType = response.getEntity().getContentType().getValue();
	}
	//��response����ֵ����ȷ��Ϊjson��ʽ��ʹ��handleJsonResponse��������response����ֵ
	if (contentType.contains(OAuthConfig.JSON_CONTENT)) {
		return handleJsonResponse(response);
	//�ж�response����ֵ��ContentType�Ƿ�Ϊx-www-form-urlencoded���͡�������
	} else if (contentType.contains(OAuthConfig.URL_ENCODED_CONTENT)) {
		return handleURLEncodedResponse(response);
		//�ж�response����ֵ��ContentType�Ƿ�ΪXML���͡�������
	} else if (contentType.contains(OAuthConfig.XML_CONTENT)) {
		return handleXMLResponse(response);
	} else {
		// Unsupported Content type
		//�����ж���ʧ��ʱ���׳�����ʱ�쳣������ʾ��Ϣ��response�޷�����������ֻ֧��json xml ��urlencoded��ʽ
		throw new RuntimeException(
				"Cannot handle "
						+ contentType
						+ " content type. Supported content types include JSON, XML and URLEncoded");
	}

}


//ʹ��json����httpresponse����ֵ
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
//����淶������
public static String encodeCredentials(String username, String password) {
	String cred = username + ":" + password;
	String encodedValue = null;
	byte[] encodedBytes = Base64.encodeBase64(cred.getBytes());
	encodedValue = new String(encodedBytes);
	System.out.println("encodedBytes " + new String(encodedBytes));

	byte[] decodedBytes = Base64.decodeBase64(encodedBytes);
	System.out.println("decodedBytes " + new String(decodedBytes));

	return encodedValue;
}
//���httpresponse����ֵ����URLEncoded��ʽ����
public static Map handleURLEncodedResponse(HttpResponse response) {
	Map<String, Charset> map = Charset.availableCharsets();
	Map<String, String> oauthResponse = new HashMap<String, String>();
	Set<Map.Entry<String, Charset>> set = map.entrySet();
	Charset charset = null;
	HttpEntity entity = response.getEntity();

	System.out.println();
	System.out.println("********** Response Received **********");

	for (Map.Entry<String, Charset> entry : set) {
		System.out.println(String.format("  %s = %s", entry.getKey(),
				entry.getValue()));
		if (entry.getKey().equalsIgnoreCase(HTTP.UTF_8)) {
			charset = entry.getValue();
		}
	}

	try {
		List<NameValuePair> list = URLEncodedUtils.parse(
				EntityUtils.toString(entity), Charset.forName(HTTP.UTF_8));
		for (NameValuePair pair : list) {
			System.out.println(String.format("  %s = %s", pair.getName(),
					pair.getValue()));
			oauthResponse.put(pair.getName(), pair.getValue());
		}

	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw new RuntimeException("Could not parse URLEncoded Response");
	}

	return oauthResponse;
}
//���httpresponse����ֵ����XML��ʽ����
public static Map handleXMLResponse(HttpResponse response) {
	Map<String, String> oauthResponse = new HashMap<String, String>();
	try {

		String xmlString = EntityUtils.toString(response.getEntity());
		DocumentBuilderFactory factory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder db = factory.newDocumentBuilder();
		InputSource inStream = new InputSource();
		inStream.setCharacterStream(new StringReader(xmlString));
		Document doc = db.parse(inStream);

		System.out.println("********** Response Receieved **********");
		parseXMLDoc(null, doc, oauthResponse);
	} catch (Exception e) {
		e.printStackTrace();
		throw new RuntimeException(
				"Exception occurred while parsing XML response");
	}
	return oauthResponse;
}
//���httpresponse����ֵ����XML��ʽ����ʱ�����õ�XMLdoc������֯����
public static void parseXMLDoc(Element element, Document doc,
		Map<String, String> oauthResponse) {
	NodeList child = null;
	if (element == null) {
		child = doc.getChildNodes();

	} else {
		child = element.getChildNodes();
	}
	for (int j = 0; j < child.getLength(); j++) {
		if (child.item(j).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
			org.w3c.dom.Element childElement = (org.w3c.dom.Element) child
					.item(j);
			if (childElement.hasChildNodes()) {
				System.out.println(childElement.getTagName() + " : "
						+ childElement.getTextContent());
				oauthResponse.put(childElement.getTagName(),
						childElement.getTextContent());
				parseXMLDoc(childElement, null, oauthResponse);
			}

		}
	}
}
public static boolean isValid(String str) {
	return (str != null && str.trim().length() > 0);
}
}