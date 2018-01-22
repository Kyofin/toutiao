package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by nowcoder on 2016/7/14.
 */
@Component
public class LikeHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        //获得触发事件的用户
        User user = userService.getUser(model.getActorId());
        message.setToId(model.getEntityOwnerId());
        message.setContent("用户" + user.getName() +
                " 赞了你的资讯,http://127.0.0.1:8080/news/"
                + String.valueOf(model.getEntityId()));
        // 发送人为管理员账号
        message.setFromId(3);
        message.setCreatedDate(new Date());
        String coversationId = null;   //id大的放前面
        if (message.getFromId() > message.getToId())
        {
            coversationId = String.format("%d-%d",message.getFromId(),message.getToId());
        }else
            coversationId = String.format("%d-%d",message.getToId()  ,message.getFromId());
        message.setConversationId(coversationId);
        messageService.insertMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        //该handler支持事件类型的集合
        return Arrays.asList(EventType.LIKE);
    }
}
