package com.nowcoder;

import com.nowcoder.dao.*;
import com.nowcoder.model.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
//@Sql("/init-schema.sql")
public class InitDatabaseTests {
	@Autowired
	private UserDAO userDao;

	@Autowired
	private NewsDAO newsDao;

	@Autowired
	private LoginTicketDAO loginTicketDAO ;

	@Autowired
	private MessageDao messageDao;



	@Test
	public void initData() {
		Random random = new Random();
		for (int i = 0; i < 11; i++) {
			User user = new User();
			user.setName(String.format("user%d",i));
			user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",random.nextInt(1000)));
			user.setSalt(UUID.randomUUID().toString().substring(0,5));
			user.setPassword("");

			userDao.addUser(user);

			user.setPassword("kopass");
			userDao.updatePassword(user); //已更新该用户

			//新建news对象
			News news = new News();
			news.setTitle(String.format("title_%d",i+1));
			news.setImage(String.format("http://images.nowcoder.com/head/%dm.png",random.nextInt(1000)));
			news.setLink("https://bbs.feng.com/read-htm-tid-10727935.html");
			news.setCommentCount(random.nextInt(10));
			news.setLikeCount(random.nextInt(20));
			news.setUserId(i+1);
			//创建日期
			Date date = new Date();
			date.setTime(date.getTime()+1000*5*3600*i);// more five minute
			news.setCreatedDate(date);

			LoginTicket loginTicket = new LoginTicket();
			loginTicket.setStatus(0);
			loginTicket.setExpired(new Date());
			loginTicket.setTicket(String.format("ticket%d",i*10));
			loginTicket.setUserId(i+1);

			loginTicketDAO.addLoginTicket(loginTicket);
			loginTicketDAO.updateStatus(1,String.format("ticket%d",i*10));


			//news添加到数据库
			newsDao.addNews(news);

			if (i%2==0)
			{

				news.setTitle(String.format("title_%d",i*3));
				news.setImage(String.format("http://images.nowcoder.com/head/%dm.png",random.nextInt(1000)));
				news.setLink("https://bbs.feng.com/read-htm-tid-10727935.html");
				news.setCommentCount(random.nextInt(10));
				news.setLikeCount(random.nextInt(20));
				news.setUserId(i+1);
				//创建日期
				date.setTime(date.getTime()+1000*5*3600*i);// more five minute
				news.setCreatedDate(date);
				newsDao.addNews(news);

				//添加站内消息
				Message message = new Message();
				message.setContent("我是消息"+i);
				message.setConversationId("1_2");
				message.setCreatedDate(new Date());
				message.setFromId(1);
				message.setToId(2);
				message.setHasRead(0);  //未读

				messageDao.addMessage(message);
			}
			//添加站内消息
			Message message = new Message();
			message.setContent("我是消息"+i);
			message.setConversationId("1_2");
			message.setCreatedDate(new Date());
			message.setFromId(2);
			message.setToId(1);
			message.setHasRead(1);  //未读

			messageDao.addMessage(message);
		}
		//newsDao.selectByUserIdAndOffset(0,0,10);
		newsDao.updateCommentCount(1,100);

		//删除id =1的用户
		userDao.deleteById(1);

		Assert.assertEquals("kopass",userDao.selectById(2).getPassword());
		Assert.assertNull(userDao.selectById(1));
		Assert.assertEquals(2,loginTicketDAO.selectTicket("ticket10").getUserId());
	}

}
