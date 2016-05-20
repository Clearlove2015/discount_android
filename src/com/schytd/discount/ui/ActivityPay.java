package com.schytd.discount.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.schytd.discount_android.R;

public class ActivityPay extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_layout);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pay_back:
			finish();
			break;
		default:
			break;
		}

	}

}
