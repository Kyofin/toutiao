package com.nowcoder.lucene;

import com.nowcoder.ToutiaoApplication;
import com.nowcoder.model.News;
import com.nowcoder.service.NewsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
public class LuceneAdapterTest {

	@Autowired
	LuceneAdapter luceneAdapter;

	@Autowired
	NewsService newsService;

	@Test
	public void addIndex() throws Exception {

		long beginTime =System.currentTimeMillis();
		luceneAdapter.initNewsIndex();
		long endTime = System.currentTimeMillis();

		System.out.println("重建索引耗时："+(endTime-beginTime));
	}

	@Test
	public void searchNewsIndex() throws Exception {
		List<News> newsList = luceneAdapter.searchNewsIndex("笔记本");
		for (News news : newsList) {
			System.out.println(news);
		}
	}


	@Test
	public void searchNews() throws Exception {
		List<News> newsList = luceneAdapter.searchNewsIndex("笔记本");
		for (News news : newsList) {
			System.out.println(news);
		}
	}

	@Test
	public void updateIndex() throws Exception{
		News news = new News();
		news.setId(115645);
		news.setTitle("2018最火热笔记本有哪些");
		news.setContent("2018最火热笔记本有哪些!!!!!!!!!!!!!内容");
		luceneAdapter.updateIndex(news);
	}

	@Test
	public void deleteIndex() throws Exception{

		luceneAdapter.deleteIndex(String.valueOf(115645));
	}
}