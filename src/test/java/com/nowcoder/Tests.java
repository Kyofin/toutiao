package com.nowcoder;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.MessageDao;
import com.nowcoder.dao.NewsDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.Message;
import com.nowcoder.model.News;
import com.nowcoder.model.User;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)

public class Tests {
	@Autowired
	private UserDAO userDao;

	@Autowired
	private NewsDAO newsDao;

	@Autowired
	private LoginTicketDAO loginTicketDAO ;

	@Autowired
	private MessageDao messageDao;



	@Test
	public void test()
	{
		System.out.println(JSONObject.toJSONString(messageDao.getConversationContents("4-2",0,10)));;
		List<Message> messageList = messageDao.getConversationContents("4-2",0,10);

	}

	@Test
	public void testUpdateUserPassword()
	{
		for (int i = 2; i <= 15; i++) {

			if (i==12)
			{
				continue;
			}
		  	User user = userDao.selectById(i);
		  	user.setPassword(ToutiaoUtil.MD5(123456+user.getSalt()));
			userDao.updatePassword(user);
			Assert.assertEquals(ToutiaoUtil.MD5(123456+user.getSalt()),userDao.selectById(i).getPassword());
		}

	}

}
