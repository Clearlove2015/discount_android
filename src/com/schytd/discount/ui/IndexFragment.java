package com.schytd.discount.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.schytd.discount.tools.StrTools;
import com.schytd.discount_android.R;

public class IndexFragment extends Fragment {
	private LinearLayout mLinearLayout_location;
	// private MyPopupWindow myPopupWindow;
	private static TextView mTextView_position;
	private int i = 1;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_index_layout, container,
				false);
		init(v);
		return v;
	}

	private int mScreenHeight;

	private void init(View v) {
		
//		mLinearLayout_location = (LinearLayout) v
//				.findViewById(R.id.index_location);
		
		
//		mTextView_position = (TextView) v.findViewById(R.id.tv_choice_point);
		
		
		mInflater = getActivity().getLayoutInflater();
		DisplayMetrics metric = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenHeight = metric.heightPixels; // 屏幕高度（像素）
		// myPopupWindow = new MyPopupWindow(mInflater, mScreenHeight,
		// getActivity(), this);
//		mLinearLayout_location.setOnClickListener(mClickListener);
	}

	public static void setText(String add) {
		if (StrTools.isNull(add)) {
			add = "成都-天府广场";
		}
//		mTextView_position.setText(add);
	}

	private LayoutInflater mInflater;
	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// 弹出popupWindow
			// myPopupWindow.getInstance();
			switch (v.getId()) {
//			case R.id.index_location:
//				DetailFragment.reLocation();
//				break;
			default:
				break;
			}
		}
	};
}