package klsps;

import java.util.List;

import klsps.CConnectInterface;

public class klsps {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CConnectInterface cc = new CConnectInterface();
		//��ʼֵ�趨
		String proxy = cc.getProxyAddr();
		cc.setUserName("13400231981");
		cc.setPassWord("123456");
		cc.setAuthenticationServerUrl("http://123.127.139.225:9099/tgis_server/api/user/login");
		cc.setSourceServerUrl("http://123.127.139.225:9099/tgis_server/api/userpoint/list");
		//��ȡtoken����
//		List<String> list = cc.getServerStatus();
//		String accessToken = list.get(0);
//		int status= Integer.parseInt(list.get(1));
		
		int status = cc.getServerStatus();
		String accessToken = cc.getToken();
		System.out.println(accessToken);
		System.out.println(status);
		cc.getProtectedResource(proxy);
		//��ȡ�ܱ�����Դ����
		
	}

}
