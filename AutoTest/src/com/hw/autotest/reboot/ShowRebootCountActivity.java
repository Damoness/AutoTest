package com.hw.autotest.reboot;

import com.hw.autotest.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ShowRebootCountActivity extends Activity{
    private SharedPreferences mSP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reboot);
        mSP = getSharedPreferences("auto_test_preferences", 0);
        TextView countTv = (TextView)findViewById(R.id.rebootCount);
        Button btn = (Button)findViewById(R.id.rebootBtn);
        countTv.setText("ÖØÆô´ÎÊý£º"+mSP.getInt("auto_test_preferences", 0));
        btn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                int count = mSP.getInt("auto_test_preferences", 0);
                mSP.edit().putInt("auto_test_preferences", ++count).commit();
                PowerManager pm = (PowerManager) ShowRebootCountActivity.this.getSystemService(Context.POWER_SERVICE);
                pm.reboot(null);
            }
        });
    }
    
}
