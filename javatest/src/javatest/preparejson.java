package javatest;
import org.json.*;
import java.io.File;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class preparejson {
	JSONObject js=null;
	public JSONObject prepareJonObject(List<List<String>> ls) {
		//预留String type参数，RS文件格式相同，使用同一种方法产生json
		for(int i = 0;i<ls.size();i++) {
			List<String> temp = (List<String>) ls.get(i);
			Map mpt = buildJson(temp);
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonObject = new JSONObject();
			jsonArray.put(mpt);
			JSONObject js = jsonObject.put("S",jsonArray);
			System.out.println(jsonObject.toString());
			}
		return js;
	}

	private Map buildJson(List<String> str) {
		if(str.size()<6) {
			System.out.println("Error:Json need at lest 6 param!");
			return null;
		}
		Map map = new HashMap();
		map.put("Type",str.get(0));
		map.put("SLine",str.get(1));
		map.put("SPoint",str.get(2));
		map.put("SX",str.get(3));
		map.put("SY",str.get(4));
		map.put("SZ",str.get(5));
		
		return map;
		}
} 


