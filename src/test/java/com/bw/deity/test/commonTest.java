package com.bw.deity.test;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

public class commonTest {

	//字符串替换的test
	@Test
	public void test1() throws Exception
	{
		String fname = "public class ItemTest↵{↵ private final int id;↵ public ItemTest(int id){this.id=id;}↵ public void updateId(int newId){id=newId;}↵↵ public static void main(String[] args)↵ {↵  ItemTest fa = new ItemTest(42);↵  fa.updateId(69);↵  System.out.println(fa.id);↵ }↵}↵下面描述正确的是：".replaceAll("↵", "").replaceAll(" ", "").replaceAll("\\s*|\t|\r|\n", "");
		System.out.println(fname);
		System.out.println(fname);
	}
	public static String StringFilter(String str) throws Exception { 
	// 只允许字母和数字 // String regEx ="[^a-zA-Z0-9]"; 
	// 清除掉所有特殊字符 
	String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"; 
	Pattern p = Pattern.compile(regEx); 
	Matcher m = p.matcher(str);
	return m.replaceAll("").trim();
	} 
	
	//analysis to message
	@Test
	public void test2() throws Exception
	{
		Document parse = Jsoup.parse(new File("C:\\Users\\MagicBook\\Desktop\\k.html"),"utf-8");
		
		
		//anaylysis to message
		Elements problems = parse.select(".question");
		problems.forEach(e->{
			//拿到 q
			System.out.println(e.select(".question-id").text());
			
			//拿到problems
			System.out.println(e.select(".titleContent").text());
			
			//拿到all a
			System.out.println(e.select(".question-list-item").toString());
			
			//拿到 t
			String type =e.select(".question-type").text();
			System.out.println(e.select(".question-type").text());
			
			
			//先判断此类型
			if(type=="0")
			{
				//查询出结果
				HashMap map = new HashMap();
				Object object = map.get("answers");
				if(object.equals("A"))
				{
					e.select("sdf").forEach(a->{a.text().equals("");});
				}
				
			}
			if(type=="1")
			{
				//查询出结果
				HashMap map = new HashMap();
				Object object = map.get("answers");
				//拿到正确选项   根据if else  将正确的答案取出
				if(object.equals("A"))
				{
					Object object2 = map.get("a");
					e.select("sdf").forEach(a->{a.text().equals(object2);});
				}
			}
			if(type=="2")
			{
				
			}
		});
		
		//拿到40 个size
		System.out.println(problems.size());
	}
	
	//测试answers
	@Test
	public void test3() throws Exception
	{
Document parse = Jsoup.parse(new File("C:\\Users\\MagicBook\\Desktop\\k.html"),"utf-8");
		
		
		//anaylysis to message
		Elements problems = parse.select(".question");
		
		problems.forEach(e->{
			
			
			
			Elements Eans = e.select(".question-list-item");
			Eans.forEach(a->{
					System.out.println( a.select(".question-input").attr("value").toString());
			});
		});
		
	}
}
