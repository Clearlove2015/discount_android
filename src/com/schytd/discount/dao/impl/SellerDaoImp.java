package com.schytd.discount.dao.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.schytd.discount.dao.SellerDao;
import com.schytd.discount.enties.SellerDetail;
import com.schytd.discount.enties.SellerIndex;
import com.schytd.discount.enties.SellerInfoItem;

public class SellerDaoImp implements SellerDao {
	private AndroidSQLiteOpenHelper androidSQLiteOpenHelper;

	public SellerDaoImp(Context context) {
		androidSQLiteOpenHelper = new AndroidSQLiteOpenHelper(context);
	}

	//
	@Override
	public void InsertSeller(List<SellerInfoItem> list) throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		// 开启事务
		String sql = "INSERT INTO "
				+ AndroidSQLiteOpenHelper.TABLE_SELLER_LIST
				+ " (bid,weight,status,businessName,issigned,address,contactPhoneNum,lng,lat,businessDesc,logoPic,discount) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
		db.beginTransaction();
		try {
			// 批量处理操作
			for (SellerInfoItem sellerInfoItem : list) {
				db.execSQL(
						sql,
						new Object[] { sellerInfoItem.getId(),
								sellerInfoItem.getWeight(),
								sellerInfoItem.getStatus(),
								sellerInfoItem.getBusinessName(),
								sellerInfoItem.getIssigned(),
								sellerInfoItem.getAddress(),
								sellerInfoItem.getContactPhoneNum(),
								sellerInfoItem.getLng(),
								sellerInfoItem.getLat(),
								sellerInfoItem.getBusinessDesc(),
								sellerInfoItem.getLogoPic(),
								sellerInfoItem.getDiscount() });
			}
			// 设置事务标志为成功，当结束事务时就会提交事务
			db.setTransactionSuccessful();
		} finally {
			// 结束事务
			db.endTransaction();
			if (db != null && db.isOpen()) {
				db.close();
				db = null;
			}
		}
	}

	@Override
	public boolean selectSellerByBid(String bid) throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String sql = "SELECT *FROM seller_list " + " WHERE bid = ?";
		Cursor c = null;
		try {
			c = db.rawQuery(sql, new String[] { bid });
			if (c.getCount() > 0) {
				return true;
			}
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
			if (db != null && db.isOpen()) {
				db.close();
				db = null;
			}
		}
		return false;
	}

	@Override
	public ArrayList<SellerInfoItem> selectSeller(String condition)
			throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		ArrayList<SellerInfoItem> sellerInfoItems = null;
		SellerInfoItem sellerInfo = null;
		String sql = "SELECT *FROM seller_list INNER JOIN seller_list_index on seller_list.bid = seller_list_index.bid "
				+ "WHERE seller_list_index.sid = ?";
		Cursor c = null;
		try {
			c = db.rawQuery(sql, new String[] { condition });
			// 加入consumHistoryItems集合
			sellerInfoItems = new ArrayList<SellerInfoItem>();
			while (c.moveToNext()) {
				sellerInfo = new SellerInfoItem();
				sellerInfo.setId(c.getString(c.getColumnIndex("bid")));
				sellerInfo.setWeight(c.getString(c.getColumnIndex("weight")));
				sellerInfo.setStatus(c.getString(c.getColumnIndex("status")));
				sellerInfo.setBusinessName(c.getString(c
						.getColumnIndex("businessName")));
				sellerInfo
						.setIssigned(c.getString(c.getColumnIndex("issigned")));
				sellerInfo.setAddress(c.getString(c.getColumnIndex("address")));
				sellerInfo.setContactPhoneNum(c.getString(c
						.getColumnIndex("contactPhoneNum")));
				sellerInfo.setLat(c.getString(c.getColumnIndex("lat")));
				sellerInfo.setLng(c.getString(c.getColumnIndex("lng")));
				sellerInfo.setBusinessDesc(c.getString(c
						.getColumnIndex("businessDesc")));
				sellerInfo.setLogoPic(c.getString(c.getColumnIndex("logoPic")));
				sellerInfo
						.setDiscount(c.getString(c.getColumnIndex("discount")));
				// 添加
				sellerInfoItems.add(sellerInfo);
			}
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
			if (db != null && db.isOpen()) {
				db.close();
				db = null;
			}
		}
		if (sellerInfoItems.size() == 0) {
			return null;
		}
		return sellerInfoItems;
	}

	@Override
	public void delteSeller() throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String table = AndroidSQLiteOpenHelper.TABLE_SELLER_LIST;
		try {
			db.delete(table, null, null);
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
				db = null;
			}
		}
	}

	// 插入商家详细信息
	@Override
	public void insertSellerInfo(SellerDetail sellerDetail) throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String sql = "INSERT INTO "
				+ AndroidSQLiteOpenHelper.TABLE_SELLER_DETAIL
				+ " (id,baseInfoId,carouselImgs,detailsDesc,feature,environment,tips,address,contactPhoneNum) VALUES(?,?,?,?,?,?,?,?,?)";
		try {
			db.execSQL(
					sql,
					new String[] { sellerDetail.getId(),
							sellerDetail.getBaseInfoId(),
							sellerDetail.getCarouselImgs(),
							sellerDetail.getDetailsDesc(),
							sellerDetail.getFeature(),
							sellerDetail.getEnvironment(),
							sellerDetail.getTips(), sellerDetail.getAddress(),
							sellerDetail.getContactPhoneNum() });
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
				db = null;
			}
		}
	}

	// 查询商家详细信息
	@Override
	public SellerDetail selectSellerInfo(String baseInfoId) throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		Cursor c = null;
		SellerDetail sellerDetail = null;
		String sql = "SELECT * FROM "
				+ AndroidSQLiteOpenHelper.TABLE_SELLER_DETAIL
				+ " WHERE baseInfoId = ?";
		try {
			c = db.rawQuery(sql, new String[] { baseInfoId });
			sellerDetail = new SellerDetail();
			while (c.moveToNext()) {
				sellerDetail.setId(c.getString(c.getColumnIndex("id")));
				sellerDetail.setBaseInfoId(c.getString(c
						.getColumnIndex("baseInfoId")));
				sellerDetail.setCarouselImgs(c.getString(c
						.getColumnIndex("carouselImgs")));
				sellerDetail.setDetailsDesc(c.getString(c
						.getColumnIndex("detailsDesc")));
				sellerDetail
						.setFeature(c.getString(c.getColumnIndex("feature")));
				sellerDetail.setEnvironment(c.getString(c
						.getColumnIndex("environment")));
				sellerDetail.setTips(c.getString(c.getColumnIndex("tips")));
				sellerDetail
						.setAddress(c.getString(c.getColumnIndex("address")));
				sellerDetail.setContactPhoneNum(c.getString(c
						.getColumnIndex("contactPhoneNum")));
			}

		} finally {
			if (db != null && db.isOpen()) {
				db.close();
				db = null;
			}
		}
		return sellerDetail;
	}

	@Override
	public void deleteSellerInfo() throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String table = AndroidSQLiteOpenHelper.TABLE_SELLER_DETAIL;
		try {
			db.delete(table, null, null);
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
				db = null;
			}
		}

	}

	// 商家列表索引
	@Override
	public void insertSellerIndex(List<SellerIndex> sellerindex)
			throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String sql = "INSERT INTO "
				+ AndroidSQLiteOpenHelper.TABLE_SELLER_INDEX
				+ " (sid,bid) VALUES(?,?)";
		// 开启事务
		db.beginTransaction();
		try {
			// 批量处理操作
			for (SellerIndex sellerInfoItem : sellerindex) {
				db.execSQL(sql, new Object[] { sellerInfoItem.getSid(),
						sellerInfoItem.getBid() });
			}
			// 设置事务标志为成功，当结束事务时就会提交事务
			db.setTransactionSuccessful();
		} finally {
			// 结束事务
			db.endTransaction();
			if (db != null && db.isOpen()) {
				db.close();
				db = null;
			}
		}
	}

	@Override
	public void delteSellerIndex() throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String table = AndroidSQLiteOpenHelper.TABLE_SELLER_INDEX;
		try {
			db.delete(table, null, null);
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
				db = null;
			}
		}

	}

	@Override
	public void insertReadHistroy(int id, String bid, String time)
			throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		String sql = "INSERT INTO "
				+ AndroidSQLiteOpenHelper.TABLE_READ_HISTORY
				+ " (id,bid,time) VALUES(?,?,?)";
		try {
			db.execSQL(sql, new Object[] { id, bid, time });
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
				db = null;
			}
		}
	}

	// 得到浏览历史
	@Override
	public ArrayList<SellerInfoItem> selectReadHistory() throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		ArrayList<SellerInfoItem> sellerInfoItems = null;
		SellerInfoItem sellerInfo = null;
		String sql = "SELECT *FROM seller_list INNER JOIN read_history on seller_list.bid = read_history.bid ";
		Cursor c = null;
		try {
			c = db.rawQuery(sql, null);
			// 加入consumHistoryItems集合
			sellerInfoItems = new ArrayList<SellerInfoItem>();
			while (c.moveToNext()) {
				sellerInfo = new SellerInfoItem();
				sellerInfo.setId(c.getString(c.getColumnIndex("bid")));
				sellerInfo.setWeight(c.getString(c.getColumnIndex("weight")));
				sellerInfo.setStatus(c.getString(c.getColumnIndex("status")));
				sellerInfo.setBusinessName(c.getString(c
						.getColumnIndex("businessName")));
				sellerInfo
						.setIssigned(c.getString(c.getColumnIndex("issigned")));
				sellerInfo.setAddress(c.getString(c.getColumnIndex("address")));
				sellerInfo.setContactPhoneNum(c.getString(c
						.getColumnIndex("contactPhoneNum")));
				sellerInfo.setLat(c.getString(c.getColumnIndex("lat")));
				sellerInfo.setLng(c.getString(c.getColumnIndex("lng")));
				sellerInfo.setBusinessDesc(c.getString(c
						.getColumnIndex("businessDesc")));
				sellerInfo.setLogoPic(c.getString(c.getColumnIndex("logoPic")));
				sellerInfo
						.setDiscount(c.getString(c.getColumnIndex("discount")));
				// 添加
				sellerInfoItems.add(sellerInfo);
			}
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
			if (db != null && db.isOpen()) {
				db.close();
				db = null;
			}
		}
		return sellerInfoItems;
	}

	@Override
	public ArrayList<String> getReadTime() throws Exception {
		SQLiteDatabase db = androidSQLiteOpenHelper.getWritableDatabase();
		ArrayList<String> time;
		String sql = "SELECT time FROM read_history";
		Cursor c = null;
		try {
			c = db.rawQuery(sql, null);
			time = new ArrayList<String>();
			// 加入consumHistoryItems集合
			while (c.moveToNext()) {
				time.add(c.getString(c.getColumnIndex("time")));
			}
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
			if (db != null && db.isOpen()) {
				db.close();
				db = null;
			}
		}
		return time;

	}
}
