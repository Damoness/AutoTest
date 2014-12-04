package com.hw.autotest.sleepwake;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.hw.autotest.R;

/**
 * Activity
 *		屏幕休眠唤醒
 * 
 * @author wangwei
 * @date 2014-5-22
 */
public class SleepWakeActivity extends Activity implements OnClickListener {

	DevicePolicyManager policyManager;
	ComponentName componentName;
	
	boolean active;
	int input;
	
	private Button btnStart, btnStop, btnActive;
	private EditText etInput;
	
	Timer timer = new Timer();
	TimerTask task;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sleepwake);
		
		init();
		
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		btnActive = (Button) findViewById(R.id.btnActive);
		btnActive.setOnClickListener(this);
		
		btnStart = (Button) findViewById(R.id.btnStart);
		btnStart.setOnClickListener(this);
		btnStart.setEnabled(false);
		
		btnStop = (Button) findViewById(R.id.btnStop);
		btnStop.setOnClickListener(this);
		btnStop.setEnabled(false);
		
		etInput = (EditText) findViewById(R.id.etInput);
		
		policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		componentName = new ComponentName(this, AdminReceiver.class);
		
		// 管理设备管理状态
		active = policyManager.isAdminActive(componentName);
		if(active){
			btnActive.setText("取消设备管理");
			btnStart.setEnabled(true);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		active = policyManager.isAdminActive(componentName);
		if(active){
			btnStart.setEnabled(true);
			btnActive.setText("取消设备管理");
		}else{
			// System.exit(0);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 监听点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnStart:
			click_start();
			break;
		case R.id.btnStop:
			click_stop();
			break;
		case R.id.btnActive:
			click_active();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 点击【btnStart】
	 */
	private void click_start(){
		
		btnStart.setEnabled(false);
		btnStop.setEnabled(true);
		btnActive.setEnabled(false);
		
		try{
			input = Integer.parseInt(etInput.getText().toString());
		}catch (Exception e) {
			input = 3000;
			e.printStackTrace();
		}
		
		// 休眠唤醒定时任务
		task = new TimerTask() {
			@Override
			public void run() {
				lockScreen();
				try {
					Thread.sleep(input);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// 发送唤醒广播
				sendBroadcast(new Intent("android.my.wakeLock"));
			}
		};
		timer.schedule(task, input * 2, input * 2);
	}
	
	/**
	 * 点击【btnStop】
	 */
	private void click_stop(){
		btnStart.setEnabled(true);
		btnStop.setEnabled(false);
		btnActive.setEnabled(true);
		
		task.cancel();
	}
	
	/**
	 * 按钮点击事件
	 * 		激活设备管理/取消设备管理  
	 */
	private void click_active(){
		active = policyManager.isAdminActive(componentName);
		if(!active){
			activeManager();
		}else{
			removeActive();
		}
	}
	
	/**
	 * 休眠
	 */
	private void lockScreen(){
		policyManager.lockNow();
	}
	
	/**
	 * 激活设备管理 
	 */
	public void activeManager(){
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "xxx xxx xxx");
		startActivityForResult(intent, 0);
	}
	
	/**
	 * 取消设备管理 
	 */
	private void removeActive(){
		policyManager.removeActiveAdmin(componentName);
		btnActive.setText("激活设备管理");
		btnStart.setEnabled(false);
	}
}
