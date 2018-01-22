package com.nowcoder;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.MessageDao;
import com.nowcoder.dao.NewsDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.util.MailSender;
import com.nowcoder.util.ToutiaoUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)

public class mainTests {
	@Autowired
	MailSender mailSender;





	@Test
	public void test() {
		Map<String,Object> map = new HashMap<>();
		map.put("username","peterpoker");
		mailSender.sendWithHTMLTemplate("1040080742@163.com", "登录异常", "mails/welcome.html", map);
	}

}
