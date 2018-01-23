package com.tencent.wstt.gt.collector.util;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by p_hongjcong on 2017/8/21.
 */
public class CPUUtil {
    private static final int BUFFER_SIZE = 128;

    // 获取CPU的最大频率，单位KHZ
    public static String getMaxCpuFreq() {
        StringBuilder builder = new StringBuilder();
        InputStream in = null;

        try {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
            ProcessBuilder cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            in = process.getInputStream();

            int read;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((read = in.read(buffer)) != -1) {
                builder.append(new String(buffer, 0, read));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            builder.delete(0, builder.length());
            builder.append("N/A");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return builder.toString().trim();
    }

    // 获取CPU最小频率（单位KHZ）
    public static String getMinCpuFreq() {
        StringBuilder builder = new StringBuilder();
        InputStream in = null;

        try {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"};
            ProcessBuilder cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            in = process.getInputStream();

            int read;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((read = in.read(buffer)) != -1) {
                builder.append(new String(buffer, 0, read));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            builder.delete(0, builder.length());
            builder.append("N/A");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return builder.toString().trim();
    }

    // 实时获取CPU当前频率（单位KHZ）
    public static String getCurCpuFreq() {
        String result = "N/A";
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(
                    "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"));
            String text = br.readLine();
            result = text.trim();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    // 获取CPU名字
    public static String getCpuName() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/cpuinfo"));
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
