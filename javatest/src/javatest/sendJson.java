package javatest;

import org.json.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.List;
import java.net.URL;

class sendJson{
	public static String sendJson(String url, String param) throws IOException {
		String result="";
		BufferedReader in= null;
			//构造URL 
			URL realUrl= new URL(url);
			//连接一个指定的URL
			URLConnection connection = realUrl.openConnection();
			
			//杂项设置
			connection.setRequestProperty("accept","*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			
			
			//开启conn.getOutputStream().write()使用，大数据量写
			connection.setDoOutput(true);
			//开启conn.getInputStream().read()使用，大数据量读
			connection.setDoInput(true);

			
			try {
				
			//组织json数据
				JSONObject user = new JSONObject();
				user.put("email", "123@163.com"); 
                user.put("password", "123456789");
                
            //将数据添加至输出流
            OutputStream out = connection.getOutputStream();  
            
            //确定编码
            out.write(user.toString().getBytes());
            
            //创建处理流,处理字节流和字符流
            PrintWriter  pwriter=new PrintWriter(out);
            
            //刷新缓冲区
            pwriter.flush();
            
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while((line=in.readLine())!=null){
            	result +=line;
            }
				
			}catch(Exception e) {}
		
	return result;
	}
	}