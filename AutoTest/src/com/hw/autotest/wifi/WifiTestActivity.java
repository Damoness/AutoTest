package com.hw.autotest.wifi;

import java.util.List;

import com.hw.autotest.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class WifiTestActivity extends Activity implements OnClickListener{

	
	private WifiAdmin mWifiAdmin;
	private ScrollView sView;
	private TextView allNetWork;
	
	private ScanResult mScanResult;
	
	private Button scan;
	private Button start;
	private Button stop;
	private Button check;
	
	private Handler handler = new Handler();
	private Handler startApHandler = new Handler();
	private Button openAp;
	private Button closeAp;
	
	private Button connect;
	private Button disconnect;
	
	private Spinner protocolSpinner;
	private EditText ssidEdit;
	private EditText passwordEdit;
	
	private EditText numEdit;
	private Button autoWifiTest;
	private Button autoApTest;
	private Button stopService;
	
	private static final String[] protocols = {
		"OPEN",
		"WEP",
		"WPA-PSK",		
	};
	
	
	private static final String TAG  = WifiTestActivity.class.getSimpleName();

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_test);
		mWifiAdmin = new WifiAdmin(WifiTestActivity.this);
	    init();
	}

	
	
	private void init() {
		// TODO Auto-generated method stub
		sView = (ScrollView)findViewById(R.id.mScrollView);
		allNetWork = (TextView)findViewById(R.id.allNetWork);
		scan = (Button)findViewById(R.id.scan);
		start = (Button)findViewById(R.id.start);
		stop = (Button)findViewById(R.id.stop);
		check = (Button)findViewById(R.id.check);
		
		closeAp = (Button)findViewById(R.id.closeAp);
		openAp = (Button)findViewById(R.id.openAp);
		
		connect = (Button)findViewById(R.id.connect);
		disconnect = (Button)findViewById(R.id.disconnect);
		
		protocolSpinner = (Spinner)findViewById(R.id.protocol);
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, protocols);
		protocolSpinner.setAdapter(aa);
		
		ssidEdit = (EditText) findViewById(R.id.ssid);
		passwordEdit = (EditText) findViewById(R.id.password);
		
		autoWifiTest = (Button) findViewById(R.id.autoWifiTest);
		autoApTest = (Button) findViewById(R.id.autoApTest);
		stopService =(Button) findViewById(R.id.stopService);
		
		scan.setOnClickListener(this);
		start.setOnClickListener(this);
		stop.setOnClickListener(this);
		check.setOnClickListener(this);
		closeAp.setOnClickListener(this);
		openAp.setOnClickListener(this);
		connect.setOnClickListener(this);
		disconnect.setOnClickListener(this);
		
		numEdit = (EditText) findViewById(R.id.num);
		autoWifiTest.setOnClickListener(this);
		autoApTest.setOnClickListener(this);
		stopService.setOnClickListener(this);
		
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_wifi, menu);
		return true;
	}

	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
	switch(item.getItemId()){
	case R.id.menu_wifi_settings:
		Intent intent  = new Intent(this,WifiSettingsActivity.class);
		this.startActivity(intent);
		break;
		}
		
		return true;
	}


	public void start(){
		mWifiAdmin.openWifi();
		check();
	}
	
	public void stop(){
		mWifiAdmin.closeWifi();
		check();
	}

	public void check(){
		Log.i(TAG, "Wifi state: " +mWifiAdmin.checkState());
		Toast.makeText(this, "当前Wifi网卡状态为："+mWifiAdmin.checkState(), Toast.LENGTH_SHORT).show();
	}
	
	public void getAllNetWorkList(){
		StringBuffer stringBuffer = new StringBuffer();
		
		mWifiAdmin.startScan();
		
		List<ScanResult> list = mWifiAdmin.getWifiList();
		
		if(list !=null){
			for(int i=0;i<list.size();i++){
				mScanResult = list.get(i);
				
				stringBuffer.append(mScanResult.BSSID).append(" ")
				.append(mScanResult.capabilities).append(" ")
				.append(mScanResult.frequency).append(" ")
				.append(mScanResult.level).append(" ")
				.append(mScanResult.SSID).append(" ").append("\n");
			}
			
		//	allNetWork.setText("扫描到的所有wifi网络：\n"+stringBuffer.toString());
			
			allNetWork.setText(mWifiAdmin.lookUpScan());
			
		}
	}
	
	public void showToast(String str){
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.scan:
			this.getAllNetWorkList();
			break;
		case R.id.start:
			this.start();
			//Settings.System.putInt(MainActivity.this.getContentResolver(), Settings.System.WIFI_SWITCH_ON, 0);
			break;
		case R.id.stop:
			this.stop();
			//Settings.System.putInt(MainActivity.this.getContentResolver(), Settings.System.WIFI_SWITCH_ON, 1);
			break;
		case R.id.check:
			this.check();
			break;
		case R.id.openAp:
			this.openAp();
			break;
		case R.id.closeAp:
			this.closeAp();
			break;
		case R.id.connect:
			this.connect();
			break;
		case R.id.disconnect:
			this.disconnect();
			break;
		case R.id.autoWifiTest:
			this.autoWifiTest(Integer.valueOf(numEdit.getText().toString()));
			break;
		case R.id.autoApTest:
			this.autoApTest(Integer.valueOf(numEdit.getText().toString()));
			break;
		case R.id.stopService:
			this.stopService();
			break;	
		default:
			
			break;
		}
	}







	private void stopService() {
		// TODO Auto-generated method stub
		Intent i1 = new Intent(this,AutoApTestService.class);
		Intent i2 = new Intent(this,AutoWifiTestService.class);
	
		Log.i(TAG,"stopService");
		
		this.stopService(i1);
		this.stopService(i2);
	}



	public void cpuRate(View v){
		this.showToast(Double.toString(ProcessCpuRate.getProcessCpuRate()));
	}
	
	
	public void wifiInfo(View v){
		
		WifiManager mWifiManager = (WifiManager) WifiTestActivity.this.getSystemService(Context.WIFI_SERVICE);		
		this.showToast(mWifiManager.getConnectionInfo().toString());
		
		//this.showToast(mWifiAdmin.getWifiInfo());
	}
	
	
	
	public void connect(){
		
		String ssid = ssidEdit.getText().toString();
		String password = passwordEdit.getText().toString();
		int protocol = protocolSpinner.getFirstVisiblePosition()+1;
		
		//Log.d(TAG, ""+protocol);
		
		mWifiAdmin.openWifi();
		
		mWifiAdmin.startScan();
		WifiConfiguration wcg =  mWifiAdmin.CreateWifiInfo(ssid, password, protocol);
		//this.showToast(wcg.toString());
		mWifiAdmin.addNetWork(wcg);
		//this.showToast(mWifiAdmin.getConfigurations().toString());
		WifiManager mWifiManager = (WifiManager) WifiTestActivity.this.getSystemService(Context.WIFI_SERVICE);		
		this.showToast(mWifiManager.getConnectionInfo().toString());
		
		//allNetWork.setText(mWifiAdmin.getConfigurations().toString());
		//check();
	}

	public void disconnect(){
		
		mWifiAdmin.disconnectCurrentWifi();
		
	}
	
	
	class closeWifiThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i(TAG,"closeWifiThread...."+mWifiAdmin.getWifiState());
			int state = mWifiAdmin.getWifiState();

			if(state == WifiManager.WIFI_STATE_ENABLED || state == WifiManager.WIFI_STATE_ENABLING){
				mWifiAdmin.closeWifi();
				startApHandler.postDelayed(new startApThread(), 1000);
			}
		}
		
	}
	
	class startApThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			openAp();
		}
		
	}
	
	public void openAp(){
		
	
		
		Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.postDelayed(new closeWifiThread(), 1000);
			}
			
		});
		
		thread.start();
		
		mWifiAdmin.getWifiApState();
		mWifiAdmin.startWifiAp();
		mWifiAdmin.getWifiApState();
	}
	
	public void closeAp(){
		
		mWifiAdmin.getWifiApState();
		mWifiAdmin.closeWifiAp();
		mWifiAdmin.getWifiApState();
	}
	

	public void  autoWifiTest(int num) {
		Intent intent = new Intent(this,AutoWifiTestService.class);
		String ssid = ssidEdit.getText().toString();
		String password = passwordEdit.getText().toString();
		int protocol = protocolSpinner.getFirstVisiblePosition()+1;
		
		intent.putExtra("num", num);
		intent.putExtra("ssid",ssid);
		intent.putExtra("password", password);
		intent.putExtra("protocol", protocol);
		
		this.startService(intent);
		
	}
	
	private void autoApTest(int num) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this,AutoApTestService.class);
		String ssid = ssidEdit.getText().toString();
		String password = passwordEdit.getText().toString();
		int protocol = protocolSpinner.getFirstVisiblePosition()+1;
		
		intent.putExtra("num", num);
		intent.putExtra("ssid",ssid);
		intent.putExtra("password", password);
		intent.putExtra("protocol", protocol);
		
		this.startService(intent);
	}



	
}
