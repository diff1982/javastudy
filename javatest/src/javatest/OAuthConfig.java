package javatest;

public class OAuthConfig {
	/*OAuth�����ļ��������趨Grant-type  username password��������Ϣ��
	 * ����OAth�ĵ�Ӧʹ��pojo��bean��ʽ���Թ���,���￼�ǵ���ϱ���Ӧ�á�
	 * ʹ���˳������ã��京��ͬ���������������TokenInfo����ϸ˵����
	 * 
	 * ������Ҫ�û��Զ�����չʱ��ֱ�Ӳ��ö�ȡ�ļ���ʽ-��OAuthUtils.getClientConfigProps����
	 * 
	 * ���ڿͻ��˴�����ֱ�����ñ�����Ӧ��������-�������ô˷�����
	 * ����������ü�cehui_test.java��NotAuthTestPost������
	 */
	public static final String ACCESS_TOKEN="access_token";
	public static final String CLIENT_ID="client_id";
	public static final String CLIENT_SECRET = "client_secret";
	public static final String REFRESH_TOKEN = "refresh_token";
	//Grant-type2 ����username��password��֤
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	
	//�ֲ�ʽ����»�ȡToken����֤������url��Token�Ӵ�����
	public static final String AUTHENTICATION_SERVER_URL = "authentication_server_url";
	//�����ļ�·��
	public static final String CONFIG_FILE_PATH = "com/ibm/oauth/Oauth2Client.config";
	
	//��������   ������Դ�ķ�����   ��֤url���� 
	public static final String RESOURCE_SERVER_URL = "resource_server_url";
	
	
	public static final String GRANT_TYPE = "grant_type";
	public static final String SCOPE = "scope";
	public static final String AUTHORIZATION = "Authorization";
	
	//Token���ܷ�ʽ����Ҫ����setHeaderʱ����httpget���󣬶��û���¼״̬����ȷ����
	//�������ı�ʶ���ֶΡ�
	public static final String BEARER = "Bearer";
	public static final String BASIC = "Basic";
	public static final String JSON_CONTENT = "application/json";
	public static final String XML_CONTENT = "application/xml";
	public static final String URL_ENCODED_CONTENT = "application/x-www-form-urlencoded";
	
	//���÷���ֵ ״̬��Ϣ.
	public static final int HTTP_OK = 200;
	public static final int HTTP_FORBIDDEN = 403;
	public static final int HTTP_UNAUTHORIZED = 401;

}
