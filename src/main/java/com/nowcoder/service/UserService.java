package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by nowcoder on 2016/7/2.
 */
@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    public Map<String,Object> register(String username, String password)
    {
        Map<String,Object> map = new HashMap<>();
        if (StringUtils.isBlank(username))
        {
            map.put("msg","用户名不能为空");
            return  map;
        }

        if (StringUtils.isBlank(password))
        {
            map.put("msg","密码不能为空");
            return  map;
        }

        if (userDAO.selectByName(username)!=null)
        {
            map.put("msg","该账号已注册");
            return  map;
        }


        //检验成功，加入到数据库
        User user = new User();
        user.setName(username);

        user.setSalt(UUID.randomUUID().toString().replace("-","").substring(0,5));
        user.setPassword(ToutiaoUtil.MD5(password+user.getSalt()));

        String headUrl = String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000));
        user.setHeadUrl(headUrl);

        userDAO.addUser(user);

        //登录校验通过，下发ticket
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(UUID.randomUUID().toString().replace("-",""));
        Date date = new Date();
        date.setTime(date.getTime()+1000*3600*24);
        loginTicket.setExpired(date);   //设置有效期
        loginTicket.setStatus(0); //0为正常    1为失效
        //将新创的ticket加入数据库
        loginTicketDAO.addLoginTicket(loginTicket);
        System.out.println("注册成功下发ticket："+loginTicket.getTicket());

        map.put("ticket",loginTicket);

        return  map;
    }



    public Map<String,Object> login(String username, String password)
    {
        Map<String,Object> map = new HashMap<>();
        if (StringUtils.isBlank(username))
        {
            map.put("msg","用户名不能为空");
            return  map;//直接中断验证，返回错误信息
        }

        if (StringUtils.isBlank(password))
        {
            map.put("msg","密码不能为空");
            return  map;//直接中断验证，返回错误信息
        }

        User outUser = userDAO.selectByName(username);

        if (outUser==null)
        {
            map.put("msg","该账号未注册");
            return  map;//直接中断验证，返回错误信息
        }

        if (!ToutiaoUtil.MD5(password+outUser.getSalt()).equals(outUser.getPassword()))
        {
            map.put("msg","用户密码错误");
            return map ;  //直接中断验证，返回错误信息
        }


        //登录校验通过，下发ticket
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(outUser.getId());
        loginTicket.setTicket(UUID.randomUUID().toString().replace("-",""));
        Date date = new Date();
        date.setTime(date.getTime()+1000*3600*24);
        loginTicket.setExpired(date);   //设置有效期
        loginTicket.setStatus(0); //0为正常    1为失效
        //将新创的ticket加入数据库
        loginTicketDAO.addLoginTicket(loginTicket);


        System.out.println("登录成功下发ticket："+loginTicket.getTicket());
        map.put("userId",outUser.getId());
        map.put("ticket",loginTicket);

        return  map;
    }


    public void logout(String ticket) {
        loginTicketDAO.updateStatus(1,ticket);
    }
}
