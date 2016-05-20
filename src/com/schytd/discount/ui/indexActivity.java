package com.schytd.discount.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.schytd.discount.bussiness.SellerBussiness;
import com.schytd.discount.bussiness.impl.SellerBussinessImp;
import com.schytd.discount.enties.ConstantData;
import com.schytd.discount.enties.IndexImage;
import com.schytd.discount_android.R;

public class indexActivity extends Activity {
	public static int REQUEST_FIRST = 502;
	public static int REQUEST_MAIN_TWO = 503;
	// 记录启动
	private SharedPreferences sp;
	private SharedPreferences city_sp;
	// 得到轮播图
	private SellerBussiness mSellerBussiness;
	// 首页轮播图
	private List<IndexImage> mData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		// 去除title
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 去掉Activity上面的状态栏
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_index_layout);
		init();
	}

	// 获取屏幕像素、屏幕密度
	public int getScreenDensityDpi() {
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
		return densityDpi;
	}

	private void init() {
		mSellerBussiness = new SellerBussinessImp(this);
		mData = new ArrayList<IndexImage>();
		new GetImageTask().execute();
		// 初始化SharedPreferences
		city_sp = getSharedPreferences("city_code", Context.MODE_PRIVATE);
		sp = getSharedPreferences("user_start", Context.MODE_PRIVATE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				Editor editor = city_sp.edit();
				editor.putString("成都市", "510100000000");
				editor.putString("自贡市", "510300000000");
				editor.putString("攀枝花市", "510400000000");
				editor.putString("泸州市", "510500000000");
				editor.putString("德阳市", "510600000000");
				editor.putString("绵阳市", "510700000000");
				editor.putString("广元市", "510800000000");
				editor.putString("遂宁市", "510900000000");
				editor.putString("内江市", "511000000000");
				editor.putString("乐山市", "511100000000");
				editor.putString("南充市", "511300000000");
				editor.putString("眉山市", "511400000000");
				editor.putString("宜宾市", "511500000000");
				editor.putString("广安市", "511600000000");
				editor.putString("达州市", "511700000000");
				editor.putString("广安市", "511600000000");
				editor.putString("雅安市", "511800000000");
				editor.putString("巴中市", "511900000000");
				editor.putString("资阳市", "512000000000");
				editor.putString("阿坝藏族羌族自治州", "513200000000");
				editor.putString("甘孜藏族自治州", "513300000000");
				editor.putString("凉山彝族自治州", "513400000000");
				editor.commit();
			}
		}).start();

		Timer timer = new Timer();
		if (!sp.getBoolean("start", false)) {
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					Intent i = new Intent(indexActivity.this,
							FirstActivity.class);
					i.putExtra("path", (Serializable) mData);
					startActivityForResult(i, REQUEST_FIRST);
					// 首次启动 开启viewpager页面
					// 记录首次开启
					Editor editor = sp.edit();
					editor.putBoolean("start", true);
					editor.commit();
				}
			};
			timer.schedule(task, 1000);
		} else {
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					Intent i = new Intent(indexActivity.this,
							MainActivity.class);
					i.putExtra("path", (Serializable) mData);
					startActivityForResult(i, REQUEST_MAIN_TWO);
					// 首次启动 开启viewpager页面
					// 记录首次开启
					Editor editor = sp.edit();
					editor.putBoolean("start", true);
					editor.commit();
				}
			};
			timer.schedule(task, 1500);
		}
		ConstantData.Dpi = getScreenDensityDpi();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_FIRST || requestCode == REQUEST_MAIN_TWO) {
			// 关闭引导页 1
			finish();
		}
	}

	// 首页轮播图
	private class GetImageTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				mData = mSellerBussiness.getIndexImageList();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}

}
