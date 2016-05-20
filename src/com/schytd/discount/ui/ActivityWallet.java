package com.schytd.discount.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.schytd.discount_android.R;

public class ActivityWallet extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_wallet_layout);
	}
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.my_wallet_back:
			finish();
			break;
		case R.id.go_to_card_act:
			startActivity(new Intent(ActivityWallet.this,ActivityCardList.class));
			break;
		}
	}
}
