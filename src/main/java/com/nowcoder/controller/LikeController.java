package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.News;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.NewsService;
import com.nowcoder.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    NewsService newsService;

    @Autowired
    EventProducer eventProducer;


    @RequestMapping(path = {"/like"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("newsId") int newsId) {
        if (hostHolder.getUser()==null)
        {
            return ToutiaoUtil.getJSONString(1,"请先登录");
        }
        long likeCount = likeService.addLike(EntityType.ENTITY_NEW,newsId,hostHolder.getUser().getId());
        //持久化数据
        News news = newsService.getNewsById(newsId);
        newsService.updateLikeCount(newsId,(int)likeCount);
        //产生异步队列事件
        eventProducer.fireEvent(
                new EventModel(EventType.LIKE)
                .setEntityType(EntityType.ENTITY_NEW)
                .setEntityId(newsId)
                .setActorId(hostHolder.getUser().getId())
                .setEntityOwnerId(news.getUserId())
        );
        return ToutiaoUtil.getJSONString(0,String.valueOf(likeCount));
    }


    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("newsId") int newsId) {
        if (hostHolder.getUser()==null)
        {
            return ToutiaoUtil.getJSONString(1,"请先登录");
        }
        long likeCount = likeService.addDisLike(EntityType.ENTITY_NEW,newsId,hostHolder.getUser().getId());
        //持久化数据
        News news = newsService.getNewsById(newsId);
        newsService.updateLikeCount(newsId,(int)likeCount);
        return ToutiaoUtil.getJSONString(0,String.valueOf(likeCount));
    }

}
