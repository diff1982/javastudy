package javatest;
import java.util.*;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;


public class fileprocess {
	int[][] dataFormat= {{0,1},{2,3},{20,24},{25,27},{49,56},{59,66}};
	BufferedReader reader = null;
	List<List<String>> res= new ArrayList<List<String>>();
	public List<List<String>> sfilein(String filename) {
		File sfile = new File(filename);
		try {
		reader = new BufferedReader(new FileReader(sfile));
		String tempString = null;
		int line =1;
		while((tempString = reader.readLine())!=null) {			
			List<String> temp = new ArrayList<String>();
			String filetype = tempString.substring(0, 1);
			String lineNo = tempString.substring(2,3);
			String pointNo = tempString.substring(19,24);
			String xCodinate = tempString.substring(48,56);
			String yCodinate = tempString.substring(58,66);
			temp.add(filetype);
			temp.add(lineNo);
			temp.add(pointNo);
			temp.add(xCodinate);
			temp.add(yCodinate);
			res.add(temp);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if(reader!=null)
				try {
					reader.close();
					}catch(IOException e1) {
					}
		}
		return res;
	}
}
