package com.hw.autotest;


import com.hw.autotest.reboot.ShowRebootCountActivity;
import com.hw.autotest.record.RecordCoordinatesActivity;
import com.hw.autotest.reboot.ShowRebootCountActivity;
import com.hw.autotest.string.FileSelect;


import com.hw.autotest.bluetooth.BluetoothTest;
import com.hw.autotest.broadcast.SystemBroadcastActivity;
import com.hw.autotest.cpufull.CpuFullActivity;
import com.hw.autotest.info.InfoQuery;
import com.hw.autotest.sleeplock.SleepLockActivity;
import com.hw.autotest.sleepwake.SleepWakeActivity;
import com.hw.autotest.storage.StorageTestList;
import com.hw.autotest.wifi.WifiTestActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

public class TestListActivity extends Activity implements OnItemClickListener{

	private ListView myListView = null;
	private TextView titleTv = null;
	private String[] mStrings;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_testlist);
		myListView = (ListView) findViewById(R.id.list);
		titleTv = (TextView) findViewById(R.id.title_textview);
		titleTv.setText(getString(R.string.title_main));
		mStrings = getResources().getStringArray(R.array.test_array);
		myListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mStrings));
		myListView.setOnItemClickListener(this);
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		//StartActivity here
		switch(arg2) {
		case 0:
		    doActivity(StorageTestList.class);
		    break;
		case 1:
			doActivity(WifiTestActivity.class);
			break;
		case 2:
		    doActivity(ShowRebootCountActivity.class);
		    break;
		case 5:
			doActivity(SleepWakeActivity.class);
			break;
		case 6:
			doActivity(CpuFullActivity.class);
			break;
		case 7:
			doActivity(InfoQuery.class);
			break;
		case 8:
			doActivity(BluetoothTest.class);
			break;
		case 9:
			doActivity(RecordCoordinatesActivity.class);
			break;
		case 10:
			doActivity(FileSelect.class);
			break;
		case 11:
			doActivity(SystemBroadcastActivity.class);
			break;
		case 12:
			doActivity(SleepLockActivity.class);
			break;

		}
	}
	
	private void doActivity(Class<?> cls) {
		Intent intent = new Intent(this, cls);
		startActivity(intent);
	}
	
}
