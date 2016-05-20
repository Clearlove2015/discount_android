package com.schytd.discount.dao;

import java.util.List;

import com.schytd.discount.enties.PushItem;

public interface PushDao {
	// 存入  type 1,2 已读 未读
	public void insertPush(List<PushItem> mPush) throws Exception;
	// 删除
	public void delPush() throws Exception;
	// 查询
	public List<PushItem> selectPush() throws Exception;
//	更新
	public void updatePush(String msgId) throws Exception;
//	得到消息根据类型
	public List<PushItem> selectPushByType(String type) throws Exception;
}
