package com.schytd.discount.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.schytd.discount.enties.SellerInfoItem;
import com.schytd.discount.tools.ImageUtils;
import com.schytd.discount.tools.ImageUtils.OnLoadImageListener;
import com.schytd.discount_android.R;

public class FragmentMap extends Fragment {
	private SellerInfoItem data;
	private LinearLayout mBtnGo;
	private LinearLayout mBtnDetail;
	private double distance;
	// 当前经纬度
	private LatLng mNowPoint;
	// 当前页号的经纬度
	private LatLng mPoint;
	// 序号
	private int num;
	// 关闭activitymap viewpager
	private ImageView mImageView_close;

	// 接口
	public interface OnCloseViewPagerListener {
		public void onCloseViewPager();
	}

	private OnCloseViewPagerListener mOnCloseViewPagerListener;

	// 传递商家简单信息 显示在fragment上
	public FragmentMap(SellerInfoItem data, double distance, int num,
			LatLng NowPoint, OnCloseViewPagerListener onCloseViewPagerListener) {
		this.data = data;
		this.distance = distance;
		this.num = num;
		this.mNowPoint = NowPoint;
		this.mPoint = new LatLng(Double.parseDouble(data.getLat()),
				Double.parseDouble(data.getLng()));
		this.mOnCloseViewPagerListener = onCloseViewPagerListener;
	}

	public FragmentMap() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(
				R.layout.fragment_activity_map_viewpager_layout, container,
				false);
		init(v);
		return v;
	}

	private ImageView mImageView;
	private TextView mTextView_title, mTextView_BussinessDesc,
			mTextView_discount, mTextView_distance;

	private void init(View v) {
		mImageView_close = (ImageView) v.findViewById(R.id.map_viewpager_close);
		mImageView_close.setOnClickListener(listener);
		mTextView_title = (TextView) v
				.findViewById(R.id.activity_map_viewpager_title);
		mTextView_BussinessDesc = (TextView) v
				.findViewById(R.id.activity_map_viewpager_bussinessdesc);
		mTextView_discount = (TextView) v
				.findViewById(R.id.activity_map_viewpager_discount);
		mTextView_distance = (TextView) v
				.findViewById(R.id.activity_map_viewpager_distance);
		mImageView = (ImageView) v
				.findViewById(R.id.activity_map_viewpager_icon);
		mBtnGo = (LinearLayout) v.findViewById(R.id.btn_go);
		mBtnDetail = (LinearLayout) v.findViewById(R.id.btn_view_detail);
		mBtnGo.setOnClickListener(listener);
		mBtnDetail.setOnClickListener(listener);
		if (data != null) {
			mTextView_title.setText(num + "." + data.getBusinessName());
			if (data.getBusinessDesc().length() > 12) {
				mTextView_BussinessDesc.setText(data.getBusinessDesc()
						.substring(0, 8) + "...");
			} else {
				mTextView_BussinessDesc.setText(data.getBusinessDesc());
			}
			mTextView_discount.setText(data.getDiscount() + "折");
			String dis = distance + "";
			mTextView_distance.setText("距离:"
					+ dis.substring(0, dis.indexOf(".")) + "m");
			ImageUtils imageUtils = new ImageUtils(getActivity(),
					data.getLogoPic());
			imageUtils.onLoadImage(new OnLoadImageListener() {
				@Override
				public void OnLoadImage(Bitmap bitmap, String bitmapPath) {
					if (bitmap != null) {
						mImageView.setImageBitmap(bitmap);
					}
				}
			});
		}
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_go:
				Intent i = new Intent(getActivity(), ActivityRoutePlan.class);
				i.putExtra("sLat", mNowPoint.latitude);
				i.putExtra("sLng", mNowPoint.longitude);
				i.putExtra("eLat", mPoint.latitude);
				i.putExtra("eLng", mPoint.longitude);
				startActivity(i);
				break;
			// 商家详情
			case R.id.btn_view_detail:
				Intent intent_detail = new Intent(getActivity(),
						ActivitySellerDetail.class);
				intent_detail.putExtra("id", data.getId());
				intent_detail.putExtra("discount", data.getDiscount());
				// 商家坐标
				intent_detail.putExtra("lat", data.getLat());
				intent_detail.putExtra("lng", data.getLng());
				// 定位点坐标
				intent_detail.putExtra("nowlat", mNowPoint.latitude + "");
				intent_detail.putExtra("nowlng", mNowPoint.longitude + "");
				// 商家名
				intent_detail.putExtra("title", data.getBusinessName());
				startActivity(intent_detail);
				break;
			case R.id.map_viewpager_close:
				mOnCloseViewPagerListener.onCloseViewPager();
				break;
			default:
				break;
			}
		}
	};
}
