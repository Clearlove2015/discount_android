package com.schytd.discount.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.schytd.discount.bussiness.UserBussiness;
import com.schytd.discount.bussiness.impl.UserBussinessImpl;
import com.schytd.discount.enties.ConstantData;
import com.schytd.discount.enties.User;
import com.schytd.discount.tools.ImageUtils;
import com.schytd.discount.tools.ImageUtils.OnLoadImageListener;
import com.schytd.discount.ui.View.RoundImageView;
import com.schytd.discount_android.R;

public class ActivityPersonCenter extends Activity {
	public static int PERSON_CENTER_BACK_RESULT = 321;
	public static int REQUEST_PERSON_MESSAGE = 135;
	private UserBussiness mUserBussiness;
	// 定位点
	private TextView mTextViewPosition;
	// 用户昵称
	private TextView mTextViewNickName;
	// 用户性别
	private ImageView mImageView_icon;
	// 用户信息
	private User mUser;
	// 进度条
	private LinearLayout mProgressBar;
	private int flag = 0;
	// 头像
	private RoundImageView mImageViewIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_center);
		init();
	}

	private void init() {
		mImageViewIcon = (RoundImageView) this
				.findViewById(R.id.person_center_img);
		mProgressBar = (LinearLayout) this
				.findViewById(R.id.loading_person_center);
		mUserBussiness = new UserBussinessImpl(this);
		mTextViewPosition = (TextView) this.findViewById(R.id.position_txt);
		mTextViewNickName = (TextView) this.findViewById(R.id.user_nickname);
		mImageView_icon = (ImageView) this.findViewById(R.id.user_sex);
		String position = getIntent().getStringExtra("position");
		mTextViewPosition.setText(position);
		intent_personinfo = new Intent(ActivityPersonCenter.this,
				ActivityPersonalMessage.class);
		mTextViewPosition.setText(ConstantData.position);
	}

	private Intent intent_personinfo;

	// 得到用户的信息
	private class GetInfoTask extends AsyncTask<Void, Void, User> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (flag == 0) {
				mProgressBar.setVisibility(View.VISIBLE);
			}
		}

		@Override
		protected User doInBackground(Void... arg0) {
			// 用户信息
			User user = null;
			try {
				user = mUserBussiness.getUserinfo();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return user;
		}

		protected void onPostExecute(User result) {
			mProgressBar.setVisibility(View.GONE);
			if (result != null) {
				mUser = result;
				mUser.setName(result.getName());
				mUser.setUserIcon(result.getUserIcon());
				mTextViewNickName.setText(result.getName());
				if (result.getGender().equals("男")) {
					mImageView_icon.setImageResource(R.drawable.man);
				} else if (result.equals("女")) {
					mImageView_icon.setImageResource(R.drawable.woman);
				}
				ImageUtils imageUtils = new ImageUtils(
						ActivityPersonCenter.this, result.getUserIcon());
				imageUtils.onLoadImage(new OnLoadImageListener() {
					@Override
					public void OnLoadImage(Bitmap bitmap, String bitmapPath) {
						if (bitmap != null) {
							mImageViewIcon.setImageBitmap(bitmap);
						}
					}
				});
			}
		};
	};

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.person_center_img:
			intent_personinfo.putExtra("user", mUser);
			startActivityForResult(intent_personinfo, REQUEST_PERSON_MESSAGE);
			break;
		case R.id.go_person_info:
			intent_personinfo.putExtra("user", mUser);
			startActivityForResult(intent_personinfo, REQUEST_PERSON_MESSAGE);
			break;
		// 二维码
		case R.id.go_to_qrcode:
			try{
				Intent intent = new Intent(ActivityPersonCenter.this,
						ActivityMyQrCode.class);
				intent.putExtra("path", mUser.getUserIcon());
				intent.putExtra("name", mUser.getName());
				startActivity(intent);
			}catch (Exception e) {
				e.printStackTrace();
				startActivity(new Intent(ActivityPersonCenter.this,ActivityMyQrCode.class));
			}
			break;
		// 消费历史
		case R.id.goto_history:
			startActivity(new Intent(ActivityPersonCenter.this,
					ActivityHistoryCustoms.class));
			break;
		// 有奖推广
		case R.id.goto_push:
			startActivity(new Intent(ActivityPersonCenter.this,
					ActivityPush.class));
			break;
		// 我的钱包
		case R.id.goto_my_wallet:
			startActivity(new Intent(ActivityPersonCenter.this,
					ActivityWallet.class));
			break;
		// 注销
		case R.id.unregister_btn:
			// 注销
			// 清除SessionId 清除用户信息 如果记住密码 清除
			doLogout();
			break;
		// 积分等级
		case R.id.goto_personrank:
			startActivity(new Intent(ActivityPersonCenter.this,
					ActivityPersonRank.class));
			break;
		case R.id.person_center_back:
			if (mUser != null) {
				Intent i = new Intent();
				i.putExtra("name", mUser.getName());
				i.putExtra("path", mUser.getUserIcon());
				setResult(PERSON_CENTER_BACK_RESULT, i);
			}
			finish();
			break;
		}

	}

	// 注销
	private void doLogout() {
		mProgressBar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String result = mUserBussiness.toLogout();
					if (result.equals("0")) {
						// 删除用户的登陆信息
						mUserBussiness.removeUserData();
						runOnUiThread(new Runnable() {
							public void run() {
								// 用户主动注销
								setResult(123);
								mProgressBar.setVisibility(View.GONE);
								// 退出当前页面
								finish();
							}
						});
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	// 当该 activity 位于栈顶时
	@Override
	protected void onResume() {
		super.onResume();
		// 得到用户的信息
		new GetInfoTask().execute();
		flag++;

	}

	// 重写返回键事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent i = new Intent();
			if (mUser != null) {
				i.putExtra("name", mUser.getName());
				setResult(PERSON_CENTER_BACK_RESULT, i);
			}
			finish();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	// 返回
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_PERSON_MESSAGE) {
			if (resultCode == ActivityPersonalMessage.RESULT_PERSON_CENTER) {
				int flag = data.getIntExtra("gender", 0);
				if (flag == 0) {
					mImageView_icon.setImageResource(R.drawable.man);
				} else if (flag == 1) {
					mImageView_icon.setImageResource(R.drawable.woman);
				}
			}
		}
	}

}
