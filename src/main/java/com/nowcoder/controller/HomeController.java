package com.nowcoder.controller;

import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.News;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.NewsService;
import com.nowcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
        @Autowired
        private NewsService newsService;
        @Autowired
       private   UserService userService;

        @Autowired
        private LikeService likeService;

        @Autowired
        private HostHolder hostHolder;

        public  List<ViewObject>  getNewsViewObj (int userId ,int offset,int limit)
        {
                //获取10条的咨询
                List<News > newsList =newsService.getLastestNews(userId,offset,limit);
                //viewobj的集合
                List<ViewObject> viewObjectList = new ArrayList<>();
                //将要展示的内容打包成一个对象放入集合中
                for(News news : newsList )
                {
                        ViewObject viewObj = new ViewObject() ;
                        viewObj.set("news" , news);
                        viewObj.set("user", userService.getUser(news.getUserId()));
                        //判断用户是否登录
                        if (hostHolder.getUser()!=null)
                        {
                            viewObj.set("like",likeService.getLikeStatus(EntityType.ENTITY_NEW,news.getId(),hostHolder.getUser().getId()));
                        }else
                            viewObj.set("like",0);
                        viewObjectList.add(viewObj);
                }
                return  viewObjectList;
        }

        @RequestMapping(path = {"/","/index"})
        public  String index(Model model)
        {
                //userId = 0 时显示所有咨询
                List<ViewObject> viewObjectList =getNewsViewObj(0,0,10);
                model.addAttribute("vos" , viewObjectList);
                model.addAttribute("title","头条资讯");
                return "home";
        }

        @RequestMapping(path = {"/user/{userId}"})
        public  String userIndex(Model model, @PathVariable("userId") int userId)
        {
                List<ViewObject> viewObjectList =getNewsViewObj(userId,0,10);
                model.addAttribute("vos" , viewObjectList);
                model.addAttribute("title","我的资讯");
                return "home";
        }

}
