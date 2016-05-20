package com.schytd.discount.bussiness;

import java.util.ArrayList;

import com.schytd.discount.enties.IndexImage;
import com.schytd.discount.enties.SellerDetail;
import com.schytd.discount.enties.SellerInfo;
import com.schytd.discount.enties.SellerInfoItem;

public interface SellerBussiness {
//得到商家
	public SellerInfo getSellerInfo(String...params) throws Exception;
//	获得商家详细信息
	public SellerDetail getSellerDetailInfo(String...params) throws Exception;
//	首页轮播图
	public ArrayList<IndexImage> getIndexImageList() throws Exception;
//	优惠资讯
	public SellerInfo getDiscountInfo(String...params) throws Exception;
//	插入浏览历史
	public void addReadHistory(int id,String bid,String time) throws Exception;
//	得到浏览历史
	public ArrayList<SellerInfoItem> getReadHistory() throws Exception;
//	得到时间
	public ArrayList<String> getReadTime() throws Exception;
}
