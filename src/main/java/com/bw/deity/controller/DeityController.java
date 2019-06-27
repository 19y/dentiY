package com.bw.deity.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.magicBook.cms.dao.DeityMapper;

@Controller
public class DeityController {

	private List<Map> list = new ArrayList<Map>();
	
	private String Cookie = "";
	
	@Resource
	private DeityMapper mapper;
	
	//请求登录界面需要cookie
	@ResponseBody
	@RequestMapping(value="exam/login.do",produces="text/html;charset=UTF-8")
	public String login_do(HttpServletRequest request,HttpServletResponse response) throws IOException
	{
		Connection connect = Jsoup.connect("http://172.16.10.111/exam/login.do");
		Document document = connect.get();
		
		return document.toString();
	}
	
	//请求验证码
	@RequestMapping(value="exam/validationCode.do")
	public void validationCode(HttpServletRequest request,HttpServletResponse response) throws IOException 
	{
		
		Response rest = Jsoup.connect("http://172.16.10.111/exam//validationCode.do") .ignoreContentType(true)
                .method(Method.GET)
                .execute();
		BufferedInputStream bodyStream = rest.bodyStream();
		BufferedImage read = ImageIO.read(bodyStream);
		ImageIO.write(read, "jpeg", response.getOutputStream());
		
	}
	
	// 模拟登录
	@ResponseBody
	@RequestMapping(value="exam/gologin.do",produces="text/html;charset=UTF-8")
	public String exam_login(String username,String password,String validationCode) throws IOException
	{
		
		Connection cookie = Jsoup.connect("http://172.16.10.111/exam/exam/home.do");
		cookie.cookie("exam", "fc9c0acb-87ec-4d2f-817a-dcc0f4e43a60");
		Document document = cookie.get();
		return document.toString();
	}
	
	//考题展示
	@ResponseBody
	@RequestMapping(value="exam/exam/list.do",produces="text/html;charset=UTF-8")
	public String exam_list() throws IOException
	{
		Connection cookie = Jsoup.connect("http://172.16.10.111/exam/exam/list.do");
		cookie.cookie("exam", "fc9c0acb-87ec-4d2f-817a-dcc0f4e43a60");
		Document document = cookie.get();
		return document.toString();
		
	}
	
	//查看带考试
	@ResponseBody
	@RequestMapping(value="exam/exam/lookExamDetail.do",produces="text/html;charset=UTF-8")
	public String exam_look() throws IOException
	{
		Connection cookie = Jsoup.connect("http://172.16.10.111/exam/exam/lookExamDetail.do");
		cookie.cookie("exam", "fc9c0acb-87ec-4d2f-817a-dcc0f4e43a60");
		Document document = cookie.get();
		return document.toString();
		
	}
	
	
	//交卷
	@ResponseBody
	@RequestMapping(value="exam/exam/saveBatch.do")
	public Map<String,String> exam_postCoreScore(HttpServletRequest request,@RequestBody List<Map> dataStr) throws IOException
	{
		/*StringBuffer requestURL = request.getRequestURL();
		
		System.out.println(requestURL);
		Connection cookie = Jsoup.connect("http://172.16.10.111/exam/exam/lookExamDetail.do");
		cookie.cookie("exam", "fc9c0acb-87ec-4d2f-817a-dcc0f4e43a60");
		Document document = cookie.get();*/
		Map<String, String> map = new HashMap<String,String>();
		map.put("ok", "true");
		map.put("msg", "操作成功");
		Map<String, String[]> parameterMap = request.getParameterMap();
		Set<String> keySet = parameterMap.keySet();
		keySet.forEach(e->{Arrays.asList(parameterMap.get(e)).forEach(System.out::println);});
	
		System.out.println(dataStr.toString());
		dataStr.forEach(e->{System.out.println(e.get("q"));});
		
		System.out.println(JSONArray.toJSONString(list));
		
		
		return map;
		
	}
	
	
	//首页
	@ResponseBody
	@RequestMapping(value="exam/exam/home.do",produces="text/html;charset=UTF-8")
	public String exam_home() throws IOException
	{
		Connection cookie = Jsoup.connect("http://172.16.10.111/exam/exam/home.do");
		cookie.cookie("exam", "fc9c0acb-87ec-4d2f-817a-dcc0f4e43a60");
		Document document = cookie.get();
		return document.toString();
		
	}
	
	//前往题目界面  根据id 模拟请求的   
	//在利用jsoup 加载到试题时进行解析拿取 all question problem
	// go analyze
	// 本来打算 使用es 最好 但是  本人过于懒  使用了mysql 
	//最终决策还是使用es  because this project need es haha
	@ResponseBody
	@RequestMapping(value="exam/exam/testv2.do",produces="text/html;charset=UTF-8")
	public String exam_analogExam(String id) throws IOException
	{
		System.out.println(id);
		//这里先模拟我自己的
		Document parse = Jsoup.parse(new File("C:\\Users\\MagicBook\\Desktop\\k.html"),"utf-8");
		
		
		
		
		//anaylysis to message
		Elements problems = parse.select(".question");
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
			
			List<Map> rest = mapper.selectLikeProblem(problem, type);
			
			if(rest.size()>0)
			{
				if(type.equals("0"))
				{
					Map map = rest.get(0);
					String answers = map.get("answers").toString().equals("A")?"正确":"错误";
					Elements Eans = e.select(".question-list-item");
					Eans.forEach(a->{
						if(a.text().contains(answers))
						{
							singleAnswer.put("a", a.select(".question-input").attr("value").toString());
						}
					});
				}
				if(type.equals("1"))
				{
					Map map = rest.get(0);
					String fake_answers = "option"+map.get("answers").toString().toLowerCase();
					String true_answers = map.get(fake_answers).toString();
					Elements Eans = e.select(".question-list-item");
					Eans.forEach(a->{
						if(a.text().replaceAll("↵", "").replaceAll(" ", "").replaceAll("\\s*|\t|\r|\n", "").equals(true_answers))
						{
							singleAnswer.put("a", a.select(".question-input").attr("value").toString());
						}
					});
				}
				if(type.equals("2"))
				{
					Map map = rest.get(0);
					map.get("answers");
				}
			}else
			{
				
				singleAnswer.put("a",null);
			}
			lmp.add(singleAnswer);

			
			
			
		});
		list=lmp;
		
		return parse.toString();
		
	}
	
	
	//模拟展示考试题目界面
	@ResponseBody
	@RequestMapping(value="exam/exam/li",produces="text/html;charset=UTF-8")
	public String exam_analogExamList() throws IOException
	{
		Document parse = Jsoup.parse(new File("C:\\Users\\MagicBook\\Desktop\\p2.html"), "utf-8");
		
		return parse.toString();
		
	}
	
	
	//查询分数
	@ResponseBody
	@RequestMapping(value="exam/exam/studentScoreList1.do",produces="text/html;charset=UTF-8",method=RequestMethod.POST)
	public String exam_stupost() throws IOException
	{
		
		Connection cookie = Jsoup.connect("http://172.16.10.111/exam/exam/studentScoreList1.do?query=true");
		cookie.cookie("exam", "fc9c0acb-87ec-4d2f-817a-dcc0f4e43a60");
		Document document = cookie.get();
		
		return document.toString();
	}
	
	//查询分数   其中对查询按钮进行了修改   在完工前必须使用regx  替换掉   不能使用文档替换
	@ResponseBody
	@RequestMapping(value="exam/exam/studentScoreList1.do",produces="text/html;charset=UTF-8")
	public String exam_stuget(String query) throws IOException
	{
		System.out.println(query);
		Connection cookie = Jsoup.connect("http://172.16.10.111/exam/exam/studentScoreList1.do");
		cookie.cookie("exam", "fc9c0acb-87ec-4d2f-817a-dcc0f4e43a60");
		Document document = cookie.get();
		String html = "<!doctype html>\r\n" + 
				"<html>\r\n" + 
				" <head> \r\n" + 
				"  <!--exam server--> \r\n" + 
				"  <meta charset=\"utf-8\"> \r\n" + 
				"  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"> \r\n" + 
				"  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\"> \r\n" + 
				"  <meta name=\"apple-mobile-web-app-capable\" content=\"yes\"> \r\n" + 
				"  <meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black\"> \r\n" + 
				"  <meta name=\"format-detection\" content=\"telephone=no\"> \r\n" + 
				"  <meta name=\"format-detection\" content=\"email=no\"> \r\n" + 
				"  <meta HTTP-EQUIV=\"pragma\" CONTENT=\"no-cache\"> \r\n" + 
				"  <meta HTTP-EQUIV=\"Cache-Control\" CONTENT=\"no-cache, must-revalidate\"> \r\n" + 
				"  <meta HTTP-EQUIV=\"expires\" CONTENT=\"0\"> \r\n" + 
				"  <link href=\"http://172.16.10.111/exam/resources/bootstrap/css/bootstrap-huan.css\" rel=\"stylesheet\"> \r\n" + 
				"  <link href=\"http://172.16.10.111/exam/resources/font-awesome/css/font-awesome.min.css\" rel=\"stylesheet\"> \r\n" + 
				"  <link href=\"http://172.16.10.111/exam/resources/css/font-awesome.css\"> \r\n" + 
				"  <link href=\"http://172.16.10.111/exam/resources/css/style.css\" rel=\"stylesheet\"> \r\n" + 
				"  <link href=\"http://172.16.10.111/exam/resources/skin/assets/css/beyond.min.css\" rel=\"stylesheet\"> \r\n" + 
				"  <title>北京八维集团学院考试系统server11</title> \r\n" + 
				"  <style>\r\n" + 
				".table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td,\r\n" + 
				"	.table>tbody>tr>td, .table>tfoot>tr>td {\r\n" + 
				"	padding: 8px 0;\r\n" + 
				"	line-height: 1.42857143;\r\n" + 
				"	vertical-align: middle;\r\n" + 
				"	border-top: 1px solid #ddd;\r\n" + 
				"}\r\n" + 
				"\r\n" + 
				"a.join-practice-btn {\r\n" + 
				"	margin-top: 0;\r\n" + 
				"}\r\n" + 
				"</style> \r\n" + 
				" </head> \r\n" + 
				" <body> \r\n" + 
				"  <style type=\"text/css\">\r\n" + 
				"@charset \"utf-8\";\r\n" + 
				"\r\n" + 
				".hidden { display:none; }\r\n" + 
				"\r\n" + 
				".invisible { visibility:hidden; }\r\n" + 
				"\r\n" + 
				"/* Remove Float */\r\n" + 
				"\r\n" + 
				".clear { display:block; height:0; overflow:hidden; clear:both; }\r\n" + 
				"\r\n" + 
				".clearfix:after { content:'\\20'; display:block; height:0; clear:both; }\r\n" + 
				"\r\n" + 
				".clearfix { *zoom:1; }\r\n" + 
				"\r\n" + 
				"/* Default link styles */\r\n" + 
				"\r\n" + 
				"	.demo {\r\n" + 
				"	    margin-bottom:10px;\r\n" + 
				"	    position:relative;\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"	.demo .article {\r\n" + 
				"	    float:left;\r\n" + 
				"	    color:#FFF;\r\n" + 
				"	    display:inline-block;\r\n" + 
				"	    *display:inline; zoom:1;\r\n" + 
				"	    padding:5px 10px;\r\n" + 
				"	    border:1px solid #3079ED;\r\n" + 
				"	    background:#eee;\r\n" + 
				"	    border-radius:5px;\r\n" + 
				"	    background-color: #4D90FE;\r\n" + 
				"	    background-image:-webkit-gradient(linear,left top,left bottom,from(#4D90FE),to(#4787ED));\r\n" + 
				"	    background-image:-webkit-linear-gradient(top,#4D90FE,#4787ED);\r\n" + 
				"	    background-image:-moz-linear-gradient(center top , #4D90FE, #4787ED);\r\n" + 
				"	    background-image:linear-gradient(top,#4D90FE,#4787ED);\r\n" + 
				"	}\r\n" + 
				"	#parent{\r\n" + 
				"		float:right;\r\n" + 
				"		position:absolute;left:70%; top:5%;\r\n" + 
				"		\r\n" + 
				"	}\r\n" + 
				"	.fr .article {\r\n" + 
				"	    float:right;\r\n" + 
				"	}\r\n" + 
				"	#title{\r\n" + 
				"		width: 20px;\r\n" + 
				"		height: 130px;\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"</style> \r\n" + 
				"  <script type=\"text/javascript\">\r\n" + 
				"	function addElementDiv(obj,message) {\r\n" + 
				"	     var parent = document.getElementById(obj);\r\n" + 
				"	     //添加 div\r\n" + 
				"	     var div = document.createElement(\"div\");\r\n" + 
				"		//设置 div 属性，如 id\r\n" + 
				"		 div.setAttribute(\"class\",\"demo clearfix\");\r\n" + 
				"		 div.innerHTML = \"<div class='article'><button style='float:right;border-radius:100px; width:15px; height:15px;font-size:12px;color:white;background-color:red;border:1px solid;' onclick='test()'></button><span style='font-size:10px'>\"+message+\"</span></div>\";\r\n" + 
				"		 parent.appendChild(div);\r\n" + 
				"			var s = parent.childNodes.length;\r\n" + 
				"			setTimeout(\"remElementDiv('parent')\",10000);\r\n" + 
				"			if(s > 2){\r\n" + 
				"				remElementDiv(obj);\r\n" + 
				"			}\r\n" + 
				"	}\r\n" + 
				"	function remElementDiv(obj) {\r\n" + 
				"		var parent = document.getElementById(obj).firstChild;\r\n" + 
				"		parent.remove();\r\n" + 
				"	}\r\n" + 
				"	function test(){\r\n" + 
				"		remElementDiv('parent');\r\n" + 
				"	}\r\n" + 
				"</script> \r\n" + 
				"  <header> \r\n" + 
				"   <div class=\"container\"> \r\n" + 
				"    <div class=\"row\"> \r\n" + 
				"     <div class=\"col-xs-5\"> \r\n" + 
				"      <div class=\"logo\" style=\"margin-top: 10px;\"> \r\n" + 
				"       <h1><a href=\"#\"><img height=\"80px\" alt=\"\" src=\"http://172.16.10.111/exam/resources/images/logoPicture.png\"></a></h1> \r\n" + 
				"      </div> \r\n" + 
				"     </div> \r\n" + 
				"     <div class=\"col-xs-7\" id=\"login-info\"> \r\n" + 
				"      <div id=\"login-info-user\">\r\n" + 
				"        杨志鹏\r\n" + 
				"       <!-- </a> --> \r\n" + 
				"       <span>|</span> \r\n" + 
				"       <a href=\"http://172.16.10.111/exam/logout.do\"><i class=\"fa fa-sign-out\"></i> 退出</a> \r\n" + 
				"      </div> \r\n" + 
				"     </div> \r\n" + 
				"    </div> \r\n" + 
				"   </div> \r\n" + 
				"   <div id=\"parent\"></div> \r\n" + 
				"  </header> \r\n" + 
				"  <!-- Navigation bar starts --> \r\n" + 
				"  <div class=\"navbar bs-docs-nav\" role=\"banner\"> \r\n" + 
				"   <div class=\"container\"> \r\n" + 
				"    <nav class=\"collapse navbar-collapse bs-navbar-collapse\" role=\"navigation\"> \r\n" + 
				"     <ul class=\"nav navbar-nav\"> \r\n" + 
				"      <li> <a href=\"http://172.16.10.111/exam/exam/home.do\"><i class=\"fa fa-home\"></i>主页</a> </li> \r\n" + 
				"      <li> <a href=\"http://172.16.10.111/exam/exam/list.do\"><i class=\"fa  fa-paper-plane-o\"></i>在线考试</a> </li> \r\n" + 
				"      <li> <a href=\"http://172.16.10.111/exam/exam/studentScoreList1.do\"><i class=\"fa fa-dashboard\"></i>我的成绩</a> </li> \r\n" + 
				"      <li> <a href=\"http://172.16.10.111/exam/exam/lookExamDetail.do\"><i class=\"fa fa-list\"></i>查看待考试信息</a> </li> \r\n" + 
				"      <li> <a href=\"http://172.16.10.111/exam/exam/setting.do\"><i class=\"fa fa-cogs\"></i>个人设置</a> </li> \r\n" + 
				"     </ul> \r\n" + 
				"    </nav> \r\n" + 
				"   </div> \r\n" + 
				"  </div> \r\n" + 
				"  <!-- Navigation bar ends --> \r\n" + 
				"  <!-- Slider starts --> \r\n" + 
				"  <div class=\"content\" style=\"margin-bottom: 10px;\"> \r\n" + 
				"   <div class=\"container\" style=\"margin-top: 20px;\"> \r\n" + 
				"    <div class=\"row\"> \r\n" + 
				"     <div class=\"col-xs-12\"> \r\n" + 
				"      <div style=\"border-bottom: 1px solid #ddd;\"> \r\n" + 
				"       <h3 class=\"title\"> <i class=\"fa fa-cloud-upload\"></i>考试历史 </h3> \r\n" + 
				"      </div> \r\n" + 
				"      <br> \r\n" + 
				"      <div class=\"question-list\" style=\"width: 100%;\"> \r\n" + 
				"       <form action='/exam/exam/studentScoreList1.do' name=\"searchForm\" id=\"searchForm\" method=\"post\"> \r\n" + 
				"        <table class=\"table\"> \r\n" + 
				"         <tbody> \r\n" + 
				"          <tr class=\"Navigationtdbg\"> \r\n" + 
				"           <td align=\"right\" width=\"15%\">开始时间:&nbsp;</td> \r\n" + 
				"           <td width=\"35%\"> <input class=\"date-picker\" id=\"begintime\" name=\"begintime\" readonly value=\"2019-05-09\" type=\"text\" style=\"width:150px\">至 <input class=\"date-picker\" id=\"endtime\" readonly name=\"endtime\" value=\"2019-06-09\" type=\"text\" style=\"width:150px\"> <span> </span></td> \r\n" + 
				"           <td align=\"right\" width=\"15%\">考试类型:&nbsp;</td> \r\n" + 
				"           <td width=\"35%\"> <select name=\"type\" id=\"type\" style=\"width: 150px\"> <option value=\"\">----请选择----</option> <option value=\"0\">日考</option> <option value=\"1\">周考</option> <option value=\"2\">月考</option> <option value=\"3\">期中考试</option> <option value=\"4\">期末考试</option> </select> </td> \r\n" + 
				"          </tr> \r\n" + 
				"          <tr class=\"Navigationtdbg\"> \r\n" + 
				"           <td colspan=\"4\" align=\"right\"><input class=\"btn btn-primary btn-sm\" type=\"submit\" value=\" 查 询 \" onclick=\"pp()\" name=\"btQuery\"> <input class=\"btn btn-primary btn-sm\" type=\"reset\" value=\" 重置 \" name=\"btReset\"></td> \r\n" + 
				"          </tr> \r\n" + 
				"         </tbody> \r\n" + 
				"        </table> \r\n" + 
				"       </form> \r\n" + 
				"      </div> \r\n" + 
				"      <div class=\"question-list\"> \r\n" + 
				"       <i class=\"fa fa-table fa-fw\"></i>列表 \r\n" + 
				"       <div style=\"display:inline;\" id=\"aazone.kslszone\"> \r\n" + 
				"        <table class=\"table-striped table\"> \r\n" + 
				"         <thead> \r\n" + 
				"          <tr> \r\n" + 
				"           <td align=\"center\">所属学院</td> \r\n" + 
				"           <td align=\"center\">学号</td> \r\n" + 
				"           <td align=\"center\">姓名</td> \r\n" + 
				"           <td align=\"center\">班级</td> \r\n" + 
				"           <td align=\"center\">考试名称</td> \r\n" + 
				"           <td align=\"center\">考试科目</td> \r\n" + 
				"           <td align=\"center\">参加考试时间</td> \r\n" + 
				"           <td align=\"center\">提交考试时间</td> \r\n" + 
				"           <td align=\"center\">考试类型</td> \r\n" + 
				"           <td align=\"center\">得分</td> \r\n" + 
				"           <td align=\"center\">操作</td> \r\n" + 
				"           <!-- 										<td align=\"center\">操作</td> --> \r\n" + 
				"          </tr> \r\n" + 
				"         </thead> \r\n" + 
				"         <!-- 最近发布考试页面 --> \r\n" + 
				"         <tbody> \r\n" + 
				"         </tbody> \r\n" + 
				"        </table> \r\n" + 
				"        <div style=\"text-align: right\"> \r\n" + 
				"         <input type=\"hidden\" id=\"pageCallback_PageParameter\" value=\"0_0_0_0\"> \r\n" + 
				"         <ul class=\"pagination pagination-sm\" style=\"margin:0px; \"> \r\n" + 
				"          <li><a href=\"javascript:void(0);\">0/0,共0\r\n" + 
				"            <!-- 条记录 --></a></li> \r\n" + 
				"          <li><a href=\"javascript:void(0);\">«</a></li> \r\n" + 
				"          <li><a href=\"javascript:void(0);\">»</a></li> \r\n" + 
				"         </ul> \r\n" + 
				"        </div> \r\n" + 
				"        <!-- @end of zone [kslszone]@ -->\r\n" + 
				"       </div> \r\n" + 
				"      </div> \r\n" + 
				"     </div> \r\n" + 
				"    </div> \r\n" + 
				"   </div> \r\n" + 
				"  </div> \r\n" + 
				"  <!-- Slider Ends --> \r\n" + 
				"  <footer> \r\n" + 
				"   <div class=\"container\"> \r\n" + 
				"    <div class=\"row\"> \r\n" + 
				"     <div class=\"col-md-4 col-md-offset-4\"> \r\n" + 
				"      <div class=\"copy\"> \r\n" + 
				"       <p> Copyright © 北京八维博大科技有限公司 </p> \r\n" + 
				"      </div> \r\n" + 
				"     </div> \r\n" + 
				"    </div> \r\n" + 
				"   </div> \r\n" + 
				"  </footer>  \r\n" + 
				"  <script type=\"text/javascript\" src=\"http://172.16.10.111/exam/resources//js/jquery/jquery-1.9.0.min.js\"></script> \r\n" + 
				"  <script type=\"text/javascript\" src=\"http://172.16.10.111/exam/resources//boot.js\"></script> \r\n" + 
				"  <script type=\"text/javascript\" src=\"http://172.16.10.111/exam/resources//ajaxAnywhere/aa.js\"></script> \r\n" + 
				"  <script type=\"text/javascript\" src=\"http://172.16.10.111/exam/resources//app.js\"></script> \r\n" + 
				"  <script type=\"text/javascript\" src=\"http://172.16.10.111/exam/resources//bootstrap/js/bootstrap.min.js\"></script> \r\n" + 
				"  <script src=\"http://172.16.10.111/exam/resources//js/jquery.sliderBar.js\"></script> \r\n" + 
				"  <script type=\"text/javascript\">\r\n" + 
				"if (!ch) {alert(\"请使用谷歌浏览器登录!\"); window.location.href=\"http://172.16.10.111/exam/index.jsp\"; }\r\n" + 
				"</script> \r\n" + 
				"  <script src=\"http://172.16.10.111/exam/resources/skin/assets/js/datetime/moment.js\"></script> \r\n" + 
				"  <script src=\"http://172.16.10.111/exam/resources/skin/assets/js/datetime/bootstrap-datepicker.js\"></script> \r\n" + 
				"  <script src=\"http://172.16.10.111/exam/resources/skin/assets/js/datetime/bootstrap-timepicker.js\"></script> \r\n" + 
				"  <script src=\"http://172.16.10.111/exam/resources/skin/assets/js/datetime/daterangepicker.js\"></script> \r\n" + 
				"  <script type=\"text/javascript\">\r\n" + 
				"	function pp()\r\n" + 
				"	{\r\n" + 
				"		\r\n" + 
				"	}\r\n" + 
				"  \r\n" + 
				"	// $('.date-picker').datepicker();\r\n" + 
				"	$(function() {\r\n" + 
				"		var bt = $(\"#begintime\").datepicker({\r\n" + 
				"			format : 'yyyy-mm-dd',\r\n" + 
				"			onRender : function(date) {\r\n" + 
				"				var strTime = $(\"#endtime\").val(); //字符串日期格式           \r\n" + 
				"				if (strTime) {\r\n" + 
				"					var end = new Date(Date.parse(strTime.replace(/-/g, \"/\")));\r\n" + 
				"					return date.valueOf() > end.valueOf() ? 'disabled' : '';\r\n" + 
				"				}\r\n" + 
				"				return date.valueOf();\r\n" + 
				"			}\r\n" + 
				"		});\r\n" + 
				"		var et = $(\"#endtime\")\r\n" + 
				"				.datepicker(\r\n" + 
				"						{\r\n" + 
				"							format : 'yyyy-mm-dd',\r\n" + 
				"							onRender : function(date) {//重定向日期的操作\r\n" + 
				"								var strTime = $(\"#begintime\").val(); //字符串日期格式           \r\n" + 
				"								if (strTime) {\r\n" + 
				"									var begin = new Date(Date.parse(strTime\r\n" + 
				"											.replace(/-/g, \"/\")));\r\n" + 
				"									return date.valueOf() < begin.valueOf() ? 'disabled'\r\n" + 
				"											: '';\r\n" + 
				"								}\r\n" + 
				"								return date.valueOf();\r\n" + 
				"							}\r\n" + 
				"						});\r\n" + 
				"		//添加改变值时，弹出框的隐藏\r\n" + 
				"		bt.on('changeDate', function(e) {\r\n" + 
				"			$('#begintime').datepicker('hide');\r\n" + 
				"		});\r\n" + 
				"		et.on('changeDate', function(e) {\r\n" + 
				"			$('#endtime').datepicker('hide');\r\n" + 
				"		});\r\n" + 
				"	})\r\n" + 
				"	var baseUrl = 'http://172.16.10.111/exam/exam/';\r\n" + 
				"	/*分页查询*/\r\n" + 
				"	function pageCallback(page) {\r\n" + 
				"		var url = baseUrl + 'studentScoreList1.do?query=true';\r\n" + 
				"		__pageCallback(page, url)\r\n" + 
				"	}\r\n" + 
				"	/*内部使用的通用函数 */\r\n" + 
				"	function __pageCallback(page, url) {\r\n" + 
				"		if (page && page.current) {\r\n" + 
				"			//刷新当前页面\r\n" + 
				"			var ps = $('#pageCallback_PageParameter').val().split(\"_\");\r\n" + 
				"			url = url + '&pagesize=' + ps[2] + '&currentPage=' + ps[0];\r\n" + 
				"		} else if (page) {\r\n" + 
				"			url = url + '&pagesize=' + page.pageSize + '&currentPage='\r\n" + 
				"					+ page.currentPage;\r\n" + 
				"		}\r\n" + 
				"		document.getElementById(\"searchForm\").action = url;\r\n" + 
				"		ajaxAnywhere.formName = \"searchForm\";\r\n" + 
				"		ajaxAnywhere.submitAJAX();\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	/*指定刷新区域*/\r\n" + 
				"	ajaxAnywhere.getZonesToReload = function() {\r\n" + 
				"		return \"kslszone\";\r\n" + 
				"	};\r\n" + 
				"\r\n" + 
				"	$(function() {\r\n" + 
				"		$('.sliderbar-container').sliderBar({\r\n" + 
				"			open : false, // 默认是否打开，true打开，false关闭\r\n" + 
				"			top : 200, // 距离顶部多高\r\n" + 
				"			width : 280, // body内容宽度\r\n" + 
				"			height : 180, // body内容高度\r\n" + 
				"			theme : 'green', // 主题颜色\r\n" + 
				"			position : 'right' // 显示位置，有left和right两种\r\n" + 
				"		});\r\n" + 
				"		bindQuestionKnowledage();\r\n" + 
				"		var result = checkBrowser();\r\n" + 
				"		if (!result) {\r\n" + 
				"			alert(\"请至少更新浏览器版本至IE8或以上版本\");\r\n" + 
				"		}\r\n" + 
				"	});\r\n" + 
				"\r\n" + 
				"	function checkBrowser() {\r\n" + 
				"		var browser = navigator.appName;\r\n" + 
				"		var b_version = navigator.appVersion;\r\n" + 
				"		var version = b_version.split(\";\");\r\n" + 
				"		var trim_Version = version[1].replace(/[ ]/g, \"\");\r\n" + 
				"		if (browser == \"Microsoft Internet Explorer\"\r\n" + 
				"				&& trim_Version == \"MSIE7.0\") {\r\n" + 
				"			return false;\r\n" + 
				"		} else if (browser == \"Microsoft Internet Explorer\"\r\n" + 
				"				&& trim_Version == \"MSIE6.0\") {\r\n" + 
				"			return false;\r\n" + 
				"		} else\r\n" + 
				"			return true;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	function bindQuestionKnowledage() {\r\n" + 
				"		$(\".knowledge-title\").click(function() {\r\n" + 
				"			var ul = $(this).parent().find(\".question-list-knowledge\");\r\n" + 
				"\r\n" + 
				"			if (ul.is(\":visible\")) {\r\n" + 
				"				$(this).find(\".fa-chevron-down\").hide();\r\n" + 
				"				$(this).find(\".fa-chevron-up\").show();\r\n" + 
				"\r\n" + 
				"				$(\".question-list-knowledge\").slideUp();\r\n" + 
				"\r\n" + 
				"			} else {\r\n" + 
				"				$(\".fa-chevron-down\").hide();\r\n" + 
				"				$(\".fa-chevron-up\").show();\r\n" + 
				"\r\n" + 
				"				$(this).find(\".fa-chevron-up\").hide();\r\n" + 
				"				$(this).find(\".fa-chevron-down\").show();\r\n" + 
				"\r\n" + 
				"				$(\".question-list-knowledge\").slideUp();\r\n" + 
				"				ul.slideDown();\r\n" + 
				"\r\n" + 
				"			}\r\n" + 
				"\r\n" + 
				"		});\r\n" + 
				"	}\r\n" + 
				"	\r\n" + 
				"	/* 跳转到查看 */\r\n" + 
				"\r\n" + 
				"	function viewExamPaper(id, eid, detailId, classId, paperId) {\r\n" + 
				"\r\n" + 
				"		var flag = true;\r\n" + 
				"		// 验证该考试是否结束,如果未结束,则不能查看;如果已结束,则能查看\r\n" + 
				"		$.ajax({\r\n" + 
				"			url : \"http://172.16.10.111/exam/exam/\" + 'checkKssj.do?id=' + detailId,\r\n" + 
				"			type : 'POST',\r\n" + 
				"			cache : false,\r\n" + 
				"			async : false,\r\n" + 
				"			success : function(result) {\r\n" + 
				"				if (!result.ok) {\r\n" + 
				"					alert(result.msg);\r\n" + 
				"					flag = false;\r\n" + 
				"				}\r\n" + 
				"			}\r\n" + 
				"		});\r\n" + 
				"\r\n" + 
				"		if (!flag) {\r\n" + 
				"			return;\r\n" + 
				"		}\r\n" + 
				"		// 跳转到查看页面\r\n" + 
				"		window.open(\"http://172.16.10.111/exam/exam/\" + 'viewExamPaper.do?id=' + id + '&eid='\r\n" + 
				"				+ eid + '&detailId=' + detailId + '&classId=' + classId\r\n" + 
				"				+ '&paperId=' + paperId, '_blank');\r\n" + 
				"	}\r\n" + 
				"</script>  \r\n" + 
				" </body>\r\n" + 
				"</html>\r\n" + 
				"";
		return html;
		
	}
	
	/*@ResponseBody
	@RequestMapping(value="exam/exam/studentScoreList1.do?query=true",produces="text/html;charset=UTF-8")
	public void exam_page(String begintime,String endtime) throws IOException
	{
		System.out.println(begintime);
//		Connection cookie = Jsoup.connect("http://172.16.10.111/exam/exam/studentScoreList1.do");
//		cookie.cookie("exam", "fc9c0acb-87ec-4d2f-817a-dcc0f4e43a60");
//		Document document = cookie.get();
//		return document.toString();
		
	}*/
	
	
	
}
