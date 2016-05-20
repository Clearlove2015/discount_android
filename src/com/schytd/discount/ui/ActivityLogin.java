package com.schytd.discount.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.schytd.discount.bussiness.UserBussiness;
import com.schytd.discount.bussiness.impl.UserBussinessImpl;
import com.schytd.discount.enties.User;
import com.schytd.discount.enties.UserSessionId;
import com.schytd.discount.tools.NetTools;
import com.schytd.discount.tools.StrTools;
import com.schytd.discount.ui.switchbtn.togglebutton.ToggleButton;
import com.schytd.discount_android.R;

public class ActivityLogin extends Activity implements OnClickListener {
	private EditText mEditText_num, mEditText_password;
	private Button mButton_login;
	public static int RESULT_LOGIN_SUCCESS = 301;
	private UserBussiness mUserBusiness;
	private LinearLayout mProgressBar;
	private TextView mTextView_register;
	// 记住密码
	private ToggleButton mCheckBox_remmber;
	private LoginAsyncTask loginAsyncTask;
	// 保存登陆设置
	private SharedPreferences sp;
	private void init() {
		mProgressBar = (LinearLayout) this
				.findViewById(R.id.loading_login);
		mEditText_num = (EditText) findViewById(R.id.activity_login_phonenum);
		mEditText_password = (EditText) findViewById(R.id.activity_login_password);
		mButton_login = (Button) findViewById(R.id.activity_login_btnlogin);
		mTextView_register = (TextView) findViewById(R.id.activity_login_register);
		mUserBusiness = new UserBussinessImpl(this);
		mButton_login.setOnClickListener(mBtn_login);
		mTextView_register.setOnClickListener(mBtn_register);
		// 初始化SharedPreferences
		sp = getSharedPreferences("user_login", Context.MODE_PRIVATE);
		mCheckBox_remmber = (ToggleButton) this
				.findViewById(R.id.remmber_password);
	}
	/**
	 * 根据SharedPreferences中存储的数据 来恢复你的界面显示
	 */
	private void config() {

		boolean isAuto = sp.getBoolean("isAuto", false);
		if (isAuto) {
			mCheckBox_remmber.setToggleOn();
		}
		String phonenum = sp.getString("phonenum", null);
		if (phonenum != null) {
			mEditText_num.setText(phonenum);
		}
		String loginPassword = sp.getString("password", null);
		if (loginPassword != null) {
			mEditText_password.setText(loginPassword);
		}

	}
	private class LoginAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			mProgressBar.setVisibility(View.VISIBLE);
		}
		@Override
		protected String doInBackground(String... params) {
			List<String> result = null;
			try {
				result = mUserBusiness.userLogin(params[0], params[1]);
				if (result==null) {
					return "10";
				}

				// 封装userSessionId对象
				UserSessionId userSessionId = new UserSessionId();
				userSessionId.setSessionid(result.get(1));
				// 返回给activity
				Intent data = new Intent();
				data.putExtra("sessionId", result.get(1));
				setResult(222, data);

				userSessionId.setTime(System.currentTimeMillis());
				// 删除原有sessionid
				mUserBusiness.removeSessionId();
				mUserBusiness.newUserSessionId(userSessionId);
				// 封装user对象
				User user = new User();
				user.setNum(params[0]);
				user.setPassword(params[1]);
				mUserBusiness.addUserLoginInfo(user);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (result==null) {
				return "10";
			}else{
				return result.get(0);
			}
		}

		@Override
		protected void onPostExecute(String result) {
			mProgressBar.setVisibility(View.GONE);
			//
			if (result.equals("0")) {
				// 是否要自动登录
				if (mCheckBox_remmber.isToggle()) {
					Editor editor = sp.edit();
					editor.putString("phonenum", num);
					// 安全性考虑：加密
					editor.putString("password", password);
					editor.putBoolean("isAuto", true);
					// 生效：文件写入
					editor.commit();
				} else {
					Editor editor = sp.edit();
					editor.putString("phonenum", null);
					// 安全性考虑：加密
					editor.putString("password", null);
					editor.putBoolean("isAuto", false);
					// 生效：文件写入
					editor.commit();
				}
				Toast.makeText(ActivityLogin.this, "登录成功", Toast.LENGTH_SHORT)
						.show();
				finish();

			} else if(result.equals("2")){
				Toast.makeText(ActivityLogin.this, "密码错误", Toast.LENGTH_SHORT)
						.show();
			}else{
				Toast.makeText(ActivityLogin.this, "登陆失败", Toast.LENGTH_SHORT)
				.show();
			}
		}

	}

	private String num;
	private String password;
	private OnClickListener mBtn_login = new OnClickListener() {
		@Override
		public void onClick(View view) {
			num = mEditText_num.getText().toString();
			password = mEditText_password.getText().toString();
			final String password_1 = NetTools.getMD5Code(num.concat(password)
					.trim().getBytes());

//			Toast.makeText(ActivityLogin.this, num.concat(password).trim(),
//					Toast.LENGTH_SHORT).show();
//			Toast.makeText(ActivityLogin.this, "MD5:" + password_1,
//					Toast.LENGTH_SHORT).show();
			// 判断 长度
			if (StrTools.isNull(num) || StrTools.isNull(password)) {
				Toast.makeText(ActivityLogin.this, "账户和密码不能为空！",
						Toast.LENGTH_SHORT).show();
			} else {
				if (!StrTools.isPhoneNum(num)) {
					Toast.makeText(ActivityLogin.this, "账户不正确！",
							Toast.LENGTH_SHORT).show();
				} else {
					loginAsyncTask = new LoginAsyncTask();
					loginAsyncTask.execute(num, password_1);
				}
			}
		}
	};

	private OnClickListener mBtn_register = new OnClickListener() {

		@Override
		public void onClick(View view) {
			startActivity(new Intent(ActivityLogin.this, ActivityRegister.class));

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		init();
		// 从配置中恢复密码
		this.config();
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.btn_back:
			finish();
			break;

		case R.id.forget_password:
			// 忘记密码修改密码
			startActivity(new Intent(ActivityLogin.this,
					ActivityForgetPassword.class));
			break;
		case R.id.txt_goto_register:
			// 注册新用户
			startActivity(new Intent(ActivityLogin.this, ActivityRegister.class));
			break;
		}
	}

}
