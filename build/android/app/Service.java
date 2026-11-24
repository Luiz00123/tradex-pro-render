package android.app;
import android.content.Intent;
import android.os.IBinder;
import android.os.Bundle;
public class Service extends Activity {
    protected void onCreate(Bundle savedInstanceState) {}
    protected void onStartCommand(Object intent, int flags, int startId) {}
    public IBinder onBind(Intent intent) { return null; }
    protected void onDestroy() {}
    public int onStartCommand(Intent intent, int flags, int startId) { return 1; }
    public static final int START_STICKY = 1;
}
