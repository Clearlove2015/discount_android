package com.schytd.discount.dao.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AndroidSQLiteOpenHelper extends SQLiteOpenHelper {
	private static final String TAG = AndroidSQLiteOpenHelper.class
			.getSimpleName();

	// 定义一个数据库名字
	private static final String DB_NAME = "schytd.db";

	// 定义此次数据库的版本号
	private static final int DB_VERSION = 1;

	// 定义表名
	public static final String TABLE_SESSIONID = "tab_sessionid";

	public static final String TABLE_USER = "tab_user";

	public static final String TABLE_CONSUM_HISTORY = "consum_history";

	public static final String TABLE_SCORE_HISTORY = "score_history";

	public static final String TABLE_USE_SCORE_HISTORY = "use_score_history";
	// 登陆表
	public static final String TABLE_LOGIN = "login_info";
	// 推送消息
	public static final String TABLE_PUSH_INFO = "push_info";
	// 商家详情
	public static final String TABLE_SELLER_DETAIL = "seller_detail";
	// 商家列表索引表
	public static final String TABLE_SELLER_INDEX = "seller_list_index";
	// 商家列表
	public static final String TABLE_SELLER_LIST = "seller_list";
	// 浏览历史表
	public static final String TABLE_READ_HISTORY = "read_history";

	public AndroidSQLiteOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreate called");
		// 创建tab_sessionid表
		String sql1 = "DROP TABLE IF EXISTS " + TABLE_SESSIONID;
		db.execSQL(sql1);
		String sql2 = "CREATE TABLE " + TABLE_SESSIONID + " ("
				+ " id INTEGER PRIMARY KEY,"
				+ " sessionid VARCHAR(40) NOT NULL, "
				+ " time VARCHAR(20) NOT NULL " + ") ";
		db.execSQL(sql2);

		// 创建tab_user表
		String sql3 = "DROP TABLE IF EXISTS " + TABLE_USER;
		db.execSQL(sql3);
		String sql4 = "CREATE TABLE " + TABLE_USER + " ("
				+ " id INTEGER PRIMARY KEY," + " name VARCHAR(20), "
				+ " num VARCHAR(20), " + " gender VARCHAR(20) ,"
				+ " path VARCHAR(20) " + ")";
		db.execSQL(sql4);

		// 创建consum_history表
		String sql5 = "DROP TABLE IF EXISTS " + TABLE_CONSUM_HISTORY;
		db.execSQL(sql5);
		String sql6 = "CREATE TABLE " + TABLE_CONSUM_HISTORY + " ("
				+ " id INTEGER PRIMARY KEY, "
				+ " userbaseinfo_id VARCHAR(20), "
				+ " business_name VARCHAR(20), " + " amount VARCHAR(20), "
				+ " consumption_time VARCHAR(20)" + ") ";
		db.execSQL(sql6);

		// 创建score_history表
		String sql7 = "DROP TABLE IF EXISTS " + TABLE_SCORE_HISTORY;
		db.execSQL(sql7);
		String sql8 = "CREATE TABLE " + TABLE_SCORE_HISTORY + " ("
				+ " id INTEGER PRIMARY KEY," + " baseinfo_id VARCHAR(20),"
				+ " business_name VARCHAR(20)," + " amount VARCHAR(20),"
				+ " score VARCHAR(20)," + " time VARCHAR(20)" + ") ";
		db.execSQL(sql8);

		// 创建use_score_history表
		String sql9 = "DROP TABLE IF EXISTS " + TABLE_USE_SCORE_HISTORY;
		db.execSQL(sql9);
		String sql10 = "CREATE TABLE " + TABLE_USE_SCORE_HISTORY + " ("
				+ " id INTEGER PRIMARY KEY," + " userBaseId VARCHAR(20),"
				+ " score VARCHAR(20)," + " useType VARCHAR(20),"
				+ " useTime VARCHAR(20)" + ") ";
		db.execSQL(sql10);
		// 创建登陆表
		String sql11 = "DROP TABLE IF EXISTS " + TABLE_LOGIN;
		db.execSQL(sql11);
		String sql12 = "CREATE TABLE " + TABLE_LOGIN + " ("
				+ " id INTEGER PRIMARY KEY," + " number VARCHAR(20),"
				+ " password VARCHAR(20)" + ")";
		db.execSQL(sql12);
		// 推送消息表
		String sqll13 = "DROP TABLE IF EXISTS " + TABLE_PUSH_INFO;
		db.execSQL(sqll13);
		String sql14 = "CREATE TABLE " + TABLE_PUSH_INFO + " ("
				+ " id INTEGER PRIMARY KEY," + " msgId VARCHAR(20),"
				+ " date VARCHAR(20)," + " type VARCHAR(20),"
				+ " imgs VARCHAR(20)," + " title VARCHAR(20)" + ")";
		db.execSQL(sql14);
		// 商家详细表
		String sql15 = "DROP TABLE IF EXISTS " + TABLE_SELLER_DETAIL;
		db.execSQL(sql15);
		String sql16 = "CREATE TABLE " + TABLE_SELLER_DETAIL + " ("
				+ " id VARCHAR(20)," + " baseInfoId VARCHAR(20),"
				+ " carouselImgs VARCHAR(20)," + " detailsDesc VARCHAR(20),"
				+ " feature VARCHAR(20)," + " environment VARCHAR(20),"
				+ " tips VARCHAR(20)," + "address VARCHAR(20),"
				+ "contactPhoneNum VARCHAR(20)" + ")";
		db.execSQL(sql16);
		// 商家列表索引页表
		String sql17 = "DROP TABLE IF EXISTS " + TABLE_SELLER_INDEX;
		db.execSQL(sql17);
		String sql18 = "CREATE TABLE " + TABLE_SELLER_INDEX + " ("
				+ " sid VARCHAR(20)," + " bid VARCHAR(20)" + ")";
		db.execSQL(sql18);
		// 商家列表
		String sql19 = "DROP TABLE IF EXISTS " + TABLE_SELLER_LIST;
		db.execSQL(sql19);
		String sql20 = "CREATE TABLE " + TABLE_SELLER_LIST + " ("
				+ " bid VARCHAR(20) PRIMARY KEY," + " weight VARCHAR(20),"
				+ "status VARCHAR(20)," + "businessName VARCHAR(20),"
				+ "issigned VARCHAR(20)," + "address VARCHAR(20),"
				+ "contactPhoneNum VARCHAR(20)," + "lng VARCHAR(20),"
				+ "lat VARCHAR(20)," + "businessDesc VARCHAR(20),"
				+ "logoPic VARCHAR(20)," + "discount VARCHAR(20)" + " )";
		db.execSQL(sql20);
		// 浏览历史
		String sql21 = "DROP TABLE IF EXISTS " + TABLE_READ_HISTORY;
		db.execSQL(sql21);
		String sql22 = "CREATE TABLE " + TABLE_READ_HISTORY + " ("
				+ " id INTEGER PRIMARY KEY," + " bid VARCHAR(20) NOT NULL,"
				+ " time VARCHAR(20)" + " )";
		db.execSQL(sql22);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
