package javatest;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;


class jasontest {
	public static void jason_get() throws Exception{
		String city=java.net.URLEncoder.encode("±±¾©","UTF-8");
		
		String apiUrl = String.format("http://www.sojson.com/open/api/weather/json.shtml?city=%s", city);
		
		URL url=new URL(apiUrl);
		URLConnection open = url.openConnection();
		InputStream input=open.getInputStream();
		
		String result=org.apache.commons.io.IOUtils.toString(input,"UTF-8");
		
		System.out.println(result);
	}
}
