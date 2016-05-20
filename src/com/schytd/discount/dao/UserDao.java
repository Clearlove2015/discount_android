package com.schytd.discount.dao;

import java.util.ArrayList;
import java.util.List;

import com.schytd.discount.enties.ConsumHistoryItem;
import com.schytd.discount.enties.ScoreGetHistoryItem;
import com.schytd.discount.enties.UseScoreHistoryItem;
import com.schytd.discount.enties.User;
import com.schytd.discount.enties.UserSessionId;

public interface UserDao {
	public void insertUserSessionId(UserSessionId user) throws Exception;

	public UserSessionId selectUserSessionId() throws Exception;
	// 删除sessinId
	public int delteUserSessionId() throws Exception;

	public void insertUser(User user) throws Exception;

	public User selectUser() throws Exception;

	// 删除用户信息
	public int delteUser() throws Exception;

	// 修改用户信息
	public int updateUserInfo(Integer id, String item, String args)
			throws Exception;

	// 添加消费记录
	public void insertConsumHistory(List<ConsumHistoryItem> consumHistoryItems)
			throws Exception;

	// 添加积分记录
	public void insertScoreHistory(
			List<ScoreGetHistoryItem> scoreGetHistoryItems) throws Exception;

	// 使用积分记录
	public void insertUseScoreHistory(
			List<UseScoreHistoryItem> useScoreHistoryItems) throws Exception;

	// 查询消费列表
	public ArrayList<ConsumHistoryItem> selectConsumHistory() throws Exception;

	// 查询积分列表
	public ArrayList<ScoreGetHistoryItem> selectScoreHistory() throws Exception;

	// 查询使用积分列表
	public ArrayList<UseScoreHistoryItem> selectUseScoreHistory()
			throws Exception;

	// 删除消费记录表
	public void deleteConsumHistory() throws Exception;

	// 删除积分记录表
	public void deleteScoreHistory() throws Exception;

	// 删除使用积分记录表
	public void deleteUseScoreHistory() throws Exception;

	// 插入用户登陆信息
	public void insertUserLoginInfo(User user) throws Exception;

	// 删除用户登陆信息
	public void deleteUserLoginInfo() throws Exception;

	// 查询用户登陆信息
	public User selectUserLoginInfo() throws Exception;
	// 修改用户登陆信息
	public void updateUserLoginInfo(String item, String args) throws Exception;
}
