package com.schytd.discount.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.schytd.discount.bussiness.PushBussiness;
import com.schytd.discount.bussiness.impl.PushBussinessImp;
import com.schytd.discount.enties.PushInfo;
import com.schytd.discount.tools.StrTools;
import com.schytd.discount_android.R;

public class ActivityMessageDetail extends FragmentActivity{
	private PushBussiness mPushBussiness;
	private PushInfo mPushInfo;
	private static MessageTask mTask;
	// viewpager
	private ViewPager mViewPager;
	// 装view
	private ViewGroup mViewGroup_point;
	// 商家图片数据
	private List<String> mdata;
	private ImageView[] views;
	private TextView mTextView_title, mTextView_name, mTextView_date,
			mTextView_abstractinfo, mTextView_detail;
//	loading
	private LinearLayout mLinearLayout_loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_detail);
		init();
	}

	private String id;
	private String type_=null;
	private String type="normal";

	private void init() {
		mLinearLayout_loading=(LinearLayout) this.findViewById(R.id.message_loading);
		mTextView_title = (TextView) this.findViewById(R.id.message_title);
		mTextView_name = (TextView) this.findViewById(R.id.message_tit);
		mTextView_date = (TextView) this.findViewById(R.id.message_time);
		mTextView_abstractinfo = (TextView) this
				.findViewById(R.id.message_abstract);
		mTextView_detail = (TextView) this.findViewById(R.id.message_detail);
		mViewGroup_point = (ViewGroup) this.findViewById(R.id.view_point);
		mViewPager = (ViewPager) this.findViewById(R.id.message_detail_img);
		mPushBussiness = new PushBussinessImp(this);
		id = getIntent().getStringExtra("id");
		mTask = new MessageTask();
		type_=getIntent().getStringExtra("type");
		if (StrTools.isNull(type_)) {
			type="normal";
		}else{
			type=type_;
		}
		mTask.execute(id, type);
		mdata = new ArrayList<String>();
		mViewPager.setAdapter(mFragmentPagerAdapter);
		addPointView();
		mViewPager.addOnPageChangeListener(new MyPageChangeListener());
	}

	private void addPointView() {
		views = new ImageView[mdata.size()];
		mViewGroup_point.removeAllViews();
		for (int i = 0; i < mdata.size(); i++) {
			ImageView v = new ImageView(this);
			views[i] = v;
			if (i == 0) {
				views[i].setBackgroundResource(R.drawable.seller_detail_view_shape2);
			} else {
				views[i].setBackgroundResource(R.drawable.seller_detail_view_shape1);
			}
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.leftMargin = 15;
			params.height = 15;
			params.width = 15;
			v.setScaleType(ScaleType.CENTER_CROP);
			v.setLayoutParams(params);
			mViewGroup_point.addView(views[i]);
		}

	}

	// view原点切换
	private void changeView(int position) {
		for (int i = 0; i < views.length; i++) {
			if (i == position) {
				views[i].setBackgroundResource(R.drawable.seller_detail_view_shape2);
			} else {
				views[i].setBackgroundResource(R.drawable.seller_detail_view_shape1);
			}
		}
	}

	// viewpager适配器
	// 页面改变监听器  
	private class MyPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int position) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			changeView(position);
		}
	}

	// viewpager适配器
	private FragmentPagerAdapter mFragmentPagerAdapter = new FragmentPagerAdapter(
			getSupportFragmentManager()) {

		@Override
		public int getCount() {
			return mdata.size();
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			switch (position) {
			case 0:
				fragment = new FragmentSellerImg(mdata.get(position));
				break;
			case 1:
				fragment = new FragmentSellerImg(mdata.get(position));
				break;
			case 2:
				fragment = new FragmentSellerImg(mdata.get(position));
				break;
			case 3:
				fragment = new FragmentSellerImg(mdata.get(position));
				break;
			case 4:
				fragment = new FragmentSellerImg(mdata.get(position));
				break;
			}
			return fragment;
		}
	};
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.message_back:
			finish();
			break;
		}
	}

	// task 获得消息列表
	private class MessageTask extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			try {
				mPushInfo = mPushBussiness.getPushDetail(params[0], params[1]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mLinearLayout_loading.setVisibility(View.GONE);
			// .....界面显示
			// 图片路径
			if (mPushInfo != null) {
				mTextView_title.setText(mPushInfo.getTitle());
				mTextView_name.setText(mPushInfo.getTitle());
//				时间转换
				long time=Long.parseLong(mPushInfo.getCreateTime());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				mTextView_date.setText("日期:"+sdf.format(new Date(time)));
				mTextView_abstractinfo.setText(mPushInfo.getAbstractInfo());
				mTextView_detail.setText(mPushInfo.getContent());
				String path = mPushInfo.getCarouselImgs();
				if (!StrTools.isNull(path)) {
					String[] paths = path.split(",");
					for (String string : paths) {
						mdata.add(string);
					}
				}
			} else {
				// 为空。。。。。
				Toast.makeText(ActivityMessageDetail.this, "网络请求出错！",
						Toast.LENGTH_SHORT).show();
			}
			mFragmentPagerAdapter.notifyDataSetChanged();
			addPointView();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLinearLayout_loading.setVisibility(View.VISIBLE);
		}
		

	}

}
