package com.schytd.discount.dao.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.schytd.discount.dao.UserDao;
import com.schytd.discount.enties.ConsumHistoryItem;
import com.schytd.discount.enties.ScoreGetHistoryItem;
import com.schytd.discount.enties.UseScoreHistoryItem;
import com.schytd.discount.enties.User;
import com.schytd.discount.enties.UserSessionId;

public class UserDaoImp implements UserDao {
	private AndroidSQLiteOpenHelper androidSQLiteOpenHelper;

	public UserDaoImp() {
	}

	public UserDaoImp(Context context) {
		androidSQLiteOpenHelper = new AndroidSQLiteOpenHelper(context);
	}

	@Override
	public void insertUserSessionId(UserSessionId user) throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();

		String sql = "INSERT INTO " + AndroidSQLiteOpenHelper.TABLE_SESSIONID
				+ " (id,sessionid,time) VALUES(?,?,?)";
		try {
			// 插入信息 id 为1
			db.execSQL(sql,
					new Object[] { 1, user.getSessionid(), user.getTime() });
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
	}

	@Override
	public UserSessionId selectUserSessionId() throws Exception {
		UserSessionId userSessionId = null;
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String sql = "SELECT * FROM " + AndroidSQLiteOpenHelper.TABLE_SESSIONID;
		Cursor c = null;
		try {
			c = db.rawQuery(sql, null);
			while (c.moveToNext()) {
				userSessionId = new UserSessionId();
				userSessionId.setId(c.getInt(c.getColumnIndex("id")));
				userSessionId.setSessionid(c.getString(c
						.getColumnIndex("sessionid")));
				userSessionId.setTime(c.getLong(c.getColumnIndex("time")));
			}
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
		return userSessionId;
	}

	@Override
	public int delteUserSessionId() throws Exception {
		SQLiteDatabase db = null;
		int i = 0;
		try {
			db = androidSQLiteOpenHelper.getWritableDatabase();
			String table = AndroidSQLiteOpenHelper.TABLE_SESSIONID;
			i = db.delete(table, null, null);
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
		return i;
	}

	@Override
	public void insertUser(User user) throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();

		String sql = "INSERT INTO " + AndroidSQLiteOpenHelper.TABLE_USER
				+ " (id,name,num,gender,path) VALUES(?,?,?,?,?)";
		try {
			db.execSQL(sql, new Object[] { 1, user.getName(), user.getNum(),
					user.getGender(), user.getUserIcon() });
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
		}

	}

	@SuppressWarnings("resource")
	@Override
	public User selectUser() throws Exception {
		User user = null;
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String sql = "SELECT * FROM " + AndroidSQLiteOpenHelper.TABLE_USER;
		Cursor c = null;
		try {
			c = db.rawQuery(sql, null);
			while (c.moveToNext()) {
				user = new User();
				String name = c.getString(c.getColumnIndex("name"));
				String num = c.getString(c.getColumnIndex("num"));
				String gender = c.getString(c.getColumnIndex("gender"));
				String path = c.getString(c.getColumnIndex("path"));
				// 如果用户名或 性别 号码为空 直接返回空
				if (name == null || num == null || gender == null
						|| path == null) {
					return null;
				}
				user.setId(c.getInt(c.getColumnIndex("id")));
				user.setName(name);
				user.setNum(num);
				user.setGender(gender);
				user.setUserIcon(path);

			}
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
		return user;
	}

	@Override
	public int delteUser() throws Exception {
		SQLiteDatabase db = null;
		int i = 0;
		try {
			db = androidSQLiteOpenHelper.getWritableDatabase();
			String table = AndroidSQLiteOpenHelper.TABLE_USER;
			i = db.delete(table, null, null);
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
		return i;
	}

	@Override
	public int updateUserInfo(Integer id, String item, String args)
			throws Exception {
		SQLiteDatabase db = null;
		// 查询是否有该条数据
		if (selectUser() == null) {
			return -1;
		}
		int i = 0;
		try {
			db = androidSQLiteOpenHelper.getWritableDatabase();
			String table = AndroidSQLiteOpenHelper.TABLE_USER;
			String whereClause = "id=?";
			ContentValues values = new ContentValues();
			values.put(item, args);
			String[] whereArgs = { id.toString() };
			i = db.update(table, values, whereClause, whereArgs);
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
		return i;
	}

	@Override
	public void insertConsumHistory(List<ConsumHistoryItem> consumHistoryItems)
			throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String sql = "INSERT INTO "
				+ AndroidSQLiteOpenHelper.TABLE_CONSUM_HISTORY
				+ " (userbaseinfo_id,business_name,amount,consumption_time) VALUES(?,?,?,?)";
		// 开启事务
		db.beginTransaction();
		try {
			// 批量处理操作
			for (ConsumHistoryItem consumHistory : consumHistoryItems) {
				db.execSQL(
						sql,
						new Object[] { consumHistory.getId(),
								consumHistory.getBussinessName(),
								consumHistory.getAmount(),
								consumHistory.getConsumptionTime() });
			}
			// 设置事务标志为成功，当结束事务时就会提交事务
			db.setTransactionSuccessful();
		} finally {
			// 结束事务
			db.endTransaction();
			if (db != null && db.isOpen()) {
				db.close();
			}
		}

	}

	@Override
	public ArrayList<ConsumHistoryItem> selectConsumHistory() throws Exception {
		ArrayList<ConsumHistoryItem> consumHistoryItems = null;
		ConsumHistoryItem consumHistory = null;
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String sql = "SELECT * FROM "
				+ AndroidSQLiteOpenHelper.TABLE_CONSUM_HISTORY;
		Cursor c = null;
		try {
			c = db.rawQuery(sql, null);
			// 加入consumHistoryItems集合
			consumHistoryItems = new ArrayList<ConsumHistoryItem>();
			while (c.moveToNext()) {
				consumHistory = new ConsumHistoryItem();
				String userBaseInfoId = c.getString(c.getColumnIndex("id"));
				String bussinessName = c.getString(c
						.getColumnIndex("business_name"));
				String amount = c.getString(c.getColumnIndex("amount"));
				String consumptionTime = c.getString(c
						.getColumnIndex("consumption_time"));
				// 封装consumHistory对象
				consumHistory.setUserBaseInfoId(userBaseInfoId);
				consumHistory.setBussinessName(bussinessName);
				consumHistory.setAmount(amount);
				consumHistory.setConsumptionTime(consumptionTime);
				consumHistoryItems.add(consumHistory);

			}
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
		return consumHistoryItems;
	}

	@Override
	public void insertScoreHistory(
			List<ScoreGetHistoryItem> scoreGetHistoryItems) throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String sql = "INSERT INTO "
				+ AndroidSQLiteOpenHelper.TABLE_SCORE_HISTORY
				+ " (baseinfo_id,business_name,score,amount,time) VALUES(?,?,?,?,?)";
		// 开启事务
		db.beginTransaction();
		try {
			for (ScoreGetHistoryItem scoreHistory : scoreGetHistoryItems) {
				db.execSQL(sql, new Object[] { scoreHistory.getBaseInfoId(),
						scoreHistory.getBusinessName(),
						scoreHistory.getScore(), scoreHistory.getAmount(),
						scoreHistory.getTheTime() });
			}
			// 设置事务标志为成功，当结束事务时就会提交事务
			db.setTransactionSuccessful();
		} finally {
			// 结束事务
			db.endTransaction();
			if (db != null && db.isOpen()) {
				db.close();
			}
		}

	}

	public void insertUseScoreHistory(
			List<UseScoreHistoryItem> useScoreHistoryItems) throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String sql = "INSERT INTO "
				+ AndroidSQLiteOpenHelper.TABLE_USE_SCORE_HISTORY
				+ " (userBaseId,score,useType,useTime) VALUES(?,?,?,?)";
		// 开启事务
		db.beginTransaction();
		try {
			for (UseScoreHistoryItem useScoreHistory : useScoreHistoryItems) {
				db.execSQL(
						sql,
						new Object[] { useScoreHistory.getUserBaseId(),
								useScoreHistory.getScore(),
								useScoreHistory.getUseType(),
								useScoreHistory.getUseTime() });
			}
			// 设置事务标志为成功，当结束事务时就会提交事务
			db.setTransactionSuccessful();
		} finally {
			// 结束事务
			db.endTransaction();
			if (db != null && db.isOpen()) {
				db.close();
			}
		}

	}

	@Override
	public ArrayList<ScoreGetHistoryItem> selectScoreHistory() throws Exception {
		ArrayList<ScoreGetHistoryItem> scoreHistoryItems = null;
		ScoreGetHistoryItem scoreHistory = null;
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String sql = "SELECT * FROM "
				+ AndroidSQLiteOpenHelper.TABLE_SCORE_HISTORY;
		Cursor c = null;
		try {
			c = db.rawQuery(sql, null);
			// 加入scoreHistoryItems集合
			scoreHistoryItems = new ArrayList<ScoreGetHistoryItem>();
			while (c.moveToNext()) {
				scoreHistory = new ScoreGetHistoryItem();
				String baseInfoId = c
						.getString(c.getColumnIndex("baseinfo_id"));
				String businessName = c.getString(c
						.getColumnIndex("business_name"));
				String score = c.getString(c.getColumnIndex("score"));
				String amount = c.getString(c.getColumnIndex("amount"));
				String theTime = c.getString(c.getColumnIndex("time"));
				// 封装scoreHistory对象
				scoreHistory.setBaseInfoId(baseInfoId);
				scoreHistory.setBusinessName(businessName);
				scoreHistory.setScore(score);
				scoreHistory.setTheTime(theTime);
				scoreHistory.setAmount(amount);
				scoreHistoryItems.add(scoreHistory);
			}
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
		return scoreHistoryItems;
	}

	@Override
	public ArrayList<UseScoreHistoryItem> selectUseScoreHistory()
			throws Exception {
		ArrayList<UseScoreHistoryItem> useScoreHistoryItems = null;
		UseScoreHistoryItem useScoreHistory = null;
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String sql = "SELECT * FROM "
				+ AndroidSQLiteOpenHelper.TABLE_USE_SCORE_HISTORY;
		Cursor c = null;
		try {
			c = db.rawQuery(sql, null);
			// 加入useScoreHistoryItems集合
			useScoreHistoryItems = new ArrayList<UseScoreHistoryItem>();
			while (c.moveToNext()) {
				useScoreHistory = new UseScoreHistoryItem();
				String userBaseId = c.getString(c.getColumnIndex("userBaseId"));
				String score = c.getString(c.getColumnIndex("score"));
				int useType = c.getInt(c.getColumnIndex("useType"));
				String useTime = c.getString(c.getColumnIndex("useTime"));
				// 封装useScoreHistory对象
				useScoreHistory.setUserBaseId(userBaseId);
				useScoreHistory.setScore(score);
				useScoreHistory.setUseType(useType);
				useScoreHistory.setUseTime(useTime);
				useScoreHistoryItems.add(useScoreHistory);
			}
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
		return useScoreHistoryItems;
	}

	@Override
	public void deleteConsumHistory() throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String table = AndroidSQLiteOpenHelper.TABLE_CONSUM_HISTORY;
		try {
			db.delete(table, null, null);
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
	}

	@Override
	public void deleteScoreHistory() throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String table = AndroidSQLiteOpenHelper.TABLE_SCORE_HISTORY;
		try {
			db.delete(table, null, null);
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
	}

	@Override
	public void deleteUseScoreHistory() throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String table = AndroidSQLiteOpenHelper.TABLE_USE_SCORE_HISTORY;
		try {
			db.delete(table, null, null);
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
	}

	// 保存用户登陆信息
	@Override
	public void insertUserLoginInfo(User user) throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();

		String sql = "INSERT INTO " + AndroidSQLiteOpenHelper.TABLE_LOGIN
				+ " (id,number,password) VALUES(?,?,?)";
		try {
			db.execSQL(sql,
					new Object[] { 1, user.getNum(), user.getPassword() });
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
		}

	}

	// 修改用户登录信息

	// 删除用户登陆信息
	@Override
	public void deleteUserLoginInfo() throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String table = AndroidSQLiteOpenHelper.TABLE_LOGIN;
		try {
			db.delete(table, null, null);
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
	}

	@Override
	public User selectUserLoginInfo() throws Exception {
		User user = null;
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String sql = "SELECT * FROM " + AndroidSQLiteOpenHelper.TABLE_LOGIN;
		Cursor c = null;
		try {
			c = db.rawQuery(sql, null);
			while (c.moveToNext()) {
				user = new User();
				user.setId(c.getInt(c.getColumnIndex("id")));
				user.setNum(c.getString(c.getColumnIndex("number")));
				user.setPassword(c.getString(c.getColumnIndex("password")));
			}
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
		return user;
	}

	@Override
	public void updateUserLoginInfo(String item, String args) throws Exception {
		SQLiteDatabase db = null;
		// 查询是否有该条数据
		if (selectUser() == null) {
			return;
		}
		try {
			db = androidSQLiteOpenHelper.getWritableDatabase();
			String table = AndroidSQLiteOpenHelper.TABLE_LOGIN;
			ContentValues values = new ContentValues();
			values.put(item, args);
			db.update(table, values, null, null);
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
		return;

	}

}
