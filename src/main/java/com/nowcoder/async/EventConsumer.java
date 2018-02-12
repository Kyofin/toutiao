package com.nowcoder.async;

import com.alibaba.fastjson.JSON;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 该服务自启动就在监听
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    private Map<EventType, List<EventHandler>> config = new HashMap<>();

    private ApplicationContext applicationContext;

    @Autowired
    private JedisAdapter jedisAdapter;

    //该bean生成后就设置
    @Override
    public void afterPropertiesSet() throws Exception {
        //获取实现EventHandler接口的类
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);

        if (beans != null) {
            //遍历注册的每个eventhandler的support events将它们的支持事件注册到config文件
            for (Map.Entry<String, EventHandler> mapEntry : beans.entrySet()) {
                //获得各个handler可以解决的事件类型
                List<EventType> eventTypes = mapEntry.getValue().getSupportEventTypes();

                for (EventType type : eventTypes) {
                    if (!config.containsKey(type)) {
                        //初始化每个事件对应的处理列表
                        config.put(type, new ArrayList<EventHandler>());
                    }
                    // 注册每个事件的处理函数（handler）
                    config.get(type).add(mapEntry.getValue());
                }
            }
        }

        // 启动线程去消费事件
        Thread thread = new Thread(() -> {
            // 从队列一直消费
            while (true) {
                String key = RedisKeyUtil.getEventQueueKey();
                //如果没有元素弹出会形成阻塞（0为不设置超时）
                List<String> messages = jedisAdapter.brpop(0, key);
                // 第一个元素是队列名字
                for (String message : messages) {
                    if (message.equals(key)) {
                        continue;
                    }

                    EventModel eventModel = JSON.parseObject(message, EventModel.class);
                    // 找到这个事件的处理handler列表
                    if (!config.containsKey(eventModel.getType())) {
                        logger.error("不能识别的事件");
                        continue;
                    }

                    for (EventHandler handler : config.get(eventModel.getType())) {
                        handler.doHandle(eventModel);
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
