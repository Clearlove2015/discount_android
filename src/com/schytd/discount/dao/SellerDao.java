package com.schytd.discount.dao;

import java.util.ArrayList;
import java.util.List;

import com.schytd.discount.enties.SellerDetail;
import com.schytd.discount.enties.SellerIndex;
import com.schytd.discount.enties.SellerInfoItem;

public interface SellerDao {
	// 储存商家信息
	public void InsertSeller(List<SellerInfoItem> list) throws Exception;

	// 查询所有商家信息
	public ArrayList<SellerInfoItem> selectSeller(String condition)
			throws Exception;
	// 根据bid查询某个商家信息
	public boolean selectSellerByBid(String bid) throws Exception;
	// 删除商家信息
	public void delteSeller() throws Exception;
	// 插入搜索索引
	public void insertSellerIndex(List<SellerIndex> sellerindex)
			throws Exception;

	// 删除搜索索引
	public void delteSellerIndex() throws Exception;

	// 储存商家详细信息
	public void insertSellerInfo(SellerDetail sellerDetail) throws Exception;

	// 查询商家详细信息
	public SellerDetail selectSellerInfo(String baseInfoId) throws Exception;

	// 删除商家详细说信息
	public void deleteSellerInfo() throws Exception;

	// 插入浏览历史
	public void insertReadHistroy(int id, String bid, String time)
			throws Exception;

	// 查询浏览历史
	public ArrayList<SellerInfoItem> selectReadHistory() throws Exception;

	// 得到时间
	public ArrayList<String> getReadTime() throws Exception;
}
