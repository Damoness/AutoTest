package com.hw.autotest.wifi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AutoApTestService extends Service{

	private final String[] wifiApStates = {
			"WIFI_AP_STATE_DISABLING",
			"WIFI_AP_STATE_DISABLED",
			"WIFI_AP_STATE_ENABLING",
			"WIFI_AP_STATE_ENABLED",
			"WIFI_AP_STATE_FAILED"
	};
	
	private static final int NOTIFY_ME_ID =45;
	private WifiAdmin mWifiAdmin;
	private static final String TAG = AutoApTestService.class.getSimpleName();
	private boolean running;
	private Thread apThread;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mWifiAdmin = new WifiAdmin(this);
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
    public void onDestroy()
    {
        running = false;
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        
    }
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		final int num = intent.getIntExtra("num", 5);
		
		running = true;
		
		apThread = new Thread(){
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				
				
				
				Log.i(TAG,num+"");
				
				
				int success = 0;
				int failure = 0;
				
				mWifiAdmin.closeWifi();
				
				for(int i=0;i<num;i++){
					
					if(!running) return;
					
					mWifiAdmin.closeWifiAp();
					waitSeconds(2);
					Log.i(TAG, ""+i+":"+mWifiAdmin.getWifiApState());
					waitSeconds(5);

					mWifiAdmin.startWifiAp();
					
					waitSeconds(5);	
					if (mWifiAdmin.getWifiApState().endsWith(wifiApStates[3]))
						success++;
					else
						failure++;
					Log.i(TAG, ""+i+":"+mWifiAdmin.getWifiApState());
					waitSeconds(5);
					
					
					
				}
				
				
				String title = "Ap測試結果";
				
				String text = "测试次数:"+num+"\n"
								+"成功："+success+"\n"
								+"失败："+failure+"\n";
				
				notifyMe(title, text);
			}
		};
		
		
		apThread.start();
		
		
		
		return super.onStartCommand(intent, flags, startId);
		
	}
	
	

	
	public void notifyMe(String title,String text) {
		NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		
		PendingIntent i = PendingIntent.getActivity(this, 0, new Intent(this,
				WifiTestActivity.class), 0);
		
		Notification note = new Notification.Builder(this).setContentTitle(title).setContentText(text).setSmallIcon(android.R.drawable.btn_radio).setContentIntent(i).getNotification();


		mgr.notify(NOTIFY_ME_ID, note);
	}
	

}
