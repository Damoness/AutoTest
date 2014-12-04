package com.hw.autotest.cpufull;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hw.autotest.R;

/**
 * Activity
 * 		性能测试 控制CPU使用率占用
 * 
 * @author wangwei
 * @date 2014-6-10
 *
 */
public class CpuFullActivity extends Activity implements OnClickListener {

	static boolean flag = false;
	
	// 占用百分比
	static int percent = 30;
	
	Button btnStart,btnStop;
	EditText etPercent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cpufull);
		
		init();
	}

	/**
	 * 初始化
	 */
	private void init(){
		
		btnStart = (Button) findViewById(R.id.start);
		btnStop = (Button) findViewById(R.id.stop);
		etPercent = (EditText) findViewById(R.id.percent);
		
		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		
		btnStart.setEnabled(!flag);
		btnStop.setEnabled(flag);
		etPercent.setText(percent+"");
	}
	
	/**
	 * Cpu 使用率占用 线程
	 * 
	 * @author wangwei
	 */
	class CPUTestThread implements Runnable {  
	    @Override  
	    public void run() {  
	        int busyTime = percent * 10;  
	        int idleTime = 1000 - busyTime;  
	        long startTime = 0; 
	        
	        while (flag) {
	        	startTime = System.currentTimeMillis();
	        	while (System.currentTimeMillis() - startTime <= busyTime);
	            try {
	                Thread.sleep(idleTime);  
	            } catch (InterruptedException e) {  
	            	e.printStackTrace();
	            }  
	        }
	        
	        /*while (true) {
	            startTime = System.currentTimeMillis();  
	            // System.out.println(System.currentTimeMillis()+","+startTime+","+(System.currentTimeMillis() - startTime));  
	            // busy loop  
	            while (System.currentTimeMillis() - startTime <= busyTime);
	            // idle loop  
	            System.out.println("Sleep");
	            try {  
	                Thread.sleep(1);  
	            } catch (InterruptedException e) {  
	                System.out.println(e);  
	            }  
	        }  */
	  
	    }  
	}

	/**
	 * 监听点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start:
			click_btnStart();
			break;
		case R.id.stop:
			click_btnStop();
			break;

		default:
			break;
		}
	}
	
	/**
	 * 点击【btnStart】 
	 */
	private void click_btnStart(){
		if(!flag){
			try{
				percent = Integer.parseInt(etPercent.getText().toString());
			}catch (Exception e) {
				Toast.makeText(this, "请输入数值！", Toast.LENGTH_SHORT).show();
				return;
			}
			flag = true;
			CPUTestThread cpuTestThread = new CPUTestThread();  
	        for (int i = 0; i < 4; i++) {  
	            Thread cpuTest = new Thread(cpuTestThread);  
	            cpuTest.start(); 
	        }
	        changeBtnState(false, true);
		}
	}
	
	/**
	 * 点击【btnStop】
	 */
	private void click_btnStop(){
		if(flag){
			flag = false;
	        changeBtnState(true, false);
		}
	}
	
	/**
	 * 改变按钮状态
	 * <br/>
	 * true 启用，false 禁用
	 * 
	 * @param b1
	 * 		btnStart 状态
	 * @param b2
	 * 		btnStop 状态
	 */
	private void changeBtnState(boolean b1,boolean b2){
		btnStart.setEnabled(b1);
        btnStop.setEnabled(b2);
	}
	
	
}
