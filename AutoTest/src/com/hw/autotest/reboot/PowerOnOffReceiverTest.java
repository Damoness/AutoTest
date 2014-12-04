package com.hw.autotest.reboot;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.InputDevice;

public class PowerOnOffReceiverTest extends BroadcastReceiver{

    private static final String TAG = "PowerOnOffReceiverTest";
    private Context mContext;
    private Handler mHandler = new Handler();
    private SharedPreferences mSP;
    @Override
    public void onReceive(final Context arg0, Intent arg1) {
        // TODO Auto-generated method stub
        mContext = arg0;
        mSP = arg0.getSharedPreferences("auto_test_preferences", 0);
        Log.d(TAG, "onReceive() action="+arg1.getAction());
        if(Intent.ACTION_BOOT_COMPLETED.equals(arg1.getAction())) {
            mHandler.postDelayed(new Runnable() {
                
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if(isMouseConnected(arg0)) {
                      int count = mSP.getInt("auto_test_preferences", 0);
                      mSP.edit().putInt("auto_test_preferences", ++count).commit();
                      PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
                      pm.reboot(null);
                    } 
//                    else {
//                        arg0.startActivity(new Intent(arg0, ShowRebootCountActivity.class));
//                    }
                }
            }, 60 * 1000);
        }
    }

    @SuppressLint("NewApi")
    private boolean isMouseConnected(Context context) {
        final InputManager im = (InputManager)context.getSystemService(Context.INPUT_SERVICE);
        int [] inputDeviceIds;
        boolean hasMouseConnected = false;
            
        inputDeviceIds = im.getInputDeviceIds();
        Log.v(TAG, "inputDeviceIds.length: "+inputDeviceIds.length);
        for (int i = 0; i < inputDeviceIds.length; i++) {
            Log.v(TAG, ""+inputDeviceIds[i]);
            InputDevice device = im.getInputDevice(inputDeviceIds[i]);
            if (device != null) {
            Log.v(TAG, "device:" + device.toString());          
            if((device.getSources() & InputDevice.SOURCE_MOUSE) == InputDevice.SOURCE_MOUSE) {
                   hasMouseConnected = true;
                }
            }
        }
        Log.v(TAG, "hasMouseConnected: "+hasMouseConnected); 
        return hasMouseConnected;
    }
    
}
