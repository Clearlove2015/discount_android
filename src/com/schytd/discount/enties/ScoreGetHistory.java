package com.schytd.discount.enties;

import java.util.ArrayList;

public class ScoreGetHistory {
	private int pageNum;
	private int totalCount;
	private ArrayList<ScoreGetHistoryItem> resultList;

	public ScoreGetHistory() {
	}

	public ScoreGetHistory(int totalCount,
			ArrayList<ScoreGetHistoryItem> resultList) {
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

	public ArrayList<ScoreGetHistoryItem> getResultList() {
		return resultList;
	}

	public void setResultList(ArrayList<ScoreGetHistoryItem> resultList) {
		this.resultList = resultList;
	}
}
