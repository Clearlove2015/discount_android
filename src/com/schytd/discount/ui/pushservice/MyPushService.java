package com.schytd.discount.ui.pushservice;

import com.igexin.sdk.PushManager;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class MyPushService extends Service{
	private static final String ACTIONNAME_STRING = "com.igexin.sdk.action.i56dpx3SeR9uo3ZsY80Kc1";
	private PushDemoReceiver pushDemoReceiver=new PushDemoReceiver();
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		PushManager.getInstance().initialize(this.getApplicationContext());
		IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTIONNAME_STRING);
        registerReceiver(pushDemoReceiver, intentFilter);
		return START_STICKY;
	}
	@Override
	public void onDestroy() {
		unregisterReceiver(pushDemoReceiver);
		super.onDestroy();
		startService(new Intent(getApplicationContext(), MyPushService.class));
	}
}
