package com.schytd.discount.ui.pushservice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.schytd.discount.dao.PushDao;
import com.schytd.discount.dao.impl.PushDaoImp;
import com.schytd.discount.enties.PushItem;
import com.schytd.discount.tools.StrTools;
import com.schytd.discount_android.R;

public class PushDemoReceiver extends BroadcastReceiver {
	/**
	 * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView ==
	 * null)
	 */
	public static StringBuilder payloadData = new StringBuilder();
	private Context context;
	private SharedPreferences sp_msg, sp_cid;
	// 存入数据库
	private PushDao mPushDao;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		mPushDao = new PushDaoImp(context);
		sp_msg = context.getSharedPreferences("msg_on", Context.MODE_PRIVATE);
		Bundle bundle = intent.getExtras();
		Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));
		sp_cid = context.getSharedPreferences("user_clientid",
				Context.MODE_PRIVATE);
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {
		case PushConsts.GET_MSG_DATA:
			// 获取透传数据
			// String appid = bundle.getString("appid");
			byte[] payload = bundle.getByteArray("payload");

			String taskid = bundle.getString("taskid");
			String messageid = bundle.getString("messageid");
			// 获取透传（payload）数据
			if (payload != null) {
				String data = new String(payload);
				save2db(data);// 存入数据库
				// TODO:接收处理透传（payload）数据
				// 用户是否愿意接受消息
				if (sp_msg.getBoolean("isOn", true)) {
					showNotifiCation(data);
				}
			}
			// smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
			PushManager.getInstance().sendFeedbackMessage(context, taskid,
					messageid, 90001);
			if (payload != null) {
				String data = new String(payload);
				payloadData.append(data);
				payloadData.append("\n");
			}
			break;
		case PushConsts.GET_CLIENTID:
			// 获取ClientID(CID)
			// 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
			String cid = bundle.getString("clientid");
			Log.d("推送广播", "clientid = " + cid);

			// 将cid存入SharedPreferences中
			Editor editor = sp_cid.edit();
			editor.putString("clientid", cid);
			editor.commit();
			break;
		case PushConsts.THIRDPART_FEEDBACK:
			/*
			 * String appid = bundle.getString("appid"); String taskid =
			 * bundle.getString("taskid"); String actionid =
			 * bundle.getString("actionid"); String result =
			 * bundle.getString("result"); long timestamp =
			 * bundle.getLong("timestamp");
			 * 
			 * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo",
			 * "taskid = " + taskid); Log.d("GetuiSdkDemo", "actionid = " +
			 * actionid); Log.d("GetuiSdkDemo", "result = " + result);
			 * Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
			 */
			break;

		default:
			break;
		}
	}

	// 将值传入到详情页 ，请求详细信息
	String title = null;
	String id = null;
	String abstractInfo = null;
	String imgpath=null;

	private void save2db(String data) {
		try {
			JSONObject jsonObject = new JSONObject(data);
			title = jsonObject.getString("title");
			id = jsonObject.getString("id");
			abstractInfo = jsonObject.getString("abstractInfo");
			imgpath = jsonObject.getString("titlePicUrl");
			
			// 存入数据库
			List<PushItem> msg = new ArrayList<PushItem>();
			PushItem item = new PushItem();
			item.setId(id);
			item.setImgs(imgpath);
			item.setTitle(title);
			item.setType("1");
			item.setDate(System.currentTimeMillis()+"");
			msg.add(item);
			mPushDao.insertPush(msg);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showNotifiCation(String data) {
		// 接到透传消息后解析透传 title id abstractInfo
		// 通知管理器
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Builder builder = new Notification.Builder(context);
		// <!-- 设置点击回到程序 -->
		Intent[] intents = new Intent[2];
		intents[0] = Intent.makeRestartActivityTask(new ComponentName(context,
				com.schytd.discount.ui.MainActivity.class));
		intents[1] = new Intent(context,
				com.schytd.discount.ui.ActivityMessageDetail.class);

		if (!StrTools.isNull(title) || !StrTools.isNull(id)
				|| !StrTools.isNull(abstractInfo)) {
			intents[1].putExtra("id", id);
		}
		PendingIntent pendingIntent = PendingIntent.getActivities(context, 0,
				intents, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(pendingIntent)
		// 提醒的message
				.setTicker("data")
				// 通知的标题
				.setContentTitle("标题")
				// 通知的正文（内容）
				.setContentText(data)
				// 设置状态栏的图标，注意，还有大的图标
				.setSmallIcon(R.drawable.app_icon)
				// 设置系统震动
				.setDefaults(Notification.DEFAULT_VIBRATE)
				// 设置系统声音
				.setDefaults(Notification.DEFAULT_SOUND)
				// 全部使用系统默认
				// .setDefaults(Notification.DEFAULT_ALL)
				// 设置时间
				.setWhen(System.currentTimeMillis())
				// 设置闪光灯
				.setLights(0xFF0000, 200, 200)
				// 设置通知是否可以滑动移除
				.setAutoCancel(true);

		// API 16以上
		// Notification notification = builder.build();
		// API 16以下
		Notification notification = builder.getNotification();

		/**
		 * id 是一个随机的整数，它决定了通知是新的还是覆盖原来的 如果id值每次都不一样，那么产生多个通知，罗列到通知栏上
		 * 如果id值一样，后面的通知会覆盖前面的通知（一个）
		 */
		notificationManager.notify(Integer.parseInt(id), notification);
	}
}
