package app.wifi.bssid.changer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import app.wifi.bssid.changer.db.DatabaseHelper;
import app.wifi.bssid.changer.managers.WifiNameManager;
import app.wifi.bssid.changer.managers.BssidManager;
import app.wifi.bssid.changer.managers.BluetoothManager;
import app.wifi.bssid.changer.managers.DrmIdManager;
import app.wifi.bssid.changer.managers.SystemIdManager;
import app.wifi.bssid.changer.utils.RootUtils;
import app.wifi.bssid.changer.services.SpoofNotificationService;

public class MainActivity extends Activity {

    private DatabaseHelper dbHelper;
    private View rootIndicator;
    private EditText etWifiSsid, etBssid, etBluetoothMac, etDrmId, etSystemId;
    private Button btnRandomWifi, btnRandomBssid, btnRandomBt, btnRandomDrm, btnRandomSysId;
    private Button btnSave, btnRestore, btnStartService, btnStopService;
    private TextView tvStatus;

    private static final String KEY_WIFI_SSID = "wifi_ssid";
    private static final String KEY_BSSID = "bssid";
    private static final String KEY_BLUETOOTH_MAC = "bluetooth_mac";
    private static final String KEY_DRM_ID = "widevine_drm_id";
    private static final String KEY_SYSTEM_ID = "system_id";

    private static final String DEF_WIFI = "Original_WiFi";
    private static final String DEF_BSSID = "00:11:22:33:44:55:66:77";
    private static final String DEF_BT = "AA:BB:CC:DD:EE:FF:11:22";
    private static final String DEF_DRM = "A1B2C3D4E5F67890A1B2C3D4E5F67890";
    private static final String DEF_SYS_ID = "4012";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        rootIndicator = findViewById(R.id.root_indicator);
        etWifiSsid = findViewById(R.id.et_wifi_ssid);
        etBssid = findViewById(R.id.et_bssid);
        etBluetoothMac = findViewById(R.id.et_bluetooth_mac);
        etDrmId = findViewById(R.id.et_drm_id);
        etSystemId = findViewById(R.id.et_system_id);

        btnRandomWifi = findViewById(R.id.btn_random_wifi);
        btnRandomBssid = findViewById(R.id.btn_random_bssid);
        btnRandomBt = findViewById(R.id.btn_random_bt);
        btnRandomDrm = findViewById(R.id.btn_random_drm);
        btnRandomSysId = findViewById(R.id.btn_random_sys_id);

        btnSave = findViewById(R.id.btn_save);
        btnRestore = findViewById(R.id.btn_restore);
        btnStartService = findViewById(R.id.btn_start_service);
        btnStopService = findViewById(R.id.btn_stop_service);
        tvStatus = findViewById(R.id.tv_status);

        checkRootStatus();
        loadSavedSettings();
        setupListeners();
    }

    private void checkRootStatus() {
        boolean isRooted = RootUtils.checkRoot();
        if (isRooted) {
            rootIndicator.setBackgroundColor(Color.GREEN);
            tvStatus.setText("حالة الروت: مفعل ومتاح");
            tvStatus.setTextColor(Color.GREEN);
        } else {
            rootIndicator.setBackgroundColor(Color.RED);
            tvStatus.setText("حالة الروت: غير مفعل أو غير متاح");
            tvStatus.setTextColor(Color.RED);
        }
    }

    private void loadSavedSettings() {
        etWifiSsid.setText(dbHelper.getSettingValue(KEY_WIFI_SSID, DEF_WIFI));
        etBssid.setText(dbHelper.getSettingValue(KEY_BSSID, DEF_BSSID));
        etBluetoothMac.setText(dbHelper.getSettingValue(KEY_BLUETOOTH_MAC, DEF_BT));
        etDrmId.setText(dbHelper.getSettingValue(KEY_DRM_ID, DEF_DRM));
        etSystemId.setText(dbHelper.getSettingValue(KEY_SYSTEM_ID, DEF_SYS_ID));
    }

    private void setupListeners() {
        btnRandomWifi.setOnClickListener(v -> etWifiSsid.setText(WifiNameManager.generateRandomSSID()));
        btnRandomBssid.setOnClickListener(v -> etBssid.setText(BssidManager.generateRandomBssid()));
        btnRandomBt.setOnClickListener(v -> etBluetoothMac.setText(BluetoothManager.generateRandomMac()));
        btnRandomDrm.setOnClickListener(v -> etDrmId.setText(DrmIdManager.generateRandomDrmId()));
        btnRandomSysId.setOnClickListener(v -> etSystemId.setText(SystemIdManager.generateRandomSystemId()));

        btnSave.setOnClickListener(v -> {
            String wifi = etWifiSsid.getText().toString().trim();
            String bssid = etBssid.getText().toString().trim();
            String bt = etBluetoothMac.getText().toString().trim();
            String drm = etDrmId.getText().toString().trim();
            String sysId = etSystemId.getText().toString().trim();

            if (!WifiNameManager.validateSSID(wifi)) {
                Toast.makeText(this, "اسم الوايفاي غير صالح", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!BssidManager.validateBssid(bssid)) {
                Toast.makeText(this, "عنوان BSSID غير صالح (يجب أن يكون 6 أو 8 خانات تفصل بينها :)", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!BluetoothManager.validateMac(bt)) {
                Toast.makeText(this, "عنوان بلوتوث ماك غير صالح", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!DrmIdManager.validateDrmId(drm)) {
                Toast.makeText(this, "Widevine DRM ID غير صالح", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!SystemIdManager.validateSystemId(sysId)) {
                Toast.makeText(this, "System ID غير صالح", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.saveSetting(KEY_WIFI_SSID, wifi, DEF_WIFI);
            dbHelper.saveSetting(KEY_BSSID, bssid, DEF_BSSID);
            dbHelper.saveSetting(KEY_BLUETOOTH_MAC, bt, DEF_BT);
            dbHelper.saveSetting(KEY_DRM_ID, drm, DEF_DRM);
            dbHelper.saveSetting(KEY_SYSTEM_ID, sysId, DEF_SYS_ID);

            boolean success = RootUtils.writeConfigToSystem(wifi, bssid, bt, drm, sysId);
            if (success) {
                Toast.makeText(this, "تم حفظ وتطبيق الإعدادات بنجاح!", Toast.LENGTH_SHORT).show();
                Intent serviceIntent = new Intent(this, SpoofNotificationService.class);
                startService(serviceIntent);
            } else {
                Toast.makeText(this, "تم الحفظ محلياً ولكن فشل التطبيق على النظام (تأكد من صلاحيات الروت ومثبت Magisk)", Toast.LENGTH_LONG).show();
            }
        });

        btnRestore.setOnClickListener(v -> {
            etWifiSsid.setText(DEF_WIFI);
            etBssid.setText(DEF_BSSID);
            etBluetoothMac.setText(DEF_BT);
            etDrmId.setText(DEF_DRM);
            etSystemId.setText(DEF_SYS_ID);

            dbHelper.saveSetting(KEY_WIFI_SSID, DEF_WIFI, DEF_WIFI);
            dbHelper.saveSetting(KEY_BSSID, DEF_BSSID, DEF_BSSID);
            dbHelper.saveSetting(KEY_BLUETOOTH_MAC, DEF_BT, DEF_BT);
            dbHelper.saveSetting(KEY_DRM_ID, DEF_DRM, DEF_DRM);
            dbHelper.saveSetting(KEY_SYSTEM_ID, DEF_SYS_ID, DEF_SYS_ID);

            RootUtils.writeConfigToSystem(DEF_WIFI, DEF_BSSID, DEF_BT, DEF_DRM, DEF_SYS_ID);
            Toast.makeText(this, "تمت استعادة الإعدادات الافتراضية", Toast.LENGTH_SHORT).show();
            
            Intent serviceIntent = new Intent(this, SpoofNotificationService.class);
            startService(serviceIntent);
        });

        btnStartService.setOnClickListener(v -> {
            Intent serviceIntent = new Intent(this, SpoofNotificationService.class);
            startService(serviceIntent);
            Toast.makeText(this, "تم تشغيل خدمة التمويه في الخلفية", Toast.LENGTH_SHORT).show();
        });

        btnStopService.setOnClickListener(v -> {
            Intent serviceIntent = new Intent(this, SpoofNotificationService.class);
            stopService(serviceIntent);
            Toast.makeText(this, "تم إيقاف الخدمة", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
