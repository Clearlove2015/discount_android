package com.schytd.discount.ui.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.location.m;
import com.schytd.discount_android.R;

public class SelectPicPopupWindow extends PopupWindow {
	private TextView mTextView_TakePhoto, mTextView_GetPhoto, mTextView_Cancle;
	private View mView_bg;
	private View mMenuView;

	public SelectPicPopupWindow(Activity context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.img_popupwindow, null);
		mView_bg = (View) mMenuView.findViewById(R.id.img_bg);
		mView_bg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 销毁弹出框
				dismiss();
			}
		});
		mTextView_TakePhoto = (TextView) mMenuView
				.findViewById(R.id.take_photo);
		mTextView_GetPhoto = (TextView) mMenuView.findViewById(R.id.get_image);
		mTextView_Cancle = (TextView) mMenuView.findViewById(R.id.cancle);
		// 取消按钮
		mTextView_Cancle.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 销毁弹出框
				dismiss();
			}
		});
		// 设置按钮监听
		mTextView_TakePhoto.setOnClickListener(itemsOnClick);
		mTextView_GetPhoto.setOnClickListener(itemsOnClick);
		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		// this.setAnimationStyle(R.style.AnimBottom);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(Color.argb(120, 0, 0, 0));
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.img_top).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});

	}

}