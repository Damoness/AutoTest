package com.hw.autotest.info;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;




import com.hw.autotest.R;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.Window;

public class Status extends PreferenceActivity{

    private static final String KEY_DATA_STATE = "data_state";
    private static final String KEY_SERVICE_STATE = "service_state";
    private static final String KEY_OPERATOR_NAME = "operator_name";
    private static final String KEY_ROAMING_STATE = "roaming_state";
    private static final String KEY_NETWORK_TYPE = "network_type";
    private static final String KEY_PHONE_NUMBER = "number";
    private static final String KEY_IMEI_SV = "imei_sv";
    private static final String KEY_IMEI = "imei";
    private static final String KEY_PRL_VERSION = "prl_version";
    private static final String KEY_MIN_NUMBER = "min_number";
    private static final String KEY_ESN_NUMBER = "esn_number";
    private static final String KEY_MEID_NUMBER = "meid_number";
    private static final String KEY_SIGNAL_STRENGTH = "signal_strength";
    private static final String KEY_BATTERY_STATUS = "battery_status";
    private static final String KEY_BATTERY_LEVEL = "battery_level";
    private static final String KEY_IP_ADDRESS = "wifi_ip_address";
    private static final String KEY_WIFI_MAC_ADDRESS = "wifi_mac_address";
    private static final String KEY_BT_ADDRESS = "bt_address";
    private static final String KEY_SERIAL_NUMBER = "serial_number";
    private static final String KEY_ICC_ID = "icc_id";
    private static final String KEY_WIMAX_MAC_ADDRESS = "wimax_mac_address";
    private static final String[] PHONE_RELATED_ENTRIES = {
        KEY_DATA_STATE,
        KEY_SERVICE_STATE,
        KEY_OPERATOR_NAME,
        KEY_ROAMING_STATE,
        KEY_NETWORK_TYPE,
        KEY_PHONE_NUMBER,
        KEY_IMEI,
        KEY_IMEI_SV,
        KEY_PRL_VERSION,
        KEY_MIN_NUMBER,
        KEY_MEID_NUMBER,
        KEY_SIGNAL_STRENGTH,
        KEY_ICC_ID
    };

    private static final int EVENT_SIGNAL_STRENGTH_CHANGED = 200;
    private static final int EVENT_SERVICE_STATE_CHANGED = 300;

    private static final int EVENT_UPDATE_STATS = 500;

    private TelephonyManager mTelephonyManager;
    private Phone mPhone = null;
    private Resources mRes;
    private Preference mSignalStrength;
    private Preference mUptime;

    /* < DTS2012030605262 jkf59269 20120306 begin */
//    private static String sUnknown;
    /* < DTS2012030605262 jkf59269 20120306 end */

    private Preference mBatteryStatus;
    private Preference mBatteryLevel;
    private Preference mNetworkType;

    private Handler mHandler;
    private SignalStrength mMSimSignalStrength;

    private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                mBatteryLevel.setSummary(getBatteryPercentage(intent));
                mBatteryStatus.setSummary(getBatteryStatus(getResources(), intent));
            }
        }
    };

    private void updateNetwork(int networkType) {
    	switch(networkType) {
    	case TelephonyManager.NETWORK_TYPE_HSDPA:
        case TelephonyManager.NETWORK_TYPE_HSUPA:
        case TelephonyManager.NETWORK_TYPE_HSPA:
        case TelephonyManager.NETWORK_TYPE_HSPAP:
        	mNetworkType.setSummary("2.5G");
            break;
        case TelephonyManager.NETWORK_TYPE_UMTS:
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
        case TelephonyManager.NETWORK_TYPE_EVDO_B:
        case TelephonyManager.NETWORK_TYPE_EHRPD:
        	mNetworkType.setSummary("3G");
            break;
        case TelephonyManager.NETWORK_TYPE_LTE:
        	mNetworkType.setSummary("LTE");
            break;
    	default:
    		mNetworkType.setSummary(getResources().getString(R.string.device_info_default));
            break;
    	}
    }
    
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onDataConnectionStateChanged(int state, int networkType) {
            updateDataState();
            updateNetwork(networkType);
        }

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            // TODO Auto-generated method stub
        	mMSimSignalStrength = signalStrength;
        }
        public void onDataActivity(int direction) {

        }

        public void onServiceStateChanged(ServiceState serviceState) {
        	updateServiceState(serviceState);
        }
    };
    /* <DTS2012100905994 liwenqi KF66783 201201022 begin */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (Window.FEATURE_OPTIONS_PANEL == featureId) {
             finish();
        }
        return super.onMenuItemSelected(featureId, item);
    }
    /* DTS2012100905994 liwenqi KF66783 20121022 end >*/
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Preference removablePref;
        mTelephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);

        addPreferencesFromResource(R.xml.device_info_status);
        mBatteryLevel = findPreference(KEY_BATTERY_LEVEL);
        mBatteryStatus = findPreference(KEY_BATTERY_STATUS);
        mNetworkType = findPreference(KEY_NETWORK_TYPE);

        mRes = getResources();


        mSignalStrength = findPreference(KEY_SIGNAL_STRENGTH);
        mUptime = findPreference("up_time");

        mTelephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(mPhoneStateListener,
                PhoneStateListener.LISTEN_SERVICE_STATE
                        | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                        | PhoneStateListener.LISTEN_CALL_STATE
                        | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                        | PhoneStateListener.LISTEN_DATA_ACTIVITY);
        
        updateNetwork(mTelephonyManager.getNetworkType());
        
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatteryInfoReceiver, mIntentFilter);

        setWimaxStatus();
        setWifiStatus();
        setBtStatus();

        String serial = Build.SERIAL;
        if (serial != null && !serial.equals("")) {
            /* < DTS2012112402079 liwenqi 20121224 begin */
            setSummaryText(KEY_SERIAL_NUMBER, serial.toUpperCase());
            /* DTS2012112402079 liwenqi 20121224 end > */
        } else {
            removePreferenceFromScreen(KEY_SERIAL_NUMBER);
        }

        setSummaryText("hardwareversion", readHardWareVersion());
        
        getDeviceInfo();
        getBatteryAddress();
    }

    private void getDeviceInfo() {
        setSummaryText(KEY_IMEI, mTelephonyManager.getDeviceId());
        setSummaryText(KEY_IMEI_SV, mTelephonyManager.getDeviceSoftwareVersion());
        setSummaryText(KEY_ICC_ID, mTelephonyManager.getSimSerialNumber());
        setSummaryText(KEY_IP_ADDRESS, getLocalIpAddress());
    }
    
    private void getBatteryAddress() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(null != adapter) {
            setSummaryText(KEY_BT_ADDRESS, adapter.getAddress());
        }
    }
    
    public String getLocalIpAddress() {
        String ipaddress = "";

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ipaddress = ipaddress + ";"
                                + inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
           ex.printStackTrace();
        }
        if(ipaddress.length() > 0 && ipaddress.startsWith(";")) {
            ipaddress = ipaddress.substring(1);
        }
        return ipaddress;
    }

    
    class StatusReceier extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if(arg1.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
				
			}
		}
    	
    }
    
    public String readHardWareVersion() {
        String strName = null;
        String strValue = null;
        /*read hw version*/
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader("/proc/app_info"));
            while ((strName = in.readLine()) != null) {
                if (0 == strName.compareToIgnoreCase("board_id:")) {
                    break;
                }
            }
            strValue = in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return strValue;
    }
    /* DTS2012122505554 liwenqi 20121225 end> */

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Removes the specified preference, if it exists.
     * @param key the key for the Preference item
     */
    private void removePreferenceFromScreen(String key) {
        Preference pref = findPreference(key);
        if (pref != null) {
            getPreferenceScreen().removePreference(pref);
        }
    }

    /**
     * @param preference The key for the Preference item
     * @param property The system property to fetch
     * @param alt The default value, if the property doesn't exist
     */
    private void setSummary(String preference, String property, String alt) {
        try {
            findPreference(preference).setSummary(getProperty(property, alt));
        } catch (RuntimeException e) {

        }
    }

    private void setSummaryText(String preference, String text) {
            if (TextUtils.isEmpty(text)) {
               /* < DTS2012030605262 jkf59269 20120306 begin */
               text = getResources().getString(R.string.device_info_default);
               /* DTS2012030605262 jkf59269 20120306 end > */
             }
             // some preferences may be missing
             if (findPreference(preference) != null) {
                 findPreference(preference).setSummary(text);
             }
    }

    private void updateDataState() {
        int state = mTelephonyManager.getDataState();
        String display = mRes.getString(R.string.radioInfo_unknown);

        switch (state) {
            case TelephonyManager.DATA_CONNECTED:
                display = mRes.getString(R.string.radioInfo_data_connected);
                break;
            case TelephonyManager.DATA_SUSPENDED:
                display = mRes.getString(R.string.radioInfo_data_suspended);
                break;
            case TelephonyManager.DATA_CONNECTING:
                display = mRes.getString(R.string.radioInfo_data_connecting);
                break;
            case TelephonyManager.DATA_DISCONNECTED:
                display = mRes.getString(R.string.radioInfo_data_disconnected);
                break;
        }

        setSummaryText(KEY_DATA_STATE, display);
    }

    /*
    void updateSignalStrength(SignalStrength signalStrength) {
        if (mSignalStrength != null) {
            int level = signalStrength.get
            Resources r = getResources();

            if ((ServiceState.STATE_OUT_OF_SERVICE == state) ||
                    (ServiceState.STATE_POWER_OFF == state)) {
                mSignalStrength.setSummary("0");
            }

            int signalDbm = mPhoneStateReceiver.getSignalStrengthDbm();

            if (-1 == signalDbm) signalDbm = 0;

            int signalAsu = mPhoneStateReceiver.getSignalStrengthLevelAsu();

            if (-1 == signalAsu) signalAsu = 0;

            mSignalStrength.setSummary(String.valueOf(signalDbm) + " "
                        + r.getString(R.string.radioInfo_display_dbm) + "   "
                        + String.valueOf(signalAsu) + " "
                        + r.getString(R.string.radioInfo_display_asu));
        }
    }
    */
    
    private void updateServiceState(ServiceState serviceState) {
        int state = serviceState.getState();
        String display = mRes.getString(R.string.radioInfo_unknown);

        switch (state) {
            case ServiceState.STATE_IN_SERVICE:
                display = mRes.getString(R.string.radioInfo_service_in);
                break;
            case ServiceState.STATE_OUT_OF_SERVICE:
            case ServiceState.STATE_EMERGENCY_ONLY:
                display = mRes.getString(R.string.radioInfo_service_out);
                break;
            case ServiceState.STATE_POWER_OFF:
                display = mRes.getString(R.string.radioInfo_service_off);
                break;
        }

        setSummaryText(KEY_SERVICE_STATE, display);

        if (serviceState.getRoaming()) {
            setSummaryText(KEY_ROAMING_STATE, mRes.getString(R.string.radioInfo_roaming_in));
        } else {
            setSummaryText(KEY_ROAMING_STATE, mRes.getString(R.string.radioInfo_roaming_not));
        }
        setSummaryText(KEY_OPERATOR_NAME, serviceState.getOperatorAlphaLong());
    }


    private void setWimaxStatus() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);

        if (ni == null) {
            PreferenceScreen root = getPreferenceScreen();
            Preference ps = (Preference) findPreference(KEY_WIMAX_MAC_ADDRESS);
            if (ps != null) root.removePreference(ps);
        } else {
            Preference wimaxMacAddressPref = findPreference(KEY_WIMAX_MAC_ADDRESS);
            String macAddress = getProperty("net.wimax.mac.address");
            wimaxMacAddressPref.setSummary(macAddress);
        }
    }
    private void setWifiStatus() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        Preference wifiMacAddressPref = findPreference(KEY_WIFI_MAC_ADDRESS);

        String macAddress = wifiInfo == null ? null : wifiInfo.getMacAddress();
        /* < DTS2012112402079 liwenqi 20121224 begin */
        wifiMacAddressPref.setSummary(!TextUtils.isEmpty(macAddress) ? macAddress.toUpperCase()
                : getString(R.string.status_unavailable));
        /* DTS2012112402079 liwenqi 20121224 end > */
    }
    
    private void setBtStatus() {
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        Preference btAddressPref = findPreference(KEY_BT_ADDRESS);

        if (bluetooth == null) {
            // device not BT capable
            getPreferenceScreen().removePreference(btAddressPref);
        } else {
            String address = bluetooth.isEnabled() ? bluetooth.getAddress() : null;
            /* < DTS2012112402079 liwenqi 20121224 begin */
            btAddressPref.setSummary(!TextUtils.isEmpty(address) ? address.toUpperCase()
                    : getString(R.string.status_unavailable));
            /* DTS2012112402079 liwenqi 20121224 end > */
        }
    }

    void updateTimes() {
        long at = SystemClock.uptimeMillis() / 1000;
        long ut = SystemClock.elapsedRealtime() / 1000;

        if (ut == 0) {
            ut = 1;
        }

        mUptime.setSummary(convert(ut));
    }

    private String pad(int n) {
        if (n >= 10) {
            return String.valueOf(n);
        } else {
            return "0" + String.valueOf(n);
        }
    }

    private String convert(long t) {
        int s = (int)(t % 60);
        int m = (int)((t / 60) % 60);
        int h = (int)((t / 3600));

        return h + ":" + pad(m) + ":" + pad(s);
    }
    
    public static String getBatteryPercentage(Intent batteryChangedIntent) {
        int level = batteryChangedIntent.getIntExtra("level", 0);
        int scale = batteryChangedIntent.getIntExtra("scale", 100);
        return String.valueOf(level * 100 / scale) + "%";
    }

    public static String getBatteryStatus(Resources res, Intent batteryChangedIntent) {
        final Intent intent = batteryChangedIntent;

        int plugType = intent.getIntExtra("plugged", 0);
        int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
        String statusString;
        if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
            statusString = res.getString(R.string.battery_info_status_charging);
            if (plugType > 0) {
                statusString = statusString
                        + " "
                        + res.getString((plugType == BatteryManager.BATTERY_PLUGGED_AC)
                                ? R.string.battery_info_status_charging_ac
                                : R.string.battery_info_status_charging_usb);
            }
        } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
            statusString = res.getString(R.string.battery_info_status_discharging);
        } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
            statusString = res.getString(R.string.battery_info_status_not_charging);
        } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
            statusString = res.getString(R.string.battery_info_status_full);
        } else {
            statusString = res.getString(R.string.battery_info_status_unknown);
        }

        return statusString;
    }
    
    private String getProperty(String property) {
		String resultStr = "";
		try {
			Class cl = Class.forName(property);
			Object invoker = cl.newInstance();
			
			Method m = cl.getMethod("get", new Class[] { String.class,
					String.class });
	
			Object result = m.invoke(invoker, new Object[] {
					"gsm.version.baseband", "no message" });
			resultStr = result.toString();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return resultStr;
    }
    
    private String getProperty(String property, String defalutStr) {
		String resultStr = "";
		try {
			Class cl = Class.forName(property);
			Object invoker = cl.newInstance();
			
			Method m = cl.getMethod("get", new Class[] { String.class,
					String.class });
	
			Object result = m.invoke(invoker, new Object[] {
					"gsm.version.baseband", "no message" });
			resultStr = result.toString();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(null == resultStr || 0 == resultStr.length()) {
			return defalutStr;
		}
		return resultStr;
    }
    
}
