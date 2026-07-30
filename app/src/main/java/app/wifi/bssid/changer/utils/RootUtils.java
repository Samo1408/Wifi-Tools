package app.wifi.bssid.changer.utils;

import android.os.Build;
import java.io.DataOutputStream;

public class RootUtils {
    public static boolean checkRoot() {
        try {
            Process p = new ProcessBuilder("s" + "u").start();
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            return p.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean writeConfigToSystem(String wifi, String bssid, String bt, String drm, String sysId) {
        try {
            Process p = new ProcessBuilder("s" + "u").start();
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            
            String dir = "/data/adb/wifi_spoof";
            String file = dir + "/config.properties";
            
            os.writeBytes("mkdir -p " + dir + "\n");
            os.writeBytes("chmod 755 " + dir + "\n");
            
            os.writeBytes("echo wifi_ssid=" + wifi + " > " + file + "\n");
            os.writeBytes("echo bssid=" + bssid + " >> " + file + "\n");
            os.writeBytes("echo bluetooth_mac=" + bt + " >> " + file + "\n");
            os.writeBytes("echo widevine_drm_id=" + drm + " >> " + file + "\n");
            os.writeBytes("echo system_id=" + sysId + " >> " + file + "\n");
            
            os.writeBytes("chmod 644 " + file + "\n");
            os.writeBytes("exit\n");
            os.flush();
            return p.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
