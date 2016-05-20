package com.schytd.discount.net;

import java.util.ArrayList;

import com.schytd.discount.enties.IndexImage;
import com.schytd.discount.enties.SellerDetail;
import com.schytd.discount.enties.SellerInfo;

public interface SellerNet {
	public SellerInfo getSellerInfoFromServer(String... params)
			throws Exception;

	public SellerDetail getSellerDetailInfoFromServer(String... params)
			throws Exception;

	// 得到首页轮播图片
	public ArrayList<IndexImage> getIndexImageFromServer() throws Exception;

	// 优惠资讯
	public SellerInfo getDiscountInfoFormServer(String... params)
			throws Exception;
}
