package app.wifi.bssid.changer.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import app.wifi.bssid.changer.MainActivity;
import app.wifi.bssid.changer.R;
import app.wifi.bssid.changer.db.DatabaseHelper;
import app.wifi.bssid.changer.managers.WifiNameManager;
import app.wifi.bssid.changer.managers.BssidManager;
import app.wifi.bssid.changer.managers.BluetoothManager;
import app.wifi.bssid.changer.managers.DrmIdManager;
import app.wifi.bssid.changer.managers.SystemIdManager;
import app.wifi.bssid.changer.utils.RootUtils;

public class SpoofNotificationService extends Service {

    public static final String ACTION_RANDOM_CHANGE = "app.wifi.bssid.changer.ACTION_RANDOM";
    public static final String ACTION_STOP = "app.wifi.bssid.changer.ACTION_STOP";
    private static final String CHANNEL_ID = "spoof_service_channel";
    private static final int NOTIFICATION_ID = 101;

    private DatabaseHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DatabaseHelper(this);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            if (action.equals(ACTION_RANDOM_CHANGE)) {
                performRandomChange();
            } else if (action.equals(ACTION_STOP)) {
                stopForeground(true);
                stopSelf();
                return START_NOT_STICKY;
            }
        }
        showNotification();
        return START_STICKY;
    }

    private void performRandomChange() {
        String randomWifi = WifiNameManager.generateRandomSSID();
        String randomBssid = BssidManager.generateRandomBssid();
        String randomBt = BluetoothManager.generateRandomMac();
        String randomDrm = DrmIdManager.generateRandomDrmId();
        String randomSys = SystemIdManager.generateRandomSystemId();

        dbHelper.saveSetting("wifi_ssid", randomWifi, "Original_WiFi");
        dbHelper.saveSetting("bssid", randomBssid, "00:11:22:33:44:55:66:77");
        dbHelper.saveSetting("bluetooth_mac", randomBt, "AA:BB:CC:DD:EE:FF:11:22");
        dbHelper.saveSetting("widevine_drm_id", randomDrm, "A1B2C3D4E5F67890A1B2C3D4E5F67890");
        dbHelper.saveSetting("system_id", randomSys, "4012");

        RootUtils.writeConfigToSystem(randomWifi, randomBssid, randomBt, randomDrm, randomSys);
    }

    private void showNotification() {
        String currentWifi = dbHelper.getSettingValue("wifi_ssid", "Original_WiFi");
        String currentBssid = dbHelper.getSettingValue("bssid", "00:11:22:33:44:55:66:77");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent randomIntent = new Intent(this, SpoofNotificationService.class);
        randomIntent.setAction(ACTION_RANDOM_CHANGE);
        PendingIntent randomPendingIntent = PendingIntent.getService(this, 1, randomIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent stopIntent = new Intent(this, SpoofNotificationService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 2, stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        String infoText = "SSID: " + currentWifi + " | BSSID: " + currentBssid;

        builder.setContentTitle("تطبيق تغيير البيانات يعمل بنشاط")
                .setContentText(infoText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            builder.addAction(new Notification.Action(0, "تغيير عشوائي", randomPendingIntent));
            builder.addAction(new Notification.Action(0, "إيقاف", stopPendingIntent));
        }

        startForeground(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "خدمة تمويه العناوين",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
