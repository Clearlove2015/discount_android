package com.schytd.discount.ui;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.schytd.discount.enties.ConstantData;
import com.schytd.discount_android.R;

public class ActivityAboutUs extends Activity implements OnClickListener {
	private TextView mTextView_userPro;
	private TextView mText_Version;
	private ImageView mChangeNetwork;

	private void init() {
		mTextView_userPro = (TextView) this.findViewById(R.id.user_protol);
		mTextView_userPro.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		mText_Version = (TextView) findViewById(R.id.software_version);
		mText_Version.setText(ConstantData.SOFT_VERSION);
		mChangeNetwork = (ImageView) findViewById(R.id.change_network);
		mChangeNetwork.setOnLongClickListener(longlistener);
	}
	
	private OnLongClickListener longlistener = new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			switch (v.getId()) {
			case R.id.change_network:
				if(ConstantData.URI.equals("http://code.schytd.com:8080/discountserver/api")){
					ConstantData.URI = ConstantData.URI_LOCAL;
					Toast.makeText(ActivityAboutUs.this, ConstantData.URI, Toast.LENGTH_SHORT).show();
				}else{
					ConstantData.URI = ConstantData.URI_REMOTE;
					Toast.makeText(ActivityAboutUs.this, ConstantData.URI, Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
			}
			return false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		init();
	}

	// 重写返回键事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.push_right_out,
					R.anim.push_right_in);
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.btn_back:
			finish();
			overridePendingTransition(R.anim.push_right_out,
					R.anim.push_right_in);
			break;
		default:
			break;
		}

	}

}
