package com.schytd.discount.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.schytd.discount.bussiness.SellerBussiness;
import com.schytd.discount.bussiness.impl.SellerBussinessImp;
import com.schytd.discount.enties.ConstantData;
import com.schytd.discount.enties.SellerInfo;
import com.schytd.discount.enties.SellerInfoItem;
import com.schytd.discount.tools.ImageUtils;
import com.schytd.discount.tools.ImageUtils.OnLoadImageListener;
import com.schytd.discount.tools.SortTools;
import com.schytd.discount.tools.StrTools;
import com.schytd.discount.ui.ActivityDetail.OnGetChoiceListener;
import com.schytd.discount.ui.View.MyListView;
import com.schytd.discount.ui.progress.ProgressLayout;
import com.schytd.discount_android.R;

public class FragemntDetailListModel extends Fragment implements
		OnGetChoiceListener, SwipeRefreshLayout.OnRefreshListener {
	private static int REQUEST_SELLER_DETAIL = 115;
	public static int REQUEST_TO_ACTMAP = 401;
	static ArrayList<SellerInfoItem> mData;
	private MyListView mListView;
	private LayoutInflater mInflater;
	private SellerBussiness mSellerBussiness;
	// 初始页面
	private int mCurrentPageNum = 1;
	// 总页数
	private int mTotalPageNum = 0;
	// 加载页面
	private LinearLayout mLinearLayout_loading;
	// 定位点经纬度
	private double mLat = 0, mLng = 0;
	// 范围
	private String[] mDistanceArray = { "1000", "2000", "5000", "0" };
	// 异步任务
	private GetDataTask mTask;
	// 没有数据时
	private LinearLayout mLinearLayout_noData;
	// swiplayout
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ProgressLayout mProgress;
	// 断网时显示
	private LinearLayout mLinearLaout_noNet;
	// 类别 condition_one 行业分类 condition_two 大分类
	public String condition_one = "0", condition_two = "0";
	private String district = "0";
	// 当前位置
	private LinearLayout mLinearLayout_reLocation;
	private TextView mTextView_now_add;
	private SharedPreferences sp;
	private String nowAdd = "";// 当前位置
	// 选择折扣排序
	private boolean isDiscount = false;
	// 页数总数
	private SharedPreferences sp_seller;
	// 图片路径
	private String[] mImgArray;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private DisplayImageOptions options;
	// *************
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private static final String STATE_PAUSE_ON_SCROLL = "STATE_PAUSE_ON_SCROLL";
	private static final String STATE_PAUSE_ON_FLING = "STATE_PAUSE_ON_FLING";
	private boolean pauseOnScroll = true;
	private boolean pauseOnFling = true;
	private Typeface face;// 字体

	// 构造方法
	public FragemntDetailListModel(String condition, double lat, double lng,
			String mdistrict, String nowAdd) {
		if (!StrTools.isNull(condition)) {
			if (condition.length() > 4) {
				condition_one = condition;
			} else {
				condition_two = condition;
			}
		}
		this.district = mdistrict;
		this.mLat = lat;
		this.mLng = lng;
		this.nowAdd = nowAdd;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.fragemnt_classfiy_list_model_layout, container, false);
		init(view);
		options = setImg();
		return view;
	}

	/** 图片加载监听事件 **/
	public static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500); // 设置image隐藏动画500ms
					displayedImages.add(imageUri); // 将图片uri添加到集合中
				}
			}
		}
	}

	// 图片设置
	public DisplayImageOptions setImg() {
		// *************
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.list_default)
				.showImageOnFail(R.drawable.list_default).cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置为RGB565比起默认的ARGB_8888要节省大量的内存
				.delayBeforeLoading(50)// 载入图片前稍做延时可以提高整体滑动的流畅度
				.showImageForEmptyUri(R.drawable.list_default).build();
		// *************
		return options;
	}

	// 获得图片的连接地址
	public String[] getPath(ArrayList<SellerInfoItem> mdata) {
		String[] imgUri = new String[mdata.size()];
		if (mdata != null && mdata.size() > 0) {
			for (int i = 0; i < mdata.size(); i++) {
				SellerInfoItem item = mdata.get(i);
				imgUri[i] = item.getLogoPic();
			}
		}
		return imgUri;

	}

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();

	// 定位
	private void getPosition() {
		mTextView_now_add.setText("定位中...");
		// 定位初始化
		mLocClient = new LocationClient(getActivity());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
		int span = 1000;
		option.setScanSpan(span);// 设置发起定位请求的间隔时间为1000ms
		option.setIsNeedAddress(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// 此处设置开发者获取到的方向信息，顺时针0-360
			// ll = new LatLng(latitude, longitude);
			if (location == null) {
				mTextView_now_add.setText("定位失败");
				Toast.makeText(getActivity(), "定位失败！", Toast.LENGTH_SHORT)
						.show();
			} else {
				mLat = location.getLatitude();
				mLng = location.getLongitude();
				add = location.getCity();
				ConstantData.district = sp.getString(add, "510100000000");
				if (!StrTools.isNull(add)) {
					add = add.substring(0, add.indexOf("市"));
				} else {
					mTextView_now_add.setText("定位失败");
					Toast.makeText(getActivity(), "定位失败！", Toast.LENGTH_SHORT)
							.show();
					add = "成都";
				}
				ConstantData.position = add;
			}
			mTask = new GetDataTask();
			mTask.execute(mLat + "", mLng + "", distance, mSearchTxt, 10 + "",
					mCurrentPageNum + "", condition_one, condition_two,
					district);
			mTextView_now_add.setText(location.getAddrStr());
			mLocClient.stop();
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	private void init(View v) {

		face = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/discount_font.TTF");
		sp_seller = getActivity().getSharedPreferences("seller_count",
				Context.MODE_PRIVATE);
		mTotalPageNum = sp_seller.getInt("seller_total_count", 0);
		mTextView_now_add = (TextView) v.findViewById(R.id.tv_now_add);
		// 设置位置
		if (StrTools.isNull(nowAdd)) {
			getPosition();
		} else {
			mTextView_now_add.setText(nowAdd);
		}
		sp = getActivity().getSharedPreferences("city_code",
				Context.MODE_PRIVATE);
		mLinearLayout_reLocation = (LinearLayout) v
				.findViewById(R.id.re_location);
		mSellerBussiness = new SellerBussinessImp(getActivity());
		mInflater = getActivity().getLayoutInflater();
		mListView = (MyListView) v
				.findViewById(R.id.fragment_classfiy_listview);
		mListView.setOnScrollListener(new PauseOnScrollListener(imageLoader,
				pauseOnScroll, pauseOnFling));//图片
		
		mListView.setDividerHeight(0);
		mSwipeRefreshLayout = (SwipeRefreshLayout) v
				.findViewById(R.id.list_swipe_container);

		mLinearLayout_loading = (LinearLayout) v
				.findViewById(R.id.loadint_layout_list);
		mLinearLayout_noData = (LinearLayout) v.findViewById(R.id.list_no_data);
		mLinearLaout_noNet = (LinearLayout) v.findViewById(R.id.list_no_net);
		mLinearLaout_noNet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 如果此时网络可用
				if (isNetworkConnected()) {
					mLinearLaout_noNet.setVisibility(View.GONE);
					mTask = new GetDataTask();
					mTask.execute(mLat + "", mLng + "", distance, mSearchTxt,
							10 + "", mCurrentPageNum + "", condition_one,
							condition_two, district);
				} else {
					return;
				}
			}
		});
		mData = new ArrayList<SellerInfoItem>();
		// getPosition();
		mListView.setAdapter(mAdapter);
		// popupwindow
		mInflater = getActivity().getLayoutInflater();
		mListView.setOnItemClickListener(mItemClickListener);
		mSwipeRefreshLayout.setEnabled(false);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mProgress = (ProgressLayout) v.findViewById(R.id.progress);
		mProgress.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mListView.setOnScrollListener(mScrollListener);
		mTask = new GetDataTask();
		if (StrTools.isNull(distance)) {
			distance = 1000 + "";
		}
		if (isNetworkConnected()) {
			mTask.execute(mLat + "", mLng + "", distance, mSearchTxt, 10 + "",
					mCurrentPageNum + "", condition_one, condition_two,
					district);
		} else {
			mLinearLaout_noNet.setVisibility(View.VISIBLE);
			Toast.makeText(getActivity(), "网络不给力", Toast.LENGTH_SHORT).show();
		}
		// 定位
		mLinearLayout_reLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getPosition();
			}
		});
	}

	private String add = null;

	public String getAdd() {
		return add;
	}

	// 搜索条件
	protected String mSearchTxt = "";

	// 加载数据
	public void doSearch(String txt) {
		mSearchTxt = txt;
		mTask = new GetDataTask();
		mData.clear();
		// 判断网络
		if (isNetworkConnected()) {
			mTask.execute(mLat + "", mLng + "", distance, mSearchTxt, 10 + "",
					mCurrentPageNum + "", condition_one, condition_two,
					district);
		} else {
			if (mLinearLayout_noData.getVisibility() != View.VISIBLE) {
				mLinearLaout_noNet.setVisibility(View.VISIBLE);
				Toast.makeText(getActivity(), "网络不给力", Toast.LENGTH_SHORT)
						.show();
			}
		}
		if (mData.size() > 0) {
			mAdapter.notifyDataSetChanged();
		}
	}

	private SellerInfo sellerInfo = null;

	// 加载商家信息
	private class GetDataTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPostExecute(Boolean result) {
			mAdapter.notifyDataSetChanged();
			mLinearLayout_loading.setVisibility(View.GONE);
			mProgress.setRefreshing(false);
			mSwipeRefreshLayout.setEnabled(true);
			mSwipeRefreshLayout.setRefreshing(false);
			if (mData.size() > 0) {
				mLinearLayout_noData.setVisibility(View.GONE);
			} else {
				mLinearLayout_noData.setVisibility(View.VISIBLE);
				if (mLinearLayout_noData.getVisibility() == View.VISIBLE) {
					mLinearLaout_noNet.setVisibility(View.GONE);
				}
			}
		}

		@Override
		protected void onPreExecute() {
			mLinearLayout_loading.setVisibility(View.VISIBLE);
			mProgress.setRefreshing(true);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				if (StrTools.isNull(params[0]) || StrTools.isNull(params[1])
						|| StrTools.isNull(params[2])
						|| StrTools.isNull(params[4])
						|| StrTools.isNull(params[5])) {
					// 为空的操作。。。。。。
					Toast.makeText(getActivity(), "存在数据为空", Toast.LENGTH_SHORT)
							.show();
				} else {
					sellerInfo = mSellerBussiness.getSellerInfo(params[0],
							params[1], params[2], params[3], params[4],
							params[5], params[6], params[7], params[8]);
				}
				mData.clear();
				if (sellerInfo != null) {
					if (sellerInfo.getResultList().size() > 0) {
						mTotalPageNum = sellerInfo.getTotalPage();
						mData = sellerInfo.getResultList();
						mImgArray = getPath(mData);// 图片地址
						if (isDiscount) {
							SortTools.discountSort(mLat, mLng, mData);// 折扣排序
						} else {
							mData = SortTools.distanceSort(mLat, mLng, mData);// 距离排序
						}
						getDistance();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	// 得到商家距离信息
	private ArrayList<String> mDataDistance;

	private void getDistance() {
		mDataDistance = new ArrayList<String>();
		if (mData.size() > 0) {
			for (SellerInfoItem sellerInfoItem : mData) {
				LatLng l1 = new LatLng(mLat, mLng);
				LatLng l2 = new LatLng(Double.parseDouble(sellerInfoItem
						.getLat()), Double.parseDouble(sellerInfoItem.getLng()));
				String distance = DistanceUtil.getDistance(l1, l2) + "";
				mDataDistance
						.add(distance.subSequence(0, distance.indexOf("."))
								+ "");
			}
		}
	}

	private static class ViewHolder {
		public TextView tv_Name;
		public TextView tv_Discount;
		public TextView tv_Distance;
		public TextView tv_Detail;
		public ImageView iv_icon;
	}

	private ViewHolder mHolder;
	@SuppressLint("InflateParams")
	private BaseAdapter mAdapter = new BaseAdapter() {
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.fragment_classfiy_listview_item, null);
				mHolder = new ViewHolder();
				mHolder.tv_Name = (TextView) convertView
						.findViewById(R.id.fragment_classfiy_shopname);
				mHolder.tv_Discount = (TextView) convertView
						.findViewById(R.id.fragment_classfiy_discount);
				mHolder.tv_Detail = (TextView) convertView
						.findViewById(R.id.fragment_classfiy_special);
				mHolder.tv_Distance = (TextView) convertView
						.findViewById(R.id.fragment_classfiy_distance);
				mHolder.iv_icon = (ImageView) convertView
						.findViewById(R.id.fragment_classfiy_listview_img);
				mHolder.tv_Discount.setTypeface(face);
				mHolder.tv_Name.setTextSize(13f);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			if (mData.size() > 0 && mData.size() >= position + 1) {
				// 店招
				SellerInfoItem infoItem = (SellerInfoItem) getItem(position);
				// 图片
				if (mImgArray != null) {
					imageLoader.displayImage(mImgArray[position],
							mHolder.iv_icon, options, animateFirstListener);
				}
				mHolder.tv_Name.setText((infoItem.getBusinessName()));
				if (infoItem.getBusinessDesc().length() > 10) {
					mHolder.tv_Detail.setText((infoItem.getBusinessDesc())
							.substring(0, 8) + "...");
				} else {
					mHolder.tv_Detail.setText(infoItem.getBusinessDesc());
				}
				mHolder.tv_Discount.setText(infoItem.getDiscount() + "折");
				if (mDataDistance.size() > 0
						&& mDataDistance.size() >= position + 1) {
					mHolder.tv_Distance.setText("距离" + ":"
							+ mDataDistance.get(position) + "m");
				}
			}
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 传递数据 id 坐标 折扣
					String id = null;
					id = mData.get(position).getId();
					String discount = mData.get(position).getDiscount();
					String lat = mData.get(position).getLat();
					String lng = mData.get(position).getLng();
					Intent i = new Intent(getActivity(),
							ActivitySellerDetail.class);
					i.putExtra("is_flag", true);
					i.putExtra("id", id);
					i.putExtra("discount", discount);
					// 商家坐标
					i.putExtra("lat", lat);
					i.putExtra("lng", lng);
					// 定位点坐标
					i.putExtra("nowlat", mLat);
					i.putExtra("nowlng", mLng);
					// 商家名
					i.putExtra("title", mData.get(position).getBusinessName());
					getActivity().startActivityForResult(i,
							REQUEST_SELLER_DETAIL);
				}
			});
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public int getCount() {
			return mData.size();
		}
	};
	// item点击事件
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long duration) {
		}
	};
	// 查询范围
	private String distance = null;

	@Override
	public void onGetChoice(int type, int id, String mCon, Boolean has) {
		// 排序
		switch (type) {
		case 1:
			if (id == 0 && !has) {
				// 全部分类
				mSearchTxt = "";
				condition_one = "0";
				condition_two = "0";
				mData.clear();
				mTask = new GetDataTask();
				if (isNetworkConnected()) {
					mTask.execute(mLat + "", mLng + "", distance, mSearchTxt,
							10 + "", mCurrentPageNum + "", condition_one,
							condition_two, district);
				} else {
					if (mLinearLayout_noData.getVisibility() != View.VISIBLE) {
						mLinearLaout_noNet.setVisibility(View.VISIBLE);
						Toast.makeText(getActivity(), "网络不给力",
								Toast.LENGTH_SHORT).show();
					}
				}
			} else {
				mTask = new GetDataTask();
				if (isNetworkConnected()) {
					if (StrTools.isNull(mCon)) {
						return;
					}
					if (mCon.length() > 4) {
						condition_one = mCon;
						condition_two = "0";
					} else {
						condition_two = mCon;
						condition_one = "0";
					}
					mTask.execute(mLat + "", mLng + "", distance, mSearchTxt,
							10 + "", mCurrentPageNum + "", condition_one,
							condition_two, district);
				} else {
					if (mLinearLayout_noData.getVisibility() != View.VISIBLE) {
						mLinearLaout_noNet.setVisibility(View.VISIBLE);
						Toast.makeText(getActivity(), "网络不给力",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
			// 刷新适配器
			if (mData.size() > 0) {
				mAdapter.notifyDataSetChanged();
			}
			break;
		case 2:
			distance = null;
			if (id == 0) {
				// 1000米以内
				distance = mDistanceArray[0];
				ConstantData.ISALL = false;
			} else if (id == 1) {
				// 2000米以内
				distance = mDistanceArray[1];
				ConstantData.ISALL = false;
			} else if (id == 2) {
				// 5000米以内
				distance = mDistanceArray[2];
				ConstantData.ISALL = false;
			} else if (id == 3) {
				// 全市
				SharedPreferences sp = getActivity().getSharedPreferences(
						"city_code", Context.MODE_PRIVATE);
				distance = mDistanceArray[3];
				district = ConstantData.district;
				ConstantData.ISALL = true;
			}
			// 重新加载相应范围内的数据
			if (mTask != null) {
				mTask.cancel(true);
			}
			mTask = new GetDataTask();
			if (distance != null) {
				mData.clear();
				if (isNetworkConnected()) {
					mTask.execute(mLat + "", mLng + "", distance, mSearchTxt,
							10 + "", mCurrentPageNum + "", condition_one,
							condition_two, district);
				} else {
					if (mLinearLayout_noData.getVisibility() != View.VISIBLE) {
						mLinearLaout_noNet.setVisibility(View.VISIBLE);
						Toast.makeText(getActivity(), "网络不给力",
								Toast.LENGTH_SHORT).show();
					}
				}

			}
			break;
		case 3:
			// 折扣排序
			if (id == 0) {
				isDiscount = true;
				SortTools.discountSort(mLat, mLng, mData);
				getDistance();
				if (mData.size() > 0) {
					mAdapter.notifyDataSetChanged();
				}
				;
			} else if (id == 1) {
				// 距离排序
				// 对其进行距离排序
				isDiscount = false;
				mData = SortTools.distanceSort(mLat, mLng, mData);
				getDistance();
				if (mData.size() > 0) {
					mAdapter.notifyDataSetChanged();
				}
			}
			break;
		}
	}

	// listview滚动数据
	private OnScrollListener mScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

			switch (scrollState) {
			// 滚动时
			case OnScrollListener.SCROLL_STATE_FLING:
				mSwipeRefreshLayout.setEnabled(false);
				break;
			// 当不滚动时
			case OnScrollListener.SCROLL_STATE_IDLE:
				// 判断滚动到顶部
				if (mListView.getFirstVisiblePosition() == 0) {
					mSwipeRefreshLayout.setEnabled(true);
				}
				// 判断滚动到底部
				if (mListView.getLastVisiblePosition() == (mListView.getCount() - 1)) {
					// 加载数据
					if (mCurrentPageNum + 1 <= mTotalPageNum) {
						mCurrentPageNum++;
						if (isNetworkConnected()) {
							new GetDataTask().execute(mLat + "", mLng + "",
									distance, mSearchTxt, 10 + "",
									mCurrentPageNum + "", condition_one,
									condition_two, district);
						} else {
							if (mLinearLayout_noData.getVisibility() != View.VISIBLE) {
								mLinearLaout_noNet.setVisibility(View.VISIBLE);
								Toast.makeText(getActivity(), "网络不给力",
										Toast.LENGTH_SHORT).show();
							}
						}

					} else {
						// 没有数据 不加载
						mSwipeRefreshLayout.setEnabled(false);
						mSwipeRefreshLayout.setRefreshing(false);
					}
				}
				break;
			}

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}
	};

	@Override
	public void onRefresh() {
		// 下拉刷新
		if (mCurrentPageNum + 1 <= mTotalPageNum) {
			mCurrentPageNum++;
			if (isNetworkConnected()) {
				new GetDataTask().execute(mLat + "", mLng + "", distance,
						mSearchTxt, 10 + "", mCurrentPageNum + "",
						condition_one, condition_two, district);
			} else {
				if (mLinearLayout_noData.getVisibility() != View.VISIBLE) {
					mLinearLaout_noNet.setVisibility(View.VISIBLE);
					Toast.makeText(getActivity(), "网络不给力", Toast.LENGTH_SHORT)
							.show();
				}
			}
		} else {
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}

	public boolean isNetworkConnected() {
		// 判断网络是否连接
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		return mNetworkInfo != null && mNetworkInfo.isAvailable();
	}
}
