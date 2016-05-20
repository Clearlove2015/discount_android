package com.schytd.discount.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.schytd.discount.bussiness.UserBussiness;
import com.schytd.discount.bussiness.impl.UserBussinessImpl;
import com.schytd.discount.enties.ConstantData;
import com.schytd.discount.enties.User;
import com.schytd.discount.enties.UserQrCode;
import com.schytd.discount.tools.AESUtils;
import com.schytd.discount.tools.ImageUtils;
import com.schytd.discount.tools.ImageUtils.OnLoadImageListener;
import com.schytd.discount.tools.StrTools;
import com.schytd.discount.ui.View.RoundImageView;
import com.schytd.discount_android.R;

public class ActivityMyQrCode extends Activity implements OnClickListener {
	// 用于显示二维码的
	private ImageView mImageView_code;
	private UserBussiness mUserBussiness;
	private TextView mText_username;

	private SharedPreferences sp;
	private RoundImageView mRoundImageImg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_qrcode_layout);
		init();
		// 接收ActivityPersonCenter传的User对象
		String name =getIntent().getStringExtra("name");
			mText_username.setText(name);
	}

	private void init() {
		mRoundImageImg = (RoundImageView) this.findViewById(R.id.qrcode_img);
		mImageView_code = (ImageView) this.findViewById(R.id.iv_show_myqrcode);
		mText_username = (TextView) findViewById(R.id.username);
		mUserBussiness = new UserBussinessImpl(this);
		getQrCodeTask.execute();
		sp = getSharedPreferences("user_clientid", Context.MODE_PRIVATE);
		// 头像
		String path = getIntent().getStringExtra("path");
		ImageUtils imageUtils = new ImageUtils(ActivityMyQrCode.this, path);
		imageUtils.onLoadImage(new OnLoadImageListener() {
			@Override
			public void OnLoadImage(Bitmap bitmap, String bitmapPath) {
				if (bitmap != null) {
					mRoundImageImg.setImageBitmap(bitmap);
				}
			}
		});
	}

	// 生成二维码
	private AsyncTask<String, Void, UserQrCode> getQrCodeTask = new AsyncTask<String, Void, UserQrCode>() {

		@Override
		protected UserQrCode doInBackground(String... params) {
			UserQrCode userQrCode = null;
			String num = null;
			String name = null;
			String clientid = null;
			try {
				num = mUserBussiness.getUser().getNum();
				name = mUserBussiness.getUser().getName();
				clientid = sp.getString("clientid", null);
				Log.d("++++++++", "num = " + num);
				Log.d("++++++++", "name = " + name);
				Log.d("++++++++", "clientid = " + clientid);
				userQrCode = new UserQrCode(num, name, clientid);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return userQrCode;
		}

		protected void onPostExecute(UserQrCode userQrCode) {
			try {
				String num = userQrCode.getNum();
				String name = userQrCode.getName();
				String clientId = userQrCode.getClientid();
				if (num != null && name != null && clientId != null) {
					String qrcode_info = num + "-" + name + "-" + clientId;
					Log.d("++++++++", "qrcode_info = " + qrcode_info);
					// AES加密
					Log.d("++++++++", "qrcode_password = "
							+ ConstantData.QRCODE_PASSWORD);
					byte[] b = AESUtils.encrypt(qrcode_info,
							ConstantData.QRCODE_PASSWORD);
					Log.d("++++++++", "byte[] b = " + b);
					String result = AESUtils.parseByte2HexStr(b);
					Log.d("++++++++", "result = " + result);

					Bitmap qrCode = StrTools.cretaeQrCodeBitmap(result,
							getResources().getDrawable(R.drawable.qcode));
					mImageView_code.setImageBitmap(qrCode);
				} else {
					Log.d("+++++++++", "生成二维码出错");
					Toast.makeText(ActivityMyQrCode.this, "生成二维码出错",
							Toast.LENGTH_SHORT).show();
				}

			} catch (NotFoundException e) {
				e.printStackTrace();
			} catch (WriterException e) {
				e.printStackTrace();
			}
		};
	};

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.button_back:
			finish();
			overridePendingTransition(R.anim.push_right_out,
					R.anim.push_right_in);
			break;

		default:
			break;
		}

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

}
