package com.schytd.discount.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.schytd.discount.bussiness.UserBussiness;
import com.schytd.discount.bussiness.impl.UserBussinessImpl;
import com.schytd.discount.ui.View.ButtonIcon;
import com.schytd.discount_android.R;

public class ActivityGender extends Activity {
	public static int RESULT_GENDER = 432;
	private ImageView mImageView_man, mImageView_woman;
	private ButtonIcon mImageView_Update;
	private int flag = 1;// 1表示男，2表示女
	private UserBussiness mUserBussiness;
	private String gender, nickName;
	private int mDefault = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gender_update);
		init();
		try {
			gender = getIntent().getStringExtra("gender");
			nickName = getIntent().getStringExtra("nickname");
			if (gender.equals("男")) {
				mImageView_woman.setVisibility(View.GONE);
				mImageView_man.setVisibility(View.VISIBLE);
				mDefault = 1;
			} else {
				mImageView_man.setVisibility(View.GONE);
				mImageView_woman.setVisibility(View.VISIBLE);
				mDefault = 2;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() {
		mUserBussiness = new UserBussinessImpl(this);
		mImageView_man = (ImageView) this.findViewById(R.id.gender_man_checked);
		mImageView_woman = (ImageView) this
				.findViewById(R.id.gender_woman_checked);
		mImageView_Update = (ButtonIcon) this
				.findViewById(R.id.gender_update_ok);
		mImageView_Update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 更改性别
				if (mDefault != flag) {
					Toast.makeText(getApplicationContext(), nickName,
							Toast.LENGTH_SHORT).show();
					new UpdateGender().execute(flag + "", nickName);
				}
			}
		});
	}

	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.gender_man:
			mImageView_woman.setVisibility(View.GONE);
			mImageView_man.setVisibility(View.VISIBLE);
			flag = 1;
			break;
		case R.id.gender_woman:
			mImageView_man.setVisibility(View.GONE);
			mImageView_woman.setVisibility(View.VISIBLE);
			flag = 2;
			break;
		case R.id.gender_btn_back:
			closeAct();
			break;
		}
	}

	private void closeAct() {
		if (result) {
			Intent intent = new Intent();
			if (flag == 1) {
				intent.putExtra("up_gender", "男");
			} else if (flag == 2) {
				intent.putExtra("up_gender", "女");
			}
			setResult(RESULT_GENDER, intent);
		}
		finish();

	}

	private boolean result = false;

	private class UpdateGender extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				result = mUserBussiness
						.alterUserInfo("2", params[0], params[1]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Toast.makeText(ActivityGender.this, "修改成功", Toast.LENGTH_SHORT)
						.show();
				closeAct();
			} else {
				Toast.makeText(ActivityGender.this, "修改失败！", Toast.LENGTH_SHORT)
						.show();
			}
		}

	}
}
