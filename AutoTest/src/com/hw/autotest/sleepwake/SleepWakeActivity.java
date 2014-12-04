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
 *		��Ļ���߻���
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
	 * ��ʼ��
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
		
		// �����豸����״̬
		active = policyManager.isAdminActive(componentName);
		if(active){
			btnActive.setText("ȡ���豸����");
			btnStart.setEnabled(true);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		active = policyManager.isAdminActive(componentName);
		if(active){
			btnStart.setEnabled(true);
			btnActive.setText("ȡ���豸����");
		}else{
			// System.exit(0);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * ��������¼�
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
	 * �����btnStart��
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
		
		// ���߻��Ѷ�ʱ����
		task = new TimerTask() {
			@Override
			public void run() {
				lockScreen();
				try {
					Thread.sleep(input);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// ���ͻ��ѹ㲥
				sendBroadcast(new Intent("android.my.wakeLock"));
			}
		};
		timer.schedule(task, input * 2, input * 2);
	}
	
	/**
	 * �����btnStop��
	 */
	private void click_stop(){
		btnStart.setEnabled(true);
		btnStop.setEnabled(false);
		btnActive.setEnabled(true);
		
		task.cancel();
	}
	
	/**
	 * ��ť����¼�
	 * 		�����豸����/ȡ���豸����  
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
	 * ����
	 */
	private void lockScreen(){
		policyManager.lockNow();
	}
	
	/**
	 * �����豸���� 
	 */
	public void activeManager(){
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "xxx xxx xxx");
		startActivityForResult(intent, 0);
	}
	
	/**
	 * ȡ���豸���� 
	 */
	private void removeActive(){
		policyManager.removeActiveAdmin(componentName);
		btnActive.setText("�����豸����");
		btnStart.setEnabled(false);
	}
}
