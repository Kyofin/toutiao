package com.nowcoder.model;

import java.util.List;

/**
 * 基类page中包含公有翻页参数及保存查询到的结果以被页面遍历，
 * 被子类继承后将增加不同的查询条件 。
 *
 * @author David
 *
 */
public class Page {
	/** 每页显示条数默认为30条 */
	public static final int DEFAULT_SIZE = 30;

	/** 当前页码， 从1开始计 */
	private int current;

	/** 每页条数 */
	private int everyPageSize;

	/** 总条数 */
	private long totalCount;


	/** 当前页数据 */
	private List<Object> datas;

	public Page() {
		// 设置默认值
		current = 1;
		everyPageSize = DEFAULT_SIZE;
	}

	public Page(Integer currentPage , Integer pageSize ,List dataList , long totalCount)
	{
		this.current=currentPage;
		this.everyPageSize =pageSize;
		this.datas=dataList;
		this.totalCount=totalCount;
	}
	/** 获取当前页码 */
	public int getCurrent() {
		return current;
	}

	/** 设置当前页码 */
	public void setCurrent(int current) {
		this.current = current;
	}

	/** 获取每页显示条数 */
	public int getEveryPageSize() {
		return everyPageSize;
	}

	/** 设置每页显示条数 */
	public void setEveryPageSize(int size) {
		this.everyPageSize = size;
	}



	/** 获取当前页数据 */
	public List<Object> getDatas() {
		return datas;
	}

	/** 设置当前页数据 */
	public void setDatas(List<Object> datas) {
		this.datas = datas;
	}

	/** 获取总条数 */
	public long getTotalCount() {
		return totalCount;
	}

	/** 设置总条数 */
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	/** 获取总页数 */
	public long getTotalPages() {
		if (datas == null || datas.isEmpty())
			return 0;

		long totalPages = totalCount / everyPageSize;
		if (totalCount% everyPageSize != 0) {
			totalPages ++;
		}

		return totalPages;
	}

	/** 获取从第几条数据开始查询 */
	public long getStartRow() {
		return (current-1) * everyPageSize;
	}

	/** 判断是否还有前一页 */
	public boolean getHasPrevious() {
		return current == 1 ? false : true;
	}

	/** 判断是否还有后一页 */
	public boolean getHasNext() {
		return (getTotalPages()!=0 && getTotalPages()!=current) ? true : false;
	}

}