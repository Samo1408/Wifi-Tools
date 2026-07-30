package app.wifi.bssid.changer.managers;

import java.util.Random;

public class WifiNameManager {
    private static final String[] GLOBAL_SSIDS = {
        "Starlink_HighSpeed", "Linksys_5G_Ext", "TP-Link_Secure_Pro", 
        "Netgear_Orbi_Guest", "Google_Fiber_Wifi", "ASUS_ROG_Gaming",
        "Airport_Free_Wifi", "Hotel_Lobby_HighSpeed", "Coffee_Shop_5G",
        "Tesla_Supercharger", "XFINITY_WiFi", "Boingo_Hotspot", 
        "D-Link_SmartLife", "Eero_Mesh_Pro", "Belkin_Setup_5G"
    };

    public static String generateRandomSSID() {
        Random random = new Random();
        int index = random.nextInt(GLOBAL_SSIDS.length);
        int suffix = random.nextInt(9000) + 1000;
        return GLOBAL_SSIDS[index] + "_" + suffix;
    }

    public static boolean validateSSID(String ssid) {
        return ssid != null && !ssid.trim().isEmpty() && ssid.length() <= 32;
    }
}
