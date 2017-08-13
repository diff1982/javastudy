package javatest;

public class TokenInfo {
	//Token作用域范围
	private String scope;
	/*授权类型 包含
	 * 1.authorization_code 授权码模式(先获取code 再获取token)
	 * 测绘中心采用模式2
	 * 2.password-密码模式（使用username password 直接换Token）
	 * 
	 * 3.client-credentials-客户端模式(无任何授权用户，用户均先向服务端注册，然后
	 * 以自己当前用户访问服务端资源)
	 * 
	 * 4.implicit-简化模式（在servlet的redirect_url字段的Hash中，传递Token，仅适
	 * 用b/s模式）
	 * 			  
	 * 5.refresh_token-非获取token类，纯粹执行token_刷新，用于token排错使用
	 */
	private String grantType;
	
	//配合grantType 中3 使用，标识被授权客户端唯一性。
	private String clientId;
	//同clientId 配合使用
	private String clientSecret;
	
	/*代表oath2的access_token接口的返回值，access_token接口请求方式大多数情况为post，
	 *其作为String字段时，惯例返回Token内容主旨在建立与用户的唯一映射关系，文档中写明应当
	 *使用独立的方法来识别登录状态，而值本身的UID字段不能使用作为登录识别的判定。
	 */
	private String accessToken;
	
	//refreshToken更多的用来更改权限，以及长时间后原token失效的情况下使用。另外一个主要
	//作用则是排错时使用，仅仅重新从客户端请求刷新Token，配合其他方法重新尝试获取Token
	private String refreshToken;
	
	//grantType中2模式使用。
	private String username;
	//同username
	private String password;
	//服务端发放Token认证的servlet地址，对应web.xml解析servlet-name部内容
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
