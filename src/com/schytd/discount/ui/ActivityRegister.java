package com.schytd.discount.ui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.schytd.discount.bussiness.UserBussiness;
import com.schytd.discount.bussiness.impl.UserBussinessImpl;
import com.schytd.discount.enties.ConstantData;
import com.schytd.discount.tools.AESUtils;
import com.schytd.discount.tools.NetTools;
import com.schytd.discount.tools.StrTools;
import com.schytd.discount.ui.View.ButtonIcon;
import com.schytd.discount.ui.dialog.PromptDialog;
import com.schytd.discount.ui.switchbtn.togglebutton.ToggleButton;
import com.schytd.discount_android.R;

public class ActivityRegister extends Activity implements OnClickListener {
	private EditText mEditView_num, mEditView_password, mEditView_Code,
			mEditText_password2, mEditText_showQrCode;
	// 注册
	private Button mButton_register;
	private UserBussiness mUserBusiness;
	// 获得验证码
	private Button mButton_getCode;
	// 是否同意用户议
	private ToggleButton mSwitch_agreedUser;
	// 线程池
	private ExecutorService pool = Executors.newSingleThreadExecutor();
	// 扫描二维码
	private Button mButton_getQrCode;
	// 返回按钮
	private ButtonIcon mImageView_back;

	private TextView mUser_protocol;
//	推广码
	private String introductionCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitivity_person_register);
		init();
	}

	private void init() {
		mEditView_num = (EditText) this.findViewById(R.id.phone_num);
		mEditView_password = (EditText) this.findViewById(R.id.phone_password);
		mEditView_Code = (EditText) this.findViewById(R.id.sms_code);
		mButton_register = (Button) this.findViewById(R.id.btn_register);
		mUserBusiness = new UserBussinessImpl(this);
		mButton_register.setOnClickListener(mListener);
		mButton_getCode = (Button) this.findViewById(R.id.btn_getCode);
		mButton_getCode.setOnClickListener(mCodeListener);
		mSwitch_agreedUser = (ToggleButton) this
				.findViewById(R.id.check_isAgreed);
		mSwitch_agreedUser.toggleOn();
		mEditText_password2 = (EditText) this
				.findViewById(R.id.phone_passowrd_re);
		mButton_getQrCode = (Button) this.findViewById(R.id.btn_getQrCode);
		mEditText_showQrCode = (EditText) this.findViewById(R.id.et_showQrCode);
		mButton_getQrCode.setOnClickListener(mQrCodeListener);
		mImageView_back = (ButtonIcon) this
				.findViewById(R.id.activity_register_back);
		mImageView_back.setOnClickListener(mQrCodeListener);
		mUser_protocol = (TextView) findViewById(R.id.user_protocol);
		mUser_protocol.setOnClickListener(listener);
		mUser_protocol.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线

	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			showProtocolDialog();
		}
	};

	/**
	 * 用户协议对话框
	 */
	public void showProtocolDialog() {
		new PromptDialog.Builder(ActivityRegister.this).setTitle("用户协议")
				.setMessage("四川华宇天地网络科技有限公司", null)
				.setButton1("同意", new PromptDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog, int which) {

						dialog.dismiss();
					}
				}).setButton2("不同意", new PromptDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	// 扫描二维码
	private OnClickListener mQrCodeListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.activity_register_back:
				// 关闭当前acitivty
				finish();
				// 动画
				overridePendingTransition(R.anim.push_right_out,
						R.anim.push_right_in);
				break;

			case R.id.btn_getQrCode:
				Intent intent = new Intent();
				intent.setClass(ActivityRegister.this, ActivityQrCode.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, 100);
				break;
			}

		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 100:
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				String bundle_result = bundle.getString("result");
				// AES解密
				byte[] decryptFrom = AESUtils.parseHexStr2Byte(bundle_result);
				byte[] decryptResult = AESUtils.decrypt(decryptFrom,
						ConstantData.QRCODE_PASSWORD);

				Log.d("+++++++++", "扫描结果：" + bundle_result);
				Log.d("++++++++++", "decryptFrom = " + decryptFrom);
				Log.d("++++++++++", "decryptResult = " + decryptResult + ";"
						+ "(decryptResult为空表示解密失败)");

				String results = new String(decryptResult);
				Log.d("+++++++++", "results = " + results);
				// 显示扫描到的内容
				mEditText_showQrCode.setText(results);
			}
			break;
		}
	}

	private OnClickListener mCodeListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// 获得验证码
			final String phonenum = mEditView_num.getText().toString();
			if (StrTools.isNull(phonenum)) {
				Toast.makeText(ActivityRegister.this, "电话号码不能为空",
						Toast.LENGTH_SHORT).show();
			} else {
				CodeThread codeThread = new CodeThread(phonenum);
				pool.execute(codeThread);
			}
		}
	};

	private OnClickListener mListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			final String phonenum = mEditView_num.getText().toString();
			final String password = mEditView_password.getText().toString();
			final String code = mEditView_Code.getText().toString();
			if (StrTools.isNull(phonenum) || StrTools.isNull(phonenum)
					|| StrTools.isNull(phonenum)) {
				Toast.makeText(ActivityRegister.this, "电话号码不能为空",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (!StrTools.isPhoneNum(phonenum)) {
				Toast.makeText(ActivityRegister.this, "电话号码长度非法！",
						Toast.LENGTH_SHORT).show();
				return;
			}
			String password_re = mEditText_password2.getText().toString();
			if (StrTools.isNull(password)) {
				Toast.makeText(ActivityRegister.this, "密码不能为空！",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (!StrTools.isPassword(password)) {
				Toast.makeText(ActivityRegister.this, "密码长度必须大于6",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if ((!StrTools.isNull(password)) && StrTools.isNull(password_re)) {
				Toast.makeText(ActivityRegister.this, "密码不能为空！",
						Toast.LENGTH_SHORT).show();
				return;
			}
			// 如果两次输入的密码不相等
			if (!StrTools.isEqual(password, password_re)) {
				Toast.makeText(ActivityRegister.this, "两次输入的密码不一致",
						Toast.LENGTH_SHORT).show();
				return;
			}
			// 如果输入验证码的框为空 直接退出
			if (StrTools.isNull(code)) {
				Toast.makeText(ActivityRegister.this, "请输入验证码！",
						Toast.LENGTH_SHORT).show();
				return;
			}
			// 同意用户协议
			if (!mSwitch_agreedUser.isToggle()) {
				return;
			}
			final String password_md5 = NetTools
					.getMD5Code((phonenum + password).getBytes());
			introductionCode=mEditText_showQrCode.getText().toString();
			// 如果已经执行
			if (!isRegistering) {
				new RegisterTask().execute(code, phonenum, password_md5,introductionCode);
			}

		}
	};
	TimeCount timeCount = new TimeCount(60000, 1000);

	private class CodeThread implements Runnable {
		String params = null;

		public CodeThread(String params) {
			this.params = params;
		}

		@Override
		public void run() {
			Boolean result = false;

			try {
				result = mUserBusiness.getCaptchaCode(params);
				if (result) {
					// 60秒后解锁
					runOnUiThread(new Runnable() {
						public void run() {
							timeCount.start();
						}
					});
				}
				Log.d("+++++", "CodeThread");
				if (!result) {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(ActivityRegister.this, "获取验证码失败！",
									Toast.LENGTH_SHORT).show();
							timeCount.cancel();
						}
					});

				}
			} catch (Exception e) {

			}

		}
	}

	private boolean isRegistering = false;

	private class RegisterTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPostExecute(String result) {
			if (result.equals("0")) {
				Toast.makeText(getApplicationContext(), "注册成功",
						Toast.LENGTH_SHORT).show();
				// 注册成功跳转到个人中心
				Intent i=new Intent(ActivityRegister.this, ActivityPersonCenter.class);
				startActivity(i);
				finish();
			} else if (result.equals("1")) {
				Toast.makeText(getApplicationContext(), "该号码已经注册！",
						Toast.LENGTH_SHORT).show();
			} else if (result.equals("2")) {
				Toast.makeText(getApplicationContext(), "验证码错误！",
						Toast.LENGTH_SHORT).show();
			} else if (result.equals("5")) {
				Toast.makeText(getApplicationContext(), "不存在的推荐码！",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "注册失败",
						Toast.LENGTH_SHORT).show();
			}
			isRegistering = false;
		}

		// 执行前
		@Override
		protected void onPreExecute() {
			isRegistering = true;
		}

		@Override
		protected String doInBackground(String... params) {
			String result = 0 + "";
			try {
				boolean isRegister = mUserBusiness.userIsRegister(params[1]);
				if (isRegister) {
					return "isReigster";
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				result = mUserBusiness.userRegister(params[0], params[1],
						params[2],params[3]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
	};

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			mButton_getCode.setText("重新获取");
			mButton_getCode.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			mButton_getCode.setClickable(false);
			mButton_getCode.setText(millisUntilFinished / 1000 + "秒");
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

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.activity_register_back:
			finish();
			break;

		default:
			break;
		}

	}
}
