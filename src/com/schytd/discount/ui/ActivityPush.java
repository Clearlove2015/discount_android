package com.schytd.discount.ui;

import android.app.Activity;
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
import com.schytd.discount.enties.IntroductionInfo;
import com.schytd.discount.tools.AESUtils;
import com.schytd.discount.tools.StrTools;
import com.schytd.discount_android.R;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.RenrenSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

public class ActivityPush extends Activity implements OnClickListener {
	private UserBussiness mUserBussiness;
	// 总推广人数
	private TextView mTextView_All;
	// 有效退广人数
	private TextView mTextView_Effect;
	// 推广码
	private TextView mTextView_Code;
	// 二维码图片
	private ImageView mImageView_QrCode;
	private UMSocialService mController;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push);
		// 首先在您的Activity中添加如下成员变量
		mController = UMServiceFactory
				.getUMSocialService("com.umeng.share");
		init();
		configPlatforms();
		new GetIntroductionInfo().execute();
	}

	private void init() {
		mUserBussiness = new UserBussinessImpl(this);
		mTextView_All = (TextView) this.findViewById(R.id.all_introduction_num);
		mTextView_Effect = (TextView) this
				.findViewById(R.id.effect_introduction_num);
		mTextView_Code = (TextView) this.findViewById(R.id.introduction_code);
		mImageView_QrCode = (ImageView) this.findViewById(R.id.iv_qrcode);
	}
	/**
	 * 配置分享平台参数</br>
	 */
	private void configPlatforms() {
		// 添加新浪SSO授权
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		// 添加腾讯微博SSO授权
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		// 添加人人网SSO授权
		RenrenSsoHandler renrenSsoHandler = new RenrenSsoHandler(
				ActivityPush.this, "201874",
				"28401c0964f04a72a14c812d6132fcef",
				"3bf66e42db1e4fa9829b955cc300b737");
		mController.getConfig().setSsoHandler(renrenSsoHandler);
		// 添加QQ、QZone平台
		addQQQZonePlatform();
		// 添加微信、微信朋友圈平台
		addWXPlatform();
	}

	/**
	 * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
	 *       image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
	 *       要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
	 *       : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
	 * @return
	 */
	private void addQQQZonePlatform() {
		String appId = "100424468";
		String appKey = "c7394704798a158208a74ab60104f0ba";
		// 添加QQ支持, 并且设置QQ分享内容的target url
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(
				ActivityPush.this, appId, appKey);
		qqSsoHandler.setTargetUrl("http://www.baidu.com");
		qqSsoHandler.addToSocialSDK();

		// 添加QZone平台
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(
				ActivityPush.this, appId, appKey);
		qZoneSsoHandler.addToSocialSDK();
	}
	/**
	 * @功能描述 : 添加微信平台分享
	 * @return
	 */
	private void addWXPlatform() {
		// 注意：在微信授权的时候，必须传递appSecret
		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		String appId = "wx967daebe835fbeac";
		String appSecret = "5bb696d9ccd75a38c8a0bfe0675559b3";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(ActivityPush.this,
				appId, appSecret);
		wxHandler.addToSocialSDK();

		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(
				ActivityPush.this, appId, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	// 获取用户的退广信息
	private class GetIntroductionInfo extends
			AsyncTask<Void, Void, IntroductionInfo> {
		@Override
		protected IntroductionInfo doInBackground(Void... param) {
			IntroductionInfo introductionInfo = null;
			try {
				introductionInfo = mUserBussiness.toIntroductionInfo();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return introductionInfo;
		}

		@Override
		protected void onPostExecute(IntroductionInfo result) {
			if (result != null) {
				mTextView_All.setText(result.getAllIntroductionCount() + " 个");
				mTextView_Effect.setText(result.getEffectiveIntroductionCount()
						+ " 个");
				String codeStr = result.getIntroductionCode();
				mTextView_Code.setText(codeStr.toUpperCase());
				// AES加密
				Log.d("++++++++", "qrcode_password = "
						+ ConstantData.QRCODE_PASSWORD);
				byte[] b = AESUtils.encrypt(codeStr,
						ConstantData.QRCODE_PASSWORD);
				Log.d("++++++++", "byte[] b = " + b);
				String result1 = AESUtils.parseByte2HexStr(b);
				Log.d("++++++++", "result = " + result1);
				Bitmap mBitmap;
				try {
					mBitmap = StrTools.cretaeQrCodeBitmap(result1,
							getResources().getDrawable(R.drawable.qcode));
					mImageView_QrCode.setImageBitmap(mBitmap);
				} catch (NotFoundException e) {
					e.printStackTrace();
				} catch (WriterException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(getApplicationContext(), "数据请求错误！",
						Toast.LENGTH_SHORT).show();
			}
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

		case R.id.goto_share_push:
			// 是否只有已登录用户才能打开分享选择页
	        mController.openShare(ActivityPush.this, false);
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
