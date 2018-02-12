package com.nowcoder;

import com.nowcoder.async.EventHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.sound.midi.Soundbank;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
@WebAppConfiguration
public class ToutiaoApplicationTests {
	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void contextLoads() {
		Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
		for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
			System.out.println("Name:"+entry.getKey()+"handler:"+entry.getValue());
		}
	}

}
