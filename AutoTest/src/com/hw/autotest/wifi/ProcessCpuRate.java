package com.hw.autotest.wifi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.util.Log;

public class ProcessCpuRate {

	public static double getProcessCpuRate(){
		
		long totalCpuTime1 = getTotalCpuTime();
		long appCpuTime1 = getAppCpuTime();
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long totalCpuTime2 = getTotalCpuTime();
		long appCpuTime2 = getAppCpuTime();
		
		Log.d("app",""+(appCpuTime2 - appCpuTime1));
		Log.d("total",""+(totalCpuTime2 - totalCpuTime1));
		
		
		double CpuRate = (double)(appCpuTime2 - appCpuTime1) / (totalCpuTime2 - totalCpuTime1);
		
		return CpuRate ;
		
		
	}
	
	public static long getTotalCpuTime(){
		String[] cpuInfos = null;
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/stat")),1000);
			String load = reader.readLine();
			reader.close();
			
			cpuInfos = load.split(" ");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long totalCpu = Long.parseLong(cpuInfos[2])+Long.parseLong(cpuInfos[3])+Long.parseLong(cpuInfos[4])
				+Long.parseLong(cpuInfos[5])+Long.parseLong(cpuInfos[6])+Long.parseLong(cpuInfos[7])
				+Long.parseLong(cpuInfos[8]);
		
		return totalCpu;
	}
	
	public static long getAppCpuTime(){
	
		String[] cpuInfos = null;
		
		int pid = android.os.Process.myPid();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/"+pid+"/stat")),1000);
			String load = reader.readLine();
			reader.close();
			cpuInfos = load.split(" ");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Long appCpuTime = Long.parseLong(cpuInfos[13])
				+ Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
				+ Long.parseLong(cpuInfos[16]);
		
		return appCpuTime;
	}

}
