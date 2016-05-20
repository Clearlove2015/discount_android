package com.schytd.discount.enties;

import java.util.ArrayList;

public class SellerInfo {
	private int totalCount;
	private int totalPage;
	private ArrayList<SellerInfoItem> resultList;
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public ArrayList<SellerInfoItem> getResultList() {
		return resultList;
	}
	public void setResultList(ArrayList<SellerInfoItem> resultList) {
		this.resultList = resultList;
	}
}
