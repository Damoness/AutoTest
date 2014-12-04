package com.hw.autotest.wifi;


import com.hw.autotest.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class WifiSettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.wifi_prefs);
		

	}

	
	
}
