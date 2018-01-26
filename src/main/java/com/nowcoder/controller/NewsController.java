package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.*;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class NewsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    UserService userService;

    @Autowired
    NewsService newsService;

    @Autowired
    QiuNiuService qiuNiuService;

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @RequestMapping(value = "/uploadImage",method = RequestMethod.POST)
    @ResponseBody
    public String uploadImage(@RequestParam("file")MultipartFile file)
    {
        try {
            //图片保存到服务器
            String fileUrl = qiuNiuService.saveImageToQiuNiu(file);
            //String fileUrl = newsService.saveImage(file);
            if (fileUrl==null)
            {
                return  ToutiaoUtil.getJSONString(1,"上传图片失败");
            }
           return ToutiaoUtil.getJSONString(0,fileUrl);
        }catch (Exception e)
        {
            LOGGER.error("上传图片异常");
            return  ToutiaoUtil.getJSONString(1,"上传图片失败");
        }
    }

    @RequestMapping(path = {"/user/addNews/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addNews(@RequestParam("image") String image,
                          @RequestParam("title") String title,
                          @RequestParam("link") String link)
    {
        try {

            newsService.addNews(image,title,link);
            return ToutiaoUtil.getJSONString(0,"添加咨询成功");
        }catch (Exception e)
        {
            LOGGER.error("添加咨询失败");
            return  ToutiaoUtil.getJSONString(1,"添加咨询失败");
        }

    }

    @RequestMapping(path = {"/news/{newsId}"}, method = {RequestMethod.GET})
    public String newsDetail(@PathVariable("newsId") int newsId, @RequestParam(value = "page",required = false) Integer currentPage , Model model) {
        try {

           News outNews = newsService.getNewsById(newsId);
            if (outNews != null) {
                int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
                if (localUserId != 0) {
                    //点赞效果
                    model.addAttribute("like", likeService.getLikeStatus(EntityType.ENTITY_NEW,newsId,localUserId));
                    System.out.println("状态 like");
                } else {
                    model.addAttribute("like", 0);
                }
            }

           //加载该咨询的评论
            List<ViewObject> CommentVOs= new ArrayList<>();
            //只有用户登录了才加载评论
            if (outNews != null  && hostHolder!=null)
            {
                List<Comment> commentList  = commentService.getComments(newsId , EntityType.ENTITY_NEW ,currentPage).getList();
                for(Comment comment : commentList)
                {
                    ViewObject viewObject = new ViewObject();
                    viewObject.set("comment",comment);
                    viewObject.set("user",userService.getUser(comment.getUserId()));
                    CommentVOs.add(viewObject);
                }
                model.addAttribute("comments",CommentVOs);
                model.addAttribute("pageInfo",commentService.getComments(newsId , EntityType.ENTITY_NEW ,currentPage));
            }
            //加载咨询的相关信息
            User owner = userService.getUser(outNews.getUserId());
            model.addAttribute("news",outNews);
            model.addAttribute("owner",owner);
        }catch (Exception e)
        {
            LOGGER.error("获取咨询失败");
        }

        return  "detail";
    }


    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content) {
        try {
            Comment comment = new Comment();
            comment.setEntityType(EntityType.ENTITY_NEW);
            //过滤html的标签
            comment.setContent(HtmlUtils.htmlEscape(content));
            comment.setStatus(0);   //默认为0正常
            comment.setUserId(hostHolder.getUser().getId());
            comment.setCreatedDate(new Date());
            comment.setEntityId(newsId);

            commentService.addComment(comment);

            //更改该咨询的评论总数
            int count = commentService.getCommentCount(comment.getEntityId(),comment.getEntityType());
            newsService.updateCommentCount(newsId,count);
        }catch (Exception e)
        {
            LOGGER.error("增加评论失败");
        }
            return "redirect:/news/"+newsId;
    }

    @RequestMapping(value = "/image/{imageName}/",method = RequestMethod.GET)
    @ResponseBody
    public String getImage(@PathVariable("imageName") String imageName , HttpServletResponse response)
    {
        try {
            //设置响应的内容类型
            response.setContentType("image/jpeg");
            //利用spring流的工具类copy
            StreamUtils.copy(new FileInputStream(ToutiaoUtil.IMAGE_DIR+imageName),response.getOutputStream());
            return "";  //返回图片时不会显示
        }catch (Exception e)
        {
            LOGGER.error("读取图片发生异常");
            return ToutiaoUtil.getJSONString(1,"读取图片失败");
        }
    }

}
