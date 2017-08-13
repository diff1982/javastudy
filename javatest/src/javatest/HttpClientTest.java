package javatest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpClientTest {
	  /**
	  * 测试HttpClient发送各种请求的方法
	  * 
	  * @author Administrator
	  *
	  */
	public static final String REQUEST_URL = "http://123.127.139.225:9099/tgis_server/api/user/login";
	   /**
	   * 测试发送GET请求
	   */
	public void get() {
	       // 1. 创建一个默认的client实例
		CloseableHttpClient client = HttpClients.createDefault();
		try {
		// 2. 创建一个httpget对象
			HttpGet httpGet = new HttpGet(REQUEST_URL);
	        System.out.println("executing GET request " + httpGet.getURI());
	    // 3. 执行GET请求并获取响应对象
	        CloseableHttpResponse resp = client.execute(httpGet);
	        try {
	    // 4. 获取响应体
            HttpEntity entity = resp.getEntity();
            System.out.println("------");
       // 5. 打印响应状态
            System.out.println(resp.getStatusLine());
      // 6. 打印响应长度和响应内容
            if (null != entity) {
            	System.out.println("Response content length = " + entity.getContentLength());
	            System.out.println("Response content is:\n" + EntityUtils.toString(entity));
	           }
            System.out.println("------");
            } finally {
            	// 7. 无论请求成功与否都要关闭resp
            	resp.close();
            	}
	        } catch (ClientProtocolException e) {
	        	e.printStackTrace();
	        	} catch (ParseException e) {
	        		e.printStackTrace();
	        		} catch (IOException e) {
	        			e.printStackTrace();
	        			} finally {
	        			// 8. 最终要关闭连接，释放资源
	        			try {
	                  client.close();
	              } catch (Exception e) {
	                  e.printStackTrace();
	              }
	          }
	      }
	  
	      /**
	       * 测试发送POST请求
	       */
	      public void post() {
	          // 1. 获取默认的client实例
	          CloseableHttpClient client = HttpClients.createDefault();
	          // 2. 创建httppost实例
	          HttpPost httpPost = new HttpPost(REQUEST_URL);
	          // 3. 创建参数队列（键值对列表）
	          List<NameValuePair> paramPairs = new ArrayList<NameValuePair>();
	          paramPairs.add(new BasicNameValuePair("username", "13400231981"));
	          paramPairs.add(new BasicNameValuePair("password", "123456"));
	 
	          UrlEncodedFormEntity entity;
	  
	          try {
	              // 4. 将参数设置到entity对象中
	             entity = new UrlEncodedFormEntity(paramPairs, "UTF-8");
	  
	             // 5. 将entity对象设置到httppost对象中
	             httpPost.setEntity(entity);
	 
	             System.out.println("executing POST request " + httpPost.getURI());
	 
	             // 6. 发送请求并回去响应
	             CloseableHttpResponse resp = client.execute(httpPost);
	 
	             try {
	                 // 7. 获取响应entity
	                 HttpEntity respEntity = resp.getEntity();
	 
	                 // 8. 打印出响应内容
	                 if (null != respEntity) {
	                     System.out.println("------");
	                     System.out.println(resp.getStatusLine());
	                     System.out.println("Response content is : \n" + EntityUtils.toString(respEntity, "UTF-8"));
	 
	                     System.out.println("------");
	                 }
	             } finally {
	                 // 9. 关闭响应对象
	                 resp.close();
	             }
	 
	         } catch (ClientProtocolException e) {
	             e.printStackTrace();
	         } catch (UnsupportedEncodingException e) {
	             e.printStackTrace();
	         } catch (IOException e) {
	             e.printStackTrace();
	         } finally {
	             // 10. 关闭连接，释放资源
	             try {
	                 client.close();
	             } catch (Exception e) {
	                 e.printStackTrace();
	             }
	         }
	     }
}
