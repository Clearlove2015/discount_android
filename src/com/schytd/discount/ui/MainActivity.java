package com.schytd.discount.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.schytd.discount.bussiness.UpdateBussiness;
import com.schytd.discount.bussiness.UserBussiness;
import com.schytd.discount.bussiness.impl.UpdateBussinessImpl;
import com.schytd.discount.bussiness.impl.UserBussinessImpl;
import com.schytd.discount.enties.ConstantData;
import com.schytd.discount.enties.IndexImage;
import com.schytd.discount.enties.SellerInfoItem;
import com.schytd.discount.enties.User;
import com.schytd.discount.tools.ImageUtils;
import com.schytd.discount.tools.ImageUtils.OnLoadImageListener;
import com.schytd.discount.tools.StrTools;
import com.schytd.discount.ui.View.ImageCycleView;
import com.schytd.discount.ui.View.ImageCycleView.ImageCycleViewListener;
import com.schytd.discount.ui.View.RoundImageView;
import com.schytd.discount.ui.dialog.PromptDialog;
import com.schytd.discount.ui.pushservice.MyPushService;
import com.schytd.discount.ui.pushservice.PushDemoReceiver;
import com.schytd.discount.ui.slidingMenu.SlidingMenu;
import com.schytd.discount_android.R;

public class MainActivity extends FragmentActivity implements OnClickListener {
	private static final int REQUEST_TO_ACTMAP = 3;
	private static final int REQUEST_TO_LIST = 202;
	private static final int REQUEST_NO_NET = 4;
	private static final int REQUEST_CITY_CHOICE = 5;
	public static String tView = "";
	public static boolean USER_TO_LOGOUT = false;
	private static int REQUEST_PERSON_CENTER = 111;
	private static int REQUEST_LOGIN = 201;
	private static int REQUEST_SEARCH = 112;

	private long downloadId;
	private DownloadManager downloadManager;
	private DownloadReceiver receiver;
	private UpdateAsyncTask updateAsyncTask;

	private UserBussiness mUserBusiness = new UserBussinessImpl(this);
	private UpdateBussiness mUpdateBussiness = new UpdateBussinessImpl();

	Drawable drawable = null;
	// 侧滑
	private SlidingMenu mMenu;
	// toolbar
	private ImageCycleView mImageCycleView_img;
	// 定位点
	private static TextView mTextView_location;
	// 菜单用户名
	private TextView mTextView_name;
	// 轮播图片
	private List<IndexImage> mImageData;
	// 图片fragment
	private List<FragmentSellerImg> mFragment;
	// 图片 url
	private ArrayList<String> path;
	SharedPreferences sp_city;
	// menu 菜单头像
	private RoundImageView mRoundImageView;

	@SuppressWarnings("unchecked")
	private void init() {
		mImageData = (List<IndexImage>) getIntent()
				.getSerializableExtra("path");
		sp_city = getSharedPreferences("city_code", Context.MODE_PRIVATE);
		// 商家列表页
		intent_list = new Intent(MainActivity.this, ActivityDetail.class);
		intent_list.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		mFragment = new ArrayList<FragmentSellerImg>();
		path = new ArrayList<String>();

		mMenu = new SlidingMenu(this);
		// 设置滑动方向
		mMenu.setMode(SlidingMenu.LEFT);
		// 设置监听开始滑动的触碰范围
		mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		// 设置边缘阴影的宽度，通过dimens资源文件中的ID设置
		mMenu.setShadowWidthRes(R.dimen.shadow_width);
		// 设置边缘阴影的颜色/图片，通过资源文件ID设置
		mMenu.setShadowDrawable(R.drawable.shadow);
		// 设置menu全部打开后，主界面剩余部分与屏幕边界的距离，通过dimens资源文件ID设置
		mMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// 设置是否淡入淡出
		mMenu.setFadeEnabled(true);
		// 设置淡入淡出的值，只在setFadeEnabled设置为true时有效
		mMenu.setFadeDegree(0.35f);
		// 将menu绑定到Activity，同时设置绑定类型
		mMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		// 设置menu的layout
		mMenu.setMenu(R.layout.layout_menu);
		// 设置menu的背景
		mTextView_name = (TextView) this.findViewById(R.id.user_name);
		// toolbar
		mImageCycleView_img = (ImageCycleView) this
				.findViewById(R.id.index_img);

		receiver = new DownloadReceiver();
		// 下载完毕要接收通知
		registerReceiver(receiver, new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		// 点击通知栏要接收通知
		registerReceiver(receiver, new IntentFilter(
				DownloadManager.ACTION_NOTIFICATION_CLICKED));
		// 从系统服务中获得DownloadManager对象
		downloadManager = (DownloadManager) getSystemService(Service.DOWNLOAD_SERVICE);

		if (mImageData != null) {
			for (IndexImage img : mImageData) {
				mFragment.add(new FragmentSellerImg(img.getUrl()));
				path.add(img.getUrl());
			}
		}
		// 图片
		mImageCycleView_img.setImageResources(path, mAdCycleViewListener, 1);
		mMenu.addIgnoredView(mImageCycleView_img);
		mTextView_location = (TextView) this.findViewById(R.id.index_title);
		intent_to_person_center = new Intent(MainActivity.this,
				ActivityPersonCenter.class);
		getPosition();
		if (isNetworkConnected()) {
			mRoundImageView = (RoundImageView) this.findViewById(R.id.menu_img);
			new GetInfoTask().execute();
		} else {
			// 没网时
		}
	}

	private ImageCycleViewListener mAdCycleViewListener = new ImageCycleViewListener() {
		@Override
		public void onImageClick(int position, View imageView) {
			// 点击轮播图片
			Intent i = new Intent(MainActivity.this,
					ActivityMessageDetail.class);
			i.putExtra("id", mImageData.get(position).getId());
			i.putExtra("type", "manpage");
			startActivity(i);
		}
	};

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();

	// 定位
	private void getPosition() {
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
		int span = 1000;
		option.setScanSpan(span);// 设置发起定位请求的间隔时间为1000ms
		option.setIsNeedAddress(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	String add = "";
	private Double nowLat, nowLng;
	private String nowAdd = "";

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// 此处设置开发者获取到的方向信息，顺时针0-360
			// ll = new LatLng(latitude, longitude);
			if (location == null) {
				Toast.makeText(MainActivity.this, "定位失败！", Toast.LENGTH_SHORT)
						.show();
			} else {
				nowAdd = location.getAddrStr();
				nowLat = location.getLatitude();
				nowLng = location.getLongitude();
				intent_list.putExtra("lat", nowLat);
				intent_list.putExtra("lng", nowLng);
				intent_list.putExtra("nowAdd", nowAdd);
				add = location.getCity();
				ConstantData.district = sp_city.getString(add, "510100000000");
				if (!StrTools.isNull(add)) {
					add = add.substring(0, add.indexOf("市"));
				} else {
					Toast.makeText(MainActivity.this, "定位失败！",
							Toast.LENGTH_SHORT).show();
					add = "成都";
				}
				ConstantData.position = add;
				mTextView_location.setText(add);
			}
			mLocClient.stop();
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	// 获取屏幕像素、屏幕密度
	public int getScreenDensityDpi() {
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
		return densityDpi;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isNetworkConnected()) {
			new SessionIdTask().execute();
		} else {
			// 没有网的操作
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main_layout);
		startService(new Intent(MainActivity.this, MyPushService.class));
		init();
		update_version();
	}

	@Override
	public void onDestroy() {
		PushDemoReceiver.payloadData.delete(0,
				PushDemoReceiver.payloadData.length());
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public void onStop() {
		Log.d("GetuiSdkDemo", "onStop()");
		super.onStop();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == ActivityPersonalMessage.RESULT_TO_MAIN_NAME) {
			String name = data.getStringExtra("name");
		}
		if (requestCode == REQUEST_LOGIN) {
			if (resultCode == 222) {
				sessionId = data.getStringExtra("sessionId");
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						final String username = mUserBusiness.getUserinfo()
								.getName();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
			// 登陆成功 来到个人中心
			if (!StrTools.isNull(sessionId)) {
				startActivityForResult(new Intent(MainActivity.this,
						ActivityPersonCenter.class), REQUEST_PERSON_CENTER);
			}

		} else if (requestCode == REQUEST_PERSON_CENTER) {
			if (resultCode == 123) {
				sessionId = null;
				mTextView_name.setText("未登录");
			} else {
				// 返回传递用户姓名
				if (resultCode == ActivityPersonCenter.PERSON_CENTER_BACK_RESULT) {
					// 设置用户名
					mTextView_name.setText(data.getStringExtra("name"));
					// 设置头像
					ImageUtils imageUtils = new ImageUtils(MainActivity.this,
							data.getStringExtra("path"));
					imageUtils.onLoadImage(new OnLoadImageListener() {
						@Override
						public void OnLoadImage(Bitmap bitmap, String bitmapPath) {
							if (bitmap != null) {
								mRoundImageView.setImageBitmap(bitmap);
							}
						}
					});
				}
			}
		}
		// 搜索页面返回的值
		if (requestCode == REQUEST_SEARCH) {
			if (resultCode == ActivitySearch.RESULT_SEARCH) {
				String search = data.getStringExtra("search_txt");
				Intent intent = new Intent(MainActivity.this,
						ActivityDetail.class);
				intent.putExtra("search_txt", search);
				startActivity(intent);
			}
		}
		if (requestCode == REQUEST_TO_ACTMAP) {
			if (resultCode == ActivityMap.MAP_BACK) {
				List<SellerInfoItem> backData = new ArrayList<SellerInfoItem>();
				backData = (List<SellerInfoItem>) data
						.getSerializableExtra("map_data");
				// 得到当前的搜索位置
				double lat = data.getDoubleExtra("lat", 30);// 默认定位天府广场
				double lng = data.getDoubleExtra("lng", 104);
				// ActivityDetail.mFragment_list.handleData(backData, lat, lng);
				// 刷新位置
				// ActivityDetail.refreshLocation(lat, lng);
			}
		}
		// 城市选择页面
		if (requestCode == REQUEST_CITY_CHOICE) {
			if (resultCode == ActivityCityChoice.RESULT_CITY_CHOICE) {
				if (data != null) {
					String city = data.getStringExtra("city");
					ConstantData.district = sp_city.getString(city,
							"510100000000");
					if (city.contains(add)) {
						ConstantData.ISNOW = true;
					} else {
						ConstantData.ISNOW = false;
					}
					if (city.contains("市")) {
						city = city.substring(0, city.indexOf("市"));
					}
					if (city.length() > 3 && !city.contains("市")) {
						city = city.substring(0, 2);
					}
					if (!StrTools.isNull(city)) {
						ConstantData.position = city;
						mTextView_location.setText(city);
					}
				}
			}
		}
	}

	private String sessionId = null;
	private Intent intent_to_person_center, intent_list;

	/*
	 * 餐饮 100 中餐10001 火锅10002 烧烤10003 大排档10004 海鲜 10005 西餐10006 其他10099 娱乐 200
	 * ktv 20001 酒吧 20002 会所 20003 其他 20099 休闲 300 茶楼 30001 水吧 30002 咖啡 30003 其他
	 * 30099 住行 400 酒店 40001 公寓 40002 生活服务 40003 其他 40099
	 */
	@Override
	public void onClick(View view) {

		int id = view.getId();
		switch (id) {
		// 搜索
		case R.id.index_goto_search:
			// 跳转到搜索界面
			startActivityForResult(new Intent(MainActivity.this,
					ActivitySearch.class), REQUEST_SEARCH);
			break;
		// 菜单切换
		case R.id.menu_back:
			// 打开菜单
			mMenu.toggle();
			break;
		// 餐饮
		case R.id.catering:
			intent_list.putExtra("condition", "100");
			intent_list.putExtra("name", "餐饮");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 中餐
		case R.id.china_food:
			intent_list.putExtra("condition", "10001");
			intent_list.putExtra("name", "中餐");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 烧烤
		case R.id.Barbecue:
			intent_list.putExtra("name", "烧烤");
			intent_list.putExtra("condition", "10003");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 火锅
		case R.id.hot_pot:
			intent_list.putExtra("name", "火锅");
			intent_list.putExtra("condition", "10002");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 大排档
		case R.id.the_big_food:
			intent_list.putExtra("name", "大排档");
			intent_list.putExtra("condition", "10004");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 海鲜
		case R.id.sea_food:
			intent_list.putExtra("name", "海鲜");
			intent_list.putExtra("condition", "10005");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 西餐
		case R.id.west_food:
			intent_list.putExtra("name", "西餐");
			intent_list.putExtra("condition", "10006");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 料理
		case R.id.arrange:
			intent_list.putExtra("name", "料理");
			intent_list.putExtra("condition", "10007");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 干锅
		case R.id.gan_guo:
			intent_list.putExtra("name", "干锅");
			intent_list.putExtra("condition", "10008");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 特色餐饮
		case R.id.food_specail:
			intent_list.putExtra("name", "特色餐饮");
			intent_list.putExtra("condition", "10009");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 餐饮其他
		case R.id.food_other:
			intent_list.putExtra("name", "餐饮-其他");
			intent_list.putExtra("condition", "10099");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 娱乐
		case R.id.fun:
			intent_list.putExtra("name", "娱乐");
			intent_list.putExtra("condition", "200");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// ktv
		case R.id.ktv:
			intent_list.putExtra("name", "KTV");
			intent_list.putExtra("condition", "20001");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 酒吧
		case R.id.jiu_bar:
			intent_list.putExtra("name", "酒吧");
			intent_list.putExtra("condition", "20002");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 会所
		case R.id.club:
			intent_list.putExtra("name", "会所");
			intent_list.putExtra("condition", "20003");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 娱乐其他
		case R.id.fun_other:
			intent_list.putExtra("name", "娱乐-其他");
			intent_list.putExtra("condition", "20099");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 休闲
		case R.id.xiu_time:
			intent_list.putExtra("name", "休闲");
			intent_list.putExtra("condition", "300");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 茶楼
		case R.id.tea:
			intent_list.putExtra("name", "茶楼");
			intent_list.putExtra("condition", "30001");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 水吧
		case R.id.water_bar:
			intent_list.putExtra("name", "水吧");
			intent_list.putExtra("condition", "30002");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 咖啡
		case R.id.coffe:
			intent_list.putExtra("name", "咖啡");
			intent_list.putExtra("condition", "30003");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 休闲其他
		case R.id.xiu_other:
			intent_list.putExtra("name", "休闲-其他");
			intent_list.putExtra("condition", "30099");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 住行
		case R.id.live_line:
			intent_list.putExtra("name", "住行");
			intent_list.putExtra("condition", "400");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 酒店
		case R.id.hotal:
			intent_list.putExtra("name", "酒店");
			intent_list.putExtra("condition", "40001");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 公寓
		case R.id.apartment:
			intent_list.putExtra("name", "公寓");
			intent_list.putExtra("condition", "40002");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 生活服务
		case R.id.life_service:
			intent_list.putExtra("name", "生活服务");
			intent_list.putExtra("condition", "40003");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;
		// 住行 其他
		case R.id.live_other:
			intent_list.putExtra("name", "住行-其他");
			intent_list.putExtra("condition", "40099");
			startActivityForResult(intent_list, REQUEST_TO_LIST);
			break;

		case R.id.goto_setting:
			startActivity(new Intent(MainActivity.this, ActivitySetting.class));
			break;
		// case R.id.reLocation:
		// // 重新定位
		// DetailFragment.reLocation();
		// break;
		// 点击头像 进入个人中心
		case R.id.menu_img:
			if (isNetworkConnected()) {
				if (StrTools.isNull(sessionId)) {
					startActivityForResult(new Intent(MainActivity.this,
							ActivityLogin.class), REQUEST_LOGIN);
				} else {
					// 将位置信息传递到个人中心
					intent_to_person_center.putExtra("position", add);
					startActivityForResult(intent_to_person_center,
							REQUEST_PERSON_CENTER);
				}
			}
			break;
		// 个人中心
		case R.id.goto_personCenter:
			if (isNetworkConnected()) {
				if (StrTools.isNull(sessionId)) {
					startActivityForResult(new Intent(MainActivity.this,
							ActivityLogin.class), REQUEST_LOGIN);
				} else {
					// 将位置信息传递到个人中心
					intent_to_person_center.putExtra("position", add);
					startActivityForResult(intent_to_person_center,
							REQUEST_PERSON_CENTER);
				}
			} else {
				Intent intent_to_no_net = new Intent(MainActivity.this,
						ActivityNoNet.class);
				intent_to_no_net.putExtra("position", add);
				startActivityForResult(intent_to_no_net, REQUEST_NO_NET);
			}
			break;
		// 附近商家
		case R.id.near_seller:
			startActivity(intent_list);
			break;
		// 消息中心
		case R.id.goto_message_center:
			startActivity(new Intent(MainActivity.this,
					ActivityMessageCenter.class));
			break;
		// 优惠资讯
		case R.id.goto_discount_info:
			Intent i_discount = new Intent(MainActivity.this,
					ActivityDiscountInfo.class);
			i_discount.putExtra("lat", nowLat);
			i_discount.putExtra("lng", nowLng);
			startActivity(i_discount);
			break;
		// 进入选择城市
		case R.id.choice_city:
			Intent i = new Intent(MainActivity.this, ActivityCityChoice.class);
			i.putExtra("location", add);
			startActivityForResult(i, REQUEST_CITY_CHOICE);
			break;
		case R.id.goto_read_history:
			Intent i_read=new Intent(MainActivity.this,
					ActivityReadHistory.class);
			i_read.putExtra("lat", nowLat);
			i_read.putExtra("lng", nowLng);
			startActivity(i_read);
			break;
		}
	}

	// 获得sessionId
	private class SessionIdTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			String sessionId = null;
			try {
				sessionId = mUserBusiness.getSessionId();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return sessionId;
		}

		protected void onPostExecute(String result) {
			if (result != null) {
				sessionId = result;
			}
		};
	};

	/** * 菜单、返回键响应 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click(); // 调用双击退出函数
		}
		return false;
	}

	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true;
			Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000);
		} else {
			finish();
			overridePendingTransition(R.anim.push_right_out,
					R.anim.push_right_in);
		}
	}

	public boolean isNetworkConnected() {
		// 判断网络是否连接
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		return mNetworkInfo != null && mNetworkInfo.isAvailable();
	}

	private static String mDistance;

	// // 设置定位地点
	// public static void setText(String str, String distance) {
	// mTextView_location.setText(str);
	// mDistance = distance;
	// };
	//
	// public static void setText(String str) {
	// mTextView_location.setText(str);
	// }

	// 得到用户的信息
	private class GetInfoTask extends AsyncTask<Void, Void, User> {

		@Override
		protected User doInBackground(Void... arg0) {
			// 用户信息
			User user = null;
			try {
				user = mUserBusiness.getUserinfo();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return user;
		}

		protected void onPostExecute(User result) {
			if (result != null) {
				mTextView_name.setText(result.getName());
				ImageUtils imageUtils = new ImageUtils(MainActivity.this,
						result.getUserIcon());
				imageUtils.onLoadImage(new OnLoadImageListener() {
					@Override
					public void OnLoadImage(Bitmap bitmap, String bitmapPath) {
						if (bitmap != null) {
							mRoundImageView.setImageBitmap(bitmap);
						}
					}
				});
			}
		};
	};

	// 更新版本
	public void update_version() {
		Log.d("MainActivity method", "update_version called...");
		updateAsyncTask = new UpdateAsyncTask();
		updateAsyncTask.execute();
	}

	private class UpdateAsyncTask extends AsyncTask<Void, Void, List<String>> {

		@Override
		protected List<String> doInBackground(Void... arg0) {
			try {
				List<String> versioninfo = mUpdateBussiness.update();
				return versioninfo;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<String> versioninfo) {
			if (versioninfo != null) {
				String code = versioninfo.get(0);
				if (code.equals("0")) {
					String software_version = ConstantData.SOFT_VERSION;
					String version = versioninfo.get(1);
					Log.d("++++++", "software_version = " + software_version);
					Log.d("++++++", "version = " + version);
					if (software_version.equals(version)) {
						Log.d("++++++", "已经是最新版本");
						// Toast.makeText(MainActivity.this, "已经是最新版本",
						// Toast.LENGTH_SHORT).show();
					} else {
						String download_url = versioninfo.get(2);
						Log.d(">>>>>>>>>>", "download_path = " + download_url);
						showUpdatDialog(version, download_url);
					}
				} else {
					// Toast.makeText(MainActivity.this, "更新失败...",
					// Toast.LENGTH_SHORT).show();
					Log.d("更新返回code码", "code = " + code);
				}
			} else {
				Log.d("MainActivity", "服务器无响应。versioninfo为空");
				// Toast.makeText(MainActivity.this, "服务器无响应",
				// Toast.LENGTH_SHORT)
				// .show();
			}
		}
	}

	/**
	 * 更新对话框
	 */
	public void showUpdatDialog(String version, final String download_url) {
		new PromptDialog.Builder(MainActivity.this)
				.setMessage("最新版本：" + version + "\n\n是否更新？", null)
				.setTitle("发现新版本")
				.setButton1("立即更新", new PromptDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog, int which) {
						// 开始下载
						try {
							download_file(download_url);
						} catch (Exception e) {
							e.printStackTrace();
						}
						dialog.dismiss();
					}
				}).setButton2("以后再说", new PromptDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	// 下载更新文件
	public void download_file(String urlPath) {
		Log.d("method", "download_file called...");
		// 构造下载指定目录
		File downloadDir = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "schytd");
		if (!downloadDir.exists()) {
			downloadDir.mkdirs();
		}
		// 文件名
		String fileName = "update.apk";
		// 构建请求
		Uri uri = Uri.parse(urlPath);
		DownloadManager.Request request = new DownloadManager.Request(uri);
		// 运行在wifi 或者手机网络情况下都下载
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
				| DownloadManager.Request.NETWORK_MOBILE);
		// 是否允许在漫游状态下载
		request.setAllowedOverRoaming(false);
		// 设置下载通知栏上的标题名字，如果不写，默认以下载文件名显示
		request.setTitle("下载apk更新文件...");
		// 设置下载通知栏上的描述信息（相当于副标题）
		request.setDescription("惠消费");
		// 指定下载的目录和文件
		// 第一参数只能是目录名字（不能是downloadDir.getAbsolutePath()）
		// 第二参数只能是文件名（downloadFile.getAbsolutePath()）
		request.setDestinationInExternalPublicDir("schytd", fileName);
		// 默认download目录
		// request.setDestinationInExternalPublicDir(
		// Environment.DIRECTORY_DOWNLOADS, fileName);

		// 设置通知栏显示信息
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		// 此次下载的随机id值
		downloadId = downloadManager.enqueue(request);
	}
	// 下载更新文件返回信息
	private class DownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			long completedDownloadId = intent.getLongExtra(
					DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			if (downloadId == completedDownloadId) {
				Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
				// 下载完成自动弹出安装
				Intent intent1 = new Intent(Intent.ACTION_VIEW);
				intent1.setDataAndType(Uri.fromFile(new File(Environment
						.getExternalStorageDirectory()
						+ File.separator
						+ "schytd" + File.separator + "update.apk")),
						"application/vnd.android.package-archive");
				MainActivity.this.startActivity(intent1);
			}
		}
	}

}
