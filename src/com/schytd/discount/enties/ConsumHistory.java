package com.schytd.discount.enties;

import java.util.ArrayList;

public class ConsumHistory {
	private int totalCount;
	private ArrayList<ConsumHistoryItem> resultList;
	private int pageNum;
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public ArrayList<ConsumHistoryItem> getResultList() {
		return resultList;
	}
	public void setResultList(ArrayList<ConsumHistoryItem> resultList) {
		this.resultList = resultList;
	}
	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
}
