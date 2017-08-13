package javatest;

import java.io.FileWriter;
import java.util.Map;
//注意导入的json包
import net.sf.json.*;

public class JsonFormatAnalysis {
	public static void PointList(JSONArray entry) {
//		JSONArray jsonData = JSONArray.fromObject(strData);
		for(int i=0;i<entry.size();i++){
			JSONObject job = entry.getJSONObject(i);
			System.out.println("RTime :" + job.get("RTime"));
			System.out.println("Remarks :" + job.get("Remarks"));
			System.out.println("UserID :" + job.get("UserID"));
			System.out.println("UTime :" + job.get("UTime"));
			System.out.println("PointID :" + job.get("PointID"));
			System.out.println("PointName :" + job.get("PointName"));
			System.out.println("PointType :" + job.get("PointType"));
			System.out.println("X :" + job.get("X"));
			System.out.println("Y :" + job.get("Y"));
			try {
				FileWriter writer = new FileWriter("i:\\pointlist.text",true);
				writer.write("RTime :" + job.get("RTime"));
				writer.write("\r\n");
				writer.write("Remarks :" + job.get("Remarks"));
				writer.write("\r\n");
				writer.write("UserID :" + job.get("UserID"));
				writer.write("\r\n");
				writer.write("UTime :" + job.get("UTime"));
				writer.write("\r\n");
				writer.write("PointID :" + job.get("PointID"));
				writer.write("\r\n");
				writer.write("PointName :" + job.get("PointName"));
				writer.write("\r\n");
				writer.write("PointType :" + job.get("PointType"));
				writer.write("\r\n");
				writer.write("X :" + job.get("X"));
				writer.write("\r\n");
				writer.write("Y :" + job.get("Y"));
				writer.write("\r\n");
				writer.write("\r\n");
				writer.write("\r\n");
				writer.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
