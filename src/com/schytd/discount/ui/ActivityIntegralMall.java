package com.schytd.discount.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.schytd.discount.enties.CommodityHistory;
import com.schytd.discount.enties.CommodityLatest;
import com.schytd.discount.ui.View.ButtonIcon;
import com.schytd.discount_android.R;

public class ActivityIntegralMall extends Activity {
	private GridView mGridViewLatest, mGridViewHistory;
	private ButtonIcon mBtn_back;

	List<CommodityLatest> mData_latest;
	List<CommodityHistory> mData_history;
	private CommodityLatest mCommoditylatest;
	private CommodityHistory mCommodityhistory;

	public void init() {
		mGridViewLatest = (GridView) findViewById(R.id.integral_mall_gridview_latest);
		mGridViewHistory = (GridView) findViewById(R.id.integral_mall_gridview_history);
		// ScrollView起始位置不是最顶部的解决办法(ScrollView内部嵌套了gridview，只需要设置gridview获取焦点为false即可。)
		mGridViewLatest.setFocusable(false);
		mGridViewHistory.setFocusable(false);
		mBtn_back = (ButtonIcon) findViewById(R.id.integral_mall_back);
		mBtn_back.setOnClickListener(listener);
		
		mCommoditylatest = new CommodityLatest("惠消费01", "100", "");
		mData_latest = new ArrayList<CommodityLatest>();
		mData_latest.add(mCommoditylatest);
		mData_latest.add(mCommoditylatest);
		mData_latest.add(mCommoditylatest);
		mData_latest.add(mCommoditylatest);
		mData_latest.add(mCommoditylatest);
		mData_latest.add(mCommoditylatest);
		mData_latest.add(mCommoditylatest);
		mData_latest.add(mCommoditylatest);
		mGridViewLatest.setAdapter(adapter_latest);

		mCommodityhistory = new CommodityHistory("惠消费02", "200", "");
		mData_history = new ArrayList<CommodityHistory>();
		mData_history.add(mCommodityhistory);
		mData_history.add(mCommodityhistory);
		mData_history.add(mCommodityhistory);
		mData_history.add(mCommodityhistory);
		mData_history.add(mCommodityhistory);
		mData_history.add(mCommodityhistory);
		mData_history.add(mCommodityhistory);
		mData_history.add(mCommodityhistory);
		mGridViewHistory.setAdapter(adapter_history);
	}

	public OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.integral_mall_back:
				finish();
				break;

			default:
				break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_integral_mall);
		init();
	}

	private static class ViewHolder {
		public TextView tv_Name;
		public TextView tv_Price;
		public ImageView iv_Picture;
	}

	private ViewHolder mHolder;
	private BaseAdapter adapter_latest = new BaseAdapter() {

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				mHolder = new ViewHolder();
				LayoutInflater layoutInflater = ActivityIntegralMall.this
						.getLayoutInflater();
				convertView = layoutInflater.inflate(
						R.layout.activity_integral_mall_item, null);

				mHolder.tv_Name = (TextView) convertView
						.findViewById(R.id.commodity_name);
				mHolder.tv_Price = (TextView) convertView
						.findViewById(R.id.commodity_price);
				mHolder.iv_Picture = (ImageView) convertView
						.findViewById(R.id.commodity_picture);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			CommodityLatest commodity_latest = mData_latest.get(position);
			if (commodity_latest != null) {
				mHolder.tv_Name.setText(commodity_latest.getName());
				mHolder.tv_Price.setText("¥" + commodity_latest.getPrice());
				mHolder.iv_Picture
						.setImageResource(R.drawable.gridview_default);
			}

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(ActivityIntegralMall.this,
							ActivityCommodityDetail.class));
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
			return mData_latest.get(position);
		}

		@Override
		public int getCount() {
			return mData_latest.size();
		}
	};

	private BaseAdapter adapter_history = new BaseAdapter() {

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				mHolder = new ViewHolder();
				LayoutInflater layoutInflater = ActivityIntegralMall.this
						.getLayoutInflater();
				convertView = layoutInflater.inflate(
						R.layout.activity_integral_mall_item, null);

				mHolder.tv_Name = (TextView) convertView
						.findViewById(R.id.commodity_name);
				mHolder.tv_Price = (TextView) convertView
						.findViewById(R.id.commodity_price);
				mHolder.iv_Picture = (ImageView) convertView
						.findViewById(R.id.commodity_picture);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			CommodityHistory commodity_history = mData_history.get(position);
			if (commodity_history != null) {
				mHolder.tv_Name.setText(commodity_history.getName());
				mHolder.tv_Price.setText("¥" + commodity_history.getPrice());
				mHolder.iv_Picture
						.setImageResource(R.drawable.gridview_default);
			}

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(ActivityIntegralMall.this,
							ActivityCommodityDetail.class));
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
			return mData_history.get(position);
		}

		@Override
		public int getCount() {
			return mData_history.size();
		}
	};

	public OnItemClickListener item_listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long duration) {
			Log.d("++++++", "OnItemClickListener called...");
			Log.d("++++++", (String) parent.getItemAtPosition(position));
		}
	};

}
