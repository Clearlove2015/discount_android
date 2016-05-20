package com.schytd.discount.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.schytd.discount.bussiness.SellerBussiness;
import com.schytd.discount.bussiness.impl.SellerBussinessImp;
import com.schytd.discount.enties.SellerDetail;
import com.schytd.discount.tools.StrTools;
import com.schytd.discount.ui.View.ButtonFloat;
import com.schytd.discount.ui.View.ButtonIcon;
import com.schytd.discount_android.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.RenrenShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.RenrenSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class ActivitySellerDetail extends FragmentActivity {
	private SellerBussiness mSellerBussiness;
	private ButtonIcon mButtonIcon_back, mButtonIcon_share, mButtonIcon_go;
	private ViewPager mViewPager_seller_img;
	// 商家图片数据
	private List<String> mdata;
	private ImageView[] views;
	// 折扣
	private TextView mTextView_discount;
	// 商家详细描述
	private TextView mTextView_detail;
	// 商家特色
	private TextView mTextView_feature;
	// 商家环境
	private TextView mTextView_environment;
	// 提示
	private TextView mTextView_tips;
	// 地址
	private TextView mTextView_address;
	// 网络不可用
	private LinearLayout mLinearLayout_no_net;
	// 再次连接
	private ImageView mImageView_connect;
	// 标题
	private TextView mTextView_title;
	// 联系商家
	private LinearLayout mLinearLayout_contact_seller;
	// 商家号码
	private TextView mTextView_num;
	// 商家名称
	private TextView mTextView_name;
	private SellerDetail mSellerDetail = null;
	// 坐标 折扣
	private String mDiscount, mlat, mlng;
	private Double mNowLat, mNowLng;
	// 原点
	private ViewGroup mViewGroup_point;
	// 支付
	private ButtonFloat mButtonFloat_pay;
	private UMSocialService mController;
	// 用于浏览历史计数
	private SharedPreferences mReadHistory;
	// 商家bid
	private String bid;
//	是否保存浏览记录
	private boolean flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seller_detail);
		// 首先在您的Activity中添加如下成员变量
		mController = UMServiceFactory.getUMSocialService("com.umeng.share");
		init();
		configPlatforms();
		setShareContent();
		readHis(bid);
	}

	// 浏览历史
	private void readHis(final String bid) {
		if (!flag) {
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				int read_count = mReadHistory.getInt("read_history", 0);
				try {
					mSellerBussiness.addReadHistory(read_count++, bid,
							System.currentTimeMillis() + "");
					Editor editor = mReadHistory.edit();
					editor.putInt("read_history", read_count++);
					editor.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 根据不同的平台设置不同的分享内容</br>
	 */
	private void setShareContent() {

		// 配置SSO
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(
				ActivitySellerDetail.this, "100424468",
				"c7394704798a158208a74ab60104f0ba");
		qZoneSsoHandler.addToSocialSDK();
		mController
				.setShareContent("友盟社会化组件（SDK）让移动应用快速整合社交分享功能。http://www.umeng.com/social");

		// APP ID：201874, API
		// * KEY：28401c0964f04a72a14c812d6132fcef, Secret
		// * Key：3bf66e42db1e4fa9829b955cc300b737.
		RenrenSsoHandler renrenSsoHandler = new RenrenSsoHandler(
				ActivitySellerDetail.this, "201874",
				"28401c0964f04a72a14c812d6132fcef",
				"3bf66e42db1e4fa9829b955cc300b737");
		mController.getConfig().setSsoHandler(renrenSsoHandler);

		UMImage localImage = new UMImage(ActivitySellerDetail.this,
				R.drawable.ic_launcher);
		UMImage urlImage = new UMImage(ActivitySellerDetail.this,
				"http://www.umeng.com/images/pic/social/integrated_3.png");
		// UMImage resImage = new UMImage(getActivity(), R.drawable.icon);
		// UMEmoji emoji = new UMEmoji(getActivity(),
		// "http://www.pc6.com/uploadimages/2010214917283624.gif");
		// UMEmoji emoji = new UMEmoji(getActivity(),
		// "/storage/sdcard0/emoji.gif");

		WeiXinShareContent weixinContent = new WeiXinShareContent();
		weixinContent
				.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-微信。http://www.umeng.com/social");
		weixinContent.setTitle("友盟社会化分享组件-微信");
		weixinContent.setTargetUrl("http://www.umeng.com/social");
		weixinContent.setShareMedia(urlImage);
		mController.setShareMedia(weixinContent);

		// 设置朋友圈分享的内容
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia
				.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-朋友圈。http://www.umeng.com/social");
		circleMedia.setTitle("友盟社会化分享组件-朋友圈");
		circleMedia.setShareMedia(urlImage);
		// circleMedia.setShareMedia(uMusic);
		// circleMedia.setShareMedia(video);
		circleMedia.setTargetUrl("http://www.umeng.com/social");
		mController.setShareMedia(circleMedia);

		// 设置renren分享内容
		RenrenShareContent renrenShareContent = new RenrenShareContent();
		renrenShareContent.setShareContent("人人分享内容");
		UMImage image = new UMImage(ActivitySellerDetail.this,
				BitmapFactory.decodeResource(getResources(),
						R.drawable.ic_launcher));
		image.setTitle("thumb title");
		image.setThumb("http://www.umeng.com/images/pic/social/integrated_3.png");
		renrenShareContent.setShareImage(image);
		renrenShareContent.setAppWebSite("http://www.umeng.com/social");
		mController.setShareMedia(renrenShareContent);

		UMImage qzoneImage = new UMImage(ActivitySellerDetail.this,
				"http://www.umeng.com/images/pic/social/integrated_3.png");
		qzoneImage
				.setTargetUrl("http://www.umeng.com/images/pic/social/integrated_3.png");

		// 设置QQ空间分享内容
		QZoneShareContent qzone = new QZoneShareContent();
		qzone.setShareContent("share test");
		qzone.setTargetUrl("http://www.umeng.com");
		qzone.setTitle("QZone title");
		qzone.setShareMedia(urlImage);
		// qzone.setShareMedia(uMusic);
		mController.setShareMedia(qzone);
		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能 -- QQ");
		qqShareContent.setTitle("hello, title");
		qqShareContent.setShareMedia(image);
		qqShareContent.setTargetUrl("http://www.umeng.com/social");
		mController.setShareMedia(qqShareContent);
		TencentWbShareContent tencent = new TencentWbShareContent();
		tencent.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-腾讯微博。http://www.umeng.com/social");
		// 设置tencent分享内容
		mController.setShareMedia(tencent);

		SinaShareContent sinaContent = new SinaShareContent();
		sinaContent
				.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-新浪微博。http://www.umeng.com/social");
		sinaContent.setShareImage(new UMImage(ActivitySellerDetail.this,
				R.drawable.ic_launcher));
		mController.setShareMedia(sinaContent);
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
				ActivitySellerDetail.this, "201874",
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
				ActivitySellerDetail.this, appId, appKey);
		qqSsoHandler.setTargetUrl("http://www.baidu.com");
		qqSsoHandler.addToSocialSDK();

		// 添加QZone平台
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(
				ActivitySellerDetail.this, appId, appKey);
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
		UMWXHandler wxHandler = new UMWXHandler(ActivitySellerDetail.this,
				appId, appSecret);
		wxHandler.addToSocialSDK();

		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(
				ActivitySellerDetail.this, appId, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	private void init() {
		mReadHistory = getSharedPreferences("read_history",
				Context.MODE_PRIVATE);
		mButtonFloat_pay = (ButtonFloat) this.findViewById(R.id.go_to_pay);
		mViewGroup_point = (ViewGroup) this.findViewById(R.id.view_point);
		mTextView_title = (TextView) this.findViewById(R.id.seller_title);
		mTextView_name = (TextView) this.findViewById(R.id.seller_name);
		// 折扣字体
		mTextView_discount = (TextView) this.findViewById(R.id.seller_discount);
		Typeface face = Typeface.createFromAsset(getAssets(),
				"fonts/discount_font.TTF");
		mTextView_discount.setTypeface(face);
		mTextView_address = (TextView) this.findViewById(R.id.seller_address);
		mTextView_num = (TextView) this.findViewById(R.id.tv_seller_nummber);
		mTextView_detail = (TextView) this.findViewById(R.id.seller_detail);
		mTextView_feature = (TextView) this.findViewById(R.id.seller_feature);
		mTextView_environment = (TextView) this
				.findViewById(R.id.seller_envirment);
		mTextView_tips = (TextView) this.findViewById(R.id.seller_tips);
		mLinearLayout_contact_seller = (LinearLayout) this
				.findViewById(R.id.contact_seller);
		mLinearLayout_contact_seller.setOnClickListener(mClickListener);
		mImageView_connect = (ImageView) this
				.findViewById(R.id.list_detail_to_connect);
		mImageView_connect.setOnClickListener(mClickListener);
		mLinearLayout_no_net = (LinearLayout) this
				.findViewById(R.id.list_detail_no_net);
		mButtonIcon_back = (ButtonIcon) this.findViewById(R.id.detail_back);
		mButtonIcon_share = (ButtonIcon) this.findViewById(R.id.goto_share);
		mButtonIcon_go = (ButtonIcon) this.findViewById(R.id.goto_there);
		mSellerBussiness = new SellerBussinessImp(this);
		flag=getIntent().getBooleanExtra("is_flag", false);
		// 商家坐标
		mlat = getIntent().getStringExtra("lat");
		mlng = getIntent().getStringExtra("lng");
		// 定位坐标
		mNowLat = getIntent().getDoubleExtra("nowlat", 0);
		mNowLng = getIntent().getDoubleExtra("nowlng", 0);
		if (isNetworkConnected()) {
			bid = getIntent().getStringExtra("id");
			mDiscount = getIntent().getStringExtra("discount");
			mTextView_discount.setText(mDiscount + "折");
			String title = getIntent().getStringExtra("title");
			mTextView_name.setText(title);
			if (title.length() > 8) {
				title = title.substring(0, 7) + "...";
			}
			mTextView_title.setText(title);
			new TaskDetail().execute(bid);
		} else {
			mLinearLayout_no_net.setVisibility(View.VISIBLE);
		}
		mButtonIcon_back.setOnClickListener(mClickListener);
		mButtonIcon_share.setOnClickListener(mClickListener);
		mButtonIcon_go.setOnClickListener(mClickListener);
		mButtonFloat_pay.setOnClickListener(mClickListener);
		mViewPager_seller_img = (ViewPager) this
				.findViewById(R.id.seller_detail_img);
		mdata = new ArrayList<String>();
		mViewPager_seller_img.setAdapter(mFragmentPagerAdapter);
		addPointView();
		mViewPager_seller_img
				.addOnPageChangeListener(new MyPageChangeListener());
	}

	private void addPointView() {
		views = new ImageView[mdata.size()];
		mViewGroup_point.removeAllViews();
		for (int i = 0; i < mdata.size(); i++) {
			ImageView v = new ImageView(this);
			views[i] = v;
			if (i == 0) {
				views[i].setBackgroundResource(R.drawable.seller_detail_view_shape2);
			} else {
				views[i].setBackgroundResource(R.drawable.seller_detail_view_shape1);
			}
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.leftMargin = 15;
			params.height = 15;
			params.width = 15;
			v.setScaleType(ScaleType.CENTER_CROP);
			v.setLayoutParams(params);
			mViewGroup_point.addView(views[i]);
		}
	}

	// view原点切换
	private void changeView(int position) {
		for (int i = 0; i < views.length; i++) {
			if (i == position) {
				views[i].setBackgroundResource(R.drawable.seller_detail_view_shape2);
			} else {
				views[i].setBackgroundResource(R.drawable.seller_detail_view_shape1);
			}
		}
	}

	// viewpager适配器
	// 页面改变监听器  
	private class MyPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int position) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			changeView(position);
		}
	}

	// viewpager适配器
	private FragmentPagerAdapter mFragmentPagerAdapter = new FragmentPagerAdapter(
			getSupportFragmentManager()) {

		@Override
		public int getCount() {
			return mdata.size();
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			switch (position) {
			case 0:
				fragment = new FragmentSellerImg(mdata.get(position));
				break;
			case 1:
				fragment = new FragmentSellerImg(mdata.get(position));
				break;
			case 2:
				fragment = new FragmentSellerImg(mdata.get(position));
				break;
			case 3:
				fragment = new FragmentSellerImg(mdata.get(position));
				break;
			case 4:
				fragment = new FragmentSellerImg(mdata.get(position));
				break;
			}
			return fragment;
		}
	};

	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.detail_back:
				finish();
				overridePendingTransition(R.anim.push_right_out,
						R.anim.push_right_in);
				break;
			// 分享
			case R.id.goto_share:
				// 是否只有已登录用户才能打开分享选择页
				mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
						SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ,
						SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA,
						SHARE_MEDIA.TENCENT, SHARE_MEDIA.DOUBAN,
						SHARE_MEDIA.RENREN);
				mController.openShare(ActivitySellerDetail.this, false);
				break;
			// 重新加载数据
			case R.id.list_detail_to_connect:
				if (isNetworkConnected()) {
					mLinearLayout_no_net.setVisibility(View.GONE);
					String id = getIntent().getStringExtra("id");
					new TaskDetail().execute(id);
				}
				break;
			// 联系商家
			case R.id.contact_seller:
				String num = mTextView_num.getText().toString();
				if (!StrTools.isNull(num)) {
					Intent intent = new Intent(Intent.ACTION_DIAL,
							Uri.parse("tel:" + num));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
				break;
			// 去这里
			case R.id.goto_there:
				Intent i = new Intent(ActivitySellerDetail.this,
						ActivityRoutePlan.class);
				i.putExtra("sLat", mNowLat);
				i.putExtra("sLng", mNowLng);
				i.putExtra("eLat", Double.parseDouble(mlat));
				i.putExtra("eLng", Double.parseDouble(mlng));
				Toast.makeText(ActivitySellerDetail.this,
						"sa" + mNowLat + "-sn+" + mNowLng, Toast.LENGTH_SHORT)
						.show();
				startActivity(i);
				break;
			// 支付
			case R.id.go_to_pay:
				Intent intent = new Intent(ActivitySellerDetail.this,
						ActivityPay.class);
				startActivity(intent);
				break;
			}
		}
	};

	public boolean isNetworkConnected() {
		// 判断网络是否连接
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		return mNetworkInfo != null && mNetworkInfo.isAvailable();
	}

	// 获得详细详细详细
	private class TaskDetail extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String detailStr = null;
			try {
				mSellerDetail = mSellerBussiness.getSellerDetailInfo(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return detailStr;
		}

		@Override
		protected void onPostExecute(String result) {
			if (mSellerDetail != null) {
				mTextView_detail.setText(mSellerDetail.getDetailsDesc());
				mTextView_environment.setText(mSellerDetail.getEnvironment());
				mTextView_feature.setText(mSellerDetail.getFeature());
				mTextView_tips.setText(mSellerDetail.getTips());
				mTextView_address.setText("地址:" + mSellerDetail.getAddress());
				mTextView_num.setText(mSellerDetail.getContactPhoneNum());
				// 图片路径
				String path = mSellerDetail.getCarouselImgs();
				if (!StrTools.isNull(path)) {
					String[] paths = path.split(",");
					for (String string : paths) {
						mdata.add(string);
					}
				}
			} else {
				Toast.makeText(ActivitySellerDetail.this, "网络请求出错！",
						Toast.LENGTH_SHORT).show();
			}
			mFragmentPagerAdapter.notifyDataSetChanged();
			addPointView();
		}

		@Override
		protected void onPreExecute() {
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mController.getConfig().cleanListeners();
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
