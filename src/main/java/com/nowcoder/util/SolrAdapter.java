package com.nowcoder.util;

import com.nowcoder.model.Page;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
public class SolrAdapter implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(SolrAdapter.class);

	private static final String SOLR_BASE_URL="http://localhost:8080/solr/collection1";

	//solr客户端
	private SolrServer solrServer;

	//初始化bean时启动solr服务
	@Override
	public void afterPropertiesSet() throws Exception {
		solrServer = new HttpSolrServer(SOLR_BASE_URL);
	}

	public SolrServer getSolrServer() {
		return solrServer;
	}

	public Page getSolrDocumentList(String queryString , Integer pageCurrent , Integer pageSize ) throws SolrServerException {
		SolrQuery solrQuery = new SolrQuery();
		// 关键词
		if(!StringUtils.isBlank(queryString))
			solrQuery.setQuery("title_ik:"+queryString+" OR content_ik:"+queryString);
		else
			solrQuery.setQuery("title_ik: OR content_ik: ");//查空白

		// 分页
		if (pageCurrent!=null)
			solrQuery.setStart((pageCurrent - 1) * pageSize);
		else
			solrQuery.setStart(0);

		if (pageSize!=null)
			solrQuery.setRows(pageSize);
		else
			solrQuery.setRows(10);


		// 只显示指定域
		solrQuery.set("fl", "id,user_id,news_id,created_date");

		//排序设置
		solrQuery.addSort("created_date", SolrQuery.ORDER.desc);

		// 执行查询
		QueryResponse response = solrServer.query(solrQuery);

		// 文档结果集
		SolrDocumentList docs = response.getResults();

		// 总条数
		long numFound = docs.getNumFound();

		//封装数据
		Page page = new Page(pageCurrent,pageSize,docs,numFound);

		return page;
	}



}

