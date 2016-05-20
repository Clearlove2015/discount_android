package com.schytd.discount.ui;
import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.schytd.discount.bussiness.UpdateBussiness;
import com.schytd.discount.bussiness.impl.UpdateBussinessImpl;
import com.schytd.discount.tools.DataCleanManager;
import com.schytd.discount.enties.ConstantData;
import com.schytd.discount.ui.dialog.PromptDialog;
import com.schytd.discount.ui.switchbtn.togglebutton.ToggleButton;
import com.schytd.discount_android.R;

public class ActivitySetting extends Activity {
	private long downloadId;

	// 消息设置
	private DownloadReceiver receiver;
	private DownloadManager downloadManager;

	private UpdateBussiness mUpdateBussiness = new UpdateBussinessImpl();

	private UpdateAsyncTask updateAsyncTask;
	private ToggleButton mToggleButton;
	// 保存消息设置
	private SharedPreferences sp_setting;
	private TextView mTextView_CacheSize;

	private void init() {
		mTextView_CacheSize = (TextView) this.findViewById(R.id.cache_size);
		try {
			mTextView_CacheSize.setText(DataCleanManager
					.getTotalCacheSize(this));
		} catch (Exception e) {
			e.printStackTrace();
		}
		mToggleButton = (ToggleButton) this
				.findViewById(R.id.setting_push_toggle);
		sp_setting = getSharedPreferences("msg_on", Context.MODE_PRIVATE);
		boolean toggle = sp_setting.getBoolean("isOn", true);
		if (toggle) {
			mToggleButton.setToggleOn();
		} else {
			mToggleButton.setToggleOff();
		}

		receiver = new DownloadReceiver();
		// 下载完毕要接收通知
		registerReceiver(receiver, new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		// 点击通知栏要接收通知
		registerReceiver(receiver, new IntentFilter(
				DownloadManager.ACTION_NOTIFICATION_CLICKED));
		// 从系统服务中获得DownloadManager对象
		downloadManager = (DownloadManager) getSystemService(Service.DOWNLOAD_SERVICE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		init();
	}

	public void onClick(View view) {
		switch (view.getId()) {
		// 返回
		case R.id.setting_back:
			finish();
			break;
		case R.id.setting_push_toggle:
			break;
		// 推送设置
		case R.id.setting_push:
			if (mToggleButton.isToggle()) {
				Editor editor = sp_setting.edit();
				// 消息设置
				editor.putBoolean("isOn", false);
				editor.commit();
				mToggleButton.setToggleOff();
			} else {
				Editor editor = sp_setting.edit();
				// 消息设置
				editor.putBoolean("isOn", true);
				editor.commit();
				mToggleButton.setToggleOn();
			}
			Toast.makeText(ActivitySetting.this,
					sp_setting.getBoolean("isOn", true) + "", Toast.LENGTH_SHORT)
					.show();
			break;
		// 意见反馈
		case R.id.setting_advise:
			startActivity(new Intent(ActivitySetting.this,
					ActivityFeedBack.class));
			break;
		// 检查更新
		case R.id.setting_update:
			// String urlPath = "http://192.168.1.118/webfs/app/1.jpg";
			// download_file(urlPath);
			update_version();

			break;
		// 清除缓存
		case R.id.setting_clear:
			DataCleanManager.clearAllCache(this);
			Toast.makeText(ActivitySetting.this, "缓存已清空", Toast.LENGTH_SHORT)
					.show();
			try {
				mTextView_CacheSize.setText(DataCleanManager
						.getTotalCacheSize(this));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		// 关于我们
		case R.id.setting_aboutus:
			startActivity(new Intent(ActivitySetting.this,
					ActivityAboutUs.class));
			break;
		}
	}

	public void update_version() {
		Log.d("ActivitySetting method", "update_version called...");
		updateAsyncTask = new UpdateAsyncTask();
		updateAsyncTask.execute();
	}

	private class UpdateAsyncTask extends AsyncTask<Void, Void, List<String>> {

		@Override
		protected List<String> doInBackground(Void... arg0) {
			try {
				List<String> versioninfo = mUpdateBussiness.update();
				return versioninfo;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<String> versioninfo) {
			if (versioninfo != null) {
				String code = versioninfo.get(0);
				if (code.equals("0")) {
					String software_version = ConstantData.SOFT_VERSION;
					String version = versioninfo.get(1);
					Log.d("++++++", "software_version = " + software_version);
					Log.d("++++++", "version = " + version);
					if (software_version.equals(version)) {
						Toast.makeText(ActivitySetting.this, "已经是最新版本",
								Toast.LENGTH_SHORT).show();
					} else {
						String download_url = versioninfo.get(2);
						Log.d(">>>>>>>>>>", "download_path = " + download_url);
						showUpdatDialog(version, download_url);
					}
				} else {
					Toast.makeText(ActivitySetting.this, "没有检查到新版本",
							Toast.LENGTH_SHORT).show();
					Log.d("更新返回code码", "code = " + code);
				}
			} else {
				Log.d("ActivitySetting", "服务器无响应。versioninfo为空");
				Toast.makeText(ActivitySetting.this, "服务器无响应",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class DownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			long completedDownloadId = intent.getLongExtra(
					DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			if (downloadId == completedDownloadId) {
				Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
				// 下载完成自动弹出安装
				Intent intent1 = new Intent(Intent.ACTION_VIEW);
				intent1.setDataAndType(Uri.fromFile(new File(Environment
						.getExternalStorageDirectory()
						+ File.separator
						+ "schytd" + File.separator + "update.apk")),
						"application/vnd.android.package-archive");
				ActivitySetting.this.startActivity(intent1);
			}
		}
	}

	public void download_file(String urlPath) {
		Log.d("method", "download_file called...");
		// 构造下载指定目录
		File downloadDir = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "schytd");
		if (!downloadDir.exists()) {
			downloadDir.mkdirs();
		}
		// 文件名
		String fileName = "update.apk";
		// 构建请求
		Uri uri = Uri.parse(urlPath);
		DownloadManager.Request request = new DownloadManager.Request(uri);
		// 运行在wifi 或者手机网络情况下都下载
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
				| DownloadManager.Request.NETWORK_MOBILE);
		// 是否允许在漫游状态下载
		request.setAllowedOverRoaming(false);
		// 设置下载通知栏上的标题名字，如果不写，默认以下载文件名显示
		request.setTitle("下载apk更新文件...");
		// 设置下载通知栏上的描述信息（相当于副标题）
		request.setDescription("惠消费");
		// 指定下载的目录和文件
		// 第一参数只能是目录名字（不能是downloadDir.getAbsolutePath()）
		// 第二参数只能是文件名（downloadFile.getAbsolutePath()）
		request.setDestinationInExternalPublicDir("schytd", fileName);
		//默认download目录
		// request.setDestinationInExternalPublicDir(
		// Environment.DIRECTORY_DOWNLOADS, fileName);

		// 设置通知栏显示信息
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		// 此次下载的随机id值
		downloadId = downloadManager.enqueue(request);
	}

	/**
	 * 更新对话框
	 */
	public void showUpdatDialog(String version, final String download_url) {
		new PromptDialog.Builder(ActivitySetting.this)
				.setMessage("最新版本：" + version + "\n\n是否更新？", null)
				.setTitle("发现新版本")
				.setButton1("立即更新", new PromptDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog, int which) {
						// 开始下载
						try {
							download_file(download_url);
						} catch (Exception e) {
							e.printStackTrace();
						}
						dialog.dismiss();
					}
				}).setButton2("以后再说", new PromptDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

}
