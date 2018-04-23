package com.nowcoder;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.MessageDao;
import com.nowcoder.dao.NewsDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.*;
import com.nowcoder.util.SolrAdapter;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)

public class SolrTest {

	@Autowired
	SolrAdapter solrAdapter;

	@Test
	public void testQuery() throws SolrServerException {
		SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");


		// 查询 关键词  过滤条件
		// 价格排序 分页 开始行 每页数 高亮 默认域 只查询指定域
		SolrQuery solrQuery = new SolrQuery();
		// 关键词
		solrQuery.setQuery("*:*");


		// 执行查询
		QueryResponse response = solrServer.query(solrQuery);
		// 文档结果集
		SolrDocumentList docs = response.getResults();


		// 总条数
		long numFound = docs.getNumFound();
		System.out.println("总条数："+numFound);
		for (SolrDocument doc : docs) {
			System.out.println(doc.get("id"));
		}
	}

	@Test
	public void delete() throws IOException, SolrServerException {
		solrAdapter.getSolrServer().deleteByQuery("*:*");

	}



}
