package com.schytd.discount.enties;

import java.util.ArrayList;

public class UseScoreHistory {
	private int pageNum;
	private int totalCount;
	private ArrayList<UseScoreHistoryItem> resultList;

	public UseScoreHistory() {
	}

	public UseScoreHistory(int totalCount,
			ArrayList<UseScoreHistoryItem> resultList) {
		this.totalCount = totalCount;
		this.resultList = resultList;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public ArrayList<UseScoreHistoryItem> getResultList() {
		return resultList;
	}

	public void setResultList(ArrayList<UseScoreHistoryItem> resultList) {
		this.resultList = resultList;
	}
}
