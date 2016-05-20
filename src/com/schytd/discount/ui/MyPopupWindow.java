package com.schytd.discount.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.schytd.discount.tools.StrTools;
import com.schytd.discount_android.R;

public class MyPopupWindow {
	private LayoutInflater mInflater;
	private List<String> mData = new ArrayList<String>();
	private int mScreenHeight;
	private Activity context;
	private OnUserSelectedListener mOnUserSelectedListener;

	// 传递选中参数
	public interface OnUserSelectedListener {
		public void onSelected(String item, int position);
	}

	public MyPopupWindow(LayoutInflater Inflater, int ScreenHeight,
			Activity context,OnUserSelectedListener listener) {
		this.mInflater = Inflater;
		this.mScreenHeight = ScreenHeight;
		this.context = context;
		// 获得数据
		mData = StrTools.getJsonData(context);
		mOnUserSelectedListener=listener;
		// mData.add("1");
		// mData.add("1");
		// mData.add("1");
		// mData.add("1");
		// mData.add("1");
		// mData.add("1");
		// mData.add("1");
		// mData.add("1");
	};

	private PopupWindow mPopupWindow;

	public void getInstance() {
		// 弹出popupWindow
		View v = mInflater.inflate(R.layout.popupwindow_layout, null);
		mPopupWindow = new PopupWindow(v, LayoutParams.MATCH_PARENT,
				5 * mScreenHeight / 12);
		// 初始化 popupwindow中的组件
		GridView gridView = (GridView) v.findViewById(R.id.location_city_area);
		gridView.setAdapter(mAdapter);
		gridView.setOnItemClickListener(mItemClickListener);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
		// 设置动画效果
//		mPopupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
		mPopupWindow.showAtLocation(v, Gravity.TOP, 0, v.getHeight());
		final WindowManager.LayoutParams params = context.getWindow()
				.getAttributes();
		
		v.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					mPopupWindow.dismiss();
					params.alpha = 1f;
					context.getWindow().setAttributes(params);
					return true;
				}
				return false;
			}
		});
		mPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				params.alpha = 1f;
				context.getWindow().setAttributes(params);
			}
		});
		if (mPopupWindow.isShowing()) {
			params.alpha = 0.7f;
			context.getWindow().setAttributes(params);
		}

	}

	private OnItemClickListener mItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long arg3) {
			mPopupWindow.dismiss();
			Toast.makeText(context, position + "", Toast.LENGTH_LONG).show();
			mOnUserSelectedListener.onSelected(mData.get(position), position);
		}
	};

	private BaseAdapter mAdapter = new BaseAdapter() {
		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.city_area_gridview_item, null);
			}
			TextView name = (TextView) convertView.findViewById(R.id.tv_region);
			name.setText(mData.get(position));
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

}
