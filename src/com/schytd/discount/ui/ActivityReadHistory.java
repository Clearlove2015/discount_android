package com.schytd.discount.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.schytd.discount.bussiness.SellerBussiness;
import com.schytd.discount.bussiness.impl.SellerBussinessImp;
import com.schytd.discount.enties.SellerInfoItem;
import com.schytd.discount.tools.StrTools;
import com.schytd.discount.ui.View.MyListView;
import com.schytd.discount_android.R;

public class ActivityReadHistory extends ImageActivity {
	private MyListView mListView;
	private ArrayList<SellerInfoItem> mData;
	private String[] imageUriArray;
	private SellerBussiness mSellerBussiness;
	private ArrayList<String> mTime;
	private ProgressBar mProgressBar;
	private Double mLat, mLng;
	private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_history_layout);
		init();
	}

	private void init() {
		options = setImg();// 设置图片加载
		mData = new ArrayList<SellerInfoItem>();
		mTime = new ArrayList<String>();
		mLat = getIntent().getDoubleExtra("lat", 0);
		mLng = getIntent().getDoubleExtra("lng", 0);
		mListView = (MyListView) this.findViewById(R.id.read_his_listview);
		applyScrollListener(mListView);
		mSellerBussiness = new SellerBussinessImp(this);
		mListView.setAdapter(mAdapter);
		mProgressBar = (ProgressBar) this.findViewById(R.id.read_progress_bar);
		new ReadHisTask().execute();
	}

	private void applyScrollListener(ListView listView) {
		listView.setOnScrollListener(new PauseOnScrollListener(imageLoader,
				pauseOnScroll, pauseOnFling));
	}
	static class ViewHolder {
		TextView mTextView_name;
		TextView mTextView_special;
		TextView mTextView_discount;
		TextView mTextView_distance;
		ImageView iv_icon;
	}

	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
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
				Typeface face = Typeface.createFromAsset(getAssets(),
						"fonts/discount_font.TTF");
				mHolder.mTextView_discount.setTypeface(face);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			// 填充数据
			// 店招
			SellerInfoItem infoItem = (SellerInfoItem) getItem(position);
			if (imageUriArray != null) {
				imageLoader.displayImage(imageUriArray[position],
						mHolder.iv_icon, options, animateFirstListener);
			}
			mHolder.mTextView_name.setTextSize(13f);
			mHolder.mTextView_name.setText((infoItem.getBusinessName()));
			if (infoItem.getBusinessDesc().length() > 10) {
				mHolder.mTextView_special.setText((infoItem.getBusinessDesc())
						.substring(0, 8) + "...");
			} else {
				mHolder.mTextView_special.setText(infoItem.getBusinessDesc());
			}

			mHolder.mTextView_discount.setText(infoItem.getDiscount() + "折");
			// 时间转换
			if (mTime != null && mTime.size() > 0) {
				String timestr = mTime.get(position);
				if (!StrTools.isNull(timestr)) {
					long time = Long.parseLong(timestr);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					mHolder.mTextView_distance.setText("时间："
							+ sdf.format(new Date(time)));
				}
			}
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// // 传递数据 id 坐标 折扣
					String id = null;
					id = mData.get(position).getId();
					String discount = mData.get(position).getDiscount();
					String lat = mData.get(position).getLat();
					String lng = mData.get(position).getLng();
					Intent i = new Intent(ActivityReadHistory.this,
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
					startActivity(i);
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

	// 获得浏览历史
	private class ReadHisTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			setImg();
			mAdapter.notifyDataSetChanged();
			mProgressBar.setVisibility(View.GONE);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				mData = mSellerBussiness.getReadHistory();
				mTime = mSellerBussiness.getReadTime();
				imageUriArray = getPath(mData);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.read_his_back:
			finish();
			break;
		}
	}
}
