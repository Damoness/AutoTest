package com.hw.autotest.bluetooth;

import java.util.EmptyStackException;

import com.hw.autotest.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothTest extends Activity implements OnClickListener{

	private TextView addrTV, nameTV;
	private Switch bluetoothSwitch;
	private Button getAddrBtn, bluetoothSwitchBtn, getNameBtn, modifyBTName;
	private EditText switchCountET, modifyNameET;
	private BluetoothAdapter mAdapter;
    private static final int DISABLE_TIMEOUT = 60000; // ms timeout for BT disable
    private static final int ENABLE_TIMEOUT = 60000;  // ms timeout for BT enable
    private static final int POLL_TIME = 400;         // ms to poll BT state
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth);
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		initView();
	}
	
	private void initView() {
		bluetoothSwitch = (Switch) findViewById(R.id.bluetooth_switch);
		addrTV = (TextView) findViewById(R.id.bluetooth_addr_tv);
		getAddrBtn = (Button) findViewById(R.id.bluetooth_get_addr_btn);
		nameTV = (TextView) findViewById(R.id.bluetooth_name_tv);
		getNameBtn = (Button) findViewById(R.id.bluetooth_get_name_btn);
		getAddrBtn.setOnClickListener(this);
		getNameBtn.setOnClickListener(this);
		bluetoothSwitchBtn = (Button) findViewById(R.id.bluetooth_switch_btn);
		switchCountET = (EditText) findViewById(R.id.bluetooth_switch_count);
		modifyNameET = (EditText) findViewById(R.id.bluetooth_modify_name_et);
		modifyBTName = (Button) findViewById(R.id.bluetooth_modify_name_btn);
		modifyBTName.setOnClickListener(this);
		bluetoothSwitchBtn.setOnClickListener(this);
		if(null == mAdapter) {
			bluetoothSwitch.setChecked(false);
		} else {
			if(BluetoothAdapter.STATE_OFF == mAdapter.getState()) {
				bluetoothSwitch.setChecked(false);
			} else if(BluetoothAdapter.STATE_ON == mAdapter.getState()) {
				bluetoothSwitch.setChecked(true);
			}	
		}
		bluetoothSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(null == mAdapter) {
					Toast.makeText(BluetoothTest.this, "¿∂—¿π§◊˜“Ï≥£", Toast.LENGTH_SHORT).show();
					bluetoothSwitch.setChecked(false);
				} else {
					if(arg1) {
						enable();
					} else {
						disable();
					}	
				}
			}
		});
	}

	private void enable() {
		if(null == mAdapter || BluetoothAdapter.STATE_ON == mAdapter.getState()) {
			return ;
		} else {
			mAdapter.enable();
		}
	}
	
	private void disable() {
		if(null == mAdapter || BluetoothAdapter.STATE_OFF == mAdapter.getState()) {
			return ;
		} else {
			mAdapter.disable();
		}
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()) {
		case R.id.bluetooth_get_addr_btn:
			if(null != mAdapter) {
				addrTV.setText(mAdapter.getAddress());
			}
			break;
		case R.id.bluetooth_switch_btn:
			break;
		case R.id.bluetooth_get_name_btn:
			if(null != mAdapter) {
				nameTV.setText(mAdapter.getName());
				modifyNameET.setText(mAdapter.getName());
			}
			break;
		case R.id.bluetooth_modify_name_btn:
			if(null != mAdapter) {
				String name = modifyNameET.getText().toString();
				if(null == name || 0 == name.length()) {
					Toast.makeText(BluetoothTest.this, "«Î ‰»Î¿∂—¿√˚≥∆", Toast.LENGTH_SHORT).show();
				}
				if(mAdapter.setName(modifyNameET.getText().toString())){
					nameTV.setText(mAdapter.getName());
				}
			}
			break;
		}
	}
	
	private void switchBluetooth(int times) {
		
	}

    private static void sleep(long t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {}
    }
	
	
}
