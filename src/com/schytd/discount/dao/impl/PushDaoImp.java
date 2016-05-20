package com.schytd.discount.dao.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.schytd.discount.dao.PushDao;
import com.schytd.discount.enties.PushItem;

public class PushDaoImp implements PushDao {
	private AndroidSQLiteOpenHelper androidSQLiteOpenHelper;

	public PushDaoImp(Context context) {
		androidSQLiteOpenHelper = new AndroidSQLiteOpenHelper(context);
	}

	@Override
	public void insertPush(List<PushItem> mPush) throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		// 开启事务
		db.beginTransaction();
		try {
			for (PushItem pushItem : mPush) {
				String sql = "INSERT INTO "
						+ AndroidSQLiteOpenHelper.TABLE_PUSH_INFO
						+ " (msgId,date,type,imgs,title) VALUES(?,?,?,?,?)";
				db.execSQL(sql,
						new Object[] { pushItem.getId(), pushItem.getDate(),
								pushItem.getType(), pushItem.getImgs(),
								pushItem.getTitle() });
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
	public void delPush() throws Exception {
		SQLiteDatabase db = null;
		int i = 0;
		try {
			db = androidSQLiteOpenHelper.getWritableDatabase();
			String table = AndroidSQLiteOpenHelper.TABLE_PUSH_INFO;
			i = db.delete(table, null, null);
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
	}

	@Override
	public List<PushItem> selectPush() throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String sql = "SELECT * FROM " + AndroidSQLiteOpenHelper.TABLE_PUSH_INFO;
		Cursor c = null;
		List<PushItem> data = null;
		PushItem item = null;
		try {
			data = new ArrayList<PushItem>();
			c = db.rawQuery(sql, null);
			while (c.moveToNext()) {
				item = new PushItem();
				item.setId(c.getString(c.getColumnIndex("msgId")));
				item.setTitle(c.getString(c.getColumnIndex("title")));
				item.setDate(c.getString(c.getColumnIndex("date")));
				item.setType(c.getString(c.getColumnIndex("type")));
				item.setImgs(c.getString(c.getColumnIndex("imgs")));
				data.add(item);
			}
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
		if (data.size() > 0) {
			return data;
		} else {
			return null;
		}
	}

	@Override
	public void updatePush(String msgId) throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String whereClause = "msgId=?";
		ContentValues values = new ContentValues();
		values.put("type", "2");
		String[] whereArgs = { msgId.toString() };
		db.update(AndroidSQLiteOpenHelper.TABLE_PUSH_INFO, values, whereClause,
				whereArgs);
	}

	@Override
	public List<PushItem> selectPushByType(String type) throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String sql = "SELECT * FROM "
				+ AndroidSQLiteOpenHelper.TABLE_PUSH_INFO
				+ " WHERE type = ?";
		Cursor c = null;
		List<PushItem> data = null;
		PushItem item = null;
		try {
			data = new ArrayList<PushItem>();
			c = db.rawQuery(sql,new String[] { type });
			while (c.moveToNext()) {
				item = new PushItem();
				item.setId(c.getString(c.getColumnIndex("msgId")));
				item.setTitle(c.getString(c.getColumnIndex("title")));
				item.setDate(c.getString(c.getColumnIndex("date")));
				item.setType(c.getString(c.getColumnIndex("type")));
				item.setImgs(c.getString(c.getColumnIndex("imgs")));
				data.add(item);
			}
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
		if (data!=null&&data.size() > 0) {
			return data;
		} else {
			return null;
		}
	}
}
