package com.schytd.discount.ui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.schytd.discount.bussiness.UserBussiness;
import com.schytd.discount.bussiness.impl.UserBussinessImpl;
import com.schytd.discount.enties.User;
import com.schytd.discount.tools.StrTools;
import com.schytd.discount_android.R;

public class ActivityFeedBack extends Activity implements OnClickListener {
	private EditText mEditText;
	private TextView mTextView;
	private final int MAX_LENGTH = 500;
	private int Rest_Length = MAX_LENGTH;
	private UserBussiness mUserBussiness;

	private void init() {
		mEditText = (EditText) findViewById(R.id.feedback_et);
		mTextView = (TextView) findViewById(R.id.feedback_tv);
		mUserBussiness = new UserBussinessImpl(this);
		// 获得用户电话号码 没有弹出对话框
		pool1.execute(t1);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		init();
		init2();
	}

	private void init2() {
		mEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (Rest_Length > 0) {
					Rest_Length = MAX_LENGTH - mEditText.getText().length();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				mTextView.setText("剩余" + Rest_Length + "字");
			}

			@Override
			public void afterTextChanged(Editable s) {
				mTextView.setText("剩余" + Rest_Length + "字");
			}
		});
	}

	// 线程池
	private ExecutorService pool1 = Executors.newSingleThreadExecutor();
	private ExecutorService pool2 = Executors.newSingleThreadExecutor();
	// 电话号码
	private static String num = null;
	private static String content = null;
	Thread t1 = new Thread(new Runnable() {
		@Override
		public void run() {
			User user = null;
			try {
				user = mUserBussiness.getUserinfo();
				if (user != null) {
					num = user.getNum();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	});
	Thread t2 = new Thread(new Runnable() {
		@Override
		public void run() {
			try {
				mUserBussiness.UserAdvice(num, content);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	});

	// 输入电话号码
	private void getNum() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				ActivityFeedBack.this);
		builder.setTitle("电话号码:");
		final EditText text = new EditText(ActivityFeedBack.this);
		builder.setView(text);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				num = text.getText().toString();
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		builder.show();
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		content = mEditText.getText().toString();
		switch (id) {
		case R.id.btn_back:
			finish();
			overridePendingTransition(R.anim.push_right_out,
					R.anim.push_right_in);
			break;
		case R.id.feedback_commit:
			// 提交
			try {
				if (num != null) {
					if (!StrTools.isNull(content)) {
						pool2.execute(t2);
						Toast.makeText(ActivityFeedBack.this, "提交成功",
								Toast.LENGTH_SHORT).show();
						finish();
					} else {
						Toast.makeText(ActivityFeedBack.this, "请输入内容",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					// 弹出对话框 输入电话号码
					getNum();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 关闭线程池
		pool1.shutdown();
		pool2.shutdown();
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
