package javatest;

import java.util.*;

import javax.swing.text.html.HTMLDocument.Iterator;

public class javatest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		// jasontest.jason_get();
		List<List<String>> temp = null;
		// 读取文件整理格式
		fileprocess file = new fileprocess();
		temp = file.sfilein("d://1.s");		
		for (int i = 0; i < temp.size(); i++) {
			List<String> temp1 = temp.get(i);
			for (int j = 0; j < 5; j++) {
				System.out.println(temp1.get(j));
			}

		}

	}

}
