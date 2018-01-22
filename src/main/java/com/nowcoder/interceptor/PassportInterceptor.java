package com.nowcoder.interceptor;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
@Component
public class PassportInterceptor implements HandlerInterceptor {
    @Autowired
    LoginTicketDAO loginTicketDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ticket = null;
        //循环查找客户端是否存在ticket
       if (httpServletRequest.getCookies()!=null)
       {
           for(Cookie cookie : httpServletRequest.getCookies())
           {
               if (cookie.getName().equals("ticket"))
               {
                   ticket = cookie.getValue();
                   break;
               }
           }
       }

        //验证该ticket是否真实存在
        LoginTicket  loginTicket= null;
        if (ticket!=null )
        {
            loginTicket = loginTicketDAO.selectTicket(ticket);
        }

        //验证该ticket是否有效
        if(loginTicket==null || loginTicket.getExpired().before(new Date())||loginTicket.getStatus()!=0)
        {
            return  true;
        }

        //保存登录用户状态
        User loginUser = userDAO.selectById(loginTicket.getUserId());
        hostHolder.setUser(loginUser);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null)
        {
            modelAndView.addObject("user",hostHolder.getUser());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear();
    }
}
