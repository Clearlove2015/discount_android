package com.schytd.discount.ui;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.schytd.discount_android.R;

public class ActivityNoNet extends Activity {
	private ImageView mImageView_connect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_no_net);
		init();
	}

	private void init() {
		mImageView_connect = (ImageView) this.findViewById(R.id.to_connect);
		mImageView_connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ActivityNoNet.this.isNetworkConnected()) {
					Intent i = new Intent(ActivityNoNet.this,
							ActivityPersonCenter.class);
					i.putExtra("position",
							getIntent().getStringExtra("position"));
					startActivity(i);
					finish();
				} else {
					Toast.makeText(ActivityNoNet.this, "网速不给力啊",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public boolean isNetworkConnected() {
		// 判断网络是否连接
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		return mNetworkInfo != null && mNetworkInfo.isAvailable();
	}

}
