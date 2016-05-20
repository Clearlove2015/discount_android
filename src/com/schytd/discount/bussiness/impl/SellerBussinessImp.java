package com.schytd.discount.bussiness.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.schytd.discount.bussiness.SellerBussiness;
import com.schytd.discount.bussiness.UserBussiness;
import com.schytd.discount.dao.SellerDao;
import com.schytd.discount.dao.impl.SellerDaoImp;
import com.schytd.discount.enties.IndexImage;
import com.schytd.discount.enties.SellerDetail;
import com.schytd.discount.enties.SellerIndex;
import com.schytd.discount.enties.SellerInfo;
import com.schytd.discount.enties.SellerInfoItem;
import com.schytd.discount.enties.UserSessionId;
import com.schytd.discount.net.SellerNet;
import com.schytd.discount.net.impl.SellerNetImpl;
import com.schytd.discount.tools.StrTools;

public class SellerBussinessImp implements SellerBussiness {
	private SellerNet mSellerNet;
	private SellerDao mSellerDao;
	private UserBussiness mUserBussiness;
	private SharedPreferences sp_seller;

	public SellerBussinessImp(Context context) {
		mSellerDao = new SellerDaoImp(context);
		mSellerNet = new SellerNetImpl();
		mUserBussiness = new UserBussinessImpl(context);
		sp_seller = context.getSharedPreferences("seller_count",
				Context.MODE_PRIVATE);
	}

	@Override
	public SellerInfo getSellerInfo(String... params) throws Exception {
		// 判断是否有缓存
		SellerInfo sellerInfo = new SellerInfo();
		String condition = StrTools.subLatLng(params[0])
				+ StrTools.subLatLng(params[1]) + params[2] + params[3]
				+ params[5] + params[6] + params[7] + params[8];
		// 搜索条件
		condition = String.valueOf(StrTools.ELFHash(condition));
		ArrayList<SellerInfoItem> resultList = null;
		// 范围查询
		resultList = mSellerDao.selectSeller(condition);
		if (resultList != null) {
			sellerInfo.setResultList(resultList);
		} else {
			resultList = null;
			// 从网络获取
			UserSessionId userSessionId = mUserBussiness.getUserSessionId();
			if (userSessionId != null) {
				sellerInfo = mSellerNet.getSellerInfoFromServer(params[0],
						params[1], params[2], params[3], params[4], params[5],
						params[6], params[7], params[8],
						userSessionId.getSessionid());
			} else {
				sellerInfo = mSellerNet.getSellerInfoFromServer(params[0],
						params[1], params[2], params[3], params[4], params[5],
						params[6], params[7], params[8], "");
			}
			if (sellerInfo != null) {
				Editor editor = sp_seller.edit();
				editor.putInt("seller_total_page", sellerInfo.getTotalPage());
				editor.putInt("seller_total_count", sellerInfo.getTotalCount());
				editor.commit();
				resultList = sellerInfo.getResultList();
				// 储存条件索引
				List<SellerIndex> mSellerIndex = new ArrayList<SellerIndex>();
				if (resultList != null) {
					for (SellerInfoItem sellerItem : resultList) {
						// 条件进行hashcode处理
						mSellerIndex.add(new SellerIndex(condition, sellerItem
								.getId()));
					}
					// 判断如果长度和 商家个数匹配
					if (mSellerIndex.size() == resultList.size()) {
						// 插入数据库
						mSellerDao.insertSellerIndex(mSellerIndex);// 插入索引
						// 查询是否有该条信息存在
						ArrayList<SellerInfoItem> newResultData = new ArrayList<SellerInfoItem>();
						for (int i = 0; i < resultList.size(); i++) {
							SellerInfoItem sellerItem = resultList.get(i);
							boolean has = mSellerDao
									.selectSellerByBid(sellerItem.getId());
							Log.d("是否存在", has + "");
							if (!has) {
								newResultData.add(sellerItem);
								Log.d("缓存商家名：", sellerItem.getBusinessName());
							}
						}
						if (newResultData.size() > 0) {
							mSellerDao.InsertSeller(newResultData);// 插入商家
						}
					}
				}
			}
		}
		return sellerInfo;
	}

	@Override
	public SellerDetail getSellerDetailInfo(String... params) throws Exception {
		SellerDetail sellerDetail = null;
		// 判断是否有缓存
		sellerDetail = mSellerDao.selectSellerInfo(params[0]);
		if (sellerDetail == null || StrTools.isNull(sellerDetail.getAddress())
				|| StrTools.isNull(sellerDetail.getCarouselImgs())
				|| StrTools.isNull(sellerDetail.getDetailsDesc())
				|| StrTools.isNull(sellerDetail.getEnvironment())
				|| StrTools.isNull(sellerDetail.getFeature())) {
			// 从网络端获取
			sellerDetail = mSellerNet.getSellerDetailInfoFromServer(params[0]);
			// 并且存入数据库
			mSellerDao.insertSellerInfo(sellerDetail);
		}
		return sellerDetail;
	}

	@Override
	public ArrayList<IndexImage> getIndexImageList() throws Exception {
		return mSellerNet.getIndexImageFromServer();
	}

	@Override
	public SellerInfo getDiscountInfo(String... params) throws Exception {
		return mSellerNet.getDiscountInfoFormServer(params[0], params[1],
				params[2], params[3], params[4]);
	}

	@Override
	public ArrayList<SellerInfoItem> getReadHistory() throws Exception {
		return mSellerDao.selectReadHistory();
	}

	@Override
	public void addReadHistory(int id, String bid, String time)
			throws Exception {
		mSellerDao.insertReadHistroy(id, bid, time);
	}

	@Override
	public ArrayList<String> getReadTime() throws Exception {
		return mSellerDao.getReadTime();
	}
}
