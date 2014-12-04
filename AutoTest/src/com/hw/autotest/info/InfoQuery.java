package com.hw.autotest.info;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;

import com.hw.autotest.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;

public class InfoQuery extends PreferenceActivity{
	private static final String TAG = "InfoQuery";
	private static final String FILENAME_MSV = "/sys/board_properties/soc/msv";
	 private static final String KERNEL_VERSION = "3.0.8\nandroid@localhost #1";
    private static final String KEY_KERNEL_VERSION = "kernel_version";
    private static final String KEY_BUILD_NUMBER = "build_number";
    private static final String KEY_DEVICE_MODEL = "device_model";
    private static final String KEY_BASEBAND_VERSION = "baseband_version";
	private static final String KEY_FIRMWARE_VERSION = "firmware_version";
    private static final String KEY_CPU = "cpu";
    private static final String KEY_RAM = "ram";
    private static final String KEY_RESOLUTION = "resolution";
    private static final String KEY_INTERNAL_STORAGE = "internal_storage";  
    private static final String KEY_EMUI_VERSION = "emui_version";
    private static final String PROPERTY_BUILD_EMUI = "ro.build.version.emui";
    private static final String KEY_HARDWARE_VERSION = "hardware_version";
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.device_info_settings);  
		setStringSummary(KEY_FIRMWARE_VERSION, Build.VERSION.RELEASE);
		
        setStringSummary(KEY_DEVICE_MODEL, Build.MODEL + getMsvSuffix());
//        setValueSummary(KEY_EQUIPMENT_ID, PROPERTY_EQUIPMENT_ID);
        setStringSummary(KEY_DEVICE_MODEL, Build.MODEL);
   	    String destineVersion = getProperty("ro.product.destine.version");
        setStringSummary(KEY_BUILD_NUMBER, ("" != destineVersion) ? destineVersion : Build.DISPLAY);
        findPreference(KEY_KERNEL_VERSION).setSummary(KERNEL_VERSION);
        setStringSummary(KEY_CPU, getCpuInfo());
        Point point = new Point();
        android.view.Display display = this.getWindowManager().getDefaultDisplay();
        display.getRealSize(point);
        String resolution = point.x + " x " + point.y;
        setStringSummary(KEY_RESOLUTION, resolution);
        setStringSummary(KEY_RAM, getTotalMemory());
        setStringSummary(KEY_INTERNAL_STORAGE, getSDCardMemory());
        removePreferenceIfEmuiMissing(getPreferenceScreen(),
                KEY_EMUI_VERSION, PROPERTY_BUILD_EMUI);
        setStringSummary(KEY_BASEBAND_VERSION, getBasebandVersion());//基带版本
        
        String hardWareVersion = getProperty("ro.product.hardwareversion");
        boolean showVersion = Boolean.valueOf(getProperty("ro.product.show_hardwareversion"));
        if (!showVersion||"".equals(hardWareVersion)) {
            getPreferenceScreen().removePreference(findPreference(KEY_HARDWARE_VERSION));
        } else {
            setStringSummary(KEY_HARDWARE_VERSION, hardWareVersion);
        }
	}
	
    private void setStringSummary(String preference, String value) {
        try {
            findPreference(preference).setSummary(value);
        } catch (RuntimeException e) {
            findPreference(preference).setSummary(
                getResources().getString(R.string.device_info_default));
        }
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
    
    private String getBasebandVersion() {
		String resultStr = getProperty("android.os.SystemProperties");
        String basebandVersion = "";
        if(!"".equals(resultStr)){
            if(resultStr.contains(",")){
            String [] mtkcdmaVersion = resultStr.split(",");
            if("".equals(mtkcdmaVersion[0])){
                basebandVersion=mtkcdmaVersion[1];
            }else if("".equals(mtkcdmaVersion[1])){
                basebandVersion=mtkcdmaVersion[0];
            }else{
                basebandVersion ="M1:"+mtkcdmaVersion[0]+"\n";
                basebandVersion =basebandVersion+"M2:"+mtkcdmaVersion[1];
                }
            }else{
                basebandVersion = resultStr;
                }
        }
        return basebandVersion;
    }
    
    private void removePreferenceIfEmuiMissing(PreferenceGroup preferenceGroup,
            String preference, String property) {
		   String resultStr = getProperty(property);
           if (resultStr.equals("")) {
                try {
                     preferenceGroup.removePreference(findPreference(preference));
                } catch (RuntimeException e) {
                     Log.d(TAG, "Property "+ property+" missing and no "
                           + preference + " preference");
                  }
           } else {
                try {
                      String emuiSummary = showEmuiVersion(resultStr);
                      if( emuiSummary != null || ! emuiSummary .equals("")){
                          findPreference(KEY_EMUI_VERSION).setSummary(emuiSummary);
                      }else {
                          preferenceGroup.removePreference(findPreference(preference));
                      }
                } catch (RuntimeException e) {
                             Log.d(TAG, "EmuiVersion missing");
                }
           }
     }
    
    private String showEmuiVersion(String emVersion) {
        String[] str = emVersion.replace("_", " ").split(" ");
          /* <DTS2012122604669 yanyongming KF67100 20121226 begin*/
          String localeString = getResources().getConfiguration().locale.toString();
          if ("zh_CN".equals(localeString)) {
              str[0] = str[0].replace("UI", getString(R.string.header_category_system));
          }
          /*  DTS2012122604669 yanyongming KF67100 20121226 end>*/
        String formatEmVersion = "";
       if (str.length > 2) {
          if (str[2].equals("BetaRel")) {
                  formatEmVersion = str[0]+ " "+ str[1]+ "\n"
                      + getResources().getString
                           (R.string.Emotion_update_to_test_version_only)
                                 + " " + str[3];
          } else if (str[2].equals("LiveRel")) {
             formatEmVersion = str[0]+ " "+ str[1] + "\n"
                   + getResources().getString(
                           R.string.Emotion_update_to_stable_version_only)
                                  + " " + str[3];
          }
        } else if(str.length == 2){
          formatEmVersion = str[0] + " " + str[1];
        }

        return formatEmVersion;
      }
   
//    private void setValueSummary(String preference, String property) {
//        try {
//            findPreference(preference).setSummary(
//                    SystemProperties.get(property,
//                            getResources().getString(R.string.device_info_default)));
//        } catch (RuntimeException e) {
//            // No recovery
//        }
//    }
    
    public String getTotalMemory() {
		String str1 = "/proc/meminfo";
		String str2="";
		String ramSizeString = "N/A";
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			while ((str2 = localBufferedReader.readLine()) != null) {
				Log.i(TAG, "---" + str2);
				if(null != str2 && str2.contains("MemTotal")) {
					 String[] array = str2.split("\\s+");
					 if(array.length < 3) {
						 return null;
					 }
					 ramSizeString = array[1];
					 String tail = array[2];
					return ramSizeString+tail;
				}
			}
		} catch (IOException e) {
		}
		return null;
	}
    
    public String getSDCardMemory() {
		long[] sdCardInfo=new long[2];
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long bSize = sf.getBlockSize();
			long bCount = sf.getBlockCount();
			long availBlocks = sf.getAvailableBlocks();

			sdCardInfo[0] = bSize * bCount / 1024;//总大小
			sdCardInfo[1] = bSize * availBlocks / 1024;//可用大小
		}
		return "总大小:"+sdCardInfo[0]+"kB "+"可用大小:"+sdCardInfo[1]+"kB ";
	}
    
    
	public String[] getVersion(){
		String[] version={"null","null","null","null"};
		String str1 = "/proc/version";
		String str2;
		String[] arrayOfString;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			version[0]=arrayOfString[2];//KernelVersion
			localBufferedReader.close();
		} catch (IOException e) {
		}
		version[1] = Build.VERSION.RELEASE;// firmware version
		version[2]=Build.MODEL;//model
		version[3]=Build.DISPLAY;//system version
		return version;
	}
    
    private String getCpuInfo() {
        String cpuInfo = "";
        int cpuCount = CpuManager.getCpuCount();
        if (cpuCount == 2) {
            cpuInfo = getResources().getString(R.string.dual_core);
        } else if (cpuCount == 4) {
            cpuInfo = getResources().getString(R.string.quad_core);
        }
        String maxCpuFreq = getResources().getString(R.string.set_cpu_info);
        cpuInfo = cpuInfo + " " + maxCpuFreq;
        return cpuInfo;
    }
    
//    private void updateStorageUsage() {
//        long freeStorage = 0L;
//        long totalStorage = 0L;
//        long ramLong = 0L;
//        StorageManager mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
//        String path = "";
//        for (StorageVolume storageVolume : mStorageManager.getVolumeList()) {
//            if ((!storageVolume.isRemovable()) && storageVolume.isEmulated()) {
//                path = storageVolume.getPath();
//            }
//        }
//        freeStorage = getAvailableSize(path);
//        String totalStorageString = SystemInfo.getDeviceEmmc();
//        /* < DTS2013010707475 liwenqi KF66783 20121229 begin */
//        if (null != totalStorageString && !totalStorageString.isEmpty()) {
//            totalStorage = Long.parseLong(totalStorageString) * 1024L;
//            if (totalStorage < TOTAL_STORAGE) {
//                totalStorage = TOTAL_STORAGE;
//            } else if (totalStorage < 2L * TOTAL_STORAGE) {
//                totalStorage = 2L * TOTAL_STORAGE;
//            } else if (totalStorage < 4L * TOTAL_STORAGE) {
//                totalStorage = 4L * TOTAL_STORAGE;
//            } else {
//                totalStorage = 8L * TOTAL_STORAGE;
//            }
//        /* DTS2013010707475 liwenqi KF66783 20121229 end > */
//        }
//        String sizeStr = Formatter.formatFileSize(this, freeStorage);
//        String sizeStr2 = Formatter.formatFileSize(this, totalStorage);
//        sizeStr = Utils.addBlankIntoText(sizeStr);
//        sizeStr2 = Utils.addBlankIntoText(sizeStr2);
//        String background = getResources().getString(R.string.service_background_processes, sizeStr);
//        String total = getResources().getString(R.string.service_total_processes, sizeStr2);
//        setStringSummary(KEY_INTERNAL_STORAGE, background + ", " + total);
//
//        String ramString = SystemInfo.getDeviceRam();
//        ramLong = Long.parseLong(ramString);
//        long times = ramLong / (256L * 1024L);
//        long residue = ramLong % (256L * 1024L);
//        long tail = (((double) residue) / (256L * 1024L)) < DEVIATION ? 0L : 1L;
//        ramLong = (times + tail) * 256L * 1024L * 1024L;
//        String sizeStr3 = "";
//        if (ramLong != 0L) {
//            //Show the real RAM while needed
//            if (SystemProperties.get(MODIFY_RAM_SHOW, "false").equals("true")) {
//                if (ramLong < GIGABYTE) {
//                    sizeStr3 = "1G";
//                } else if(ramLong >= GIGABYTE && ramLong < 2*GIGABYTE){
//                    sizeStr3 = "2G";
//                } else {
//                    sizeStr3 = Formatter.formatShortFileSize(this, ramLong);
//                }
//            } else {
//                sizeStr3 = Formatter.formatShortFileSize(this, ramLong);
//            }
//            setStringSummary(KEY_RAM, sizeStr3);
//        }
//    }
    
    private String getMsvSuffix() {
        // Production devices should have a non-zero value. If we can't read it, assume it's a
        // production device so that we don't accidentally show that it's an ENGINEERING device.
        try {
            String msv = readLine(FILENAME_MSV);
            // Parse as a hex number. If it evaluates to a zero, then it's an engineering build.
            if (Long.parseLong(msv, 16) == 0) {
                return " (ENGINEERING)";
            }
        } catch (IOException ioe) {
            // Fail quietly, as the file may not exist on some devices.
        } catch (NumberFormatException nfe) {
            // Fail quietly, returning empty string should be sufficient
        }
        return "";
    }
    
    private String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }
	
	@Override
	@Deprecated
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		// TODO Auto-generated method stub
		if (preference.getKey().equals("status_info")) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setClassName("com.hw.autotest",com.hw.autotest.info.Status.class.getName());
            try {
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Unable to start activity " + intent.toString());
            }
        }
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
	
}
