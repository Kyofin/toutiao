package com.nowcoder.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.nowcoder.model.Message;
import com.nowcoder.model.Page;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.util.SolrAdapter;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


public class SearchController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

	@Autowired
	private SolrAdapter solrAdapter;

	@RequestMapping(path = {"/search"}, method = {RequestMethod.GET})
	@ResponseBody
	public String searchByQueryString(
			@RequestParam("query") String query,
			@RequestParam("page") Integer page,
			@RequestParam("size") Integer size) {
		Page resultPage = null;

		try {
			resultPage = solrAdapter.getSolrDocumentList(query,page,size);


		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("搜索失败" + e.getMessage());
		}
		return JSONObject.toJSONString(resultPage);
	}
}
