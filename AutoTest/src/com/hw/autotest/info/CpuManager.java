/* <DTS2012101208448 yanyongming KF67100 20121012 created> */
package com.hw.autotest.info;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CpuManager {
    private final static String PROCESSOR_HEADER = "processor";
    private final static String RAM_HEADER = "mem";

    public static String getMaxCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = { "/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" };
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        return result.trim();
    }

    public static String getMinCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = { "/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq" };
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        return result.trim();
    }

    public static String getCurCpuFreq() {
        String result = "N/A";
        try {
            FileReader fr = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            if (text != null) {
                result = text.trim();
            }
            br.close();
            fr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            br.close();
            fr.close();
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getCpuCount() {
        int cpuCount = 0;
        ArrayList<String> list = new ArrayList<String>();
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String[] array = null;
            String text = null;
            while ((text = br.readLine()) != null) {
                array = text.split(":\\s+", 2);
                if (PROCESSOR_HEADER.equals(array[0].trim())) {
                    list.add(array[1]);
                }
            }
            for (int i = 0; i < list.size(); i++) {
                int temp = Integer.parseInt(list.get(i));
                cpuCount = Math.max(cpuCount, temp);
            }
            br.close();
            fr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cpuCount + 1;
    }

    public static long getRamSize() {
        String ramSizeString = "N/A";
        long ramSizeLong = 0L;
        long times = 1L;
        try {
            FileReader fr = new FileReader("/proc/cmdline");
            BufferedReader br = new BufferedReader(fr);
            String[] array = null;
            String[] record = null;
            String value = "";
            String text = br.readLine();
            if (text != null) {
                array = text.split("\\s+");
                for (int i = 0; i < array.length; i++) {
                    record = array[i].split("=", 2);
                    if (record.length < 2) {
                        continue;
                    }
                    if (RAM_HEADER.equals(record[0])) {
                        ramSizeString = record[1];
                    }
                }
                if (ramSizeString.endsWith("G")) {
                    times = 1024L * 1024L * 1024L;
                    value = ramSizeString.substring(0, ramSizeString.length() - 1);
                } else if (ramSizeString.endsWith("GB")) {
                    times = 1024L * 1024L * 1024L;
                    value = ramSizeString.substring(0, ramSizeString.length() - 2);
                } else if (ramSizeString.endsWith("M")) {
                    times = 1024L * 1024L;
                    value = ramSizeString.substring(0, ramSizeString.length() - 1);
                } else if (ramSizeString.endsWith("MB")) {
                    times = 1024L * 1024L;
                    value = ramSizeString.substring(0, ramSizeString.length() - 2);
                }
                ramSizeLong = (long) (Double.parseDouble(value) * times);
            }
            br.close();
            fr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ramSizeLong;
    }
}