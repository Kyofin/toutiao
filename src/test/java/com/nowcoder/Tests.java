package com.nowcoder;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.MessageDao;
import com.nowcoder.dao.NewsDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.Message;
import com.nowcoder.model.News;
import com.nowcoder.model.User;
import com.nowcoder.service.NewsService;
import com.nowcoder.util.ToutiaoUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

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


	@Autowired
	private  NewsDAO newsDAO;

	@Autowired
	private NewsService newsService;


	@Test
	public void  pageHelper()
	{
		Integer page = null;
		System.out.println(JSONObject.toJSONString(newsService.getNewsByPage(0,2).getNavigateLastPage()));
	}


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
