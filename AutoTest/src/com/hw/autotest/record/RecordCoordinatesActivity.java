package com.hw.autotest.record;


import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

import com.hw.autotest.R;

public class RecordCoordinatesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.activity_record_coordinates);
	}

	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		switch (keyCode){
		
		
		case KeyEvent.KEYCODE_HOME:
			Toast.makeText(getApplicationContext(), "KEYCODE_HOME", Toast.LENGTH_SHORT).show();
			break;
		case KeyEvent.KEYCODE_BACK:
			Toast.makeText(getApplicationContext(), "KEYCODE_BACK", Toast.LENGTH_SHORT).show();
			break;
		case KeyEvent.KEYCODE_VOLUME_UP:
			Toast.makeText(getApplicationContext(), "KEYCODE_VOLUME_UP", Toast.LENGTH_SHORT).show();
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			Toast.makeText(getApplicationContext(), "KEYCODE_VOLUME_DOWN", Toast.LENGTH_SHORT).show();
			break;
		case KeyEvent.KEYCODE_VOLUME_MUTE:
			Toast.makeText(getApplicationContext(), "KEYCODE_VOLUME_MUTE", Toast.LENGTH_SHORT).show();
			break;
		case KeyEvent.KEYCODE_POWER:
			Toast.makeText(getApplicationContext(), "KEYCODE_POWER", Toast.LENGTH_SHORT).show();
			break;
		}
		
		return super.onKeyDown(keyCode, event);
	}



	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		   float x = event.getX();
		    float y = event.getY();
		    switch (event.getAction()) {
		        case MotionEvent.ACTION_DOWN:
		            Toast.makeText(getApplicationContext(), "Touch Down x="+x+"  y="+y ,Toast.LENGTH_SHORT).show();
		            //Handle Touch Down
		            break;
		        case MotionEvent.ACTION_MOVE:
		        	//Toast.makeText(getApplicationContext(), "Touch Move" ,Toast.LENGTH_SHORT).show();
			          
		            //Handle Touch Move
		            break;
		        case MotionEvent.ACTION_UP:
		            Toast.makeText(getApplicationContext(), "Touch Up x="+x+"  y="+y ,Toast.LENGTH_SHORT).show();
		            //Handle Touch Up
		            break;
		       
		    }
		    return false;
		
	}

	
	
	
}
