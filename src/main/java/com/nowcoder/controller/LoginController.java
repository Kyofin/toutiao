package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);


    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping("/reg")
    @ResponseBody
    public  String reg(@RequestParam("username") String username,
                                   @RequestParam("password") String password,
                       HttpServletResponse response)
    {
        Map<String,Object> map = new HashMap<>();
        System.out.println(username+password);
        map = userService.register(username,password);

        if (map.containsKey("ticket"))
        {
            //将ticket保存到客户端以此做票据
            LoginTicket ticket = (LoginTicket) map.get("ticket");
            Cookie cookie = new Cookie("ticket", ticket.getTicket());
            cookie.setPath("/");  //全网有效
            response.addCookie(cookie);

            return ToutiaoUtil.getJSONString(0,"注册成功");
        }
        return  ToutiaoUtil.getJSONString(1,map); //注册失败，返回错误信息
    }


    @RequestMapping("/login")
    @ResponseBody
    public  String login(@RequestParam("username") String username,
                         @RequestParam("password") String password,
                         @RequestParam(value = "remember",defaultValue = "0") int remember,
                         HttpServletResponse response)
    {
        Map<String,Object> map = new HashMap<>();
        System.out.println(username+password);
        map = userService.login(username,password);

        if (map.containsKey("ticket"))
        {
            //将ticket保存到客户端以此做票据
            LoginTicket ticket = (LoginTicket) map.get("ticket");
            Cookie cookie = new Cookie("ticket", ticket.getTicket());
            if (remember>0)
            {
                cookie.setMaxAge(3600*1000*24);
            }
            cookie.setPath("/");  //全网有效
            response.addCookie(cookie);
            eventProducer.fireEvent(
                            new EventModel(EventType.LOGIN)
                            .setActorId((int) map.get("userId"))
                            .setExt("username", username)
                            .setExt("to", "1040080742@163.com")//应改为每个用户自己的邮箱
            );
            return ToutiaoUtil.getJSONString(0,"登录成功");
        }
        return  ToutiaoUtil.getJSONString(1,map); //注册失败，返回错误信息
    }

    @RequestMapping("/logout")
    public  String logout(@CookieValue("ticket") String ticket)
    {
        userService.logout(ticket);
        return "redirect:/";
    }

}
