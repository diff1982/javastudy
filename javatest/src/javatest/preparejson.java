package javatest;
import org.json.*;
import java.io.File;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import org.json.*;



public class preparejson {	
	public void prepareJonObject(List<List<String>> ls) {
		for(int i = 0;i<ls.size();i++) {
			List<String> temp = (List<String>) ls.get(i);
			buildJson(temp);
				
			}
	}

	private String buildJson(List<String> str) {
		if(str.size()<5) {
			System.out.println("Error:Json need at lest 5 param!");
			return null;
		}
		Map map = new HashMap();
		map.put("SLine", str.get(0));
		map.put("SPoint",str.get(1));
		map.put("SPoint",str.get(2));
		map.put("SPoint",str.get(3));
		map.put("SPoint",str.get(4));
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		jsonArray.put(map);
		jsonObject.put("S",jsonArray);
		System.out.println(jsonObject.toString());
		return null;
		}
} 


