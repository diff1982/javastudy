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
	
	//创建从配置文件OAuthConfig中获取各种参数设置的方法
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
	
	//将物理配置文件转为流进行读取，并直接返回OAuthConfig对象的方法
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
	
	//获取受Token保护的资源方法
	public static void getProtectedResource(Properties config,boolean flag) {
		//flag控制是否使用代理服务器 true为使用，flase为不使用		
		//获取资源服务器认证URL
		DefaultHttpClient client ;
		String resourceURL = config
				.getProperty(OAuthConfig.RESOURCE_SERVER_URL);
		
		//调入OAuthConfig对象读取所有常量对应值。
		TokenInfo oauthDetails = createOAuthDetails(config);
		
		//使用Httpget(url)方法实例化httpget对象，
		//准备以资源服务器验证URL为参数发送get请求。
		HttpGet get = new HttpGet(resourceURL);
		
		//继续准备httpget对象这里主要工作是设置http头响应结构，以及添加加密方式，和获取的Token
		//此步骤为oauth2验证用户登录状态核心步骤
		get.addHeader(OAuthConfig.AUTHORIZATION,
				getAuthorizationHeaderForAccessToken(oauthDetails
						.getAccessToken()));
		
		//创建java httpclient对象，默认参数为空
		//判断flag是否使用代理设置
		if(flag) {
			HttpHost proxy = new HttpHost("proxy3.bj.petrochina", 8080); 
			client  = new DefaultHttpClient();  
			client .getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}else {
			client  = new DefaultHttpClient();  
		}
		//初始化服务端响应信息对象
		HttpResponse response = null;
		//初始化服务端响应码
		int code = -1;
		try {
			//执行get请求，并将服务端返回值赋予httpresponse对象
			response = client.execute(get);
			//服务端响应获取及响应码判定，这里只做出400以及以上判定
			//oauth源文档标准400以上判定的两种情况（常见）。
			//400-500(不包含5000)Token值非法，需要客户端重新申请，并客户端进行refresh_token操作
			//500+（含500）Token值过期，已执行servlet.destory()方法，（oauth servlet实现）
			//需要客户端重新申请token，服务端执行refresh_token进行刷新以确保token同步，此refresh_token
			//也为客户端重新获得合法token重要判定之一。
			code = response.getStatusLine().getStatusCode();
			if (code >= 400) {
				
				// Access token is invalid or expired.Regenerate the access
				// token
				//输出Token
				System.out
						.println("Access token is invalid or expired. Regenerating access token....");
				//执行getAccessToken 获取token
				String accessToken = getAccessToken(oauthDetails,flag);
				
				
				//判定token是否有效
				if (isValid(accessToken)) {
					// update the access token
					// System.out.println("New access token: " + accessToken);
					oauthDetails.setAccessToken(accessToken);
					get.removeHeaders(OAuthConfig.AUTHORIZATION);
					get.addHeader(OAuthConfig.AUTHORIZATION,
							getAuthorizationHeaderForAccessToken(oauthDetails
									.getAccessToken()));
					//释放后使用含有当前token信息的header重新连接服务端
					//确认token有效性
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
	//httpget对象中 setHeader的具体内容组织方法
	public static String getAuthorizationHeaderForAccessToken(String accessToken) {
		return OAuthConfig.BEARER + " " + accessToken;
	}
	
	
	//获取Token
	public static String getAccessToken(TokenInfo oauthDetails,boolean flag) {
		//flag控制是否使用代理服务器 true为使用，flase为不使用	
		//获得认证服务器认证url
		DefaultHttpClient client;
		HttpPost post = new HttpPost(oauthDetails.getAuthenticationServerUrl());
		
		String clientId = oauthDetails.getClientId();
		String clientSecret = oauthDetails.getClientSecret();
		String scope = oauthDetails.getScope();
		
		
		//采用基础password方式的grant-type验证
		List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
		parametersBody.add(new BasicNameValuePair(OAuthConfig.GRANT_TYPE,
				oauthDetails.getGrantType()));
		parametersBody.add(new BasicNameValuePair(OAuthConfig.USERNAME,
				oauthDetails.getUsername()));
		parametersBody.add(new BasicNameValuePair(OAuthConfig.PASSWORD,
				oauthDetails.getPassword()));

		//判定clientId，clientSecret,Scope同时有效性，
		//从而确定用户验证字段中是否含有client_id
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

		//初始化httpclient,httpresponse对象，以及准备accessToken存储对象。
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
			//标准化编码处理
			post.setEntity(new UrlEncodedFormEntity(parametersBody, HTTP.UTF_8));
			
			
			//执行post请求
			response = client.execute(post);
			//获取服务端响应返回码
			int code = response.getStatusLine().getStatusCode();
			
			//服务端响应返回码判定，故障部分具体见getProtectedResource一节故障码判断部分
			
			//这里的返回码用于验证方式判定，如返回400+则判定则判定为服务端采用基本验证
			
			if (code >= 400) {
				System.out
						.println("Authorization server expects Basic authentication");
				// Add Basic Authorization header
				//判定结果后，提示：采用username+password 进行验证
				//由此可见getAccessToken即Token获取过程默认采用grant-type3，在基础验证信息中
				//一次性将username方式以及client_id等信息加入，然后分别进行判定。
				
				//由服务端响应码判定服务器Token生成方式标准是>=400
				//一开始随着param参数进入的grant-type字段只是基本排除1，4，5模式，
				//而确定2或3模式则由以下步骤判定，先2后3
				
				//对httppost对象进行header设置，并设置为使用username+password基础验证的header结构
				//并提示重新尝试登录信息，再次登录.
				post.addHeader(
						OAuthConfig.AUTHORIZATION,
						getBasicAuthorizationHeader(oauthDetails.getUsername(),
								oauthDetails.getPassword()));
				System.out.println("Retry with login credentials");
				post.releaseConnection();
				response = client.execute(post);
				//再次获得服务器返回码并进行判定，>=400情况则重置header结构，
				//采用client_id,client_secret方式再次认证
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
					//连续2次判定失败后，抛出运行时异常，提示无法为当前用户获取token
					if (code >= 400) {
						throw new RuntimeException(
								"Could not retrieve access token for user: "
										+ oauthDetails.getUsername());
					}
				}

			}
			
			//格式化响应数据 数据格式化见handleResponse方法
			Map<String, String> map = handleResponse(response);
			//从格式化数据map中取得token,并返回.
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

//格式化数据，默认采用json格式
public static Map handleResponse(HttpResponse response) {
	//初始设置httpresponse响应格式为json 值为"application/json"
	String contentType = OAuthConfig.JSON_CONTENT;
	//判定httpresponse中是否有ContentType字段，并根据其内容映射jsp中MIME的响应，进行处理。
	//当response返回值ContentType字段不为空时，将值赋给contentType进而进行格式判断
	if (response.getEntity().getContentType() != null) {
		contentType = response.getEntity().getContentType().getValue();
	}
	//当response返回值对象确认为json格式，使用handleJsonResponse方法处理response返回值
	if (contentType.contains(OAuthConfig.JSON_CONTENT)) {
		return handleJsonResponse(response);
	//判定response返回值的ContentType是否为x-www-form-urlencoded类型。并处理
	} else if (contentType.contains(OAuthConfig.URL_ENCODED_CONTENT)) {
		return handleURLEncodedResponse(response);
		//判定response返回值的ContentType是否为XML类型。并处理
	} else if (contentType.contains(OAuthConfig.XML_CONTENT)) {
		return handleXMLResponse(response);
	} else {
		// Unsupported Content type
		//所有判定均失败时，抛出运行时异常，并提示信息，response无法解析，程序只支持json xml 和urlencoded方式
		throw new RuntimeException(
				"Cannot handle "
						+ contentType
						+ " content type. Supported content types include JSON, XML and URLEncoded");
	}

}


//使用json解析httpresponse返回值
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
//编码规范化方法
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
//针对httpresponse返回值进行URLEncoded格式方法
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
//针对httpresponse返回值进行XML格式方法
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
//针对httpresponse返回值进行XML格式方法时，调用的XMLdoc数据组织方法
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