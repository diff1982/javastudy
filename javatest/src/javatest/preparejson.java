package javatest;
import org.json.*;
import java.io.File;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import javax.json;



public class preparejson {	
	public static void prepareJonObject(List<List<String>> ls) {
		for(int i = 0;i<ls.size();i++) {
			List<String> temp = (List<String>) ls.get(i);			
			for(int j=0;j<5;j++) {
				
				}
			}
	}

	private String buildJson(String[] str) {
		if(str.length<5) {
			System.out.println("Error:Json need at lest 5 param!");
			return null;
		}
		Map map = new HashMap();
		map.put("SLine", str[0]);
		map.put("SPoint",str[1]);
		map.put("SPoint",str[2]);
		map.put("SPoint",str[3]);
		map.put("SPoint",str[4]);
		JSONObject jsonobject = JSONObject.fromObject(map);
		return map;
		}
		
	
} 


