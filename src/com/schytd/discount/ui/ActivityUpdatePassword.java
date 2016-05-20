package com.schytd.discount.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.schytd.discount.bussiness.UserBussiness;
import com.schytd.discount.bussiness.impl.UserBussinessImpl;
import com.schytd.discount.tools.StrTools;
import com.schytd.discount.ui.View.ButtonIcon;
import com.schytd.discount_android.R;

public class ActivityUpdatePassword extends Activity {
	private EditText mEditText_oldPassword, mEditText_newPassword,
			mEditText_newPassword2;
	private ButtonIcon mButton_alter;
	private ButtonIcon mImgaeView_back;
	private UserBussiness mUserBussiness;
	// 保存登陆设置
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitivity_new_password);
		init();
	}

	private void init() {
		// 初始化SharedPreferences
		sp = getSharedPreferences("user_login", Context.MODE_PRIVATE);
		mEditText_oldPassword = (EditText) this
				.findViewById(R.id.alter_oldpassword);
		mEditText_newPassword = (EditText) this
				.findViewById(R.id.alter_new_password);
		mEditText_newPassword2 = (EditText) this
				.findViewById(R.id.alter_new_password2);
		mButton_alter = (ButtonIcon) this.findViewById(R.id.btn_alter_password);
		mImgaeView_back = (ButtonIcon) this
				.findViewById(R.id.activity_updatepassword_back);
		mUserBussiness = new UserBussinessImpl(this);
		mButton_alter.setOnClickListener(mClickListener);
		mImgaeView_back.setOnClickListener(mClickListener);
	}

	private String oldPassword = null;
	private String newPassword = null;
	private String newPassword2 = null;
	private String phoneNum = null;
	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_alter_password:
				// 修改密码
				oldPassword = mEditText_oldPassword.getText().toString();
				newPassword = mEditText_newPassword.getText().toString();
				newPassword2 = mEditText_newPassword2.getText().toString();
				// 密码不为空 并且长度大于6
				if (StrTools.isNull(oldPassword)) {
					Toast.makeText(getApplicationContext(), "请输入原密码！",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (StrTools.isNull(newPassword)) {
					Toast.makeText(getApplicationContext(), "请输入新密码！",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (!StrTools.isPassword(oldPassword)) {
					Toast.makeText(getApplicationContext(), "原密码长度错误！",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (!StrTools.isPassword(newPassword)) {
					Toast.makeText(getApplicationContext(), "新密码长度错误！",
							Toast.LENGTH_SHORT).show();
					return;
				}
				// 如果原密码和新密码相同
				if (StrTools.isEqual(oldPassword, newPassword)) {
					Toast.makeText(getApplicationContext(), "新密码不能和原密码相同！",
							Toast.LENGTH_SHORT).show();
					return;
				}
				// 两次新密码必须相同
				if (!newPassword.equals(newPassword2)) {
					Toast.makeText(getApplicationContext(), "两次新密码必须相同",
							Toast.LENGTH_SHORT).show();
					return;
				}
				// 不能含有空格
				if (StrTools.hasNull(oldPassword)
						|| StrTools.hasNull(newPassword)
						|| StrTools.hasNull(newPassword2)) {
					Toast.makeText(getApplicationContext(), "不能含有空格！",
							Toast.LENGTH_SHORT).show();
					return;
				}
				// 更改密码
				alterPsdTask.execute("3", oldPassword, newPassword);
				break;

			case R.id.activity_updatepassword_back:
				finish();
				overridePendingTransition(R.anim.push_right_out,
						R.anim.push_right_in);
				break;
			}
		}
	};
	private AsyncTask<String, Void, Boolean> alterPsdTask = new AsyncTask<String, Void, Boolean>() {

		@Override
		protected void onPostExecute(Boolean result) {
			mButton_alter.setEnabled(true);
			if (result != null && result) {
				Toast.makeText(getApplicationContext(), "修改成功！",
						Toast.LENGTH_SHORT).show();
				Editor editor=sp.edit();
				editor.putString("password", newPassword);
				editor.commit();
				finish();
			} else {
				Toast.makeText(getApplicationContext(), "修改失败！",
						Toast.LENGTH_SHORT).show();
				mEditText_newPassword.setText("");
				mEditText_oldPassword.setText("");
				mEditText_newPassword2.setText("");
			}
		}

		@Override
		protected void onPreExecute() {
			mButton_alter.setEnabled(false);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean result = false;
			try {
				phoneNum = mUserBussiness.getUserinfo().getNum();
				if (!StrTools.isNull(phoneNum)) {
					result = mUserBussiness.alterUserInfo(params[0],
							StrTools.getRightPassword(phoneNum, params[1]),
							StrTools.getRightPassword(phoneNum, params[2]));
					return result;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	};

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
