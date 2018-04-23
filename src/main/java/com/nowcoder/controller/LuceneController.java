package com.nowcoder.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.nowcoder.lucene.LuceneAdapter;
import com.nowcoder.model.News;
import com.nowcoder.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
								//todo 1展示未处理，2分页未处理
public class LuceneController {

	@Autowired
	LuceneAdapter luceneAdapter;

	@RequestMapping("/search")
	public String search(@RequestParam(value = "query", required = false, defaultValue = "") String query,
						 @RequestParam(value = "page", required = false, defaultValue = "1") int page,
						 Model model,
						 HttpServletRequest request) throws Exception {
		List<News> newsList = luceneAdapter.searchNewsIndex(query);

		PageInfo<News> pageInfo = new PageInfo<>(newsList);

		model.addAttribute("pageInfo",pageInfo);
		model.addAttribute("query",pageInfo);
		model.addAttribute("pageTitle", "搜索关键字'" + query + "'结果页面");



		return "queryResult";
	}

	@RequestMapping(value = "/init",method = RequestMethod.POST)
	@ResponseBody
	public String initIndex() throws Exception {
		try {
			luceneAdapter.initNewsIndex();
		}catch (Exception e )
		{
			e.printStackTrace();
			return ToutiaoUtil.getJSONString(1,"重建索引失败");
		}
		return ToutiaoUtil.getJSONString(0,"成功重建索引");
	}

}
