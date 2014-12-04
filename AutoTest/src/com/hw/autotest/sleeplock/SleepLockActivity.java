package com.hw.autotest.sleeplock;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.hw.autotest.R;

/**
 * Activity 
 * 		ÐÝÃßËø
 * 
 * @author wangwei
 * @date 2014-6-12
 */
public class SleepLockActivity extends Activity {

	private CheckBox ckLock;
	
	PowerManager pm;
	private static PowerManager.WakeLock wl;
	
	private static boolean isAcquire = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sleeplock);
		init();
	}

	/**
	 * ³õÊ¼»¯
	 */
	private void init(){
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		
		if(wl == null){
			wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "tag");
		}
		ckLock = (CheckBox) findViewById(R.id.ckLock);
		// ³õÊ¼ckLock¹´Ñ¡×´Ì¬
		if(isAcquire){
			ckLock.setChecked(true);
		}else{
			ckLock.setChecked(false);
		}
		
		ckLock.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					wl.acquire();
					isAcquire = true;
				}else{
					wl.release();
					isAcquire = false;
				}
			}
		});
	}
	
}
