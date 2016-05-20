package com.schytd.discount.ui;

import com.schytd.discount.ui.View.ButtonIcon;
import com.schytd.discount_android.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ActivityCommodityDetail extends Activity {
	private ButtonIcon mBtn_back;

	public void init() {
		mBtn_back = (ButtonIcon) findViewById(R.id.commodity_detail_back);
		mBtn_back.setOnClickListener(listener);
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.commodity_detail_back:
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
		setContentView(R.layout.activity_commodity_detail);
		init();
	}

}
