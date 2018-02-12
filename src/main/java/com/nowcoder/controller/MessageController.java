package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    /**
     * 添加站内信息
     * @param fromId
     * @param toId
     * @param content
     * @return
     */
    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                             @RequestParam("toId") int toId,
                             @RequestParam("content") String content) {
        try {
            Message message = new Message();
            message.setFromId(fromId);
            message.setToId(toId);
            message.setHasRead(0); //未读0
            message.setContent(content);
            message.setCreatedDate(new Date());
            String coversationId = null;   //id大的放前面
            if (fromId > toId)
            {
                coversationId = String.format("%d-%d",fromId,toId);
            }else
                coversationId = String.format("%d-%d",toId  ,fromId);
            message.setConversationId(coversationId);

            messageService.insertMessage(message);
            return ToutiaoUtil.getJSONString(0,"添加站内信成功，ci ="+coversationId);
        }catch (Exception e)
        {
            LOGGER.error("添加站内信失败"+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"添加站内信失败");
        }
    }

    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public String conversationDetail(Model model, @RequestParam("conversationId") String conversationId) {
        try {
            //获取所有对话信息
            List<Message>  messageList = messageService.getConversationDetail(conversationId,0,10); //显示前10条
            List<ViewObject> messageVOs = new ArrayList<>();
            for (Message message : messageList) {
                ViewObject viewObject = new ViewObject();
                viewObject.set("message",message);
                User user =  userService.getUser(message.getFromId());
                if(user == null)
                {
                    continue;
                }

                viewObject.set("headUrl",user.getHeadUrl());
                viewObject.set("userId",user.getId());
                viewObject.set("userName",user.getName());
                messageVOs.add(viewObject);
            }
            model.addAttribute("messages",messageVOs);
        }catch (Exception e )
        {
            LOGGER.error("查看站内对话详细内容失败"+e.getMessage());
        }

        return "letterDetail";
    }




    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String conversationList(Model model) {
        try {
            User localUser = hostHolder.getUser();
            if (localUser==null)
            {
                LOGGER.error("用户未登录");
                throw  new Exception();
            }
            List<Message> messageList= messageService.getConversationList(localUser.getId(),0,10);//返回前十条
            List<ViewObject> messageVOs = new ArrayList<>();
            for (Message msg : messageList) {
                ViewObject vo = new ViewObject() ;
                vo.set("conversation", msg);
                int targetId = msg.getFromId() == localUser.getId() ? msg.getToId() : msg.getFromId();
                User user = userService.getUser(targetId);
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userName", user.getName());
                vo.set("targetId", targetId);
                vo.set("totalCount", msg.getId());
                vo.set("unreadCount", messageService.getUnreadCount(localUser.getId(), msg.getConversationId()));
                messageVOs.add(vo);
            }
            model.addAttribute("conversations",messageVOs);
        }catch (Exception e )
        {
            e.printStackTrace();
            LOGGER.error("展示站内信列表失败" +e.getMessage());
        }
        return "letter";
    }
}
