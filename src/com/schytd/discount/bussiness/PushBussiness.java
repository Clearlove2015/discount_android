package com.schytd.discount.bussiness;

import java.util.List;

import com.schytd.discount.enties.PushInfo;
import com.schytd.discount.enties.PushItem;

public interface PushBussiness {
	// 得到当天发布的消息
	public List<PushItem> getPushData() throws Exception;

	// 得到消息的详细信息
	public PushInfo getPushDetail(String... params) throws Exception;

	public void insertPushData(List<PushItem> mPush) throws Exception;

	public void removePushData() throws Exception;

	// 更新
	public void refreshPushData(String id) throws Exception;
//	根据类型获得消息
	public List<PushItem> getPushDataByType(String type) throws Exception;
}
