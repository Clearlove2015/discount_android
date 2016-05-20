package com.schytd.discount.bussiness.impl;

import java.util.List;

import android.content.Context;
import android.util.Log;

import com.schytd.discount.bussiness.PushBussiness;
import com.schytd.discount.dao.PushDao;
import com.schytd.discount.dao.impl.PushDaoImp;
import com.schytd.discount.enties.PushInfo;
import com.schytd.discount.enties.PushItem;
import com.schytd.discount.net.PushNet;
import com.schytd.discount.net.impl.PushNetImpl;

public class PushBussinessImp implements PushBussiness {
	private PushDao mPushDaoImp;
	private PushNet mPushNetImp;

	public PushBussinessImp(Context c) {
		mPushDaoImp = new PushDaoImp(c);
		mPushNetImp = new PushNetImpl();
	}

	@Override
	public void insertPushData(List<PushItem> mPush) throws Exception {
		mPushDaoImp.insertPush(mPush);
	}

	@Override
	public void removePushData() throws Exception {
		mPushDaoImp.delPush();
	}

	@Override
	public PushInfo getPushDetail(String... params) throws Exception {
		return mPushNetImp.getPushDetail(params[0], params[1]);
	}

	// 得到当天的消息，并存入数据库
	@Override
	public List<PushItem> getPushData() throws Exception {
		// 数据库中是否有数据
		List<PushItem> mPushData = null;
		boolean hasToday = true;
		mPushData = mPushDaoImp.selectPush();
		if (mPushData == null) {
			// 没有缓冲
			mPushData = mPushNetImp.getPushInfoOfDay();
			// 存入数据库
			mPushDaoImp.insertPush(mPushData);
		} else {
			for (PushItem pushItem : mPushData) {
				Log.d("++++++++++++++", pushItem.getType());
			}
			// 有缓冲
//			mPushData = mPushDaoImp.selectPush();
//			// 判断时间 是否有今天的时间 沒有今天的
//			for (PushItem pushItem : mPushData) {
//				// if (pushItem.getDate()) {
//				// hasToday=false;
//				// }
//			}
//			// 如果数据库没有今天的消息 去服务器请求
//			if (!hasToday) {
//				mPushData = mPushNetImp.getPushInfoOfDay();
//				// 存入数据库
//				mPushDaoImp.insertPush(mPushData);
//			}
		}
		return mPushData;
	}

	@Override
	public void refreshPushData(String id) throws Exception {
		mPushDaoImp.updatePush(id);
	}

	@Override
	public List<PushItem> getPushDataByType(String type) throws Exception {
		// 数据库中是否有数据
		List<PushItem> mPushData = null;
		mPushData = mPushDaoImp.selectPushByType(type);
		return mPushData;
	}
}
