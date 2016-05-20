package com.schytd.discount.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.schytd.discount.bussiness.UserBussiness;
import com.schytd.discount.bussiness.impl.UserBussinessImpl;
import com.schytd.discount.tools.StrTools;
import com.schytd.discount.ui.View.ButtonIcon;
import com.schytd.discount_android.R;

public class ActivityUpdataNickName extends Activity implements OnClickListener {
	public static final int RESULTCODE = 100;
	private EditText mEditTextName;
	private ButtonIcon mImageViewComplete;
	// 调用业务层方法 修改用户信息
	private UserBussiness mUserBusiness;

	private void init() {
		mEditTextName = (EditText) findViewById(R.id.update_nickname_et);
		mImageViewComplete = (ButtonIcon) findViewById(R.id.update_nickname_complete);
		mUserBusiness = new UserBussinessImpl(this);
		mImageViewComplete.setOnClickListener(this);
		// 得到数据
		String oldName = getIntent().getStringExtra("oldName");
		if (oldName != null) {
			mEditTextName.setText(oldName);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_nickname);
		init();
	}

	private String newNickName = null;

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.btn_back:
			finish();
			break;

		case R.id.update_nickname_complete:
			// 完成更新
			newNickName = mEditTextName.getText().toString();
			// 不为空
			if (!StrTools.isNull(newNickName)) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						// 修改用户昵称
						try {
							// 1表示修改用户名昵称
							mUserBusiness.alterUserInfo("1", newNickName);
							// 将修改的昵称传回去
							Intent data = new Intent();
							data.putExtra("NickName", mEditTextName.getText()
									.toString());
							setResult(RESULTCODE, data);
							runOnUiThread(new Runnable() {
								public void run() {
									finish();
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
				Toast.makeText(getApplicationContext(), "修改成功！",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "请输入修改用户名！",
						Toast.LENGTH_SHORT).show();
			}
			break;
		}

	}

}
