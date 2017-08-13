package javatest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;;

public class httpPostWithJSON {
	
	public httpPostWithJSON() {};
	
	public static String postJson(String url)throws Exception{
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpClient client = HttpClients.createDefault();
		String respContent  = null;
		
		JSONObject jsonParam = new JSONObject();
		jsonParam.put("name", "admin");
		jsonParam.put("password", "123456");

		StringEntity entity = new StringEntity(jsonParam.toString(),"utf-8");
		
		entity.setContentEncoding("UTF-8");
		entity.setContentType("application/json");
		httpPost.setEntity(entity);
		System.out.println();
		
		HttpResponse resp = client.execute(httpPost);
		if(resp.getStatusLine().getStatusCode()==200) {
			HttpEntity he = resp.getEntity();
			respContent = EntityUtils.toString(he,"UTF-8");
		}
		return respContent;
		
	}

}
