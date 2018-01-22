package com.nowcoder.controller;

import com.nowcoder.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;


/*@Controller*/
public class IndexController {
	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);


	@RequestMapping(value= {"/","/index"})
	@ResponseBody
	public String index()
	{
		logger.info("visited index");
		return "Hello index!";
	}
	
	
	@RequestMapping("/profile/{groupId}/{userId}")
	@ResponseBody
	public String profile(
			@PathVariable("groupId") String groupId,
			@PathVariable("userId") int userId,
			@RequestParam(value="type",defaultValue="1") int type,
			@RequestParam(value="key",defaultValue="nowcoder") String key)
	{
		return String.format("GID{%s},UID{%d},TYPE{%d},KEY{%s}", groupId,userId,type,key);
		
	}
	
	@RequestMapping("/vm")
	public String news(Model model)
	{
		model.addAttribute("value1","peter");
		
		List<String> colors = Arrays.asList(new String[] {"Red","Yellow","Green"});
		model.addAttribute("colors", colors);
		
		
		Map<String, String> maps = new HashMap<>();
		for(int i = 0 ;i<4 ;i++)
		{
			maps.put(String.valueOf(i), String.valueOf(i*i));
		}
		model.addAttribute("maps", maps);
		  for (String number :  maps.keySet()) {
			    System.out.println(number);
			    System.out.println(maps.get(number));
		  }
		  
		  model.addAttribute("user",new User("peter"));
		return "news";
	}
	
	
	@RequestMapping("/request")
	@ResponseBody
	public  String  request(
							HttpServletRequest request,
							HttpServletResponse response,
				  			HttpSession session)
	{
		  StringBuilder strBuilder = new StringBuilder();
		  Enumeration<String> headerNames = request.getHeaderNames();
		  while (headerNames.hasMoreElements())
		  {
		  	  String name = headerNames.nextElement();
		  	  strBuilder.append(name +"   :  "+request.getHeader(name)+"<br>");
		  }
		  for (Cookie cookie:  request.getCookies()) {
			    strBuilder.append("COOKIE:"+cookie.getName()+"=="+cookie.getValue()+"<br>");
		  }
		  return strBuilder.toString();
	}

	@RequestMapping("/response")
	@ResponseBody
	public  String response(@CookieValue(value = "nowcoder",defaultValue = "a") String nowcoderId,
				  			@RequestParam(value = "key",defaultValue = "key") String key,
				  			@RequestParam(value = "value",defaultValue = "value") String value,
				  			HttpServletResponse response)
	{
	  	  response.addCookie(new Cookie(key,value));
	  	  return "nowcoderId from Cookie :" + nowcoderId;
	}

	@RequestMapping("/admin")
	@ResponseBody
	public  String admin(@RequestParam(value = "admin",required = false) String  admin)
	{
	  	  if ("admin".equals(admin))
		  {
		  	  return "hello world";
		  }else
		  	  throw  new IllegalArgumentException("error");
	}

	@RequestMapping("/redirect")
	public  String redirect()
	{
		return "redirect:/";
	}

	@ExceptionHandler
	@ResponseBody
	public  String error(Exception e)
	{
		  return "error page!";
	}



}
