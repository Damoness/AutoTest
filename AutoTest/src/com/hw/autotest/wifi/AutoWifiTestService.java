package com.hw.autotest.wifi;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class AutoWifiTestService extends Service {

	private static final int NOTIFY_ME_ID =44;
	private WifiAdmin mWifiAdmin;
	private static final String TAG = AutoWifiTestService.class.getSimpleName();
	
	private boolean running ;
	private Thread wifiThread;
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private void waitSeconds(int i){
		try {
			Thread.sleep(i*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		running = false;
		super.onDestroy();
		Log.i(TAG, "onDestroy");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		mWifiAdmin = new WifiAdmin(this);
		
		final int num = intent.getIntExtra("num", 5);
		final String ssid = intent.getStringExtra("ssid");
		final String password = intent.getStringExtra("password");
		final int protocol = intent.getIntExtra("protocol", 1);
		

		
		mWifiAdmin.openWifi();
		running = true;
		
		wifiThread = new Thread(){
			
			@Override
			public void run(){
				
				int success = 0;
				int failure = 0;
				
				for(int i=0;i<num;i++){
					
					if(!running) return;
					
					mWifiAdmin.disconnectCurrentWifi();
					waitSeconds(2);
					Log.i(TAG, ""+i+":"+mWifiAdmin.checkState()+"---"+mWifiAdmin.getWifiInfo());
					waitSeconds(5);
					
					mWifiAdmin.startScan();
					waitSeconds(3);
					WifiConfiguration wc = mWifiAdmin.CreateWifiInfo(ssid, password, protocol);
					if(mWifiAdmin.addNetWork(wc)) success++;
					else failure++;
					
					waitSeconds(5);
					Log.i(TAG, ""+i+":"+mWifiAdmin.checkState()+"---"+mWifiAdmin.getWifiInfo());
					waitSeconds(5);
					
				}
				
				
				String title = "Wifi測試結果";
				
				String result = "测试次数:"+num+"\n"
								+"成功："+success+"\n"
								+"失败："+failure+"\n";
						
				notifyMe(title,result);
			}
		};
		
	
		wifiThread.start();
		
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	public void notifyMe(String title,String text) {
		NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		
		PendingIntent i = PendingIntent.getActivity(this, 0, new Intent(this,
				WifiTestActivity.class), 0);
		
		Notification note = new Notification.Builder(this).setContentTitle(title).setContentText(text).setSmallIcon(android.R.drawable.btn_star).setContentIntent(i).getNotification();


		mgr.notify(NOTIFY_ME_ID, note);
	}
	

}
