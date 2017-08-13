package javatest;

public class TokenInfo {
	//Token������Χ
	private String scope;
	/*��Ȩ���� ����
	 * 1.authorization_code ��Ȩ��ģʽ(�Ȼ�ȡcode �ٻ�ȡtoken)
	 * ������Ĳ���ģʽ2
	 * 2.password-����ģʽ��ʹ��username password ֱ�ӻ�Token��
	 * 
	 * 3.client-credentials-�ͻ���ģʽ(���κ���Ȩ�û����û�����������ע�ᣬȻ��
	 * ���Լ���ǰ�û����ʷ������Դ)
	 * 
	 * 4.implicit-��ģʽ����servlet��redirect_url�ֶε�Hash�У�����Token������
	 * ��b/sģʽ��
	 * 			  
	 * 5.refresh_token-�ǻ�ȡtoken�࣬����ִ��token_ˢ�£�����token�Ŵ�ʹ��
	 */
	private String grantType;
	
	//���grantType ��3 ʹ�ã���ʶ����Ȩ�ͻ���Ψһ�ԡ�
	private String clientId;
	//ͬclientId ���ʹ��
	private String clientSecret;
	
	/*����oath2��access_token�ӿڵķ���ֵ��access_token�ӿ�����ʽ��������Ϊpost��
	 *����ΪString�ֶ�ʱ����������Token������ּ�ڽ������û���Ψһӳ���ϵ���ĵ���д��Ӧ��
	 *ʹ�ö����ķ�����ʶ���¼״̬����ֵ�����UID�ֶβ���ʹ����Ϊ��¼ʶ����ж���
	 */
	private String accessToken;
	
	//refreshToken�������������Ȩ�ޣ��Լ���ʱ���ԭtokenʧЧ�������ʹ�á�����һ����Ҫ
	//���������Ŵ�ʱʹ�ã��������´ӿͻ�������ˢ��Token����������������³��Ի�ȡToken
	private String refreshToken;
	
	//grantType��2ģʽʹ�á�
	private String username;
	//ͬusername
	private String password;
	//����˷���Token��֤��servlet��ַ����Ӧweb.xml����servlet-name������
	private String authenticationServerUrl;
	
	
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getGrantType() {
		return grantType;
	}
	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getClientSecret() {
		return clientSecret;
	}
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public String getAuthenticationServerUrl() {
		return authenticationServerUrl;
	}
	public void setAuthenticationServerUrl(String authenticationServerUrl) {
		this.authenticationServerUrl = authenticationServerUrl;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}
