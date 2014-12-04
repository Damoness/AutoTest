package com.hw.autotest.wifi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.preference.PreferenceManager;
import android.util.Log;

public class WifiAdmin {

	private static final String TAG = WifiAdmin.class.getSimpleName();
	
	public static final int WIFI_AP_STATE_DISABLING = 10;
	public static final int WIFI_AP_STATE_DISABLED = 11;
	public static final int WIFI_AP_STATE_ENABLING = 12;
	public static final int WIFI_AP_STATE_ENABLED = 13;
	public static final int WIFI_AP_STATE_FAILED = 14;
	
	private final String[] wifiApStates = {
			"WIFI_AP_STATE_DISABLING",
			"WIFI_AP_STATE_DISABLED",
			"WIFI_AP_STATE_ENABLING",
			"WIFI_AP_STATE_ENABLED",
			"WIFI_AP_STATE_FAILED"
	};
	
	
	private WifiManager mWifiManager;
	private WifiInfo mWifiInfo;
	private List<ScanResult> mWifiList;
	private List<WifiConfiguration> mWifiConfigurations;
	private WifiLock mWifiLock;
	private ScanResult scanResult;
	private Context context;
	
	public WifiAdmin(Context c){
		this.context = c;
		
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		
		mWifiInfo = mWifiManager.getConnectionInfo();	
		
	}
	
	
	public int getWifiState(){
		
		return mWifiManager.getWifiState();
	
	}
	
	public void startScan(){
		mWifiManager.startScan();
		mWifiList = mWifiManager.getScanResults();
		mWifiConfigurations = mWifiManager.getConfiguredNetworks();
	}
	
	public List<ScanResult> getWifiList(){
		return mWifiList;
	}
	
	public List<WifiConfiguration> getConfigurations(){
		return mWifiConfigurations;
	}
	
	
	public String getBSSID(){
		return (mWifiInfo == null) ? "NULL" :mWifiInfo.getBSSID();
	}
	

	
	public String getMacAddress(){
		 
		return (mWifiInfo == null) ? "NULL" :mWifiInfo.getMacAddress();
	}
	
	public int getIpAddress(){
	
		return (mWifiInfo == null) ? 0: mWifiInfo.getIpAddress();
		
	}
	
	public int getNetWorkId(){
		
		return (mWifiInfo == null) ? 0 :mWifiInfo.getNetworkId();
	}
	
	
	public String getWifiInfo(){
		 
		return mWifiManager.getConnectionInfo().toString();
	}
	
	
	public void createWifiLock(){
		
	}
	
	
	public void openWifi(){
		if(!mWifiManager.isWifiEnabled()){
			mWifiManager.setWifiEnabled(true);
		}
	}
	
	public void closeWifi(){
		if(mWifiManager.isWifiEnabled()){
			mWifiManager.setWifiEnabled(false);
		}
	}
	
	public String checkState(){
		
		String states[]  = {
				"WIFI_STATE_DISABLING",
				"WIFI_STATE_DISABLED",
				"WIFI_STATE_ENABLING",
				"WIFI_STATE_ENABLED ",
				"WIFI_STATE_UNKNOWN"
				
		};	
		
		return states[mWifiManager.getWifiState()];
		
	}
	
	
	
	public void connectionConfiguration(int index){
		if(index > mWifiConfigurations.size()) return;
		
		mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true);
	}
	
	
	public boolean disconnectCurrentWifi(){
		
		return mWifiManager.disconnect();
	}
	
	public boolean disconnectWifi(int netId){
		
		return mWifiManager.disableNetwork(netId);
		//mWifiManager.disconnect();
	}
	
	public boolean addNetWork(WifiConfiguration wcg){
		
		
		 int wcgID = mWifiManager.addNetwork(wcg);
		 
		return mWifiManager.enableNetwork(wcgID, true);
		 
	}
	
	public StringBuffer lookUpScan(){
		StringBuffer stringBuffer = new StringBuffer();
		
		for(int i=0;i<mWifiList.size();i++){
			stringBuffer.append("Index_"+new Integer(i+1).toString()+":");
			stringBuffer.append(mWifiList.get(i).toString());
			stringBuffer.append("\n");
		}
		
		return stringBuffer;
	}
	
	
	   
    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type)   
    {   
    	   WifiConfiguration config = new WifiConfiguration();     
           config.allowedAuthAlgorithms.clear();   
           config.allowedGroupCiphers.clear();   
           config.allowedKeyManagement.clear();   
           config.allowedPairwiseCiphers.clear();   
           config.allowedProtocols.clear();   
          config.SSID = "\"" + SSID + "\"";     
            
          WifiConfiguration tempConfig = this.IsExsits(SSID);             
          if(tempConfig != null) {    
              mWifiManager.removeNetwork(tempConfig.networkId);    
          }  
            
          if(Type == 1) //WIFICIPHER_NOPASS  
          {   
               config.wepKeys[0] = "";   
               config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);   
               config.wepTxKeyIndex = 0;   
          }   
          if(Type == 2) //WIFICIPHER_WEP  
          {   
              config.hiddenSSID = true;  
              config.wepKeys[0]= "\""+Password+"\"";   
              config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);   
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);   
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);   
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);   
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);   
              config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);   
              config.wepTxKeyIndex = 0;   
          }   
          if(Type == 3) //WIFICIPHER_WPA  
          {   
          config.preSharedKey = "\""+Password+"\"";   
          config.hiddenSSID = true;     
          config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);     
          config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);                           
          config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);                           
          config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);                      
          //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);    
          config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);  
          config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);  
          config.status = WifiConfiguration.Status.ENABLED;     
          }  
           return config;   
    }   
      
    private WifiConfiguration IsExsits(String SSID)    
    {    
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();    
           for (WifiConfiguration existingConfig : existingConfigs)     
           {    
             if (existingConfig.SSID.equals("\""+SSID+"\""))    
             {    
                 return existingConfig;    
             }    
           }    
        return null;     
    } 
    
    
    
    public void startWifiAp() {  
        // WifiManager wifi = (WifiManager)  
        // getSystemService(Context.WIFI_SERVICE);  
        // wifiManager.setWifiEnabled(false);  
        Method method1 = null;  
        try {  
            method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",  
                    WifiConfiguration.class, boolean.class);  
            WifiConfiguration netConfig = new WifiConfiguration();  
            
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            
            String ssid = prefs.getString("pref_key_ap_ssid", "wd");
            String password = prefs.getString("pref_key_ap_password", "12345678");
            
            Log.i(TAG,ssid+"------------"+password);
            
            //wifi»»µ„√˚◊÷  
            netConfig.SSID = ssid;  
            netConfig.allowedAuthAlgorithms  
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);  
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);  
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);  
            netConfig.allowedKeyManagement  
                    .set(WifiConfiguration.KeyMgmt.WPA_PSK);  
            netConfig.allowedPairwiseCiphers  
                    .set(WifiConfiguration.PairwiseCipher.CCMP);  
            netConfig.allowedPairwiseCiphers  
                    .set(WifiConfiguration.PairwiseCipher.TKIP);  
            netConfig.allowedGroupCiphers  
                    .set(WifiConfiguration.GroupCipher.CCMP);  
            netConfig.allowedGroupCiphers  
                    .set(WifiConfiguration.GroupCipher.TKIP);  
            //√‹¬Î  
            netConfig.preSharedKey = password;  
  
            method1.invoke(mWifiManager, netConfig, true);
            
   /*         Method method2 = mWifiManager.getClass().getMethod("getWifiApState");  
            int state = (Integer) method2.invoke(mWifiManager);
            Log.i(TAG,"state:"+state);
            if(state ==13) return true;
            else return false;*/
            
            //Log.i("wifi state" + state);  
        } catch (IllegalArgumentException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (IllegalAccessException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (InvocationTargetException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (SecurityException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (NoSuchMethodException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }
        
        
  
    }  
    
    public void closeWifiAp(){
    	try {
    		
			Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class,boolean.class);
			
			method.invoke(mWifiManager, null,false);
			
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    
    
    
    public String getWifiApState() {  
        try {  
            Method method = mWifiManager.getClass().getMethod("getWifiApState");  
            int i = (Integer) method.invoke(mWifiManager);  
            Log.i(TAG,"wifi state:  " + wifiApStates[i-10]);  
            return wifiApStates[i-10];  
            
        } catch (Exception e) {  
            Log.i(TAG,"Cannot get WiFi AP state" + e);  
            
            return "";
           
        }  
    }  
    
}
