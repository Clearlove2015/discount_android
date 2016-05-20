package com.schytd.discount.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.schytd.discount.bussiness.SellerBussiness;
import com.schytd.discount.bussiness.impl.SellerBussinessImp;
import com.schytd.discount.enties.SellerInfo;
import com.schytd.discount.enties.SellerInfoItem;
import com.schytd.discount.tools.SortTools;
import com.schytd.discount.tools.StrTools;
import com.schytd.discount.ui.progress.ProgressLayout;
import com.schytd.discount_android.R;

public class ActivityDiscountInfo extends ImageActivity implements
		SwipeRefreshLayout.OnRefreshListener {
	public static int REQUEST_TO_DISCOUNT_DETAIL = 421;
	private SwipeRefreshLayout mSwipeLayout;
	private ListView mListView;
	private ArrayList<SellerInfoItem> mData;
	private ProgressLayout progress;
	private ProgressBar loading;
	private double mLat, mLng;
	private SellerInfo mSellerInfo = null;
	private SellerBussiness mSellerBussiness;
	// 总页数
	private int mTotalPageNum = 0;
	// 初始页面
	private int mCurrentPageNum = 1;
	private DisplayImageOptions options;
	private String[] imageUriArray;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private Typeface face;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discountinfo_layout);
		init();
	}

	private void init() {
		face = Typeface.createFromAsset(getAssets(), "fonts/discount_font.TTF");
		options = setImg();// 设置图片加载
		loading = (ProgressBar) this.findViewById(R.id.progress_bar);
		mSellerBussiness = new SellerBussinessImp(this);
		mLat = getIntent().getDoubleExtra("lat", 0);
		mLng = getIntent().getDoubleExtra("lng", 0);
		mData = new ArrayList<SellerInfoItem>();
		mListView = (ListView) findViewById(R.id.discount_listview);
		applyScrollListener(mListView);
		progress = (ProgressLayout) findViewById(R.id.progress);
		progress.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		mSwipeLayout.setEnabled(false);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mListView.setAdapter(mAdapter);
		new GetDataTask().execute(mLat + "", mLng + "", 10000 + "", 1000 + "",
				mCurrentPageNum + "");
	}

	private void applyScrollListener(ListView listView) {
		listView.setOnScrollListener(new PauseOnScrollListener(imageLoader,
				true, true));
	}

	// 加载商家信息
	private class GetDataTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPostExecute(Boolean result) {
			setImg();
			loading.setVisibility(View.GONE);
			mAdapter.notifyDataSetChanged();
			progress.setRefreshing(false);
			mSwipeLayout.setEnabled(true);
			mSwipeLayout.setRefreshing(false);
		}

		@Override
		protected void onPreExecute() {
			loading.setVisibility(View.VISIBLE);
			progress.setRefreshing(true);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			if (StrTools.isNull(params[0]) || StrTools.isNull(params[1])
					|| StrTools.isNull(params[2]) || StrTools.isNull(params[4])) {
			} else {
				try {
					mSellerInfo = mSellerBussiness.getDiscountInfo(params[0],
							params[1], params[2], params[3], params[4]);
				} catch (Exception e) {
					e.printStackTrace();
				}
				mData.clear();
				if (mSellerInfo != null) {
					if (mSellerInfo.getResultList().size() > 0) {
						mTotalPageNum = mSellerInfo.getTotalPage();
						mData = mSellerInfo.getResultList();
						imageUriArray = getPath(mData);
						// 对其进行距离排序
						mData = SortTools.distanceSort(mLat, mLng, mData);
						getDistance();
					}
				}
			}
			return null;
		}

	}

	static class ViewHolder {
		TextView mTextView_name;
		TextView mTextView_special;
		TextView mTextView_discount;
		TextView mTextView_distance;
		ImageView iv_icon;
	}

	ViewHolder mHolder;
	public BaseAdapter mAdapter = new BaseAdapter() {
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			LayoutInflater mInflater = getLayoutInflater();
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.fragment_classfiy_listview_item, null);
				mHolder = new ViewHolder();
				mHolder.mTextView_name = (TextView) convertView
						.findViewById(R.id.fragment_classfiy_shopname);
				mHolder.mTextView_discount = (TextView) convertView
						.findViewById(R.id.fragment_classfiy_discount);
				mHolder.mTextView_special = (TextView) convertView
						.findViewById(R.id.fragment_classfiy_special);
				mHolder.mTextView_distance = (TextView) convertView
						.findViewById(R.id.fragment_classfiy_distance);
				mHolder.iv_icon = (ImageView) convertView
						.findViewById(R.id.fragment_classfiy_listview_img);
				mHolder.mTextView_name.setTextSize(13f);
				mHolder.mTextView_discount.setTypeface(face);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			// 填充数据
			// 店招
			SellerInfoItem infoItem = mData.get(position);
			if (imageUriArray != null) {
				imageLoader.displayImage(imageUriArray[position],
						mHolder.iv_icon, options, animateFirstListener);
			}
			mHolder.mTextView_name.setText((infoItem.getBusinessName()));
			if (infoItem.getBusinessDesc().length() > 10) {
				mHolder.mTextView_special.setText((infoItem.getBusinessDesc())
						.substring(0, 8) + "...");
			} else {
				mHolder.mTextView_special.setText(infoItem.getBusinessDesc());
			}
			mHolder.mTextView_discount.setText(infoItem.getDiscount() + "折");
			mHolder.mTextView_distance.setText("距离" + ":"
					+ mDataDistance.get(position) + "m");
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 传递数据 id 坐标 折扣
					String id = null;
					id = mData.get(position).getId();
					String discount = mData.get(position).getDiscount();
					String lat = mData.get(position).getLat();
					String lng = mData.get(position).getLng();
					Intent i = new Intent(ActivityDiscountInfo.this,
							ActivitySellerDetail.class);
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
					startActivityForResult(i, REQUEST_TO_DISCOUNT_DETAIL);
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

	@Override
	public void onRefresh() {
		// 下拉刷新
		if (mCurrentPageNum + 1 <= mTotalPageNum) {
			mCurrentPageNum++;
			if (isNetworkConnected()) {
				new GetDataTask().execute(mLat + "", mLng + "", 10000 + "",
						1000 + "", mCurrentPageNum + "");
			} else {
				// mLinearLaout_noNet.setVisibility(View.VISIBLE);
				// Toast.makeText(getActivity(), "网络不给力", Toast.LENGTH_SHORT)
				// .show();
			}
		} else {
			mSwipeLayout.setRefreshing(false);
		}
	}

	// 点击事件
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.discount_back:
			finish();
			break;
		}
	}

	public boolean isNetworkConnected() {
		// 判断网络是否连接
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		return mNetworkInfo != null && mNetworkInfo.isAvailable();
	}
}
