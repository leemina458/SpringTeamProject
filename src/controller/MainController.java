package controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainController extends Action{
	
	public String main(HttpServletRequest request, HttpServletResponse response)  throws Throwable { 
		return  "/jsp/main.jsp"; 
	}
	public String startupKeyword(HttpServletRequest request, HttpServletResponse response)  throws Throwable { 
		return  "/jsp/startupKeyword.jsp"; 
	}
	public String startupWeather(HttpServletRequest request, HttpServletResponse response)  throws Throwable { 
		return  "/jsp/startupWeather.jsp"; 
	}
}