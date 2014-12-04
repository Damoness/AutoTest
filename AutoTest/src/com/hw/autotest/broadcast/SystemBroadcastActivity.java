package com.hw.autotest.broadcast;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.hw.autotest.R;
import com.hw.autotest.wifi.WifiTestActivity;

public class SystemBroadcastActivity extends Activity {

	private Button send ;
	private BroadcastReceiverUtils receiver;
	
	private ListView listView;
	
	private String[] str = {
			Intent.ACTION_BATTERY_CHANGED,
			Intent.ACTION_TIME_TICK,
			Intent.ACTION_POWER_CONNECTED,
			Intent.ACTION_POWER_DISCONNECTED,
			Intent.ACTION_BATTERY_LOW,
			Intent.ACTION_BATTERY_OKAY,
			Intent.ACTION_CLOSE_SYSTEM_DIALOGS,
			Intent.ACTION_DATE_CHANGED,
			Intent.ACTION_DEVICE_STORAGE_LOW,
			Intent.ACTION_DEVICE_STORAGE_OK,
			Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE,
			Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE,
			Intent.ACTION_INPUT_METHOD_CHANGED,
			Intent.ACTION_LOCALE_CHANGED,
			Intent.ACTION_MANAGE_PACKAGE_STORAGE,
			Intent.ACTION_MEDIA_BAD_REMOVAL,
			Intent.ACTION_MEDIA_BUTTON,
			Intent.ACTION_MEDIA_CHECKING,
			Intent.ACTION_MEDIA_EJECT,
			Intent.ACTION_MEDIA_MOUNTED,
			Intent.ACTION_MEDIA_REMOVED,
			Intent.ACTION_MEDIA_SCANNER_FINISHED,
			Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
			Intent.ACTION_MEDIA_SCANNER_STARTED,
			Intent.ACTION_MEDIA_SHARED,
			Intent.ACTION_MEDIA_UNMOUNTED,
			Intent.ACTION_PACKAGE_ADDED,
			Intent.ACTION_PACKAGE_CHANGED,
			Intent.ACTION_PACKAGE_REMOVED,
			Intent.ACTION_PACKAGE_REPLACED,
			Intent.ACTION_PACKAGE_RESTARTED,
			Intent.ACTION_REBOOT,
			Intent.ACTION_SCREEN_OFF,
			Intent.ACTION_SCREEN_ON,
			Intent.ACTION_TIMEZONE_CHANGED,
			Intent.ACTION_TIME_CHANGED,
			Intent.ACTION_UID_REMOVED,
			Intent.ACTION_UMS_CONNECTED,
			Intent.ACTION_UMS_DISCONNECTED,
			Intent.ACTION_WALLPAPER_CHANGED
			};
	
	private boolean[] flags = new boolean[str.length];
	
	private int registeredNo =0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_broadcast);
		
		receiver = new BroadcastReceiverUtils();
		
		listView = (ListView) findViewById(R.id.list);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_checked, str);
		
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				CheckedTextView checked = (CheckedTextView) view;

				if(checked.isChecked()){
					
					SystemBroadcastActivity.this.unregisterReceiver(receiver);
					
					checked.setChecked(false);
					flags[position] = false;
					for(int i= 0;i<flags.length;i++){
						if(flags[i]) {
							IntentFilter filter = new IntentFilter(str[i]);
							SystemBroadcastActivity.this.registerReceiver(receiver, filter);
						}
					}
					 
				}else{
					checked.setChecked(true);
					flags[position] = true;					
					IntentFilter filter = new IntentFilter(str[position]);
					SystemBroadcastActivity.this.registerReceiver(receiver, filter);
				}
				
			}
		});
	
		
	}

	
	public void  notify(int id,String title,String text){
		
		NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
	
		PendingIntent i = PendingIntent.getActivity(this, 0, new Intent(this,
				SystemBroadcastActivity.class), 0);
		
		Notification note = new Notification.Builder(this)
				.setContentTitle(title).setContentText(text)
				.setSmallIcon(android.R.drawable.ic_menu_report_image)
				.setContentIntent(i)
				.getNotification();

		mgr.notify(id, note);
	}
	
	
	public int getId(String s){
		
		for(int i=0;i<str.length;i++){
			
			if(s.equals(str[i]))  return 4000+i;
			
		}
		
		return 10000;
	}
	
	
	public class BroadcastReceiverUtils extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Toast.makeText(context,  intent.getAction(), Toast.LENGTH_SHORT).show();
			
			
			String title = "拦截到系统广播";
			String text = intent.getAction();
			int id = getId(text);
			SystemBroadcastActivity.this.notify(id, title, text);
		}
		
	}
	
	
	
	public class BroadcastReceiverUtil extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
            String batteryInfo="";  
            int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);      //获得当前电量   
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);         //获得总电量   
            int level = -1;   
            if (rawlevel >= 0 && scale > 0) {   
                level = (rawlevel * 100) / scale;   
            }   
            batteryInfo="当前电量: " + String.valueOf(level) + "%";   
            Toast.makeText(context, batteryInfo, Toast.LENGTH_SHORT).show();  
		}

	}
	
	
	
	@Override
	public void onDestroy(){
		
/*		if(receiver!=null)
    		super.unregisterReceiver(receiver); */
		
		super.onDestroy();
	}
	

}
