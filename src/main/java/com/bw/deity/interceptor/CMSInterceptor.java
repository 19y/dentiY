package com.bw.deity.interceptor;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


/**
 * intercept operation 拦截 操作
 * 
 * @author MagicBook
 *
 */

public class CMSInterceptor extends HandlerInterceptorAdapter {


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		// 取得url请求链接
		String url = request.getRequestURI();
		System.out.println(url);
		
		
		
		request.getRequestDispatcher("/aa.jsp").forward(request, response);
		
		response.getWriter().write("nihao");
		
		return false;

	}



}
