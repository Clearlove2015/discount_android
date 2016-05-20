package com.schytd.discount.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMapTouchListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.schytd.discount.bussiness.SellerBussiness;
import com.schytd.discount.bussiness.impl.SellerBussinessImp;
import com.schytd.discount.enties.ConstantData;
import com.schytd.discount.enties.SellerInfo;
import com.schytd.discount.enties.SellerInfoItem;
import com.schytd.discount.tools.SortTools;
import com.schytd.discount.tools.StrTools;
import com.schytd.discount.ui.FragmentMap.OnCloseViewPagerListener;
import com.schytd.discount_android.R;

public class ActivityMap extends FragmentActivity implements
		OnCloseViewPagerListener {
	// 当当前activity被销毁的时候 返回当前的检索的数据
	public static int MAP_BACK = 501;
	private ViewPager mViewPage_detail;
	private List<FragmentMap> mListFragment;
	private ImageView mImageView_show;
	// viewpager隐藏显示
	private boolean isOpen = false;
	// 再次定位
	private boolean isLocation = false;
	// 控制显示
	// private Projection mProjection;
	// 用于标志是否为认为请求刷新
	private boolean isUserDo = true;
	private ImageView mImageView_location;
	// 返回
	private ImageView mImageVew_back;
	// 请求数据
	private SellerBussiness mSellerBussiness;
	private ArrayList<SellerInfoItem> mData;
	// 接受距离数据
	private String distance = "1000";
	// 接受搜索条件
	private String searchTxt = "";
	// 加载视图
	private LinearLayout mLinearlayout_loading;
	// 地图UI设置
	private UiSettings mUiSettings;
	// 地图当前的缩放级别
	private float mCurrentZoom;
	// 网络不可用
	private LinearLayout mLinearLayout_no_net;
	// 再次连接网络
	private ImageView mImageView_to_connect;
	// 分类
	private String mCondition_one = "0", mCondition_two = "0", mCondition="";
	private String district="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_map_layout);
		distance = getIntent().getStringExtra("distance");
		searchTxt = getIntent().getStringExtra("search");
		mCondition = getIntent().getStringExtra("condition");
		if (!StrTools.isNull(mCondition)) {
			if (mCondition.length() > 3) {
				mCondition_one = mCondition;
			} else {
				mCondition_two = mCondition;
			}
		} else {
			mCondition = "0";
		}
		if (StrTools.isNull(searchTxt)) {
			searchTxt = "";
		}
		if (StrTools.isNull(distance)) {
			distance = "1000";
		}
		init();
	}

	public boolean isNetworkConnected() {
		// 判断网络是否连接
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		return mNetworkInfo != null && mNetworkInfo.isAvailable();
	}

	private void init() {
		mImageView_to_connect = (ImageView) this
				.findViewById(R.id.map_to_connect);
		mImageView_to_connect.setOnClickListener(mClickListener);
		mLinearLayout_no_net = (LinearLayout) this
				.findViewById(R.id.map_no_net);
		if (!isNetworkConnected()) {
			mLinearLayout_no_net.setVisibility(View.VISIBLE);
		}
		mLinearlayout_loading = (LinearLayout) this
				.findViewById(R.id.map_loading);
		mViewPage_detail = (ViewPager) this
				.findViewById(R.id.activity_map_viewpage);
		mSellerBussiness = new SellerBussinessImp(this);
		mData = new ArrayList<SellerInfoItem>();
		// mData = (ArrayList<SellerInfoItem>) getIntent().getSerializableExtra(
		// "data");
		// if (mData.size() == 0) {

		// }
		// 隐藏显示
		mImageView_show = (ImageView) this.findViewById(R.id.off);
		mImageView_show.setEnabled(false);
		// 定位
		mImageView_location = (ImageView) this
				.findViewById(R.id.activity_map_location);
		mImageVew_back = (ImageView) this.findViewById(R.id.activity_map_back);
		mListFragment = new ArrayList<FragmentMap>();
		mViewPage_detail.setAdapter(mFragmentPagerAdapter);
		mMapView = (MapView) this.findViewById(R.id.bmapView);
		if (mBaiduMap == null) {
			mBaiduMap = mMapView.getMap();
			mUiSettings = mBaiduMap.getUiSettings();
		}
		// 去掉百度logo
		mMapView.removeViewAt(1);
		mMapView.showZoomControls(false);// 隐藏默认缩放按钮
		mMapView.showScaleControl(false);// 隐藏默认比例尺
		mBaiduMap.getUiSettings().setCompassEnabled(false);
		// 范围
		// mProjection = mBaiduMap.getProjection();
		mLocationMarker = BitmapDescriptorFactory// 构建mark图标
				.fromResource(R.drawable.location_point);
		// 定位
		getPosition();
		mBaiduMap.setOnMapStatusChangeListener(mMapStatusChange);
		// pool.execute(t);
		mImageView_show.setOnClickListener(mClickListener);
		mImageView_location.setOnClickListener(mClickListener);
		mImageVew_back.setOnClickListener(mClickListener);
		mBaiduMap.setOnMarkerClickListener(listener);
		// 添加数据 count 从服务器获得
		mViewPage_detail.setOnPageChangeListener(mPageChangeListener);
		mBaiduMap.setOnMapTouchListener(mTouchListenr);
	}

	// 判断版本
	private Bitmap getViewBitmap(int id, int position) {
		LayoutInflater inflater = this.getLayoutInflater();
		View addViewContent = inflater.inflate(id, null);
		TextView textView_num = (TextView) addViewContent
				.findViewById(R.id.list_num);
		textView_num.setText("" + (position + 1));
		addViewContent.setDrawingCacheEnabled(true);
		addViewContent.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		addViewContent.layout(0, 0, addViewContent.getMeasuredWidth(),
				addViewContent.getMeasuredHeight());

		addViewContent.buildDrawingCache();
		Bitmap cacheBitmap = addViewContent.getDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

		return bitmap;
	}

	// 定位
	private void getPosition() {
		district=ConstantData.district;
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

	// 按钮点击事件
	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.off:
				if (isOpen) {
					mViewPage_detail.setVisibility(View.GONE);
					mImageView_show.setVisibility(View.VISIBLE);
					isOpen = false;
				} else {
					mViewPage_detail.setVisibility(View.VISIBLE);
					mImageView_show.setVisibility(View.GONE);
					isOpen = true;
				}
				break;
			case R.id.activity_map_location:
				isLocation = true;
				mBaiduMap.clear();
				drawMarker(latitude, longitude, mLocationMarker, "原点");
				getPosition();
				nowLat = lastLat;
				nowLng = lastLng;
				searchTxt = "";// 清空搜索选项
				new GetDataTask().execute(latitude + "", longitude + "",
						distance, searchTxt, 10 + "", 1 + "", mCondition_one,
						mCondition_two, district);
				break;
			case R.id.activity_map_back:
				finish();
				overridePendingTransition(R.anim.push_right_out,
						R.anim.push_right_in);
				break;
			case R.id.map_to_connect:
				if (isNetworkConnected()) {
					mLinearLayout_no_net.setVisibility(View.GONE);
					new GetDataTask().execute(nowLat + "", nowLng + "",
							distance, searchTxt, 10 + "", 1 + "",
							mCondition_one, mCondition_two, district);
				}
				break;
			}
		}
	};

	// 绘制marker点
	private void drawMarker(double lat, double lng,
			BitmapDescriptor markerIcon, String title) {
		LatLng ll = new LatLng(lat, lng);
		// 设置缩放比例,更新地图状态
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,
				mCurrentZoom);
		mBaiduMap.animateMapStatus(u);
		overlay(ll, markerIcon, mBaiduMap, title);
	}

	// 添加fragment 并传递商家信息
	private void addFragment() {
		mListFragment = new ArrayList<FragmentMap>();
		for (int i = 0; i < mData.size(); i++) {
			// 商家信息和距离
			LatLng latLng = new LatLng(
					Double.parseDouble(mData.get(i).getLat()),
					Double.parseDouble(mData.get(i).getLng()));
			mListFragment.add(new FragmentMap(mData.get(i), DistanceUtil
					.getDistance(new LatLng(nowLat, nowLng), latLng), i + 1,
					new LatLng(nowLat, nowLng), ActivityMap.this));
		}
	}

	// viewpager适配器
	private FragmentStatePagerAdapter mFragmentPagerAdapter = new FragmentStatePagerAdapter(
			getSupportFragmentManager()) {
		@Override
		public int getCount() {
			return mListFragment.size();
		}

		@Override
		public Fragment getItem(int position) {
			return mListFragment.get(position);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	};
	MapView mMapView;
	BaiduMap mBaiduMap;
	boolean isFirstLoc = true;// 是否首次定位

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	BitmapDescriptor mCurrentMarker;// Marker图标
	BitmapDescriptor mLocationMarker;
	// 定位经纬
	double latitude;
	double longitude;
	// 定位点
	LatLng ll;

	private void overlay(LatLng point, BitmapDescriptor bitmap,
			BaiduMap baiduMap, String title) {
		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap).title(title);
		// 在地图上添加Marker，并显示
		baiduMap.addOverlay(option);
	}

	OnMarkerClickListener listener = new OnMarkerClickListener() {
		/**
		 * 地图 Marker 覆盖物点击事件监听函数
		 * 
		 * @param marker
		 *            被点击的 marker
		 */
		public boolean onMarkerClick(Marker marker) {
			String id = marker.getTitle();
			if (id.equals("原点") || id.equals("当前点")) {
				return false;
			}
			int i = Integer.parseInt(id);
			if (i >= 0 && i <= mData.size()) {
				mViewPage_detail.setCurrentItem(i);
				mViewPage_detail.setVisibility(View.VISIBLE);
				mImageView_show.setVisibility(View.GONE);
				isOpen = true;
			}
			return true;
		}
	};
	// viewpager事件
	private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			SellerInfoItem item = mData.get(position);
			double lat = Double.parseDouble(item.getLat());
			double lng = Double.parseDouble(item.getLng());
			if (item != null) {
				drawMarker(lat, lng,
						BitmapDescriptorFactory.fromBitmap(getViewBitmap(
								R.layout.layout_marker, position)), position
								+ "");
			}
			isUserDo = false;
		}

		@Override
		public void onPageScrolled(int position, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int position) {
		}
	};

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// mapview 销毁后不在处理新接收的位置
			if (mMapView == null)
				return;
			Toast.makeText(ActivityMap.this, "正在为您定位", Toast.LENGTH_SHORT)
					.show();
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			latitude = location.getLatitude();
			longitude = location.getLongitude();

			nowLat = location.getLatitude();
			nowLng = location.getLongitude();
			ll = new LatLng(latitude, longitude);
			// 根据获得数据添加fragment
			addFragment();
			mFragmentPagerAdapter.notifyDataSetChanged();
			// 设置缩放比例,更新地图状态
			float f = mBaiduMap.getMaxZoomLevel();// 19.0
			mCurrentZoom = (float) (f - 3.5);
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,
					(float) (f - 3.5));
			mBaiduMap.animateMapStatus(u);
			overlay(ll, mLocationMarker, mBaiduMap, "原点");
			for (int i = 0; i < mData.size(); i++) {
				LatLng point = new LatLng(Double.parseDouble(mData.get(i)
						.getLat()), Double.parseDouble(mData.get(i).getLng()));
				overlay(point,
						BitmapDescriptorFactory.fromBitmap(getViewBitmap(
								R.layout.layout_marker, i)), mBaiduMap, i + "");
			}
			mLocClient.stop();
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	// 记录上次请求的经纬度
	private double lastLat, lastLng, nowLat, nowLng;
	// 地图状态改变
	private OnMapStatusChangeListener mMapStatusChange = new OnMapStatusChangeListener() {

		@Override
		public void onMapStatusChangeStart(MapStatus staus) {
		}

		@Override
		public void onMapStatusChangeFinish(MapStatus staus) {
			LatLng la = mBaiduMap.getMapStatus().target;
			nowLat = la.latitude;
			nowLng = la.longitude;
			LatLng oldLa = new LatLng(lastLat, lastLng);
			// 判断两点的距离 大于100 才绘制
			if (DistanceUtil.getDistance(oldLa, la) >= 100 && isUserDo) {
				// overlay(la, mCurrentMarker, mBaiduMap);
				// lastLat = la.latitude;
				// lastLng = la.longitude;
				lastLat = nowLat;
				lastLng = nowLng;
				// 清除之前的mark点
				mBaiduMap.clear();
				drawMarker(latitude, longitude, mLocationMarker, "原点");
				drawMarker(la.latitude, la.longitude,
						BitmapDescriptorFactory
								.fromResource(R.drawable.map_no_draw), "当前点");
				new GetDataTask().execute(nowLat + "", nowLng + "", distance,
						searchTxt, 10 + "", 1 + "", mCondition_one,
						mCondition_two, district);

			}
			mCurrentZoom = staus.zoom;// 得到当前的缩放级别
		}

		@Override
		public void onMapStatusChange(MapStatus staus) {
		}
	};
	// 地图滑动事件
	private OnMapTouchListener mTouchListenr = new OnMapTouchListener() {

		@Override
		public void onTouch(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				isUserDo = true;
			}

		}
	};

	// 加载商家信息
	class GetDataTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			mLinearlayout_loading.setVisibility(View.VISIBLE);
			mImageView_show.setEnabled(false);
			mUiSettings.setScrollGesturesEnabled(false);// 禁止平移
			mUiSettings.setZoomGesturesEnabled(false);// 禁止缩放
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				SellerInfo sellerinfo = null;
				if (StrTools.isNull(params[0]) || StrTools.isNull(params[1])
						|| StrTools.isNull(params[4])
						|| StrTools.isNull(params[5])) {
					return false;
				} else {
					if (StrTools.isNull(params[2])) {
						params[2] = 1000 + "";
					}
					sellerinfo = mSellerBussiness.getSellerInfo(params[0],
							params[1], params[2], params[3], params[4],
							params[5], params[6], params[7], params[8]);
					if (sellerinfo != null) {
						mData = sellerinfo.getResultList();
						if (sellerinfo.getTotalCount() != 0) {
							return true;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				if (mData.size() > 0) {
					mImageView_show.setEnabled(true);
					// 对数据进行距离排序
					mData = (ArrayList<SellerInfoItem>) SortTools.distanceSort(
							lastLat, lastLng, mData);
					for (int i = 0; i < mData.size(); i++) {
						LatLng point = new LatLng(Double.parseDouble(mData.get(
								i).getLat()), Double.parseDouble(mData.get(i)
								.getLng()));
						overlay(point,
								BitmapDescriptorFactory
										.fromBitmap(getViewBitmap(
												R.layout.layout_marker, i)),
								mBaiduMap, i + "");
					}
					if (isLocation) {
						drawMarker(latitude, longitude, mLocationMarker, "原点");
						isLocation = false;
					}
				} else {
					mImageView_show.setEnabled(false);
				}
			} else {
				// 没有数据 则清除上次marker
				mBaiduMap.clear();
				// drawMarker(lastLat, lastLng, mLocationMarker, "当前点");
				mData = new ArrayList<SellerInfoItem>();
				Toast.makeText(ActivityMap.this, "无相关商家", Toast.LENGTH_SHORT)
						.show();
				// viewpager不显示
				if (isOpen) {
					mViewPage_detail.setVisibility(View.GONE);
					isOpen = false;
					mImageView_show.setVisibility(View.VISIBLE);
				}
				mImageView_show.setEnabled(false);
			}
			addFragment();
			mFragmentPagerAdapter.notifyDataSetChanged();
			mLinearlayout_loading.setVisibility(View.GONE);
			mUiSettings.setScrollGesturesEnabled(true);// 取消禁止平移
			mUiSettings.setZoomGesturesEnabled(true);// 取消禁止缩放
		}

	}

	@Override
	public void finish() {
		// Intent intent = new Intent();
		// intent.putExtra("map_data", (Serializable) mData);
		// // 返回当前查询位置点经纬度
		// intent.putExtra("lat", lastLat);
		// intent.putExtra("lng", lastLng);
		// setResult(MAP_BACK, intent);
		super.finish();
	}

	// 重写返回键事件

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.push_right_out,
					R.anim.push_right_in);
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	// 关闭viewpager
	@Override
	public void onCloseViewPager() {
		mImageView_show.setVisibility(View.VISIBLE);
		mViewPage_detail.setVisibility(View.GONE);
		isOpen = false;
	}

}
