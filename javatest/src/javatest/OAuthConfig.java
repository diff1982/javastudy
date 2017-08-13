package javatest;

public class OAuthConfig {
	/*OAuth配置文件，用来设定Grant-type  username password等连接信息。
	 * 根据OAth文档应使用pojo或bean形式予以共享,这里考虑到结合本身应用。
	 * 使用了常量设置，其含义同变量命名。具体见TokenInfo中详细说明。
	 * 
	 * 具体需要用户自定义扩展时，直接采用读取文件方式-见OAuthUtils.getClientConfigProps方法
	 * 
	 * 或在客户端代码中直接设置变量对应常量即可-本例采用此方法，
	 * 具体变量设置见cehui_test.java中NotAuthTestPost方法；
	 */
	public static final String ACCESS_TOKEN="access_token";
	public static final String CLIENT_ID="client_id";
	public static final String CLIENT_SECRET = "client_secret";
	public static final String REFRESH_TOKEN = "refresh_token";
	//Grant-type2 采用username和password认证
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	
	//分布式情况下获取Token的认证服务器url，Token从此生成
	public static final String AUTHENTICATION_SERVER_URL = "authentication_server_url";
	//配置文件路径
	public static final String CONFIG_FILE_PATH = "com/ibm/oauth/Oauth2Client.config";
	
	//真正用于   访问资源的服务器   认证url链接 
	public static final String RESOURCE_SERVER_URL = "resource_server_url";
	
	
	public static final String GRANT_TYPE = "grant_type";
	public static final String SCOPE = "scope";
	public static final String AUTHORIZATION = "Authorization";
	
	//Token加密方式，主要用于setHeader时加入httpget对象，对用户登录状态进行确认所
	//必须明文标识的字段。
	public static final String BEARER = "Bearer";
	public static final String BASIC = "Basic";
	public static final String JSON_CONTENT = "application/json";
	public static final String XML_CONTENT = "application/xml";
	public static final String URL_ENCODED_CONTENT = "application/x-www-form-urlencoded";
	
	//设置返回值 状态信息.
	public static final int HTTP_OK = 200;
	public static final int HTTP_FORBIDDEN = 403;
	public static final int HTTP_UNAUTHORIZED = 401;

}
